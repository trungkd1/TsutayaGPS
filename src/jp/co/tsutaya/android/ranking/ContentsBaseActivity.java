package jp.co.tsutaya.android.ranking;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.appAdForce.android.AdManager;
import jp.appAdForce.android.LtvManager;
import jp.co.tsutaya.android.ranking.model.StoreDao;
import jp.co.tsutaya.android.ranking.slide.MenuAdapter;
import jp.co.tsutaya.android.ranking.slide.ScrollerLinearLayout;
import jp.co.tsutaya.android.ranking.util.Utils;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.auriq.myrt.android.MyrtDataSender;

public class ContentsBaseActivity extends Activity {

	/** 追加ボタン表示フラグ */
	protected boolean addFavBtnEnable = false;

	protected static boolean isCouponViewed = false;

	/** クーポンチェックサービス起動済フラグ */
	protected static boolean couponCheckServiceOn = false;

	/** 設定画面からのログイン処理後クーポンページから設定画面にリダイレクトするかの判定 */
	protected static boolean redirectSettingPage = false;

	public boolean isCouponCheckServiceOn() {
		return couponCheckServiceOn;
	}

	public void setCouponCheckServiceOn(boolean on) {
		if (on) {
			couponCheckServiceOn = true;
		} else {
			couponCheckServiceOn = false;
		}
	}

	/** サイドメニュー */
	private ScrollerLinearLayout sideSlideLayout;
	protected EditText keywordEditText;
	protected Spinner genreSpinner;
	private ListView listView;
	private ImageView sclearView;

	private final String[] sidemenuInfoList = {
			R.string.menu_home + Utils.DS + R.drawable.tsc_menu_icon_feature + Utils.DS + R.drawable.tsc_menu_icon_feature_current + Utils.DS
					+ Utils.MENU_TOP,
			R.string.menu_storesearch + Utils.DS + R.drawable.tsc_menu_icon_storesearch + Utils.DS + R.drawable.tsc_menu_icon_storesearch_current
					+ Utils.DS + Utils.MENU_STORESEARCH,
			R.string.menu_release + Utils.DS + R.drawable.tsc_menu_icon_releaseinfo + Utils.DS + R.drawable.tsc_menu_icon_releaseinfo_current
					+ Utils.DS + Utils.MENU_RELEASE,
			R.string.menu_ranking + Utils.DS + R.drawable.tsc_menu_icon_ranking + Utils.DS + R.drawable.tsc_menu_icon_ranking_current + Utils.DS
					+ Utils.MENU_RANKING,
			R.string.menu_coupon + Utils.DS + R.drawable.tsc_menu_icon_coupon + Utils.DS + R.drawable.tsc_menu_icon_coupon_current + Utils.DS
					+ Utils.MENU_COUPON,
			R.string.menu_favorite + Utils.DS + R.drawable.tsc_menu_icon_favorite + Utils.DS + R.drawable.tsc_menu_icon_favorite_current + Utils.DS
					+ Utils.MENU_FAVORITE,
			R.string.menu_tlog + Utils.DS + R.drawable.tsc_menu_icon_tlog + Utils.DS + R.drawable.tsc_menu_icon_tlog_current + Utils.DS
					+ Utils.MENU_TLOG,
			R.string.menu_tsutayaar + Utils.DS + R.drawable.tsc_menu_icon_tsutayaar + Utils.DS + R.drawable.tsc_menu_icon_tsutayaar_current
					+ Utils.DS + Utils.MENU_TSUTAYAAR,
			R.string.menu_related + Utils.DS + R.drawable.tsc_menu_icon_relatedservice + Utils.DS + R.drawable.tsc_menu_icon_relatedservice_current
					+ Utils.DS + Utils.MENU_RELATED,
			R.string.menu_facebook + Utils.DS + R.drawable.tsc_menu_icon_facebook + Utils.DS + R.drawable.tsc_menu_icon_facebook_current + Utils.DS
					+ Utils.MENU_FACEBOOK,
			R.string.menu_settings + Utils.DS + R.drawable.tsc_menu_icon_setting + Utils.DS + R.drawable.tsc_menu_icon_setting_current + Utils.DS
					+ Utils.MENU_SETTINGS };

	/** 描画領域のView */
	protected WebView tabContents;

	/** ナビゲータ領域のView */
	protected WebView navitator;

	/** UI用イベントハンドラ */
	Handler mUiHandler;

	/** localStrageの最大容量 */
	protected static final int MAX_DATABASE_SIZE = 1024 * 1024;

	private static final String ACTION_ALERT = "jp.co.tsutaya.android.ranking.ACTION_ALERT";
	private static final String ACTION_MTMCPN = "jp.co.tsutaya.android.ranking.ACTION_COUPON";

	/** 実行中のActivity */
	protected String instancedActivity;

	/** ユーザの状態 */
	protected static int userLoginStatus = Utils.USER_IS_NOT_LOGIN;
	protected static int appMyfcMergeStatus = Utils.APP_MYFC_NOT_MERGED;

	public int getUserLoginStatus() {
		return userLoginStatus;
	}

	public int getAppMyfcMergeStatus() {
		return appMyfcMergeStatus;
	}

	protected static int climorCouponCount = 0;

	public int getClimorCouponCount() {
		return climorCouponCount;
	}

	public void setClimorCouponCount(int val) {
		if (val >= 0) {
			climorCouponCount = val;
		}
	}

	public boolean isNoLayaut = false;
	/* ICS 対応 */
	protected String requestURL = "";

	public String getRequestURL() {
		return ContentsBaseActivity.this.requestURL;
	}

	public void setRequestURL(String url) {
		ContentsBaseActivity.this.requestURL = url;
	}

	/**
	 * assetへのパス
	 */
	protected static final String FILE_SCHEME = "file:///android_asset/";
	// private static final String HTTP_SCHEME = "http://";
	/**
	 * ロード中のバックグラウンド色
	 */
	protected static final int LOADING_BACKGROUND_COLOR = Color.argb(175, 50, 50, 50);

	/** ロード中画面用 */
	protected View loadView;
	protected FrameLayout frame;

	private BroadcastReceiver mBreceiver;

	/** CyberZ LTV計測が必要かどうか */
	protected boolean isCyberZLtv;

	/** RTメトリクス用変数 */
	private MyrtDataSender mMyrt;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("ContentsBaseActivity", "onCreateBaseaActivity is started");
		mMyrt = new MyrtDataSender(this, "", "www.tsutaya.co.jp/rt/rt_mark.gif", "tsutaya");
		mMyrt.setSendMode(MyrtDataSender.PACKETCAPTUREMODE);
		this.instancedActivity = this.getLocalClassName();
		// 全てのActivityでCookieを使えるようにしておく
		CookieSyncManager.createInstance(this);
		CookieManager.getInstance().setAcceptCookie(true);
		CookieManager.getInstance().removeExpiredCookie();
		// Activity起動時に検索BOXのソフトキーボードが開かなようにする
		this.getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		if ("TopMenuActivity".equals(this.instancedActivity)) {
			setContentView(R.layout.top_contents_test);
			Log.i("ContentsBaseActivity", "setContent:" + "TopMenu");
		} else {
			setContentView(R.layout.contents);
			tabContents = (WebView) findViewById(R.id.tabContents);
			if (isCyberZLtv) {
				Log.i("ContentsBaseActivity", "Set cyber Z LTV");
				AdManager ad = new AdManager(getApplicationContext());
				LtvManager ltv = new LtvManager(ad);
				ltv.setLtvCookie();
			}
			frame = (FrameLayout) findViewById(R.id.contentsFrameLayout);
		}
		
		// ロード中画面
		// frame = (FrameLayout) findViewById(R.id.contentsFrameLayout);
		loadView = findViewById(R.id.loading);

		// メニューの準備
		initSideMenu();

		// 描画用UIスレッドハンドラの設定
		mUiHandler = new Handler();

		// 現状TOPのみ
		if (tabContents == null) {
			loadView.setVisibility(View.GONE);
			return;
		}

		// コンテンツの準備
		tabContents.setBackgroundColor(0);
		// WebView設定
		WebSettings setting = tabContents.getSettings();
		setting.setAllowFileAccess(true);
		setting.setBlockNetworkImage(false);
		setting.setJavaScriptCanOpenWindowsAutomatically(true);
		setting.setJavaScriptEnabled(true);
		setting.setSupportZoom(false);
		setting.setGeolocationEnabled(true);

		/* クーポンに関しては、スマートフォン対応していないページを表示することがある。 */
		if ("ClimorCouponActivity".equals(this.instancedActivity)) {
			setting.setSupportZoom(true);
			setting.setLoadWithOverviewMode(true);
			setting.setUseWideViewPort(true);
			setting.setBuiltInZoomControls(true);
		}

		// LocalStrageの使用を許可
		setting.setDomStorageEnabled(true);

		// DB格納領域にLocalStrage用ファイルを格納
		File databasePath = getDatabasePath("dbchache");
		setting.setDatabasePath(databasePath.toString());

		// スクロールバーの表示設定
		tabContents.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

		// WebClient、WebChromeClientの設定
		// これをやっておかないとLogが出力できなかったり画面遷移が別ブラウザで行われたりする
		tabContents.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				String url_ = url;
				Log.i("ContentBaseActivity", "URL:" + url);
				// String cact = ContentsBaseActivity.this.getLocalClassName();
				if (url.startsWith("app::menu::")) { // Topページ対応
					url_ = url.replace("app::menu::", "");
					int menuId = Utils.getMenuID(url_);
					Utils.defaultMenuItemSelected(menuId, ContentsBaseActivity.this);
					return true;
				} else if (url.startsWith("app::special::")) { // 発掘良品の画面遷移時使用
					// 発掘良品（URLの頭に"app::special::"がついている場合）
					url_ = url.replace("app::special::", "");
					goToUrl(url_);
					return true;
				} else if (url.startsWith(getString(R.string.url_works))) { // 作品ページへ遷移
					String afterstr = url.replace(getString(R.string.url_works), "");
					String urlCd = afterstr.replace(".html", "");
					if (urlCd != null) {
						url_ = "work_detail.html?urlCd=" + urlCd;
						goToUrl(url_);
						return true;
					}
				} else if (url.indexOf("tsutaya.co.jp/smartphone/ap/tlog/tlog_app.html") > 0) {
					url_ = "tlog_list_thumb.html?viewtype=2&startindex=1&endindex=20&top=app";
					// ContentsBaseActivity.this.setRequestURL(url_);
					goToUrl(url_);
					return true;
				} else if (url.indexOf("play.google.com/store/apps/details") > 0) {
					Uri uri = Uri.parse(url.replaceAll("https://play.google.com/store/apps/", "market://"));
					Intent gplayIntent = new Intent(Intent.ACTION_VIEW, uri);
					try {
						startActivity(gplayIntent);
					} catch (ActivityNotFoundException e) {
						// ブラウザ起動できない端末はないと思うが念のため
						Log.w("ContentsBaseActivity", e);
					}
					return true;
				} else if (url.startsWith("app::rels::")) { // リリカレページ内のリンクURL対応
					String checkUrl = url.replace("app::rels::", "");
					if (checkUrl.startsWith(getString(R.string.url_products))) { // 商品詳細ページへ移動
						String afterstr = checkUrl.replace(getString(R.string.url_products), "");
						// 商品詳細ページへ遷移
						if (afterstr != null) {
							String productkey = null;
							if (afterstr.indexOf("/") > 6) {
								productkey = afterstr.split("/")[1];
								productkey = productkey.substring(0, productkey.indexOf("."));
							}
							url_ = "product_detail.html?productKey=" + productkey;
							goToUrl(url_);
							return true;
						}
					}
				} 
				else if (url.startsWith(getString(R.string.url_movie))) {
					// 取得したURLに遷移遷移
					goToUrl(url);
					return true;
				} 
				else if (url.startsWith("app::safari::") || url.startsWith("http://m.youtube.com/")) { // ブラウザ立ち上げ
					// デフォルトのブラウザを起動する（又はhttpを扱えるアプリ）
					Uri uri = Uri.parse(url.replaceAll("app::safari::", ""));
					Intent videoClient = new Intent(Intent.ACTION_VIEW, uri);
					try {
						startActivity(videoClient);
					} catch (ActivityNotFoundException e) {
						// ブラウザ起動できない端末はないと思うが念のため
						Log.w("ContentsBaseActivity", e);
					}
					return true;
				} else if (url.startsWith("app::launch::")) { // アプリ起動用タグ
					String appid = url.replaceAll("app::launch::", "");
					PackageManager pm = getPackageManager();
					Intent appInt = Utils.getCallableIntent(pm, appid);
					// アプリを起動するだけ。付加情報は現在呼び出しアプリが対応していないので未実装としておきます。
					if (appInt == null) { // アプリがインストールされていない場合は、GooglePlayStoreへ誘導
						// 外部サイトでこのタグ記述するページを用意していないので、単純にGooglePlayへ誘導
						Uri uri = Uri.parse("market://details?id=" + appid);
						appInt = new Intent(Intent.ACTION_VIEW, uri);
					}
					try {
						startActivity(appInt);
					} catch (ActivityNotFoundException e) {
						// ブラウザ起動できない端末はないと思うが念のため
						Log.w("ContentsBaseActivity", e);
					}
					return true;
				} else if (url.startsWith("youtube:")) { // Youtube Link
					String videoId = url.replaceAll("youtube:", "");
					Intent youtubePlayer = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd." + url));
					youtubePlayer.putExtra("VIDEO_ID", videoId);
					/*
					 * Intent youtubePlayer = new
					 * Intent(Intent.ACTION_VIEW,Uri.parse
					 * ("http://m.youtube.com/watch?v="+ videoId));
					 */
					startActivity(youtubePlayer);
					try {
						startActivity(youtubePlayer);
					} catch (ActivityNotFoundException e) {
						Log.w("ContentsBaseActivity", e);
						youtubePlayer.setData(Uri.parse("http://m.youtube.com/watch?v=" + videoId));
						startActivity(youtubePlayer);
					}
					return true;
				} else if (url.startsWith("tel:")) {
					// 電話機能呼び出し時使用
					// TELスキームをエミュレート
					call(url);
					return true;
				} else if (url.startsWith(getString(R.string.url_climor))) {
					postRTMetrics(new String[] { "TS17", "coupon_info" });
				}
				// その他
				return false;
			}

			String loginCookie = "";

			@Override
			public void onPageStarted(WebView wv, String url, Bitmap favicon) {
				super.onPageStarted(wv, url, favicon);
				int mStatus = getAppMyfcMergeStatus();
				if (url.indexOf("c.tsutaya.co.jp/mfl") > 0) {
					if (mStatus == Utils.APP_MYFC_NOT_MERGED) {
						wv.stopLoading();
						String newUrl = FILE_SCHEME + "climorcoupon.html";
						wv.loadUrl(newUrl);
						return;
					} else {
						if (url.indexOf("?") < 0 && mStatus == Utils.APP_MYFC_MERGED) {
							wv.loadUrl(url + "?_=" + System.currentTimeMillis());
							return;
						}
					}
				} else if (url.indexOf("tsutaya.co.jp/smartphone/ap/tlog/tlog_app.html") > 0) {
					wv.stopLoading();
					String url_ = "tlog_list_thumb.html?viewtype=2&startindex=1&endindex=20&top=app";
					goToUrl(url_);
					return;
				}
			}

			@Override
			public void onLoadResource(WebView wv, String url) {
				// Cookieを取り込む
				loginCookie = CookieManager.getInstance().getCookie(url);
				super.onLoadResource(wv, url);
			}

			@Override
			public void onPageFinished(WebView wv, String url) {
				// Cookieを設定する
				if (loginCookie != null) {
					CookieManager.getInstance().setCookie(url, loginCookie);
				}
				CookieSyncManager.getInstance().sync();
				if (url.indexOf("c.tsutaya.co.jp/mfl") > 0 || url.indexOf("c.tsutaya.co.jp/mc?") > 0) {
					int lStatus = getUserLoginStatus();
					int mStatus = getAppMyfcMergeStatus();
					if (mStatus == Utils.APP_MYFC_MERGED && lStatus == Utils.USER_LOGINED) {
						wv.loadUrl("javascript:android.getCouponCount(TOLClimorCoupon.couponCount())");
					}
				} else if (url.indexOf("/tm/logout") > 0) {
					ContentsBaseActivity.this.setUserLoginStatus(Utils.USER_IS_NOT_LOGIN);
					ContentsBaseActivity.this.setAppMyfcMergeStatus(Utils.APP_MYFC_NOT_MERGED);
					ContentsBaseActivity.this.setCouponCheckServiceOn(false);
					CookieManager.getInstance().removeAllCookie();
				} else if (url.indexOf("store_search.html") > 0) {
					tabContents.clearCache(true);
					tabContents.loadUrl("javascript:CampaignLoad()");
				}
				super.onPageFinished(wv, url);
			}

			@Override
			public void onReceivedError(WebView wv, int errorCode, String description, String failingUrl) {
				// Log.w("onReceivedError","url:" + failingUrl);
				// Log.w("onReceivedError","description:" + description);
				/* カスタムエラーページを表示する。 */
				StringBuilder sb = new StringBuilder();
				sb.append("<html><head>");
				sb.append("<meta name=\"viewport\" content=\"user-scalable=no, width=device-width\" >");
				sb.append("<meta http-equiv=\"content-type\" content=\"text/html;charset=UTF-8\" >");
				sb.append("<link rel=\"stylesheet\" href=\"css/style.css\" />");
				sb.append("</head><body>");
				sb.append("<div align=\"center\"><h3>読み込みに失敗しました。</h3>");
				sb.append("<div><a href=\"").append(failingUrl).append("\">もう一度読み込む</a></div> ");
				sb.append("</body></html>");
				wv.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);
				// super.onReceivedError(wv,errorCode,description,failingUrl);
			}
		});

		tabContents.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onConsoleMessage(String message, int lineNumber, String sourceID) {
				Log.w("MyApplication", message + " -- From line " + lineNumber + " of " + sourceID);
			}

			@Override
			public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
				callback.invoke(origin, true, false);
			}

			@Override
			public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize, long totalUsedQuota,
					QuotaUpdater quotaUpdater) {
				// DBサイズを拡張しておく
				quotaUpdater.updateQuota(MAX_DATABASE_SIZE);
			}

			@Override
			public void onProgressChanged(WebView wv, int progress) {
				if (progress == 100) {
					loadView.setVisibility(View.GONE);
					wv.requestFocus(WebView.FOCUS_DOWN);
				}
			}
		});

		// JavaScriptInterfaceの設定
		JsInterface jo = new JsInterface(this);
		tabContents.addJavascriptInterface(jo, "android");
		// String html = Utils.getTemplateHtml(getAssets(),
		// "stocksearch_index.html");

		// インターネット接続の確認
		if (!Utils.isConnected(this)) {
			showAlert(getString(R.string.error_title), getString(R.string.error_internet_offline), true);
			frame.removeView(loadView);
			return;
		}

		// 画面描画開始
		Intent intent = getIntent();
		String url = intent.getExtras().getString(Utils.URL);
		if (url == null) {
			return;
		}
		// 取得したURLに遷移
		if (url.startsWith("http")) {
			tabContents.loadUrl(url);
		} else {
			ContentsBaseActivity.this.setRequestURL(url);
			/*
			 * html = html.replaceAll("\\{android::gotoUrl\\}",url);
			 * tabContents.loadDataWithBaseURL(HTTP_SCHEME, html, "text/html",
			 * "utf-8", null);
			 */
			int respos = url.indexOf("?");
			String pUrl = url;
			if (respos > 0) {
				pUrl = url.substring(0, respos);
			}
			tabContents.loadUrl(FILE_SCHEME + pUrl);
		}
	}

	/**
	 * サイドメニューの表示およびタップ時の挙動を設定する
	 */
	protected void initSideMenu() {
		Log.i("ContentsBaseActivity", "initSideMenu");
		sideSlideLayout = (ScrollerLinearLayout) findViewById(R.id.menu_content_side_slide_layout);

		// 検索BOXの設定
		keywordEditText = (EditText) findViewById(R.id.keyword_edit_text);
		genreSpinner = (Spinner) findViewById(R.id.genre_spinner);
		ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		aAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		SharedPreferences preferences = getSharedPreferences("search", MODE_PRIVATE);

		// ドロップダウンの設定
		addItems(aAdapter, getResources().getStringArray(R.array.genre_text));
		genreSpinner.setAdapter(aAdapter);
		String[] genreIdArray = getResources().getStringArray(R.array.genre_value);
		int position = 0;
		String item = preferences.getString("item", null);
		if (item != null) {
			for (int i = 0; i < genreIdArray.length; i++) {
				if (genreIdArray[i].equals(item)) {
					position = i;
					break;
				}
			}
			genreSpinner.setSelection(position);
		}

		sclearView = (ImageView) findViewById(R.id.search_clear);
		if (sclearView != null) {
			// sclearView.setClickable(true);
			// sclearView.setFocusable(true);
			sclearView.setVisibility(View.GONE);
		}

		// エディットテキストの設定
		keywordEditText.setText(preferences.getString("k", ""));
		keywordEditText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.i("ContentsBaseActivity", "edittext clicked");
				if (!sideSlideLayout.isSearchMode()) {
					sideSlideLayout.slideAll();
					// 表示幅の調整
					setSearchBoxLayout(true);
					listView.setVisibility(View.GONE);
					sclearView.setVisibility(View.VISIBLE);
				}
			}
		});

		// EditTextのフォーカスが外れた場合ソフトキーボードを非表示にする
		keywordEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});

		// クリアボタンの設定
		View clearText = findViewById(R.id.keyword_clear_text);
		clearText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				keywordEditText.getEditableText().clear();
				// // クリアの時点でキーワードを消しておく
				// SharedPreferences preference = getSharedPreferences("search",
				// Activity.MODE_PRIVATE);
				// Editor editor = preference.edit();
				// editor.putString("k", "");
				// editor.commit();
			}
		});
		// 検索ボタンの設定
		View searchButton = findViewById(R.id.search_button);
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				doSearch();
			}
		});

		// 表示幅の調整
		setSearchBoxLayout(false);

		// メニュー項目の設定
		listView = (ListView) findViewById(R.id.menu_content_menulist);
		if (listView != null) {
			ArrayList<String> items = new ArrayList<String>(Arrays.asList(sidemenuInfoList));
			MenuAdapter menuAdapter = new MenuAdapter(getApplicationContext(), items, ContentsBaseActivity.this);
			listView.setScrollingCacheEnabled(false);
			listView.setFadingEdgeLength(0);
			listView.setAdapter(menuAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					int menuID = view.getId();
					// 在庫検索は、遷移ではなくて検索ボックス入力にする。
					if (menuID == Utils.MENU_STOCKSEARCH) {
						Log.v("sidemenu", "STOCK SEARCH IS SELECTED");
						keywordEditText.performClick();
						// ソフトキーボードを表示にする場合は、コメントアウト外す
						// InputMethodManager manager = (InputMethodManager)
						// getSystemService(Context.INPUT_METHOD_SERVICE);
						// manager.toggleSoftInput(1,
						// InputMethodManager.SHOW_IMPLICIT);
						return;
					} else if (menuID == Utils.MENU_TSUTAYAAR) {
						startExternalApplication(getResources().getString(R.string.app_id_tsutata_ar));
					} else if (menuID == Utils.MENU_FACEBOOK) {
						Uri uri = Uri.parse(getString(R.string.url_facebook));
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
					sideSlideLayout.closeSidemenu();
					setContent(menuID, null);
				}
			});
			listView.setDivider(new ColorDrawable(Color.rgb(30, 30, 30)));
			listView.setDividerHeight(1);
		}
		// ヘッダーのメニューボタンの設定
		Button menuButton = (Button) findViewById(R.id.main_menu_button);
		if (menuButton != null) {
			menuButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (sideSlideLayout.isMenuOpened()) {
						sideSlideLayout.closeSidemenu();
						sidemenuClose();
					} else {
						sidemenuOpen();
					}

				}
			});
		}

		genreSpinner.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Your code goes here
				if (!sideSlideLayout.isSearchMode()) {
					sideSlideLayout.slideAll();
					// 表示幅の調整
					setSearchBoxLayout(true);
					listView.setVisibility(View.GONE);
					sclearView.setVisibility(View.VISIBLE);
				}
				return false;
			}

		});

		sclearView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				sideSlideLayout.openSidemenu();
				setSearchBoxLayout(false);
				listView.setVisibility(View.VISIBLE);
				sclearView.setVisibility(View.GONE);
				return true;
			}
		});
		// サイドメニューが下層(閉じられている時)にでも、検索ボックス部分をタップするとonclickが走ってしまうので上層レイヤーで止める
		View header = findViewById(R.id.header);
		header.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionevent) {
				// 下層レイヤーにonTouchをさせないだけなのでtrueを返せばOK
				return true;
			}
		});
	}

	protected void sidemenuOpen() {
		sideSlideLayout.openSidemenu();
		keywordEditText.requestFocus();
	}

	protected void sidemenuClose() {
		sideSlideLayout.closeSidemenu();
		keywordEditText.clearFocus();
	}

	public void showStockSearch(View view) {
		if (!sideSlideLayout.isMenuOpened()) {
			sidemenuOpen();
		}
		keywordEditText.performClick();
	}

	private void setSearchBoxLayout(boolean isFullSize) {
		DisplayMetrics metrics = Utils.getDisplayMetrics(getBaseContext());
		View clearText = findViewById(R.id.keyword_clear_text);
		int layoutWidth;
		if (isFullSize) {
			layoutWidth = metrics.widthPixels - 70;
			clearText.setVisibility(View.VISIBLE);
		} else {
			layoutWidth = (int) (metrics.widthPixels - (getResources().getInteger(R.integer.slide_base_width) * metrics.density));
			clearText.setVisibility(View.GONE);
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(layoutWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
		keywordEditText.setLayoutParams(params);
	}

	public void destroySidemenu() {
		keywordEditText.removeCallbacks(null);
		keywordEditText = null;
		genreSpinner.setAdapter(null);
		genreSpinner.removeAllViewsInLayout();
		genreSpinner.removeCallbacks(null);
		genreSpinner = null;
		sclearView.removeCallbacks(null);
		sclearView = null;
		listView.setAdapter(null);
		listView.removeAllViewsInLayout();
		listView.removeCallbacks(null);
		listView = null;
	}

	/**
	 * 在庫検索を実行する
	 */
	private void doSearch() {
		String keyword = ((SpannableStringBuilder) keywordEditText.getText()).toString();
		if (keyword == null || keyword.length() < 1) {
			return;
		}
		String[] genreIdArray = getResources().getStringArray(R.array.genre_value);
		String genreId = genreIdArray[genreSpinner.getSelectedItemPosition()];
		Map<String, String> params = new HashMap<String, String>();
		params.put("k", keyword);
		params.put("item", genreId);
		// キーワードとジャンルはずっと保持しておく
		SharedPreferences preference = getSharedPreferences("search", Activity.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putString("k", keyword);
		editor.putString("item", genreId);
		editor.commit();

		setSearchBoxLayout(false);
		listView.setVisibility(View.VISIBLE);
		sideSlideLayout.closeSidemenu();
		Utils.defaultMenuItemSelected(Utils.MENU_STOCKSEARCH, this, this.getClass(), params);
	}

	/**
	 * ArrayAdapterに配列の要素をすべて追加する
	 *
	 * @param adapter
	 * @param items
	 * @return
	 */
	private void addItems(ArrayAdapter<String> adapter, String[] items) {
		for (int i = 0; i < items.length; i++) {
			adapter.add(items[i]);
		}
	}

	//
	// /**
	// * サイドメニューの選択状態を描画する
	// *
	// * @param listView
	// * サイドメニューのListView
	// * @param menuID
	// * 選択状態にするメニューのID
	// */
	// private void setSelect(int menuID) {
	// for (int count = 0; count < listView.getChildCount(); count++) {
	// View listItem = listView.getChildAt(count);
	// listItem.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sidemenu));
	// ((TextView)
	// listItem.findViewById(R.id.menu_title)).setTextColor(Color.GRAY);
	// }
	// listView.findViewById(menuID).setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sidemenu_selected));
	// ((TextView)
	// listView.findViewById(menuID).findViewById(R.id.menu_title)).setTextColor(Color.WHITE);
	// }

	/**
	 * メニューのタップ時の画面遷移描画を設定する
	 */
	public void setContent(int menuID, Map<String, String> params) {
		Log.i("ContentBaseActivity", "setContent menuId:" + menuID);
		Utils.defaultMenuItemSelected(menuID, this, this.getClass(), params);
	}

	/**
	 * ヘッダーのHOMEボタンタップ時の処理(XMLのonClickにて設定)
	 *
	 * @param view
	 */
	public void onClickMoveTop(View view) {
		Intent intent = new Intent(getApplication(), TopMenuActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void onResume() {
		CookieSyncManager.getInstance().startSync();
		super.onResume();
		// 画面遷移可能に設定
		if (tabContents != null) {
			String curl = tabContents.getUrl();
/*			if ((curl != null) && (curl.indexOf("store_search.html") > 0) ) {
				tabContents.clearCache(true);
				tabContents.loadUrl("javascript:CampaignLoad()");
			}*/

			this.instancedActivity = this.getLocalClassName();
			if ("ClimorCouponActivity".equals(this.instancedActivity)) {
				if (curl != null) {
					if (this.getUserLoginStatus() > 0) {
						if (curl.indexOf("tsutaya.co.jp") > 0) {
							if (curl.indexOf("c.tsutaya.co.jp/mc?") < 0) {
								tabContents.reload();
							}
						} else {
							if (curl.equals(FILE_SCHEME + "climorcoupon.html")) {
								ContentsBaseActivity.this.showNotifyCoupon(0);
								finish();
							}
						}
					} else {
						if (curl.indexOf("tsutaya.co.jp/smartphone/ap/") < 0 && curl.indexOf("tsutaya.co.jp/tm/login.html") < 0) { // ログイン手続き中
							if (curl.equals(FILE_SCHEME + "climorcoupon.html")) {
								finish();
							} else {
								tabContents.loadUrl(FILE_SCHEME + "climorcoupon.html");
							}
						}
					}
				}
			} else if ("TsutayaLogActivity".equals(this.instancedActivity)) {
				if (curl != null) {
					if (this.getUserLoginStatus() > 0) {
						if (curl.equals(FILE_SCHEME + "tsutayalogapp.html")) {
							finish();
						} else if (curl.indexOf("tsutaya.co.jp/smartphone/ap/tlog") > 0 || curl.indexOf("tsutaya.co.jp/tm/login.html") > 0) {
							finish();
						}
					} else {
						if (curl.indexOf("tsutaya.co.jp/smartphone/ap/tlog") < 0 && curl.indexOf("tsutaya.co.jp/tm/login.html") < 0) {
							if (curl.equals(FILE_SCHEME + "tsutayalogapp.html")) {
								finish();
							} else {
								tabContents.loadUrl(FILE_SCHEME + "tsutayalogapp.html");
							}
						}
					}
				}
			} else {
				tabContents.loadUrl("javascript:if(typeof refreshView == 'function') window.refreshView();");
			}
		}
		// ブロードキャストレシーバを登録（Alert受信用）
		mBreceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context con, Intent intent) {

				String action = intent.getAction();
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					// 指定された引数でAlertを表示します。
					if (action.equals(ACTION_ALERT)) {
						// alert表示開始
						showAlert(bundle.getString("TITLE"), bundle.getString("MESSAGE"), true);
					} else if (action.equals(ACTION_MTMCPN)) {
						// coupon
						setCouponCount(bundle.getString("NEWARRIVALS"));
					}
				}
				removeStickyBroadcast(intent);

			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_ALERT);
		filter.addAction(ACTION_MTMCPN);
		registerReceiver(mBreceiver, filter);
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	public void onStart() {
		super.onStart();
		mMyrt.start();
	}

	@Override
	public void onStop() {
		super.onStop();
		mMyrt.stop();
	}

	@Override
	public void onPause() {
		Log.i("ContentsBaseActivity", "onPause");
		if (tabContents != null) {
			ContentsBaseActivity.this.tabContents.stopLoading();
		}
		super.onPause();
		CookieSyncManager.getInstance().stopSync();
		unregisterReceiver(mBreceiver);
	}

	@Override
	public void onDestroy() {
		Log.i("ContentsBaseActivity", "onDestroy");
		super.onDestroy();
		mMyrt.destroy();
		// destroySidemenu();
		// if (tabContents != null) {
		// tabContents.stopLoading();
		// tabContents.setWebChromeClient(null);
		// tabContents.setWebViewClient(null);
		// unregisterForContextMenu(tabContents);
		// tabContents.destroy();
		// tabContents = null;
		// Log.i("ContentsBaseActivity", "tabContents解放");
		// }
		// cleanupView(findViewById(R.id.rootLayout));
		// System.gc();
	}

	/**
	 * 指定したビュー階層内のDrawableをクリアする。 （Drawableのコールバックメソッドによるアクティビティのリークを防ぐため）
	 *
	 * @param view
	 */
	public static final void cleanupView(View view) {
		if (view instanceof ImageButton) {
			ImageButton ib = (ImageButton) view;
			ib.removeCallbacks(null);
			ib.setImageDrawable(null);
			Log.i("ContentsBaseActivity", ib.getId() + ":imageButton解放");
		} else if (view instanceof ImageView) {
			ImageView iv = (ImageView) view;
			iv.removeCallbacks(null);
			iv.clearAnimation();
			iv.setImageDrawable(null);
			Log.i("ContentsBaseActivity", iv.getId() + ":ImageView解放");
		} else if (view instanceof Button) {
			Button btn = (Button) view;
			btn.removeCallbacks(null);
			btn.removeTextChangedListener(null);
			Log.i("ContentsBaseActivity", btn.getId() + ":Button解放");
		} else if (view instanceof TextView) {
			TextView tv = (TextView) view;
			tv.removeCallbacks(null);
			tv.removeTextChangedListener(null);
			Log.i("ContentsBaseActivity", tv.getId() + ":TextView解放");
		}
		view.setBackgroundDrawable(null);
		if (view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) view;
			int size = vg.getChildCount();
			for (int i = 0; i < size; i++) {
				cleanupView(vg.getChildAt(i));
			}
		}
		view = null;
	}

	/**
	 * 指定されたタイトルとメッセージでアラートを表示します。
	 *
	 * @param title
	 * @param message
	 * @param isCallback
	 */
	protected void showAlert(String title, String message, boolean isCallback) {

		final String title_ = title;
		final String message_ = message;
		final boolean isCallback_ = isCallback;

		LayoutInflater factory = LayoutInflater.from(ContentsBaseActivity.this);
		View messageView = factory.inflate(R.layout.alert, null);
		((TextView) messageView.findViewById(R.id.alert_message)).setText(message_);
		final View messageView_ = messageView;

		// アラートを表示
		mUiHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					AlertDialog.Builder buildr = new AlertDialog.Builder(ContentsBaseActivity.this);
					// buildr.setTitle(title_).setMessage(message_);
					buildr.setTitle(title_).setView(messageView_);

					// ボタンを登録
					buildr.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (isCallback_) {
								// コールバック指定の場合（Javascriptから呼ばれた場合）
								tabContents.loadUrl("javascript:app.alertCallback();");
							}
						}
					});

					// キャンセル可能
					buildr.setCancelable(true);
					buildr.show();
				} catch (Exception e) {
					// 無視
					Log.w("ContentsBaseActivity", e);
				}
			}
		});
	}

	@Override
	public void startActivity(Intent intent) {
		Bundle b = intent.getExtras();
		if (b != null) {
			postRTMetrics(Utils.getRTMetricsParams(b.getString(Utils.URL)));
		}
		super.startActivity(intent);
	}

	/**
	 * 画面遷移を制御
	 *
	 * @param url
	 */
	public void goToUrl(String url) {
		Intent intent = null;
		if (url != null && url.startsWith("app::safari::")) {
			Uri uri = Uri.parse(url.replaceAll("app::safari::", ""));
			intent = new Intent(Intent.ACTION_VIEW, uri);
		} else {
			intent = new Intent(ContentsBaseActivity.this, ContentsBaseActivity.this.getClass());
			intent.putExtra(Utils.URL, url);
			Log.w("ContentBaseActivity", "goToURL:" + url);
		}
		startActivity(intent);
	}

	/**
	 * 画面遷移を制御
	 *
	 * @param url
	 */
	protected void goToUrlWithNewClass(String url, int id) {
		Class<?> newClass = null;
		switch (id) {
		case Utils.MENU_TOP:
			newClass = TopMenuActivity.class;
			break;
		case Utils.MENU_RANKING:
			newClass = RankingActivity.class;
			break;
		// case Utils.MENU_SPECIAL:
		// newClass = SpecialActivity.class;
		// break;
		case Utils.MENU_FAVORITE:
			newClass = FavoriteActivity.class;
			break;
		case Utils.MENU_SETTINGS:
			newClass = SettingActivity.class;
			break;
		case Utils.MENU_STORESEARCH:
			newClass = StoreSearchActivity.class;
			break;
		case Utils.MENU_COUPON:
			newClass = ClimorCouponActivity.class;
			break;
		case Utils.MENU_RELEASE:
			newClass = ReleaseCalendarActivity.class;
			break;
		case Utils.MENU_RELATED:
			newClass = RelatedLinkActivity.class;
			break;
		}

		Intent intent = new Intent(ContentsBaseActivity.this, newClass);
		intent.putExtra(Utils.URL, url);
		startActivity(intent);
	}

	public void postRTMetrics(String[] params) {
		if (params != null && params[Utils.RT_KEY] != null && params[Utils.RT_VALUE] != null) {
			mMyrt.post(params[Utils.RT_KEY], params[Utils.RT_VALUE]);
			Log.d("RTMetrics", "post value[" + params[Utils.RT_KEY] + ":" + params[Utils.RT_VALUE] + "]");
		}
	}

	/**
	 * 電話を呼び出すメソッド
	 *
	 * @param telno
	 */
	public void call(String telno) {

		final String telno_ = telno.replace("-", "");
		final String title_ = getString(R.string.call_title);
		final String message_ = telno.replace("tel:", "");
		final String yesTitle_ = getString(R.string.call_yes_text);
		final String noTitle_ = getString(R.string.call_no_text);

		LayoutInflater factory = LayoutInflater.from(ContentsBaseActivity.this);
		View messageView = factory.inflate(R.layout.alert, null);
		((TextView) messageView.findViewById(R.id.alert_message)).setText(message_);
		final View messageView_ = messageView;

		// 電話機能の有無の確認
		Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(telno_));
		List<ResolveInfo> callInfo = getPackageManager().queryIntentActivities(callIntent, PackageManager.MATCH_DEFAULT_ONLY);

		if (callInfo == null || callInfo.size() == 0) {
			showAlert(getString(R.string.error_title), getString(R.string.error_call_unuse), false);
			return;
		}

		// 確認ダイアログを出力
		mUiHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					AlertDialog.Builder buildr = new AlertDialog.Builder(ContentsBaseActivity.this);

					buildr.setTitle(title_);
					buildr.setView(messageView_);
					// buildr.setMessage(message_);
					buildr.setPositiveButton(noTitle_, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});
					buildr.setNegativeButton(yesTitle_, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telno_));
							startActivity(intent);
						}
					});
					buildr.create();
					buildr.show();
				} catch (Exception e) {
					// 無視
					Log.w("ContentsBaseActivity", e);
				}
			}
		});
	}

	/**
	 * ------------------------------------------------
	 *
	 * オプションメニュー
	 *
	 * ------------------------------------------------
	 */
	/**
	 * オプションメニューの実装
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
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
	 * ------------------------------------------------
	 *
	 * タッチイベント
	 *
	 * ------------------------------------------------
	 */
	/**
	 * キーイベント制御
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		Log.v("KeyCode", e.getKeyCode() + "");
		// 戻るボタンを制御し、ブラウザバック可能な場合はブラウザバックさせる
		if (e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			Log.i("ContentBaseActivity", "KEYCODE_BACK");
			if (e.getAction() == KeyEvent.ACTION_DOWN) {
				// ボタンが押し込まれた時のみ反応させる(ACTION_UPなら、ボタンを押して、離した時)
				if (sideSlideLayout.isSearchMode()) {
					sideSlideLayout.openSidemenu();
					setSearchBoxLayout(false);
					listView.setVisibility(View.VISIBLE);
					sclearView.setVisibility(View.GONE);
					return true;
				} else if (sideSlideLayout.isMenuOpened()) {
					sideSlideLayout.closeSidemenu();
					return true;
				}

				// TOPの場合はWebViewを使っていないので別処理
				if (tabContents == null) {
					boolean flg = super.dispatchKeyEvent(e);
					finish();
					return flg;
				}
				String url = tabContents.getUrl();
				if (url != null && url.startsWith(FILE_SCHEME)) {
					// 「戻る」がassets領域の場合はブラウザバックしない（OS2.2対策）
					boolean flg = super.dispatchKeyEvent(e);
					finish();
					return flg;
				}
				if (tabContents.canGoBack()) {
					tabContents.goBack();
					return false;
				} else {
					boolean flg = super.dispatchKeyEvent(e);
					finish();
					return flg;
				}
			}
			return true;
		} else if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			// サイドメニューの検索
			if (e.getAction() == KeyEvent.ACTION_UP && sideSlideLayout.isSearchMode()) {
				doSearch();
				return true;
			}
		}
		return super.dispatchKeyEvent(e);
	}

	/**
	 * 通知エリアに通知アイコンを表示する
	 *
	 * @return なし
	 */
	public void showNotifyCoupon(int newArrivals) {
		if (newArrivals < 0)
			return;

		// Intent i = new
		// Intent(getApplicationContext(),ClimorCouponActivity.class);
		Intent i = new Intent(getApplicationContext(), AppRootActivity.class);
		i.putExtra("COUPON", 1);
		PendingIntent pend = PendingIntent.getActivity(this, 0, i, 0);

		NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (newArrivals == 0) {
			nManager.cancelAll();
		} else {
			Notification n = new Notification();
			n.number = newArrivals;
			n.flags = Notification.FLAG_AUTO_CANCEL;
			n.icon = R.drawable.ic_coupon;
			n.when = System.currentTimeMillis();
			// n.tickerText = "新着クーポン(" + n.number + "件)";

			CharSequence appName = getResources().getText(R.string.app_name);
			CharSequence notifyMsg = "未読のクーポンは" + newArrivals + "件です。";
			n.setLatestEventInfo(getApplicationContext(), appName, notifyMsg, pend);

			nManager.notify(1, n);
		}
	}

	/**
	 * 画面上部のバーに本日分未読件数の表示を行う。
	 *
	 * @param arrivals
	 */
	public void setCouponCount(String arrivals) {
		TextView tv = (TextView) this.findViewById(R.id.coupontextView1);
		CharSequence couponCount;
		if (arrivals == null || "0".equals(arrivals)) {
			couponCount = "";
		} else {
			couponCount = "未読:" + arrivals;
		}
		// test
		// arrivals = "10";
		if (tv != null) {
			tv.setText(couponCount);
		}
		return;
	}

	/**
	 * Cookieをクリアする
	 *
	 * @return なし
	 */
	public void clearCookies() {
		CookieManager.getInstance().removeAllCookie();
	}

	/**
	 * ログイン状態の設定
	 */
	public void setUserLoginStatus(int status) {
		// Log.d("setUserLoginStatus", new Integer(status).toString());
		if (status == Utils.USER_LOGINED) {
			userLoginStatus = Utils.USER_LOGINED;
		} else {
			userLoginStatus = Utils.USER_IS_NOT_LOGIN;
		}
	}

	/**
	 * 店舗おまとめ状況 の設定
	 */
	public void setAppMyfcMergeStatus(int status) {
		// Log.d("setAppMyfcMergeStatus", new Integer(status).toString());
		if (status == Utils.APP_MYFC_MERGED) {
			appMyfcMergeStatus = Utils.APP_MYFC_MERGED;
		} else {
			appMyfcMergeStatus = Utils.APP_MYFC_NOT_MERGED;
		}
	}

	/**
	 * 端末ディスプレイサイズの取得
	 */
	public int getDisplayInfo(int hw) {
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (hw == Utils.DISP_HEIGHT) {
			return display.getHeight();
		} else {
			return display.getWidth();
		}
	}

	/**
	 * AlarmManagerにIntentServiceを登録
	 *
	 * @param tolid
	 */
	public void putAlarmIntenteService(String tolid) {
		if (tolid == null || "".equals(tolid.trim())) {
			return;
		}
		if (isCouponCheckServiceOn()) {
			// Log.d("putAlarmIntentService","Service alrready Started..");
			return;
		}

		AlarmManager alm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), CouponArrivalCheckService.class);
		intent.putExtra(Utils.TOLID, tolid);
		intent.putExtra(Utils.URL, "climorcoupon.html");
		// PendingIntent pint =
		// PendingIntent.getService(getApplicationContext(),-1,intent,PendingIntent.FLAG_CANCEL_CURRENT);
		PendingIntent pint = PendingIntent.getService(getApplicationContext(), -1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// alm.setInexactRepeating(AlarmManager.RTC,System.currentTimeMillis(),AlarmManager.INTERVAL_HOUR,pint);
		if (pint != null) {
			// ELAPSED_REALTIME::SystemClock.elapsedRealtime()
			// RTC::System.currentTimeMillis()
			alm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_HALF_HOUR, pint);
			setCouponCheckServiceOn(true);
		}
	}

	/**
	 * 稼働しているAlarmを取り消す
	 */
	public void cancelAlarmService() {
		AlarmManager alm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), CouponArrivalCheckService.class);
		PendingIntent pint = PendingIntent.getService(getApplicationContext(), -1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		alm.cancel(pint);
		setCouponCheckServiceOn(false);
	}

	/**
	 * 外部アプリを立ち上げ、インストールされていない場合はGooglePlayStoreへ誘導する。
	 *
	 */
	public void startExternalApplication(String appid) {
		Intent appInt = Utils.getCallableIntent(getPackageManager(), appid);
		if (appInt == null) { // アプリがインストールされていない場合は、GooglePlayStoreへ誘導
			// 外部サイトでこのタグ記述するページを用意していないので、単純にGooglePlayへ誘導
			Uri uri = Uri.parse("market://details?id=" + appid);
			appInt = new Intent(Intent.ACTION_VIEW, uri);
		}
		try {
			startActivity(appInt);
		} catch (ActivityNotFoundException e) {
			// ブラウザ起動できない端末はないと思うが念のため
			Log.w(this.getClass().getSimpleName(), e);
		}
	}

	/**
	 * ------------------------------------------------
	 *
	 * JavaScript
	 *
	 * ------------------------------------------------
	 */
	/**
	 * TabWidgetとのJavaScriptInterfaceをとる内部クラス。
	 *
	 * @author i_suyama
	 *
	 */
	class JsInterface {

		/**
		 * 対象Activityのオブジェクト
		 */
		private Context con;

		public JsInterface(Context con) {
			this.con = con;
		}

		public void blank(String url){
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(i);
		}
		
		public float convertDpToPixel(float dp,Context context){
		    Resources resources = context.getResources();
		    DisplayMetrics metrics = resources.getDisplayMetrics();
		    float px = dp * (metrics.densityDpi/160f);
		    return px;
		}
		
		public float convertPixelsToDp(float px,Context context){
		    Resources resources = context.getResources();
		    DisplayMetrics metrics = resources.getDisplayMetrics();
		    float dp = px / (metrics.densityDpi / 160f);
		    return dp;

		}
		
		public String getBannerUrl(){
			return Utils.BANNER_URL;
		}
		
		public String getBannerOnclickUrl(){
			return Utils.BANNER_ONCLICK_URL;
		}
		
		public int getDisplayWidth(){
			return (int)convertPixelsToDp(getWindowManager().getDefaultDisplay().getWidth(), con);
		}
		
		/**
		 * ブラウザバックを実行するメソッド
		 */
		public void browserBack() {
			// ブラウザバック
			mUiHandler.post(new Runnable() {
				@Override
				public void run() {
					String url = tabContents.getUrl();
					if (url != null && url.startsWith(FILE_SCHEME)) {
						// 「戻る」がassets領域の場合はブラウザバックしない（OS2.2対策）
						finish();
					} else if (tabContents.canGoBack()) {
						Log.i("logs", "browserback!");
						tabContents.goBack();
					} else {
						Log.i("logs", "can't browserback!");
						finish();
					}
				}
			});
		}

		/**
		 * アラートを表示するメソッド
		 *
		 * @param title
		 * @param message
		 */
		public void alert(String title, String message) {

			// スティッキーインテントを投げる→アクティブなActivityで受信
			Intent intent = new Intent(ACTION_ALERT);
			intent.putExtra("TITLE", title);
			intent.putExtra("MESSAGE", message);
			sendStickyBroadcast(intent);

		}

		/**
		 * コンファームを表示するメソッド
		 *
		 * @param title
		 * @param message
		 * @param yesTitle
		 * @param noTitle
		 */
		public void confirm(String title, String message, String yesTitle, String noTitle) {
			// お気に入り画面の場合、サイドバーがopenしていたらアラートキャンセル。
			// （お気に入りのスワイプでの削除機能に対応）
			if (((Activity) con).getClass().equals(new FavoriteActivity().getClass()) && sideSlideLayout.isMenuOpened()) {
				return;
			}

			final String message_ = message.replaceAll("<br>", "\n");
			final String title_ = title;
			final String yesTitle_ = yesTitle;
			final String noTitle_ = noTitle;

			LayoutInflater factory = LayoutInflater.from(ContentsBaseActivity.this);
			View messageView = factory.inflate(R.layout.alert, null);
			((TextView) messageView.findViewById(R.id.alert_message)).setText(message_);
			final View messageView_ = messageView;

			mUiHandler.post(new Runnable() {
				@Override
				public void run() {
					try {
						AlertDialog.Builder buildr = new AlertDialog.Builder(ContentsBaseActivity.this);

						buildr.setTitle(title_);
						// buildr.setMessage(message_);
						buildr.setView(messageView_);
						buildr.setPositiveButton(yesTitle_, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								tabContents.loadUrl("javascript:app.alertCallback(0)");
							}
						});
						buildr.setNegativeButton(noTitle_, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								tabContents.loadUrl("javascript:app.alertCallback(1)");
							}
						});

						buildr.create();
						buildr.show();
					} catch (Exception e) {
						// 無視
						Log.w("ContentsBaseActivity", e);
					}
				}
			});
		}

		/**
		 * 作品お気に入り追加ボタン表示フラグ
		 */
		public void setAddFavBtnEnable(boolean favBtnEnable) {
			addFavBtnEnable = favBtnEnable;
		}

		/**
		 * マップ画面を起動する。
		 *
		 * @param func
		 * @param id
		 */
		public void startMap(String func, String id) {

			Intent intent = null;

			if ("areastock".equals(func)) {
				intent = new Intent(ContentsBaseActivity.this, StockMapActivity.class);
				intent.putExtra("PRODUCT_KEY", id);
			} else if ("storemap".equals(func)) {
				intent = new Intent(ContentsBaseActivity.this, StoreMapActivity.class);
				intent.putExtra("STORE_ID", id);
			} else {
				// 何もしない
				return;
			}

			// アクティビティ起動(同一タスク)
			intent.putExtra("CLASSNAME", ContentsBaseActivity.this.getClass().getName());
			startActivity(intent);

		}

		/**
		 * 画面遷移を制御
		 *
		 * @param url
		 */
		public void goToUrl(String url) {
			ContentsBaseActivity.this.goToUrl(url);
		}
		
		/**
		 * Change Registered State
		 * @param storeId
		 * @param state
		 */
		public void updateRegisteredStore(final String storeId, final int state){
			runOnUiThread(new Runnable() {				
				@Override
				public void run() {
					StoreDao mDao = null;
					try {
						// Daoを生成
						mDao = new StoreDao(ContentsBaseActivity.this);
						mDao.updateRegisteredStore(storeId, state);
					} catch (SQLException e) {
						Log.w("StoreData", e);
					} finally {
						mDao.close();
					}
				}
			});
		}

		/**
		 * 通知エリアに新着にクリモルクーポン数を表示する。
		 *
		 * @param numOfCoupons
		 *            新着クーポン件数
		 */
		public void notifyCouponArrivals(int numOfCoupons) {
			if (numOfCoupons >= 0) {
				ContentsBaseActivity.this.showNotifyCoupon(numOfCoupons);
				// ContentsBaseActivity.this.setCouponCount(numOfCoupons);
			}
		}

		/**
		 * クリモルクーポンページのJavaScriptからクーポン券数を取得する
		 *
		 * @param returnValue
		 */
		public void getCouponCount(String returnValue) {
			if (returnValue != null && !"".equals(returnValue.trim())) {
				int i = 0;
				try {
					i = Integer.parseInt(returnValue);
				} catch (NumberFormatException ne) {
					i = 0;
				}
				if (i >= 0) {
					ContentsBaseActivity.this.setClimorCouponCount(i);
					// this.notifyCouponArrivals(i);
					// test
					// スティッキーインテントを投げる→アクティブなActivityで受信
					Intent intent = new Intent(ACTION_MTMCPN);
					intent.putExtra("NEWARRIVALS", Integer.toString(i));
					sendStickyBroadcast(intent);
					// test
				}
			}
		}

		/**
		 * Cookieを削除する機能のJavaScript連携
		 */
		public void clearAllCookies() {
			ContentsBaseActivity.this.clearCookies();
		}

		/**
		 * ログイン状態の切替
		 */
		public void changeLoginStatus(String status) {
			try {
				int stat = Integer.valueOf(status).intValue();
				ContentsBaseActivity.this.setUserLoginStatus(stat);
			} catch (Exception e) {
				Log.w("ContentsBaseActivity", e);
			}
		}

		/**
		 * ユーザログイン状態の返却
		 *
		 * @return String 0：未ログイン 1: ログイン済
		 */
		public String getUserLoginStatus() {
			String loginStat = Integer.toString(ContentsBaseActivity.this.getUserLoginStatus());
			return loginStat;
		}

		/**
		 * 店舗おまとめ状態の切替
		 */
		public void changeAppMyfcMergeStatus(String status) {
			try {
				int stat = Integer.valueOf(status).intValue();
				ContentsBaseActivity.this.setAppMyfcMergeStatus(stat);
			} catch (Exception e) {
				Log.w("ContentsBaseActivity", e);
			}
		}

		/**
		 * 店舗おまとめ状態の返却
		 *
		 * @return String ステータス 0：未統合 1：統合済
		 */
		public String getAppMyfcMergeStatus() {
			String mStatus = Integer.toString(ContentsBaseActivity.this.getAppMyfcMergeStatus());
			return mStatus;
		}

		/**
		 * 端末の解像度取得
		 */
		public int getDispInfo(int hw) {
			return ContentsBaseActivity.this.getDisplayInfo(hw);
		}

		/**
		 * 本当のクエリパラメータ付きURLを返す
		 */
		public String getRealUrl() {
			return ContentsBaseActivity.this.getRequestURL();
		}

		/**
		 * Intente用のURLを設定する。
		 *
		 * @param rurl
		 */
		public void setRealUrl(String rurl) {
			ContentsBaseActivity.this.setRequestURL(rurl);
		}

		/** 設定メニューへのリダイレクト判定 */
		public boolean isRedirectSettingPage() {
			return redirectSettingPage;
		}

		public void setRedirectSettingPage(boolean on) {
			if (on) {
				redirectSettingPage = true;
			} else {
				redirectSettingPage = false;
			}
		}

		/**
		 * 設定タブに遷移します。（強制）
		 */
		public void goToSettingMenu() {
			Map<String, String> urlParams = new HashMap<String, String>(1);
			urlParams.put("clearTop", "1");
			Utils.defaultMenuItemSelected(Utils.MENU_SETTINGS, ContentsBaseActivity.this, getApplicationContext().getClass(), urlParams);
		}

		/**
		 * 店舗検索に移動
		 */
		public void goToStoreMenu() {
			Utils.defaultMenuItemSelected(Utils.MENU_STORESEARCH, ContentsBaseActivity.this);
		}

		/**
		 * クーポン新着チェックのアラーム起動設定
		 */
		public void setAlarm(String tolid) {
			if (tolid != null && !"".equals(tolid.trim())) {
				ContentsBaseActivity.this.putAlarmIntenteService(tolid);
			}
		}

		/**
		 * クーポン新着チェックのアラーム登録を解除する
		 */
		public void resetCouponAlarmService() {
			ContentsBaseActivity.this.cancelAlarmService();
		}

		/**
		 * OSバージョンを取得します。
		 *
		 * @return
		 */
		public boolean isNeedModifyCanvas() {
			int version = Build.VERSION.SDK_INT;
			return version == Build.VERSION_CODES.ECLAIR || version == Build.VERSION_CODES.ECLAIR_0_1 || version == Build.VERSION_CODES.ECLAIR_MR1;
		}

	}
}
