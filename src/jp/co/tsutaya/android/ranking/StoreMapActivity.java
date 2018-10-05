package jp.co.tsutaya.android.ranking;

import java.util.List;

import jp.co.tsutaya.android.ranking.model.StoreDao;
import jp.co.tsutaya.android.ranking.overlay.StoreOverlay;
import jp.co.tsutaya.android.ranking.util.Utils;
import android.app.Dialog;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class StoreMapActivity extends MapBaseActivity {

	public static Boolean isTsvPassed = false;

	/**
	 * 店舗ID
	 */
	private String storeId;

	/**
	 * 選択された店舗の座標
	 */
	private GeoPoint storePoint;
	private boolean searchStoreByCurrentLocation = false;
	private Dialog errorDialog;
	
	/** GPS */
	private enum GpsState {ON, OFF, GPS_PROVIDER, NETWORK_PROVIDER};
	private GpsState gpsState = GpsState.OFF;
	private boolean goToSettingsFlag = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMyrt.post("TS11", "store_map");

		// 帯タイトルを設定
		setNaviTitle(R.string.storemap_title);

		// 店舗マップ検索では、現在地機能はなし
		findViewById(R.id.header_button_right).setVisibility(View.GONE);

		// 情報表示用Viewを追加
		initInfoView(R.layout.map_store_overlay);

		// 店舗情報検索開始
		// 店舗ID取得
		storeId = getIntent().getExtras().getString("STORE_ID");
		if(TextUtils.isEmpty(storeId)){
			searchStoreByCurrentLocation = true;
			// Change text in overlays info
			((TextView)findViewById(R.id.infoText1)).setText(R.string.registered);
			((TextView)findViewById(R.id.infoText2)).setText(R.string.unregistered);
			// Show current location button
			findViewById(R.id.header_button_right).setVisibility(View.VISIBLE);
		}

		// 初期化開始
		initStoreMap();
	}

	/**
	 * 初期化処理を実施します。
	 */
	private void initStoreMap() {
		if(!searchStoreByCurrentLocation){
			StoreDao mDao = null;

			try {
				// DB検索開始...
				mDao = new StoreDao(this);
				storePoint = mDao.getStoreGeoData(storeId);

				// タイトル書き換え（店名）
				String title = mDao.getStoreName(storeId);
				setTitleText(title);
				setTitleTextRed();

			} catch (SQLException e) {

				// DBが更新中（店舗データが更新された）の場合、SQLExceptionで落ちる。
				// 更新が完了するまでリトライ
				Log.w("StoreMapActivity", "storeSearch Failed. TryAgein...", e);

				// タイトルに検索中である旨表示
				setTitleTextRed(getString(R.string.error_storedata_updating));
				mUiHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// 決められた秒数後に自分を再コール
						initStoreMap();
					}
				}, REPEAT_INTERVAL);
				return;
			} finally {
				if (mDao != null) {
					mDao.close();
				}
			}
		}
		else{				
			// ロケーション取得＆初期移動フラグON
			startSearchLocation();
		}	

		startStoreSearchWithPoint();
	}

	/**
	 * Start search store
	 * @param animate
	 */
	public void startStoreSearchWithPoint(){
		if(storePoint == null){
			return;
		}
		// 店舗の位置情報を取得し、画面中心に設定
		animateToWithZoom(storePoint);
		setTitleTextRed(getString(R.string.searching_storedata));
		// 店舗検索開始（非同期） 検索完了時にUIスレッドをコールバックしPINを表示する
		startStoreSearch(storePoint);
	}

	// Check Gps
	public void checkGps(){
		boolean gpsProviderEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean networkProviderEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if(networkProviderEnable){
			if(gpsProviderEnable){
				gpsState = GpsState.ON;					
			}
			else{
				gpsState = GpsState.NETWORK_PROVIDER;				
			}			
		}
		else{
			if(gpsProviderEnable){
				gpsState = GpsState.GPS_PROVIDER;					
			}
			else{
				gpsState = GpsState.OFF;				
			}	
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(searchStoreByCurrentLocation){
			//Log.d("abc","StoreMapActivity::onResume");
			if(checkConditions())
			{
				if(goToSettingsFlag){
					// Wait for updating location after back from Settings
					startProgress();
					startSearchLocation();
					goToSettingsFlag = false;
				}
				else{
					// Update stores
					startStoreSearchWithPoint();
				}
			}
		}
	}

	@Override
	public void onPause() {
		if(searchStoreByCurrentLocation){
			storePoint = mapView.getMapCenter();
		}
		super.onPause();
	}

	/**
	 * Check network and gps
	 */
	public boolean checkConditions(){
		// Check internet
		if(!Utils.isConnected(StoreMapActivity.this)){
			//Log.d("abc","StoreMapActivity::checkConditions 1 ");
			stopRequestUpdateLocation();
			setTitleTextRed(getString(R.string.title_error_network));
			showAlert(R.string.title_error_network, R.string.mes_error_network);
			return false;
		}
		checkGps();		
		switch (gpsState) {
			case ON:
				
				break;
			case OFF:
				stopRequestUpdateLocation();
				setTitleTextRed(getString(R.string.title_error_location));
				showAlert(R.string.title_to_setting, R.string.mes_to_setting);
				return false;
			case NETWORK_PROVIDER:
				
				break;
			case GPS_PROVIDER:
				
				break;
			
			default:
				break;
		}
		if (!isTsvPassed) {
			stopRequestUpdateLocation();
			setTitleText(getString(R.string.can_not_download_store_map_file));
			setTitleTextRed();
			endProgress();
			return false;
		}
		return true;
	}

	@Override
	public void onGetLocationFailed() {
		// When can not get current location
		if(goToMyLocationFlg)
		{
			Log.d("abc","onGetLocationFailed 1");
			stopRequestUpdateLocation();
			setTitleTextRed(getString(R.string.title_error_location));
			showAlert(R.string.title_error_location, R.string.mes_error_location);
		}
		super.onGetLocationFailed();
	}

	@Override
	public void goToCurrentLocation() {
		//Log.d("abc", "goToCurrentLocation");
		if(!searchStoreByCurrentLocation){
			//Log.d("abc", "goToCurrentLocation 1");
			return;
		}

		// 最後に取得した位置情報を取得
		if (locationManager == null) {
			//Log.w("abc", "locationManager does not exists...");
			return;
		}
		Location loc = locationManager.getLastKnownLocation(locationProvider);
		if (loc != null) {
			//Log.d("abc", "goToCurrentLocation 2");
			myLocationOverlay.setCurrentLocation(loc);
			if (goToMyLocationFlg) {
				//Log.d("abc", "goToCurrentLocation 3");
				storePoint = new GeoPoint((int) (loc.getLatitude() * 1E6), (int) (loc.getLongitude() * 1E6));
				initStoreMap();
				setTitleText(getString(R.string.current_location_updated));
				setTitleTextBlack();
				goToMyLocationFlg = false;
			}
		}
		else{
			startProgress();
			startSearchLocation();
			startRequestUpdateLocation();
		}
	}

	/**
	 * Show alert dialog
	 * @param titleId
	 * @param mesId
	 */
	public void showAlert(final int titleId, int mesId){
		goToMyLocationFlg = false;
		endProgress();
		try{
			if(errorDialog != null && errorDialog.isShowing()){
				return;
			}
		}catch (Exception e) {
		}

		LayoutInflater factory = LayoutInflater.from(this);
		View view = factory.inflate(R.layout.alert_error_map, null);
		try {
			errorDialog = new Dialog(this);
			errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			errorDialog.setContentView(view);
			if(titleId != 0) {
				((TextView)view.findViewById(R.id.alert_title)).setText(titleId);
			}else {
				((TextView)view.findViewById(R.id.alert_title)).setText("");
			}
			if(mesId != 0) {
				((TextView)view.findViewById(R.id.alert_mes)).setText(mesId);
			}else {
				((TextView)view.findViewById(R.id.alert_mes)).setText("");
			}

			Button button1 = (Button) view.findViewById(R.id.dialog_btn_1);
			Button button2 = (Button) view.findViewById(R.id.dialog_btn_2); 

			if(titleId == R.string.title_to_setting || titleId == R.string.title_error_location){
				button1.setText(R.string.close);
				button2.setText(R.string.settings);
				button2.setVisibility(View.VISIBLE);
			}
			else{
				button1.setText(R.string.ok);
				button2.setVisibility(View.GONE);
			}

			button1.setOnClickListener(new OnClickListener() {					
				@Override
				public void onClick(View v) {
					errorDialog.cancel();
					stopRequestUpdateLocation();
					FLAG_GET_CURRENT_LOCATION = true;
					// If no network, finish activity 
					if(titleId == R.string.title_error_network){
						mUiHandler.postDelayed(new Runnable() {							
							@Override
							public void run() {
								finish();
							}
						}, 200);
					}
				}
			});

			button2.setOnClickListener(new OnClickListener() {					
				@Override
				public void onClick(View v) {
					errorDialog.cancel();
					stopRequestUpdateLocation();
					FLAG_GET_CURRENT_LOCATION = true;
					goToSettingsFlag = true;
					goToMyLocationFlg = true;
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
			});

			// キャンセル可能
			errorDialog.setCancelable(true);
			errorDialog.show();
		} catch (Exception e) {
			// 無視
			Log.w("ContentsBaseActivity", e);
		}
	}

	@Override
	public boolean endStoreSearch(String[][] result) {

		// result[*][0] = storeId
		// result[*][1] = storeName
		// result[*][2] = lat
		// result[*][3] = lon

		// 親クラスにて共通処理（既存オーバレイ削除）
		if (!super.endStoreSearch(result)) {
			return false;
		}

		// オーバレイリストの取得
		List<Overlay> overlayList = mapView.getOverlays();

		// 赤PIN用Overlay
		StoreOverlay redPinOverlay = createStoreOverlay(R.drawable.pinred);

		// 紫PIN用Overlay
		StoreOverlay purplePinOverlay = createStoreOverlay(R.drawable.pinpurple);

		boolean red = false;
		boolean purple = false;

		GeoPoint point;
		StoreOverlay overray;

		StoreDao mDao = new StoreDao(this);

		for (String[] store : result) {

			if(searchStoreByCurrentLocation){
				if (mDao.checkRegisteredStore(store[0])) {
					// registered
					point = new GeoPoint(Integer.valueOf(store[2]), Integer.valueOf(store[3]));
					overray = redPinOverlay;
					red = true;
				} else {
					// unregistered
					point = new GeoPoint(Integer.valueOf(store[2]), Integer.valueOf(store[3]));
					overray = purplePinOverlay;
					purple = true;
				}
			}
			else{
				if (store[0].equals(storeId)) {
					// 検索対象となった店舗は赤PIN
					point = storePoint;
					overray = redPinOverlay;
					red = true;
				} else {
					point = new GeoPoint(Integer.valueOf(store[2]), Integer.valueOf(store[3]));
					overray = purplePinOverlay;
					purple = true;
				}
			}

			OverlayItem item = new OverlayItem(point, store[1], store[0]);
			overray.addPoint(item);
		}

		// アイテムを登録したもののみaddしなければ画面タッチした際落ちる
		if (red) {
			overlayList.add(redPinOverlay);
		}
		if (purple) {
			overlayList.add(purplePinOverlay);
		}
		mapView.invalidate();

		return true;
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
	public void onDestroy() {
		super.onDestroy();
		mMyrt.destroy();
	}

	@Override
	public void onClickCurrentLocation(View view) {
		if(checkConditions()){
			startProgress();
			startSearchLocation();
			startRequestUpdateLocation();
		}
	}
}