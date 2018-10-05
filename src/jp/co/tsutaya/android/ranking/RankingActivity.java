package jp.co.tsutaya.android.ranking;

import jp.co.tsutaya.android.ranking.util.Utils;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * ランキング用Activity。
 *
 * @author i_suyama
 *
 */
public class RankingActivity extends ContentsBaseActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Button rightBtn = (Button) findViewById(R.id.header_button_right);
		String url = getIntent().getExtras().getString(Utils.URL);
		// ランキングページの場合、ヘッダーの検索ボタンを更新ボタンに変更
		if (url != null &&
				(url.startsWith(Utils.getUrl(Utils.MENU_RANKING))
				|| url.startsWith(Utils.getUrl(Utils.MENU_RANKING_GENRE))
				|| url.startsWith(Utils.getUrl(Utils.MENU_RANKING_DETAIL)))) {

			rightBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tsc_header_btn_refresh, 0, 0);
			rightBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					tabContents.loadUrl("javascript:reloadRanking();");
				}
			});
		}
	}
}
