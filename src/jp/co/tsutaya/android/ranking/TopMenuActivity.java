/**
 * (C)Culture Convenience Club Co.,Ltd.
 */
package jp.co.tsutaya.android.ranking;

import java.io.File;
import java.io.IOException;

import jp.co.tsutaya.android.ranking.model.FeatureManager;
import jp.co.tsutaya.android.ranking.util.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * TSUTAYA Search TopMenu Activity
 *
 */
public class TopMenuActivity extends ContentsBaseActivity {
	private SharedPreferences preference;
	private static final String LAUNCHED = "Launched";
	private static final String VERSION_CODE = "VersionCode";

	private FrameLayout contentView;
	private LinearLayout contentMenuView;
	private FeatureManager fm;
	private AsyncTask<Void, Void, Boolean> requestPopup = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.isNoLayaut = true;
		getIntent().putExtra(Utils.URL, Utils.getUrl(Utils.MENU_TOP));
		super.onCreate(savedInstanceState);
		postRTMetrics(Utils.getRTMetricsParams(Utils.getUrl(Utils.MENU_TOP)));
		preference = getSharedPreferences("pref", MODE_PRIVATE);

		// Check app is updated
		int oldVersionCode = preference.getInt(VERSION_CODE, 0);
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			int newVersionCode = pInfo.versionCode;
			if(newVersionCode != oldVersionCode){
				Editor editor = preference.edit();
				editor.putBoolean(TopMenuActivity.LAUNCHED, false);
				editor.putInt(VERSION_CODE, newVersionCode);
				editor.commit();
			}
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		initViews();
	}

	private void initViews() {
		contentView = (FrameLayout) findViewById(R.id.content);
		contentView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				return true;
			}
		});
		contentMenuView = (LinearLayout) findViewById(R.id.menu_content);

		ViewGroup menuButtons = (ViewGroup) findViewById(R.id.menu_button);

		// Facebookボタンの文字スタイル設定
		//SpannableStringBuilder sb = new SpannableStringBuilder();
		//String text_sub = "毎日更新！最新＆お得情報はこちらでチェック！";
		//String text_main = "TSUTAYA Facebookページへ";
		//TextAppearanceSpan subTextSpan =  new TextAppearanceSpan(this, R.style.MenuButtonFacebookSubText);
		//TextAppearanceSpan mainTextSpan =  new TextAppearanceSpan(this, R.style.MenuButtonFacebookMainText);
		//sb.append(text_sub);
		//sb.setSpan(subTextSpan, 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		//sb.append("\n");
		//int start = sb.length();
		//sb.append(text_main);
		//sb.setSpan(mainTextSpan, start, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		//((Button)findViewById(R.id.btn_facebook)).setText(sb);
		//((Button)findViewById(R.id.btn_facebook)).setLineSpacing(0.2f, 0.8f);

		for (int rowNum = 0; rowNum < menuButtons.getChildCount(); rowNum++) {
			ViewGroup buttonRow = (ViewGroup) menuButtons.getChildAt(rowNum);
			for (int btnNum = 0; btnNum < buttonRow.getChildCount(); btnNum++) {
				Button menuBtn = (Button) buttonRow.getChildAt(btnNum);
				menuBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Button btn = (Button) v;
						int menuId = Integer.parseInt((String) btn.getTag());
						Log.d(btn.getText().toString(), menuId + "");
						if (menuId == getResources().getInteger(Utils.MENU_STOCKSEARCH)) {
							showStockSearch(null);
						} else if (menuId == getResources().getInteger(Utils.MENU_TSUTAYAAR)) {
							startExternalApplication(getResources().getString(R.string.app_id_tsutata_ar));
						} else if (menuId == getResources().getInteger(Utils.MENU_FACEBOOK)) {
							Uri uri = Uri.parse(getString(R.string.url_facebook));
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						} else {
							Utils.defaultMenuItemSelected(Utils.getMenuId(menuId, getResources()), TopMenuActivity.this, getApplicationContext()
									.getClass());
						}
					}
				});
			}
		}

		// 特集をセット
		fm = new FeatureManager(this);
		contentMenuView.addView(fm.getBlankFeatureView(), 0);
		AsyncHttpTask async = new AsyncHttpTask(contentMenuView);
		async.execute();

		// 初回起動時のみinfo画面を表示する
		Log.d("TopMenuActivity", "IS LAUNCHED ? " + preference.getBoolean(LAUNCHED, false));	

		// info画面の表示が必要でなくても、websettingをしておかなければ、LocalStorage周りで不具合が生じる
		final WebView infoView = (WebView) findViewById(R.id.informationWebView);
		infoView.setVerticalScrollbarOverlay(true);
		setWebsettings(infoView);
		if (preference.getBoolean(LAUNCHED, false) == false) {
			// クーポン情報をセット
			// オーバレイの下レイヤーのタッチイベントを無効化する
			findViewById(R.id.top_info).setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent motionevent) {
					return true;
				}
			});

			requestPopup = new AsyncTask<Void, Void, Boolean>(){
				@Override
				protected Boolean doInBackground(Void... params) {
					return checkRequestPopup(Utils.TOP_POPUP_URL);
				}

				@Override
				protected void onPostExecute(Boolean result) {
					if(result){
						infoView.loadUrl(Utils.TOP_POPUP_URL);
						postRTMetrics(new String[] { "TS00", "top_info" });
					}
					else{
						infoView.loadUrl(FILE_SCHEME + "top.html");	
					}
				}
			};
			requestPopup.execute();

			ImageView closeButton = (ImageView) findViewById(R.id.close_button);
			closeButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					try{
						if(requestPopup != null){
							requestPopup.cancel(true);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					closePopup();
				}
			});

			infoView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					findViewById(R.id.web_progress).setVisibility(View.VISIBLE);
					super.onPageStarted(view, url, favicon);
				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.stopLoading();
					if(url.startsWith("app::store_search")){
						goToUrlWithNewClass("store_search.html", Utils.MENU_STORESEARCH);
						new Handler().postDelayed(new Runnable() {							
							@Override
							public void run() {
								closePopup();
							}
						}, 500);
					}
					else{
						Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(i);
					}
					return false;
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					findViewById(R.id.web_progress).setVisibility(View.GONE);
				}

				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					infoView.loadUrl(FILE_SCHEME + "top.html");
					super.onReceivedError(view, errorCode, description, failingUrl);
				}
			});
		} else {
			findViewById(R.id.top_info).setVisibility(View.GONE);
		}
	}

	/**
	 * Close popup
	 */
	public void closePopup(){
		Intent intent = getIntent();
		intent.putExtra("couponViewed", true);
		findViewById(R.id.top_info).setVisibility(View.GONE);
		fm.fadeFeatureButton();
		// 起動済みステータスに書き換え
		Editor editor = preference.edit();
		editor.putBoolean(LAUNCHED, true);
		editor.commit();
	}

	/**
	 * Check link popup
	 * @param url
	 * @return
	 */
	public boolean checkRequestPopup(String url){
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		try {
			response = httpclient.execute(new HttpGet(url));
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == 200){
				httpclient.getConnectionManager().shutdown();
				return true;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO: handle exception
		} catch (NullPointerException e) {
			// TODO: handle exception
		}
		httpclient.getConnectionManager().shutdown();
		return false;
	}

	class AsyncHttpTask extends AsyncTask<String, String, FrameLayout> {
		LinearLayout menuView;

		public AsyncHttpTask(LinearLayout menuView) {
			super();
			this.menuView = menuView;
		}

		/**
		 * バックグランドで行う処理
		 *
		 * @return
		 */
		@Override
		protected FrameLayout doInBackground(String... value) {
			return fm.getFeatureView();

		}

		/**
		 * バックグランド処理が完了し、UIスレッドに反映する
		 */
		@Override
		protected void onPostExecute(FrameLayout featureView) {
			menuView.removeViewAt(0); // BlankFeatureViewを削除しておく
			menuView.addView(featureView, 0);
			if (preference.getBoolean(LAUNCHED, true) && fm != null) {
				fm.fadeFeatureButton();
			}
		}
	}

	/**
	 * 追加するオプションメニューの実装
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.clear();
		return true;
	}

	/**
	 * 固有のメンバ変数の値を退避
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("couponCheckService", this.isCouponCheckServiceOn());
		super.onSaveInstanceState(outState);
	}

	/**
	 * 固有のメンバ変数の値を復元
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		this.setCouponCheckServiceOn(savedInstanceState.getBoolean("couponCheckService"));
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == Utils.MENU_TOP_UPDATE) {
			setContentView(R.layout.top_contents_test);
			initSideMenu();
			initViews();
			// setScrollListener();
			loadView = findViewById(R.id.loading);
			loadView.setVisibility(View.GONE);
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	public void setWebsettings(WebView wv) {
		File databasePath = getDatabasePath("dbchache");
		WebSettings settings = wv.getSettings();
		settings.setAllowFileAccess(true);
		settings.setBlockNetworkImage(false);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		settings.setGeolocationEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setDatabasePath(databasePath.toString());
		wv.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize, long totalUsedQuota,
					QuotaUpdater quotaUpdater) {
				// DBサイズを拡張しておく
				quotaUpdater.updateQuota(MAX_DATABASE_SIZE);
			}
		});
	}

	@Override
	public void onPause() {
		Log.i("TopMenuActivity", "onPause");
		super.onPause();
		if (fm != null) {
			fm.stopAutoSlide();
		}
	}

	@Override
	public void onResume() {
		Log.i("TopMenuActivity", "onResume");
		super.onResume();
		if (fm != null) {
			fm.startAutoSlide();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		fm.resetFeatureView();
		fm = null;
		WebView infoView = (WebView) findViewById(R.id.informationWebView);
		infoView.stopLoading();
		infoView.setWebChromeClient(null);
		infoView.setWebViewClient(null);
		unregisterForContextMenu(infoView);
		infoView.destroy();
		infoView = null;
	}
}
