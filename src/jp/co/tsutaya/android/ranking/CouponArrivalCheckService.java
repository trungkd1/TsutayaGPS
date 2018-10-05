/**
 * 新着クーポンチェックサービス
 * (c) カルチュア・コンビニエンス・クラブ
 */
package jp.co.tsutaya.android.ranking;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
//import java.io.IOException;
import java.net.URLEncoder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
//import android.text.format.Time;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.co.tsutaya.android.ranking.util.Utils;

public class CouponArrivalCheckService extends IntentService {

	private final String CLIMOR_API = "http://c.tsutaya.co.jp/api/new_arrivals";
	private final String API_KEY = "tsutaya_search_android";
	private final String ARVFILE = "archfile.txt";
	private final int COONNECTION_TIMEOUT = 2000; // ミリ秒指定

	/** 新着クーポンをチェックする時間帯。９:00 - 19:00まで */
	public static final int CHECK_SERVICE_START = 9;
	public static final int CHECK_SERVICE_END = 19;

	/**
	 * クリモル新着APIにアクセスしてJSONオブジェクトを取得する
	 */
	protected JSONObject _getArrivals(String tolid) {
		JSONObject ret = null;
		String url = null;
		try {
			if (tolid != null && !"".equals(tolid.trim())) {
				url = CLIMOR_API + "?key=" + API_KEY + "&tolid="
						+ URLEncoder.encode(tolid, "UTF-8");
			} else {
				Log.w("ClimorArrivalCheck", "Not Logined.");
				return null;
			}
		} catch (Exception e) {
			Log.w("CouponCheckService", "invalid Account.");
			return null;
		}

		DefaultHttpClient curl = new DefaultHttpClient();
		HttpParams params = curl.getParams();
		HttpConnectionParams.setConnectionTimeout(params, COONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, COONNECTION_TIMEOUT);

		HttpUriRequest rqm = new HttpGet(url);
		HttpResponse response = null;
		HttpEntity resEntity = null;
		try {
			response = curl.execute(rqm);
			switch (response.getStatusLine().getStatusCode()) {
			case HttpStatus.SC_OK:
				resEntity = response.getEntity();
				if (resEntity != null) {
					String resString = EntityUtils.toString(resEntity);
					ret = new JSONObject(resString);
				}
				break;
			default:
				return null;
			}
		} catch (Exception e) {
			Log.w("CouponCheckService", "Connection Error.");
			ret = null;
		} finally {
			curl.getConnectionManager().shutdown();
		}
		return ret;
	}

	/**
	 * クーポン件数を通知する
	 */
	public void makeNotify(String tid, JSONObject dataObj) throws Exception {
		if (dataObj == null)
			return;

		int couponCount = dataObj.getInt("count");
		String date = Utils.getDateString();
		JSONObject prevData = getSavedData();
		int prevCount = 0;
		String prevDate = "";
		String prevTolid = "";
		int notifiedCount = couponCount;
		if (prevData != null) {
			prevCount = prevData.getInt("count");
			prevDate = prevData.getString("date");
			prevTolid = prevData.getString("tid");
			if (date.equals(prevDate) && tid.equals(prevTolid)) {
				notifiedCount = couponCount - prevCount;
			}
		}
		JSONArray ids = dataObj.getJSONArray("id");
		// if(ids != null){ Log.d("JSONCHK",ids.toString());} // debug

		if (notifiedCount > 0)
			this.showNotifyCoupon(notifiedCount);
		if (couponCount > -1) {
			JSONObject saveObj = new JSONObject();
			saveObj.put("date", date);
			saveObj.put("tid", tid);
			saveObj.put("count", couponCount);
			saveObj.put("arrivals", ids.toString());
			this.putStatus(saveObj);
		}
	}

	/**
	 * クーポン情報を記録
	 */
	public void putStatus(JSONObject jso) {
		try {
			FileOutputStream fos = openFileOutput(ARVFILE, Context.MODE_PRIVATE);
			if (jso != null) {
				fos.write(jso.toString().getBytes());
			}
			fos.close();
		} catch (FileNotFoundException fne) {
			Log.w("CouponCheckService", "Flie Not Found.");
		} catch (Exception e) {
			Log.w("CouponCheckService", "Error Occured..");
		}
	}

	/**
	 * ファイル読み込み
	 *
	 * @return JSONObject
	 */
	public JSONObject getSavedData() {
		FileInputStream fis = null;
		JSONObject fileObj = null;
		try {
			fis = openFileInput(ARVFILE);
			BufferedReader buf = new BufferedReader(new InputStreamReader(fis));
			String rline;
			StringBuilder sb = new StringBuilder();
			while ((rline = buf.readLine()) != null) {
				sb.append(rline);
			}
			fileObj = new JSONObject(sb.toString());
		} catch (FileNotFoundException fne) {
			Log.w("CouponCheckService", "ChacheFile does not Exists.");
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (Exception e) {
				// nop
			}
		}
		return fileObj;
	}

	/**
	 * コンストラクタ 現時点では特別な処理は行わない。
	 */
	public CouponArrivalCheckService() {
		super("CouponArrivalCheckService");
	}

	/**
	 * サービスハンドリング クリモルクーポンチェックAPIを呼び出し、新着クーポン数を通知する
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			if (!Utils.isConnected(getApplicationContext())) {
				Log.w("CouponCheckService", "no Internet Connections.");
				return;
			}
			int chkTime = Utils.getCurrentHour();
			if (chkTime >= CHECK_SERVICE_START && chkTime < CHECK_SERVICE_END) {
				String tolid = intent.getExtras().getString(Utils.TOLID);
				if (tolid == null || "".equals(tolid.trim())) {
					return;
				} else {
					JSONObject jso = this._getArrivals(tolid);
					if (jso != null) {
						int result = jso.getInt("result");
						if (result == 0) {
							JSONObject dataObj = jso.getJSONObject("data");
							this.makeNotify(tolid, dataObj);
						}
					} else {
						return;
					}
				}
			} else {
				return;
			}
		} catch (JSONException jse) {
			Log.w("CouponCheckService", "invalid Data Received.");
		} catch (Exception e) {
			Log.w("CouponCheckService", "unexpectable Error Occured.");
		}
	}

	/**
	 * 通知エリアに新着クーポン件数を表示する。
	 *
	 * @param newArrivals
	 *            新着クーポン件数
	 * @return なし
	 */
	public void showNotifyCoupon(int newArrivals) {
		if (newArrivals < 0)
			return;
		Intent i = new Intent(getApplicationContext(), AppRootActivity.class);
		i.putExtra(Utils.COUPON, 1);
		PendingIntent pend = PendingIntent.getActivity(getApplicationContext(),
				0, i, 0);

		NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (newArrivals == 0) {
			nManager.cancelAll();
		} else {
			Notification n = new Notification();
			n.number = newArrivals;
			n.flags = Notification.FLAG_AUTO_CANCEL;
			n.icon = R.drawable.ic_menu_coupon;
			n.when = System.currentTimeMillis();
			CharSequence appName = getText(R.string.app_name);
			CharSequence notifyMsg = "新着クーポンが、" + newArrivals + "件配信されています。";
			n.setLatestEventInfo(getApplicationContext(), appName, notifyMsg,
					pend);
			nManager.notify(1, n);
		}

	}

	/**
	 * 開始処理
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("CouponCheckSearvice", "Service Created");
	}

	/**
	 * 終了処理
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("CouponCheckSearvice", "Service Destroyed");
	}

}
