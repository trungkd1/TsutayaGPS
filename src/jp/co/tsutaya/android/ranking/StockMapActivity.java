package jp.co.tsutaya.android.ranking;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.tsutaya.android.ranking.model.StockDao;
import jp.co.tsutaya.android.ranking.model.StockDataManager;
import jp.co.tsutaya.android.ranking.overlay.StoreOverlay;
import jp.co.tsutaya.android.ranking.util.Utils;
import android.database.SQLException;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * 現在地から在庫検索用Activity。
 *
 * @author i_suyama
 *
 */
public class StockMapActivity extends MapBaseActivity {	
	
	/** 検索中の商品のプロダクトキー。 */
	private static String productKey;

	/** 緑PIN用Overlay(在庫あり) */
	private StoreOverlay greenPinOverlay;
	/** 赤PIN用Overlay(在庫なし) */
	private StoreOverlay redPinOverlay;
	/** 灰PIN用Overlay(取扱いなし) */
	private StoreOverlay grayPinOverlay;
	/** 紫PIN用Overlay(検索中) */
	private StoreOverlay purplePinOverlay;

	/** オーバレイ表示済みフラグ */
	private boolean greenExists = false;
	private boolean redExists = false;
	private boolean grayExists = false;
	private boolean purpleExists = false;

	private StockDao mStockDao;

	/** 店舗の同時最大検索数 */
	private static final int STOCKSEARCH_STORE_MAX = 5;

	/** 表示中の店舗情報キャッシュ。 */
	private Map<String, Map<String, Object>> cacheStoreInfo = Collections.synchronizedMap(new HashMap<String, Map<String, Object>>());
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mMyrt.post("TS04", "item_map");
		Log.d("RTMetrics", "post value[TS25:item_map]");

		// 帯タイトルを設定
		setNaviTitle(R.string.stockmap_title);

		// 情報表示用Viewを追加
		initInfoView(R.layout.map_stock_overlay);

		// 店舗情報検索開始
		// 店舗ID取得
		productKey = getIntent().getExtras().getString("PRODUCT_KEY");

		// 初期化処理開始
		initStockMap();
	}

	/**
	 * 初期化処理を実施します。
	 */
	private void initStockMap() {

		Map<String, Integer> lastLocation = null;

		try {
			// 前回検索ポイントの取得
			mStockDao = new StockDao(this);
			lastLocation = mStockDao.getLastLocation();
		} catch (SQLException e) {

			// DBが更新中（店舗データが更新された）の場合、SQLExceptionで落ちる。
			// 更新が完了するまでリトライ
			Log.w("StockMapActivity", "storeSearch Failed. TryAgein...", e);

			// タイトルに検索中である旨表示
			setTitleTextRed(getString(R.string.error_storedata_updating));
			mUiHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// 決められた秒数後に自分を再コール
					initStockMap();
				}
			}, REPEAT_INTERVAL);
			return;
		}

		if (lastLocation == null) {
			// ロケーション取得＆初期移動フラグON
			startSearchLocation();
		} else {
			GeoPoint pos = new GeoPoint(lastLocation.get("lat"), lastLocation.get("lon"));
			// その場所に移動して店舗検索開始
			animateToWithZoom(pos, lastLocation.get("zoom_level"));
			// 店舗情報検索開始
			setTitleTextRed(getString(R.string.searching_stockdata));
			startStoreSearch(pos);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mStockDao == null) {
			try {
				mStockDao = new StockDao(this);
			} catch (Exception e) {
				Log.w("StockMapActivity", e);
			}
		}
	}

	@Override
	public void onPause() {

		if (mStockDao != null) {
			// 最終表示ポイントを記録
			try {
				mStockDao.updateLastUpdate(mapView.getMapCenter().getLatitudeE6(), mapView.getMapCenter().getLongitudeE6(), mapView.getZoomLevel());
			} catch (Exception e) {
				Log.w("StockMapActivity", e);
			} finally {
				// Daoをクローズ
				try {
					mStockDao.close();
				} catch (Exception e) {
					Log.w("StockMapActivity", e);
				}
				mStockDao = null;
			}
		}
		super.onPause();
	}
		
	@Override
	public void goToCurrentLocation() {
		// 最後に取得した位置情報を取得
		if (locationManager == null) {
			Log.w("StockMapActivity", "locationManager does not exists...");
			return;
		}
		Location loc = locationManager.getLastKnownLocation(locationProvider);
		if (loc != null) {
			GeoPoint pos = new GeoPoint((int) (loc.getLatitude() * 1E6), (int) (loc.getLongitude() * 1E6));
			myLocationOverlay.setCurrentLocation(loc);
			if (goToMyLocationFlg) {
				animateToWithZoom(pos);
				// 店舗情報検索開始
				startStoreSearch(pos);
				setTitleTextRed(getString(R.string.searching_stockdata));
				goToMyLocationFlg = false;
			}
		} else {
			// 位置情報を再要求
			mUiHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
				}
			}, REPEAT_INTERVAL_GPS);
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

		// 保持しているOverlayを破棄
		greenPinOverlay = null;
		redPinOverlay = null;
		grayPinOverlay = null;
		purplePinOverlay = null;

		// キャッシュ情報の更新
		updateStoreCacheInfo(result);

		// 検索２　店舗在庫状況検索開始
		startStockSearch(result);

		// 描画
		drawStorePin(result);

		return true;
	}

	/**
	 * 在庫検索を開始します。
	 *
	 * @param storeData
	 *            店舗検索結果
	 */
	public void startStockSearch(String[][] storeData) {

		// request
		// storeData[0] = productId
		// storeData[1] = storeId

		// 検索開始
		setTitleTextRed(getString(R.string.searching_stockdata));

		boolean searchFlg = false;

		StringBuilder storeIds = new StringBuilder();
		int cnt = 0;
		for (int i = 0; i < storeData.length; i++) {
			Map<String, Object> cacheStore = cacheStoreInfo.get(storeData[i][0]);
			if (cacheStore != null && (Integer) cacheStore.get("stock") != Utils.STOCK_SEARCHING) {
				// 描画する必要がないので検索しない
				continue;
			}
			if (storeIds.length() != 0) {
				storeIds.append(",");
			}
			storeIds.append(storeData[i][0]);
			cnt++;
			if (cnt == STOCKSEARCH_STORE_MAX) {
				// 5件ずつ実行
				requestStockData(storeIds);
				storeIds = new StringBuilder();
				cnt = 0;
				searchFlg = true;
			}
		}
		if (storeIds.length() != 0) {
			// 残りも検索
			requestStockData(storeIds);
			searchFlg = true;
		}
		if (!searchFlg) {
			// 検索を行わなかった場合は検索終了
			setTitleTextBlack();
		}
	}

	/**
	 * 在庫検索を依頼します。
	 *
	 * @param storeIds
	 */
	private void requestStockData(StringBuilder storeIds) {
		StockDataManager stockDataManager = new StockDataManager(this);
		String[] params = new String[2];
		params[0] = productKey;
		params[1] = storeIds.toString();
		stockDataManager.execute(params);
	}

	private static final String PRODUCT_ISRENTAL = "1";

	/**
	 * 作品情報を設定します。
	 */
	public void setProductInfo(Map<String, Object> productInfo) {
		if (productInfo != null) {
			StringBuilder tmp = new StringBuilder();
			tmp.append("[");
			tmp.append(PRODUCT_ISRENTAL.equals(String.valueOf(productInfo.get("isRental"))) ? getString(R.string.stockmap_info_rental)
					: getString(R.string.stockmap_info_sell));
			tmp.append("][");
			tmp.append(productInfo.get("itemNameDisp"));
			tmp.append("]");
			tmp.append(productInfo.get("productName"));
			setTitleText(tmp.toString());
			setTitleTextBlack();
		}
	}

	/**
	 * 在庫検索完了時の処理
	 *
	 * @param result
	 */
	public void endStockSearch(String[][] result) {

		// 検索終了
		setTitleTextBlack();

		// 検索結果なし
		if (result == null) {
			return;
		}

		// result[*][0] = storeId
		// result[*][1] = stockInfo.symbol

		// キャッシュの在庫データ更新
		updateStockCacheInfo(result);

		// 描画
		drawStorePin(result);

	}

	/**
	 * キャッシュ情報を更新します。(店舗情報)
	 *
	 * @param storeInfo
	 * @param type
	 */
	private void updateStoreCacheInfo(String storeInfo[][]) {
		for (String[] store : storeInfo) {
			if (cacheStoreInfo.containsKey(store[0])) {
				continue;
			} else {
				HashMap<String, Object> tmp = new HashMap<String, Object>();
				tmp.put("storeId", store[0]);
				tmp.put("storeName", store[1]);
				tmp.put("lat", store[2]);
				tmp.put("lon", store[3]);
				tmp.put("stock", Utils.STOCK_SEARCHING);
				cacheStoreInfo.put(store[0], tmp);
			}
		}
	}

	/**
	 * キャッシュ情報を更新します。(在庫情報)
	 *
	 * @param stockInfo
	 * @param type
	 */
	private void updateStockCacheInfo(String storeInfo[][]) {
		for (String[] store : storeInfo) {
			Map<String, Object> tmp = cacheStoreInfo.get(store[0]);
			if (tmp == null) {
				// あり得ない
				continue;
			}
			tmp.put("stock", Utils.castStockInfo(store[1]));
			cacheStoreInfo.put(store[0], tmp);
		}
	}

	/**
	 * キャッシュと渡された配列の情報をもとに店舗PINを描画します。
	 *
	 * @param storeInfo
	 */
	private void drawStorePin(String storeInfo[][]) {

		// result[*][0] = storeId

		// オーバレイリストの取得
		List<Overlay> overlayList = mapView.getOverlays();

		// 初期表示の場合、Overlayを生成
		if (greenPinOverlay == null) {
			// 緑PIN用Overlay(在庫あり)
			greenPinOverlay = createStoreOverlay(R.drawable.pingreen);
			// 赤PIN用Overlay(在庫なし)
			redPinOverlay = createStoreOverlay(R.drawable.pinred);
			// 灰PIN用Overlay(取扱いなし)
			grayPinOverlay = createStoreOverlay(R.drawable.pingray);
			// 紫PIN用Overlay(検索中)
			purplePinOverlay = createStoreOverlay(R.drawable.pinpurple);

			greenExists = false;
			redExists = false;
			grayExists = false;
			purpleExists = false;
		} else {
			// ２回目以降の描画の際は検索中PINを削除
			if (purplePinOverlay != null) {
				purplePinOverlay.hideBalloon();
				overlayList.remove(purplePinOverlay);
			}
		}

		// Overlay追加フラグ
		boolean green = false;
		boolean red = false;
		boolean gray = false;
		boolean purple = false;

		GeoPoint point;
		StoreOverlay overlay = null;

		for (String[] store : storeInfo) {
			Map<String, Object> tmp = cacheStoreInfo.get(store[0]);
			Integer stockClass = (Integer) tmp.get("stock");
			switch (stockClass) {
			case Utils.STOCK_EXISTS:
				overlay = greenPinOverlay;
				green = true;
				break;
			case Utils.STOCK_NONE:
				overlay = redPinOverlay;
				red = true;
				break;
			case Utils.STOCK_NG:
				overlay = grayPinOverlay;
				gray = true;
				break;
			case Utils.STOCK_SEARCHING:
				overlay = purplePinOverlay;
				purple = true;
				break;
			}
			point = new GeoPoint(Integer.valueOf((String) tmp.get("lat")), Integer.valueOf((String) tmp.get("lon")));
			OverlayItem item = new OverlayItem(point, (String) tmp.get("storeName"), productKey + "," + (String) tmp.get("storeId"));
			overlay.addPoint(item);
		}

		// アイテムを登録したもののみaddしなければ画面タッチした際落ちる
		if (green && !greenExists) {
			overlayList.add(greenPinOverlay);
			greenExists = true;
		}
		if (red && !redExists) {
			overlayList.add(redPinOverlay);
			redExists = true;
		}
		if (gray && !grayExists) {
			overlayList.add(grayPinOverlay);
			grayExists = true;
		}
		if (purple && !purpleExists) {
			overlayList.add(purplePinOverlay);
			purpleExists = true;
		}

		// 再描画依頼
		mapView.invalidate();

	}

	/**
	 * オプションメニューの実装
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
		// 現在地を追加
//		menu.add(0, Utils.MENU_MYLOCATION, Utils.MENU_MYLOCATION, getString(R.string.menu_mylocation)).setIcon(android.R.drawable.ic_menu_mylocation);
//
//		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
		case Utils.MENU_MYLOCATION:
			if (locationProvider != null) {
				// 現在地へ移動＆再建策を要求
				startSearchLocation();
				goToCurrentLocation();
			} else {
				// 位置情報取得に失敗
				setTitleTextRed(getString(R.string.error_gps_unuseable));
			}
			break;
		default:
			super.onMenuItemSelected(featureId, item);
			break;
		}
		return true;
	}
	
}
