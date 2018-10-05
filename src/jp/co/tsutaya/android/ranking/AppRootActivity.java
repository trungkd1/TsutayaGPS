package jp.co.tsutaya.android.ranking;

import java.io.File;

import jp.co.tsutaya.android.ranking.model.StoreDataManager;
import jp.co.tsutaya.android.ranking.util.Utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import jp.appAdForce.android.AdManager;

public class AppRootActivity extends Activity {

	/** Intentのキー */
	private static final String URL = "URL";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.init(getApplicationContext());
		AdManager ad = new AdManager(this);
		ad.sendConversion("http://www.tsutaya.co.jp/smartphone/ap/search/a_boot.html");

		Intent ownIntent = getIntent();
		Bundle b = ownIntent.getExtras();
		int cflag = 0;
		if (b != null)
			cflag = b.getInt("COUPON");

		// 初期画面の起動
		Intent intent;
		if (cflag == 1) {
			intent = new Intent(this, ClimorCouponActivity.class);
			intent.putExtra(URL, "climorcoupon.html");
		} else {
			//intent = new Intent(this, SplashActivity.class);
			intent = new Intent(getApplication(), TopMenuActivity.class);
		}

		// アクティビティ起動
		// FLAG_ACTIVITY_REORDER_TO_FRONT
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);

		// cacheファイル移行チェック
		try {
			File databasePath = new File(getCacheDir(), "dbchache");
			File newDatabasePath = getDatabasePath("dbchache");
			if (databasePath.exists() && !newDatabasePath.exists()) {
				// 旧バージョンのファイルが存在し、新バージョンのファイルが存在しない場合
				Log.w("AppRootActivity", "renameCacheFile!");
				databasePath.renameTo(newDatabasePath);
			} else {
				Log.w("AppRootActivity", "old:" + databasePath.exists()
						+ " new:" + newDatabasePath.exists());
			}
		} catch (Exception e) {
			// エラーログを出力して次へ
			Log.w("AppRootActivity", e);
		}

		// 店舗データの更新チェック
		StoreDataManager storeManager = new StoreDataManager(this);
		storeManager.execute();

		// 終了
		this.finish();
	}
}