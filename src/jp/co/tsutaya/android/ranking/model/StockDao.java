package jp.co.tsutaya.android.ranking.model;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 店舗のデータを扱うDaoクラス
 * 
 * @author i_suyama
 * 
 */
public class StockDao {

	private SQLiteDatabase db;

	/**
	 * 固定文字列
	 */
	private static final String STOCK_LOCATION_TABLE = "STOCK_LOCATION_TABLE";

	/** 最終更新地データのカラム */
	private static final String LAT = "lat";
	private static final String LON = "lon";
	private static final String ZOOM_LEVEL = "zoom_level";

	/**
	 * コンストラクタ。
	 * 
	 * @param context
	 */
	public StockDao(Activity context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		this.db = dbHelper.getWritableDatabase();
	}

	/**
	 * 最終検索ロケーションを取得します。
	 * 
	 * @return
	 */
	public Map<String, Integer> getLastLocation() {

		Map<String, Integer> result = null;
		Cursor cursor = db.query(STOCK_LOCATION_TABLE, new String[] { LAT, LON,
				ZOOM_LEVEL }, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			result = new HashMap<String, Integer>();
			result.put(LAT, cursor.getInt(0));
			result.put(LON, cursor.getInt(1));
			result.put(ZOOM_LEVEL, cursor.getInt(2));
		}

		cursor.close();
		return result;

	}

	/**
	 * 最終検索ロケーションを更新します。
	 * 
	 * @param date
	 * @return
	 */
	public int updateLastUpdate(int lat, int lon, int zoomLevel) {

		db.delete(STOCK_LOCATION_TABLE, null, null);
		ContentValues values = new ContentValues();
		values.put(LAT, lat);
		values.put(LON, lon);
		values.put(ZOOM_LEVEL, zoomLevel);
		return (int) db.insert(STOCK_LOCATION_TABLE, null, values);

	}

	/**
	 * DBのクローズ処理を行います。
	 */
	public void close() {
		db.close();
	}

}
