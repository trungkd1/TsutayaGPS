package jp.co.tsutaya.android.ranking;

import android.os.Bundle;

/**
 * クリモルクーポン用アクティビティ
 *
 * @author Hiroshi.Shinohara@ccc.co.jp
 *
 */
public class ClimorCouponActivity extends ContentsBaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//this.isNoLayaut = true;
		this.isCyberZLtv = true;
		super.onCreate(savedInstanceState);
	}

	/**
	 * 固有のメンバ変数の値を退避
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("loginState", this.getUserLoginStatus());
		outState.putInt("appMyfcMergeStatus", this.getAppMyfcMergeStatus());
		outState.putInt("climorCouponCount", this.getClimorCouponCount());
		outState.putBoolean("couponCheckService", this.isCouponCheckServiceOn());
		super.onSaveInstanceState(outState);
	}

	/**
	 * 固有のメンバ変数の値を復元
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		this.setAppMyfcMergeStatus(savedInstanceState.getInt("loginState"));
		this.setUserLoginStatus(savedInstanceState.getInt("appMyfcMergeStatus"));
		this.setClimorCouponCount(savedInstanceState
				.getInt("climorCouponCount"));
		this.setCouponCheckServiceOn(savedInstanceState
				.getBoolean("couponCheckService"));
		super.onRestoreInstanceState(savedInstanceState);
	}

}
