/**
 * (C)Culture Convenience Club Co.,Ltd.
 */
package jp.co.tsutaya.android.ranking;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

/**
 * TSUTAYA Splash Activity
 *
 */
public class SplashActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		VideoView video = (VideoView) findViewById(R.id.splash_movie);
		video.setVideoURI(Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.splash));
		video.start();

		Handler hdl = new Handler();
		// 動画再生時間分遅らせてsplashHandlerを実行
		hdl.postDelayed(new splashHandler(), 2000);

	}
	class splashHandler implements Runnable {
		@Override
		public void run() {
			// スプラッシュ完了後に実行するActivityを指定します。
			Intent intent = new Intent(getApplication(), TopMenuActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			SplashActivity.this.finish();
		}
	}
}
