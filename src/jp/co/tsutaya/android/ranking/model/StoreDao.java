package jp.co.tsutaya.android.ranking.model;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.maps.GeoPoint;

/**
 * 店舗のデータを扱うDaoクラス
 * 
 * @author i_suyama
 * 
 */
public class StoreDao {

	private SQLiteDatabase db;

	/**
	 * 固定文字列
	 */
	private static final String STORE_DATA_TABLE = "STORE_DATA";
	private static final String STORE_DATA_UPDATE_TABLE = "STORE_DATA_UPDATE";
	private static final String REGISTERED_STORE_TABLE = "REGISTERED_STORE";

	/** 店舗更新日のカラム */
	private static final String LAST_UPDATE_DATE = "last_update_date";

	/** 店舗データのカラム */
	private static final String FCID = "fcid";
	private static final String FNAM = "fnam";
	private static final String TEL1 = "tel1";
	private static final String TEL2 = "tel2";
	private static final String TEL3 = "tel3";
	private static final String MAP_N = "map_n";
	private static final String MAP_E = "map_e";

	private static final int MAX_VIEW_COUNT = 20;

	/**
	 * コンストラクタ。
	 * 
	 * @param context
	 */
	public StoreDao(Activity context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		this.db = dbHelper.getWritableDatabase();
		dbHelper.createRegisteredStoreTable(db);
	}

	/**
	 * 最終更新日を取得します。
	 * 
	 * @return
	 */
	public String getLastUpdate() {

		String result = null;
		Cursor cursor = db
				.query(STORE_DATA_UPDATE_TABLE,
						new String[] { LAST_UPDATE_DATE }, null, null, null,
						null, null);

		if (cursor.moveToFirst()) {
			result = cursor.getString(0);
		}

		cursor.close();
		return result;

	}

	/**
	 * 最終更新日を更新します。
	 * 
	 * @param date
	 * @return
	 */
	public int updateLastUpdate(String date) {

		db.delete(STORE_DATA_UPDATE_TABLE, null, null);
		ContentValues values = new ContentValues();
		values.put(LAST_UPDATE_DATE, date);
		return (int) db.insert(STORE_DATA_UPDATE_TABLE, null, values);

	}

	/**
	 * 店舗のデータを更新します。
	 * 
	 * @return
	 */
	public int updateStoreData(String tsvData) {

		// トランザクション開始
		db.beginTransaction();

		try {

			// データ総入れ替え
			db.delete(STORE_DATA_TABLE, null, null);

			// 改行でスプリット
			String[] lines = tsvData.split("\\n");

			ContentValues values;
			int count = 0;

			// １行ずつ登録
			for (String line : lines) {
				String[] columns = line.split("\\t");

				values = new ContentValues();
				values.put(FCID, columns[0]);
				// String fname = new String(columns[1].getBytes("Shift_JIS"),
				// "UTF-8");
				String fname = columns[1];
				values.put(FNAM, fname);
				values.put(TEL1, columns[2]);
				values.put(TEL2, columns[3]);
				values.put(TEL3, columns[4]);

				try {
					double lat = Double.valueOf(columns[5].trim());
					double lng = Double.valueOf(columns[6].trim());
					double lat2 = lat - lat * 0.00010695 + lng * 0.000017464
							+ 0.0046017; // 日本→世界
					double lng2 = lng - lat * 0.000046038 - lng * 0.000083043
							+ 0.010040; // 日本→世界
					values.put(MAP_N, (int) (lat2 * 1E6));
					values.put(MAP_E, (int) (lng2 * 1E6));
				} catch (NumberFormatException e) {
					continue;
				}

				db.insert(STORE_DATA_TABLE, null, values);
				Log.d("StoreDao", String.valueOf(++count));
			}

			db.setTransactionSuccessful();

		} catch (Exception e) {
			Log.w("StoreDao", e);
		} finally {
			db.endTransaction();
		}

		return 1;

	}

	/**
	 * 指定した店舗の座標を取得します。
	 * 
	 * @param storeId
	 * @return
	 */
	public GeoPoint getStoreGeoData(String storeId) {

		String where = "FCID = ?";
		String[] args = { storeId };
		Cursor cursor = db.query(STORE_DATA_TABLE,
				new String[] { MAP_N, MAP_E }, where, args, null, null, null);

		// HashMapはコストが高い...
		int count = cursor.getCount();
		if (count != 1) {
			return null;
		}

		cursor.moveToNext();
		GeoPoint pos = new GeoPoint(cursor.getInt(0), cursor.getInt(1));

		cursor.close();
		return pos;

	}

	/**
	 * 指定した店舗の座標を取得します。
	 * 
	 * @param storeId
	 * @return
	 */
	public String getStoreName(String storeId) {

		String where = "FCID = ?";
		String[] args = { storeId };
		Cursor cursor = db.query(STORE_DATA_TABLE, new String[] { FNAM },
				where, args, null, null, null);

		// HashMapはコストが高い...
		int count = cursor.getCount();
		if (count != 1) {
			return null;
		}

		cursor.moveToNext();
		String ret = cursor.getString(0);

		cursor.close();
		return ret;

	}

	/**
	 * 指定した範囲（GEO）に存在する店舗を検索します。
	 * 
	 * @param lat
	 * @param lon
	 * @return
	 */
	public String[][] getStoreData(int[] coordinate) {

		// 指定された矩形領域に存在する店舗を検索
		String where = " ? <= MAP_N AND ? >= MAP_N AND ? <= MAP_E AND ? >= MAP_E";
		String[] args = { String.valueOf(coordinate[0]),
				String.valueOf(coordinate[1]), String.valueOf(coordinate[2]),
				String.valueOf(coordinate[3]) };
		Cursor cursor = db.query(STORE_DATA_TABLE, new String[] { FCID, FNAM,
				MAP_N, MAP_E }, where, args, null, null, null);

		// HashMapはコストが高い為Stringで返す
		int count = cursor.getCount();
		if (count == 0) {
			return new String[0][0];
		} else if (count > MAX_VIEW_COUNT) {
			return null;
		}

		String[][] result = new String[cursor.getCount()][4];
		int i = 0;
		while (cursor.moveToNext()) {
			result[i][0] = cursor.getString(0);
			result[i][1] = cursor.getString(1);
			result[i][2] = cursor.getString(2);
			result[i][3] = cursor.getString(3);
			i++;
		}

		cursor.close();
		return result;
	}

	/**
	 * DBのクローズ処理を行います。
	 */
	public void close() {
		db.close();
	}
	
	/**
	 * Update store registration.
	 * If store is registered, remove it.
	 * If store is unregistered, add it.
	 * @param storeId
	 */
	public void updateRegisteredStore(String storeId, int state){
		boolean check = checkRegisteredStore(storeId);
		if(state == 0 && check){
			String where = "FCID = ?";
			String[] args = { storeId };
			db.delete(REGISTERED_STORE_TABLE, where, args);
		}
		else if(state == 1 && !check){
			ContentValues values = new ContentValues();
			values.put(FCID, storeId);
			db.insert(REGISTERED_STORE_TABLE, null, values);
		}
	}
	
	/**
	 * Check store is registered
	 * @param storeId
	 * @return boolean
	 */	
	public boolean checkRegisteredStore(String storeId){
		String where = "FCID = ?";
		String[] args = { storeId };
		Cursor cursor = db.query(REGISTERED_STORE_TABLE, new String[] { FCID}, where, args, null, null, null);
		int count = cursor.getCount();
		cursor.close();
		if (count == 0) {
			return false;
		}
		return true;
	}

}
