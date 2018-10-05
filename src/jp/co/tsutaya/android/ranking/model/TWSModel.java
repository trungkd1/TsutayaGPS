package jp.co.tsutaya.android.ranking.model;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * TWSのAPIを起動するクラス。 メソッドを実行したスレッドでHTTPアクセスを行う為、UIスレッド以外で実行する事。
 * 
 * @author i_suyama
 * 
 */
public class TWSModel {

	private static final String JSON_ENTRY = "entry";
	private static final String PATH_SEPALATOR = "/";

	private JSONObject entry;

	/**
	 * API実行結果（JSON）を展開します。
	 * 
	 * @param responseBody
	 */
	public TWSModel(String responseBody) {
		try {
			// JSONのパース
			JSONObject rootObject = new JSONObject(responseBody);
			entry = rootObject.getJSONObject(JSON_ENTRY);

		} catch (JSONException e) {
			Log.e("TWSModel", e.getMessage());
		}
	}

	/**
	 * 検索した作品の情報を取得します。
	 * 
	 * @return
	 */
	public Map<String, Object> getProductsInfo() {
		try {

			// Entryから各情報を取得
			HashMap<String, Object> ret = new HashMap<String, Object>();
			ret.put("isRental", entry.get("isRental"));
			ret.put("itemNameDisp", entry.get("itemNameDisp"));
			ret.put("productName", entry.get("productName"));

			return ret;

		} catch (JSONException e) {
			Log.e("TWSModel", e.getMessage());
			return null;
		}
	}

	/**
	 * 店舗在庫情報を取得します。
	 * 
	 * @return
	 */
	public String[][] getStoreStockDetail() {

		try {
			JSONArray stockInfo = entry.getJSONArray("stockInfo");
			JSONObject storeInfo;

			int count = stockInfo.length();

			String[][] ret = new String[count][2];

			for (int i = 0; i < count; i++) {
				storeInfo = stockInfo.getJSONObject(i);
				ret[i][0] = (String) storeInfo.get("storeId");
				ret[i][1] = (String) storeInfo.getJSONObject("stockStatus")
						.get("symbol");
			}

			return ret;

		} catch (JSONException e) {
			Log.e("TWSModel", e.getMessage());
			return null;
		}
	}

	// private JSONArray getArrayForPath(String path) {
	// String[] pathInfo = path.split(PATH_SEPALATOR);
	// try {
	// int depth = pathInfo.length;
	// JSONObject tmp = entry.getJSONObject(pathInfo[0]);
	// for (int i = 0; i < depth - 1; i++) {
	// tmp = tmp.getJSONObject(key);
	// }
	// return tmp.getJSONArray(pathInfo[depth - 1]);
	// } catch (JSONException e) {
	// Log.e("TWSModel", e.getMessage());
	// }
	// return null;
	// }

}
