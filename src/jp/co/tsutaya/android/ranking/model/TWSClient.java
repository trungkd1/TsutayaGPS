package jp.co.tsutaya.android.ranking.model;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.net.Uri;
import android.util.Log;

/**
 * TWSのAPIを起動するクラス。 メソッドを実行したスレッドでHTTPアクセスを行う為、UIスレッド以外で実行する事。
 * 
 * @author i_suyama
 * 
 */
public class TWSClient {

	/** スキーム */
	private static final String SCHEME = "http";

	/** ドメイン */
	private static final String AUTHORITY = "tws.tsutaya.co.jp";

	/** API_KEY */
	private static final String API_KEY = "26fe50f1-8ca5-4660-b1aa-58afe27b2ab5";

	/** プラットフォーム */
	private static final String PLATFORM_CODE = "00";

	/**
	 * 各APIにURLアクセスを行い、結果をTWSModelクラスで返却します。
	 * 
	 * @param param
	 * @return
	 */
	protected TWSModel sendRequest(String apiPath, Map<String, Object> param) {

		boolean result = false;
		// HTTPアクセス開始...
		HttpClient client = null;

		try {

			client = new DefaultHttpClient();

			Uri.Builder uriBuilder = new Uri.Builder();
			uriBuilder.scheme(SCHEME);
			uriBuilder.authority(AUTHORITY);
			uriBuilder.path(apiPath);

			// 固定値
			uriBuilder.appendQueryParameter("api_key", API_KEY);
			uriBuilder.appendQueryParameter("tolPlatformCode", PLATFORM_CODE);

			// 検索条件
			for (Iterator<Entry<String, Object>> i = param.entrySet().iterator(); i.hasNext();) {
				Entry<String, Object> entry = (Entry<String, Object>) i.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				uriBuilder.appendQueryParameter(key, value);
			}

			HttpGet get = new HttpGet(uriBuilder.toString());
			get.addHeader("Content-Type", "application/json; charset=UTF-8");

			// データ取得
			HttpResponse response = client.execute(get);

			if (response == null) {
				// 検索失敗
				return null;
			}
			int ret = response.getStatusLine().getStatusCode();

			Log.i("StoreData",
					"responceCode:"
							+ String.valueOf(response.getStatusLine()
									.getStatusCode()));

			String json = null;
			if (ret == 200) {
				json = EntityUtils.toString(response.getEntity(), "UTF-8");
				Log.d("TWSClident", json);
			} else {
				// 取得に失敗
				return null;
			}
			// JSONのパース
			return new TWSModel(json);

		} catch (IOException e) {
			Log.e("TWSClient", e.getMessage());
			return null;
		} finally {
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
	}

}
