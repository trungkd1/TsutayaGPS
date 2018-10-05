package jp.co.tsutaya.android.ranking.overlay;

import java.util.ArrayList;
import java.util.List;

import jp.co.tsutaya.android.ranking.MapBaseActivity;
import jp.co.tsutaya.android.ranking.StockMapActivity;
import jp.co.tsutaya.android.ranking.StoreMapActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * 
 * 店舗検索結果を表示するクラス。
 * 
 * @author i_suyama
 * 
 */
public class StoreOverlay extends BalloonItemizedOverlay<OverlayItem> {

	private List<OverlayItem> items = new ArrayList<OverlayItem>();
	private Class<?> currentClass;
	private MapView mapView;

	private MapBaseActivity mContext;

	/**
	 * タップ判定中時間（ms）
	 */
	private static final int TAPPING_TERM = 500;

	public StoreOverlay(Drawable d, MapView mv, Class<?> c) {
		// 画像の初期位置(0,0)を中心底に設定
		super(boundCenterBottom(d), mv);
		mapView = mv;
		currentClass = c;
		mContext = (MapBaseActivity) mapView.getContext();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return items.get(i);
	}

	@Override
	public int size() {
		return items.size();
	}

	/**
	 * アイテムを追加する。
	 * 
	 * @param item
	 */
	public void addPoint(OverlayItem item) {
		try{
			items.add(item);
			populate();
		} catch (Exception e) {
		}
	}

	/**
	 * アイテムをクリアする。
	 */
	public void clear() {
		try{
			items.clear();
			populate();
		} catch (Exception e) {
		}
	}

	@Override
	public synchronized boolean draw(Canvas canvas, MapView mapView,
			boolean shadow, long when) {

		if (shadow) {
			return true;
		}
		return super.draw(canvas, mapView, shadow, when);

	}

	@Override
	protected boolean onBalloonTap(int index) {

		// アイテムを取得
		OverlayItem item = createItem(index);
		if (item != null && currentClass != null) {
			Activity con = (Activity) mapView.getContext();
			Intent intent = new Intent(con, currentClass);

			// 遷移元クラス（コンテキストから判断）により、遷移先を変更
			if (con instanceof StoreMapActivity) {
				intent.putExtra("URL",
						"store_detail.html?storeId=" + item.getSnippet());
			} else if (con instanceof StockMapActivity) {
				String[] key = item.getSnippet().split(",");
				String productKey = key[0];
				String storeId = key[1];
				intent.putExtra("URL", "product_detail.html?productKey="
						+ productKey + "&storeId=" + storeId);
			}
			con.startActivity(intent);
		}

		return true;
	}

	private void onPinTapEnd() {
		mContext.setOnPinTapping(false);
	}

	@Override
	protected boolean onTap(int index) {
		super.onTap(index);

		// タップ直後に検索が走ると、バルーンが消えてしまう。
		// タップしてから指定時間内はフラグを立て、検索が実行できないよう制御。
		mContext.setOnPinTapping(true);
		mContext.getMUiHandler().postDelayed(new Runnable() {
			@Override
			public void run() {
				onPinTapEnd();
			}
		}, TAPPING_TERM);
		return true;
	}
}