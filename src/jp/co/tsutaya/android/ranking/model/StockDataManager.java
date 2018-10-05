package jp.co.tsutaya.android.ranking.model;

import java.util.HashMap;
import java.util.Map;

import jp.co.tsutaya.android.ranking.StockMapActivity;
import android.os.AsyncTask;

public class StockDataManager extends AsyncTask<String, Integer, String[][]> {

	/**
	 * 呼び出し元Context
	 */
	private StockMapActivity mContext;

	/**
	 * API
	 */
	private static final String PATH = "/store/v0/products/detail.json";

	/** APIアクセス時の固定値。 */
	private static final String FILE_SET = "stock";

	/** APIアクセス時の固定値。 */
	private static final String ADULT_AUTH = "1";

	private Map<String, Object> productInfo;

	/**
	 * コンストラクタ このクラスの処理doInBackgroundはUIスレッドとは別のスレッドで実行される
	 *
	 * @param context
	 */
	public StockDataManager(StockMapActivity context) {
		this.mContext = context;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected String[][] doInBackground(String... args) {

		// 配列のデータ形式は以下の通り。
		// args[0] = productkey
		// args[1] = storeId（カンマ区切り）

		// 検索条件
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("productKey", args[0]);
		data.put("fieldSet", FILE_SET);
		data.put("storeId", args[1]);
		data.put("adultAuthOK", ADULT_AUTH);

		// API実行、結果取得
		TWSClient client = new TWSClient();
		TWSModel model = client.sendRequest(PATH, data);

		if (model == null) {
			return null;
		}

		productInfo = model.getProductsInfo();
		return model.getStoreStockDetail();

	}

	@Override
	protected void onPostExecute(String[][] result) {
		// ここはUIスレッドで実行される為、余計な処理は行わない
		if (productInfo != null) {
			mContext.setProductInfo(productInfo);
			mContext.endStockSearch(result);
		}
	}

}
