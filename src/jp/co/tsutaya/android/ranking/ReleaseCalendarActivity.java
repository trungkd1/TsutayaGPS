package jp.co.tsutaya.android.ranking;

import android.os.Bundle;

/**
 * リリースカレンダー用Activity
 * 
 * @author Hiroshi.Shinohara@ccc.co.jp
 * 
 */
public class ReleaseCalendarActivity extends ContentsBaseActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.isNoLayaut = false;
		super.onCreate(savedInstanceState);
	}
}
