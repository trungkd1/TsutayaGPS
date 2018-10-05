package jp.co.tsutaya.android.ranking;

import java.util.ArrayList;
import java.util.List;

import jp.co.tsutaya.android.ranking.model.StoreDataTask;
import jp.co.tsutaya.android.ranking.overlay.BalloonItemizedOverlay;
import jp.co.tsutaya.android.ranking.overlay.StoreOverlay;
import jp.co.tsutaya.android.ranking.overlay.TMyLocationOverlay;
import jp.co.tsutaya.android.ranking.util.Utils;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.auriq.myrt.android.MyrtDataSender;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MapBaseActivity extends MapActivity {

	/** ロケーションマネージャ */
	public LocationManager locationManager;

	/** ロケーションリスナ */
	public MyLocationListener locationListener;

	/** オーバーレイ */
	public TMyLocationOverlay myLocationOverlay;

	/** ロケーションプロバイダ */
	protected String locationProvider;
	/** 現在地へ移動フラグ */
	public boolean goToMyLocationFlg = false;

	/** PINタップ判定（PINタップ中はTRUE） */
	protected boolean onPinTapping = false;

	/** UI用イベントハンドラ */
	protected Handler mUiHandler;

	/** マップView */
	protected MapView mapView;

	/** マップコントローラ */
	protected MapController mapController;

	private static final int DEFAULT_ZOOM_LEVEL = 15;

	/** 繰り返し秒（ミリ秒） */
	protected static final int REPEAT_INTERVAL = 1000;

	/** 現在のクラス（タスク管理に使用） */
	protected Class<?> currentClass;

	/** タイトルテキスト */
	private TextView titleTextView;
	private String beforeText;

	/** プログレスバー */
	private View progressView;

	// RTメトリクスを送信
	MyrtDataSender mMyrt;	

	/** 繰り返し秒（ミリ秒） */
	protected static final int REPEAT_INTERVAL_GPS = 3000;

	// Time delay request GPS: 30 seconds
	public long TIME_GPS_DELAY = 30 * 1000;
	public boolean FLAG_GET_CURRENT_LOCATION = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// RTメトリクスを送信
		mMyrt = new MyrtDataSender(this, "", "www.tsutaya.co.jp/rt/rt_mark.gif", "tsutaya");
		mMyrt.setSendMode(MyrtDataSender.PACKETCAPTUREMODE);
		setContentView(R.layout.map);	

		// アクティビティがフォワードになる際に位置情報リスナー登録
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// ロケーションリスナー生成
		locationListener = new MyLocationListener();

		// MapViewを準備する
		mapView = (MapView) findViewById(R.id.map);
		mapController = mapView.getController();

		// 拡大機能利用可能
		mapView.setBuiltInZoomControls(true);
		mapView.invalidate();

		// 現在地オーバーレイの準備
		myLocationOverlay = new TMyLocationOverlay(getApplicationContext(), mapView);
		// overlay.onProviderEnabled(LocationManager.GPS_PROVIDER);
		mapView.getOverlays().add(myLocationOverlay);

		// 起動元クラスを取得
		Intent intent = getIntent();
		String currentClassName = intent.getExtras().getString("CLASSNAME");
		try {
			currentClass = Class.forName(currentClassName);
		} catch (Exception e) {
			Log.w("MapBaseActivity", e);
		}

		mUiHandler = new Handler();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * 位置情報検索の開始（位置情報検索完了時に検出した場所に移動）
	 */
	public void startSearchLocation() {
		setTitleTextRed(getString(R.string.searching_mylocation));
		goToMyLocationFlg = true;
	}

	@Override
	public void onResume() {
		// 適した位置情報プロバイダを取得
		final Criteria criteria = new Criteria();
		criteria.setBearingRequired(false); // 方位不要
		criteria.setSpeedRequired(false); // 速度不要
		criteria.setAltitudeRequired(false); // 高度不要

		locationProvider = locationManager.getBestProvider(criteria, true);

		if (locationProvider == null) {
			// 位置情報取得に失敗
			setTitleTextRed(getString(R.string.error_gps_unuseable));
		} else {
			startRequestUpdateLocation();
		}

		// overlay.enableMyLocation();
		mapView.invalidate();

		super.onResume();
	}

	public Runnable gpsRunnable = new Runnable() {				
		@Override
		public void run() {
			if(!FLAG_GET_CURRENT_LOCATION){
				FLAG_GET_CURRENT_LOCATION = true;
				onGetLocationFailed();
			}
		}
	};

	public void stopRequestUpdateLocation(){
		if (locationManager != null && locationListener != null) {
			// アクティビティがバックグラウンドに移動する際にリスナ解除
			locationManager.removeUpdates(locationListener);
		}
		
		try{
			mUiHandler.removeCallbacks(gpsRunnable);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void startRequestUpdateLocation(){
		stopRequestUpdateLocation();
		FLAG_GET_CURRENT_LOCATION = false;
		// ロケーションリスナーを登録
		locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
		mUiHandler.postDelayed(gpsRunnable, TIME_GPS_DELAY);		
	}

	@Override
	public void onPause() {
		stopRequestUpdateLocation();
		super.onPause();
	}

	/**
	 * Process when location is updated.
	 *
	 * @author synd
	 *
	 */
	public void goToCurrentLocation(){
	}

	/**
	 * Process when can not get location.
	 *
	 * @author synd
	 *
	 */
	public void onGetLocationFailed(){
	}

	/**
	 * 位置情報取リスナー
	 *
	 * @author i_suyama
	 *
	 */
	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// 位置情報が変更された際にコールバックされる
			Log.w("logs", "Location: Lon=" + location.getLongitude() + ", Lat=" + location.getLatitude());

			mUiHandler.post(new Runnable() {
				@Override
				public void run() {
					goToCurrentLocation();
					FLAG_GET_CURRENT_LOCATION = true;
				}
			});

		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.w("logs", "onProviderDisabled!");
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.w("logs", "onProviderEnabled!");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.w("logs", "onStatusChanged!");
		}

	}

	/**
	 * 情報表示用Viewを初期化します。
	 */
	public void initInfoView(int layoutId) {
		LayoutInflater factory = LayoutInflater.from(this);
		View overlay = factory.inflate(layoutId, null);
		ViewGroup frame = (ViewGroup) findViewById(R.id.contents_info_layout);
		frame.removeAllViews();
		frame.addView(overlay);
		titleTextView = (TextView) findViewById(R.id.map_title_text);
		progressView = findViewById(R.id.map_progress);
	}

	/**
	 * 帯タイトルを設定します。
	 */
	public void setNaviTitle(int id) {
		// 帯タイトルを変更
		TextView title = (TextView) findViewById(R.id.title_text);
		title.setText(id);
	}

	/**
	 * タイトルを設定します。
	 */
	public void setTitleText(String text) {
		titleTextView.setText(text);
		beforeText = text;
	}

	/**
	 * 黒色でタイトルを設定します。
	 */
	public void setTitleTextBlack(String text) {
		titleTextView.setText(text);
		titleTextView.setTextColor(Color.BLACK);
	}

	/**
	 * 黒色でタイトルを設定します。(色変更のみ)
	 */
	public void setTitleTextBlack() {
		titleTextView.setText(beforeText);
		titleTextView.setTextColor(Color.BLACK);
	}

	/**
	 * 赤色でタイトルを設定します。
	 */
	public void setTitleTextRed(String text) {
		titleTextView.setText(text);
		titleTextView.setTextColor(Color.RED);
	}

	/**
	 * 赤色でタイトルを設定します。(色変更のみ)
	 */
	public void setTitleTextRed() {
		titleTextView.setText(beforeText);
		titleTextView.setTextColor(Color.RED);
	}

	/**
	 * プログレスバーを開始します。
	 */
	public void startProgress() {
		if (progressView.getVisibility() == View.GONE) {
			Animation anim = AnimationUtils.loadAnimation(this,
					R.layout.animation_fadein);
			progressView.startAnimation(anim);
			progressView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * プログレスバーを終了します。
	 */
	public void endProgress() {
		// delayをかけて消す
		mUiHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Animation anim = AnimationUtils.loadAnimation(
						MapBaseActivity.this, R.layout.animation_fadeout);
				progressView.startAnimation(anim);
				progressView.setVisibility(View.GONE);
			}
		}, 750);
	}

	/**
	 * 指定されたポイントに移動し、ズームします。
	 *
	 * @param p
	 */
	protected void animateToWithZoom(GeoPoint p) {
		// 指定した位置に移動
		mapController.animateTo(p);
		mapController.setZoom(DEFAULT_ZOOM_LEVEL);
	}

	/**
	 * 指定されたポイントに移動し、ズームします。
	 *
	 * @param p
	 */
	protected void animateToWithZoom(GeoPoint p, int zoomLevel) {
		// 指定した位置に移動
		mapController.animateTo(p);
		mapController.setZoom(zoomLevel);
	}

	/**
	 * 店舗情報検索を開始する。
	 *
	 * @param centerPoint
	 *            中心座標
	 */
	public void startStoreSearch(GeoPoint centerPoint) {
		// インターネット接続の確認
		if (!Utils.isConnected(this)) {
			// 検索を実施せずに終了
			setTitleTextRed(getString(R.string.error_internet_offline));
			endProgress();
			if(this instanceof StoreMapActivity)
			{
				((StoreMapActivity)this).showAlert(R.string.title_error_loaddata, 0);
			}
			return;
		}

		// 検索を開始する。
		if (mapView.getLatitudeSpan() != 0 && mapView.getLongitudeSpan() != 0) {

			// プログレス開始
			startProgress();

			// AsyncTaskクラスを用いて、非同期でDBを検索する。
			StoreDataTask storeDataTask = new StoreDataTask(this);
			storeDataTask.execute(String.valueOf(centerPoint.getLatitudeE6()),
					String.valueOf(centerPoint.getLongitudeE6()),
					String.valueOf(mapView.getLatitudeSpan()),
					String.valueOf(mapView.getLongitudeSpan()));
		} else {
			// 描画中等、getLatitudeSpanで上手く値が取れない場合がある。
			// 値が取得できなかった場合は、決められた秒数後に自身を再コールする。

			final GeoPoint arg = centerPoint;
			mUiHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// 決められた秒数後に自分を再コール
					startStoreSearch(arg);
				}
			}, REPEAT_INTERVAL);
		}

	}

	/**
	 * 店舗情報検索終了時にコールバックされるメソッド。 共通処理（検索完了後の表示中overlay削除）のみ実装。
	 * 実際の検索結果描画処理は子クラスにて行う。
	 *
	 * @param reslut
	 *            [] 店舗ID
	 *
	 *
	 * @return 以後の処理を行う/以後の処理を行わない
	 */
	public boolean endStoreSearch(String[][] result) {

		// StoreDataTaskによる店舗検索が完了した際にコールバックされる。
		// 本メソッドはPINの描画を行う為、UIスレッドで実行されることを想定している。

		List<Overlay> overlayList = mapView.getOverlays();
		List<Overlay> currentOverlay = new ArrayList<Overlay>();
		// バルーンオーバレイを削除しておく
		for (Overlay overlay : overlayList) {
			if (overlay instanceof BalloonItemizedOverlay<?>) {
				((BalloonItemizedOverlay<?>) overlay).hideBalloon();
			} else {
				currentOverlay.add(overlay);
			}
		}

		overlayList.clear();
		overlayList.addAll(currentOverlay);

		// 描画完了時に文字色変更＆プログレス終了
		setTitleTextBlack();
		endProgress();

		if (result == null) {
			setTitleTextRed(getString(R.string.over_limit_storedata));
			return false;
		} else if (result.length == 0) {
			// 何もしない (検索結果なし)
			return false;
		}

		return true;
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
		menu.clear();

		// 遷移元クラスを渡しておく（戻るボタン表示用）
		// Utils.createDefaultOptionsMenu(menu, this, currentClass);

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return super.onMenuItemSelected(featureId, item);

		// 遷移元クラスを渡しておく（戻るボタン遷移処理用）
		//		Utils.defaultMenuItemSelected(item.getItemId(), this, currentClass);
		//		return true;
	}

	/**
	 * ------------------------------------------------
	 *
	 * タッチイベント
	 *
	 * ------------------------------------------------
	 */
	/**
	 * 移動中かどうかのフラグ
	 */
	private int moveStatus = 0;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		return onTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(this instanceof StoreMapActivity)
		{
			int iHeightHeader = findViewById(R.id.header).getHeight();
			int iHeight = (int) event.getRawY();
			if (iHeight < iHeightHeader + getStatusBarHeight()) {
				return super.onTouchEvent(event);
			} else if (!FLAG_GET_CURRENT_LOCATION) {
				return super.onTouchEvent(event);
			}
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			moveStatus++;
			break;
		case MotionEvent.ACTION_UP:
			if (moveStatus >= 2 && !onPinTapping) {
				Log.d("abc","onTouchEvent:: ACTION_UP");
				// 画面スクロール → 指UP時に店舗を再検索
				startStoreSearch(mapView.getMapCenter());
			}
			moveStatus = 0;
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	
	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * 店舗表示オーバレイを作成します。
	 */
	protected StoreOverlay createStoreOverlay(int resourceId) {
		Drawable marker = getResources().getDrawable(resourceId);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());
		StoreOverlay overlay = new StoreOverlay(marker, mapView, currentClass);
		// バルーンの高さを調整
		overlay.setBalloonBottomOffset(marker.getIntrinsicHeight());
		return overlay;
	}

	/**
	 * UIハンドラを取得します。
	 *
	 * @return
	 */
	public Handler getMUiHandler() {
		return mUiHandler;
	}

	/**
	 * ピンタップ中判定フラグを更新します。
	 *
	 * @param flag
	 */
	public void setOnPinTapping(boolean flag) {
		onPinTapping = flag;
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

	/**
	 * ヘッダーの現在地ボタンタップ時の処理(XMLのonClickにて設定)
	 *
	 * @param view
	 */
	public void onClickCurrentLocation(View view) {
		if (locationProvider != null) {
			// 現在地へ移動＆再建策を要求
			startSearchLocation();
			goToCurrentLocation();
		} else {
			// 位置情報取得に失敗
			setTitleTextRed(getString(R.string.error_gps_unuseable));
		}
	}
}
