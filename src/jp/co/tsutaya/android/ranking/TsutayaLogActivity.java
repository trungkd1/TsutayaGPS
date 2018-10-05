package jp.co.tsutaya.android.ranking;

import android.os.Bundle;

public class TsutayaLogActivity extends ContentsBaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.isCyberZLtv = true;
		this.isNoLayaut = true;
		super.onCreate(savedInstanceState);
	}

	/**
	 * 固有のメンバ変数の値を退避
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("loginState", this.getUserLoginStatus());
		super.onSaveInstanceState(outState);
	}
}
