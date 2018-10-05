package jp.co.tsutaya.android.ranking.model;

import jp.co.tsutaya.android.ranking.MapBaseActivity;
import android.os.AsyncTask;
import android.util.Log;

public class StoreDataTask extends AsyncTask<String, Integer, String[][]> {

	private MapBaseActivity mContext;
	private StoreDao mDao;

	/**
	 * コンストラクタ このクラスの処理doInBackgroundはUIスレッドとは別のスレッドで実行される
	 * 
	 * @param context
	 */
	public StoreDataTask(MapBaseActivity context) {
		this.mContext = context;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected String[][] doInBackground(String... args) {

		// 緯度・経度の境界値を格納する配列
		// 南、北、西、東の順で格納
		int[] coordinate = new int[4];

		try {
			int centerLat = Integer.valueOf(args[0]);
			int centerLon = Integer.valueOf(args[1]);
			int latSpan = Integer.valueOf(args[2]);
			int lonSpan = Integer.valueOf(args[3]);

			coordinate[0] = (int) (centerLat - latSpan / 2);
			coordinate[1] = (int) (centerLat + latSpan / 2);
			coordinate[2] = (int) (centerLon - lonSpan / 2);
			coordinate[3] = (int) (centerLon + lonSpan / 2);
		} catch (Exception e) {
			// 検索失敗。
			Log.w("StoreDataTask",
					"IligalArgumentException...(Fault NumberFormat)");
			Log.w("StoreDataTask", e);
			return null;
		}

		String[][] data = null;

		try {

			// Daoを生成
			mDao = new StoreDao(mContext);
			data = mDao.getStoreData(coordinate);

		} catch (Exception e) {
			Log.w("StoreData", e);
			return null;
		} finally {
			if(mDao != null){
				mDao.close();
			}
		}

		return data;
	}

	@Override
	protected void onPostExecute(String[][] result) {
		// mContextをコールバック
		mContext.endStoreSearch(result);
	}

}
