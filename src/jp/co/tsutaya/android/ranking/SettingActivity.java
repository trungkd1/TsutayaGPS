package jp.co.tsutaya.android.ranking;

import android.os.Bundle;

/**
 * 設定用Activity。 本クラスはメニューから起動された際、タスクを分ける為に使用している。
 *
 * @author i_suyama
 *
 */
public class SettingActivity extends ContentsBaseActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.isCyberZLtv = true;
		super.onCreate(savedInstanceState);
	}
}
