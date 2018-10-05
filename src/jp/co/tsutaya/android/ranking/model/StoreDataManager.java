package jp.co.tsutaya.android.ranking.model;

import java.io.IOException;

import jp.co.tsutaya.android.ranking.StoreMapActivity;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.database.SQLException;
import android.os.AsyncTask;
import android.util.Log;

public class StoreDataManager extends AsyncTask<String, Integer, Boolean> {

	private Activity mContext;

	private static final String URL = "ssshttp://tws.tsutaya.co.jp/htdocs/tsutaya_navi_iphone/data/ct_fcdb.tsv";

	/**
	 * コンストラクタ このクラスの処理doInBackgroundはUIスレッドとは別のスレッドで実行される
	 * 
	 * @param context
	 */
	public StoreDataManager(Activity context) {
		this.mContext = context;
	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected Boolean doInBackground(String... args) {
		boolean result = false;

		HttpClient client = null;
		StoreDao mDao = null;

		try {
			// Daoを生成
			mDao = new StoreDao(mContext);

			// 最終更新日を取得
			String lastUpdateDate = mDao.getLastUpdate();

			// HTTPアクセス開始...
			client = new DefaultHttpClient();
			HttpGet get = new HttpGet(URL);

			if (lastUpdateDate != null) {
				// If-Modified-Sinceを追加
				get.setHeader("If-Modified-Since", lastUpdateDate);
			}
			get.addHeader("Content-Type", "text/html; charset=UTF-8");

			HttpResponse response = client.execute(get);
			// response.containsHeader("Content-Type: text/html; charset=UTF-8");
			int ret = response.getStatusLine().getStatusCode();

			Log.i("StoreData",
					"responceCode:"
							+ String.valueOf(response.getStatusLine()
									.getStatusCode()));

			if (ret == 200) {
				Header[] header = response.getHeaders("Last-Modified");
				lastUpdateDate = header[0].getValue();
				// DB更新処理開始
				Log.i("StoreDataManager", "start StoreDataUpdate....");
				mDao.updateStoreData(EntityUtils.toString(response.getEntity(),
						"UTF-8"));
				// 登録完了後に更新日を登録
				mDao.updateLastUpdate(lastUpdateDate);
				Log.i("StoreDataManager", "....end StoreDataUpdate!");
				result = true;
			} else if (ret == 304) {
				// 処理なし
				result = true;
			} else {
				// 取得に失敗
				result = false;
			}

		} catch (IOException e) {
			// データ取得失敗
			Log.w("StoreData", e);
			result = false;
		} catch (SQLException e) {
			// 更新中である為処理不要
			Log.w("StoreData", e);
			result = false;
		} catch (Exception e) {
			// データ更新失敗
			Log.w("StoreData", e);
			result = false;
		} finally {
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
			if (mDao != null) {
				mDao.close();
			}
		}
		return result;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		StoreMapActivity.isTsvPassed = result;
	}

}
