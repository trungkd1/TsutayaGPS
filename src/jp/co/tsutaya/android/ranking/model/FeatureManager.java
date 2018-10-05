package jp.co.tsutaya.android.ranking.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.tsutaya.android.ranking.ContentsBaseActivity;
import jp.co.tsutaya.android.ranking.R;
import jp.co.tsutaya.android.ranking.util.HttpUtils;
import jp.co.tsutaya.android.ranking.util.ImageUtils;
import jp.co.tsutaya.android.ranking.util.Utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class FeatureManager {

	private ContentsBaseActivity baseActivity;
	private Context baseContext;

	private FrameLayout ll;
	private ViewFlipper viewFlipper;

	private List<FeatureModel> features;
	private TextView featureTitle;
	private TextView featureText;
	private LinearLayout featureNextWrapper;
	private LinearLayout featurePrevWrapper;
	private ImageView featureNext;
	private ImageView featurePrev;

	private Handler handler = new Handler();
	private AlphaAnimation alphaAnim;

	// private float firstTouchX;
	// private float firstTouchY;
	// private boolean isFlip = false;

	private List<Integer> selectorIdList;

	private int imageCount;
	private int location;

	private final String FEATURE_XML_URL = "http://www.tsutaya.co.jp/library/media/ap/search/img/topics/Tokusyu.xml";
	private final String FEATURE_XML_OFFLINE_PATH = "xml/feature_offline.xml";

	private float density;

	private final int SLIDE_INTERVAL = 3900;
	private Handler slideHandler = new Handler();
	private Timer slideTimer = null;
	private TimerTask slideTimerTask = null;

	// private final int TOUCH_MAX_LENGTH = 7;
	// private final int FLIP_LENGTH = 50;

	public FeatureManager(Activity act) {
		baseActivity = (ContentsBaseActivity) act;
		baseContext = act.getApplicationContext();
		ll = (FrameLayout) LayoutInflater.from(baseContext).inflate(R.layout.feature, null);
		viewFlipper = (ViewFlipper) ll.findViewById(R.id.flipper);
		featureTitle = (TextView) ll.findViewById(R.id.feature_title);
		featureText = (TextView) ll.findViewById(R.id.feature_text);
		selectorIdList = new ArrayList<Integer>();
		featureNextWrapper = (LinearLayout) ll.findViewById(R.id.feature_button_next_wrapper);
		featurePrevWrapper = (LinearLayout) ll.findViewById(R.id.feature_button_prev_wrapper);
		featureNext = (ImageView) ll.findViewById(R.id.feature_button_next);
		featurePrev = (ImageView) ll.findViewById(R.id.feature_button_prev);
		density = Utils.getDisplayMetrics(baseContext).density;

		// スライドボタンアニメーションの設定
		alphaAnim = new AlphaAnimation(1.0f, 0.0f);
		alphaAnim.setDuration(1000);
		alphaAnim.setFillAfter(true);
		alphaAnim.setFillEnabled(true);
	}


	/**
	 * 特集スライドショーのViewを取得する
	 *
	 * @return
	 */
	public FrameLayout getFeatureView() {
		ImageView featureImage = null;

		boolean isConnected = Utils.isConnected(baseContext);

		// XMLの情報を取得
		if (isConnected) {
			features = parse(FEATURE_XML_URL, true);
			// 通信に失敗した場合
			if (features == null) {
				features = parse(FEATURE_XML_OFFLINE_PATH, false);
			}
		} else {
			features = parse(FEATURE_XML_OFFLINE_PATH, false);
		}
		// InputStream is =
		// getResources().openRawResource(R.raw.feature);//デバッグ用ローカルXML読み込み
		imageCount = features.size();
		for (location = 0; location < imageCount; location++) {
			// 特集情報をセット
			FeatureModel featureInfo = features.get(location);
			// ImageView生成
			featureImage = new ImageView(ll.getContext());
			featureImage.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

			AsyncHttpImageTask asyncImage = new AsyncHttpImageTask(featureImage, featureInfo.getBanner());
			asyncImage.execute(isConnected);

			// ImageViewのラッパー生成
			LinearLayout featureWrapper = new LinearLayout(ll.getContext());
			featureWrapper.addView(featureImage);
			// フリップの動作セット
			// featureWrapper.setOnTouchListener(new OnTouchListener() {
			// @Override
			// public boolean onTouch(View view, MotionEvent motionevent) {
			// int x = (int) motionevent.getRawX();
			// int y = (int) motionevent.getRawY();
			// switch (motionevent.getAction()) {
			// case MotionEvent.ACTION_DOWN:
			// firstTouchX = motionevent.getRawX();
			// firstTouchY = motionevent.getRawY();
			// return true;
			// case MotionEvent.ACTION_MOVE:
			// if (!isFlip) {
			// if (x - firstTouchX > FLIP_LENGTH) { // 右フリップ
			// slidePrev(view);
			// } else if (firstTouchX - x > FLIP_LENGTH) { // 左フリップ
			// slideNext(view);
			// }
			// }
			// break;
			// case MotionEvent.ACTION_UP:
			// if (!isFlip) {
			// if (Math.abs(firstTouchX - x) < TOUCH_MAX_LENGTH &&
			// Math.abs(firstTouchY - y) < TOUCH_MAX_LENGTH) {
			// // 画像クリックとして判定する。
			// Log.v("FeatureSetting", "OnClickEvent");
			// linkToUrl(features.get(viewFlipper.getDisplayedChild()).getUrl());
			// }
			// }
			// isFlip = false;
			// break;
			// }
			// return false;
			// }
			// });
			// Flipperにセット
			viewFlipper.addView(featureWrapper);

			ImageView selectorImage = new ImageView(ll.getContext());
			selectorImage.setLayoutParams(new LayoutParams(Math.round(18 * density), Math.round(18 * density)));
			selectorImage.setPadding(Math.round(5 * density), 0, Math.round(5 * density), 0);
			if (location == 0) {
				selectorImage.setImageResource(R.drawable.tsc_top_feature_circle_on);
				featureTitle.setText(featureInfo.getTitle());
				featureText.setText(featureInfo.getComment());
			} else {
				selectorImage.setImageResource(R.drawable.tsc_top_feature_circle_off);
			}
			selectorImage.setId(Integer.valueOf(location));
			selectorImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					viewFlipper.setInAnimation(null);
					viewFlipper.setOutAnimation(null);
					setDisplayImage(viewFlipper.getDisplayedChild(), view.getId());
				}
			});
			if(imageCount == 1){
				selectorImage.setVisibility(View.INVISIBLE);
				featureNext.setVisibility(View.INVISIBLE);
				featurePrev.setVisibility(View.INVISIBLE);
			}
			((LinearLayout) ll.findViewById(R.id.image_selector)).addView(selectorImage);
			selectorIdList.add(location, selectorImage.getId());

			// スライドボタンの設定
			featureNextWrapper.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					slideNext(v);
					startAutoSlide();
				}
			});
			featurePrevWrapper.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					slidePrev(v);
					startAutoSlide();
				}
			});


		}
		startAutoSlide();
		return ll;
	}

	class AsyncHttpImageTask extends AsyncTask<Boolean, String, Bitmap> {
		private ImageView image;
		private String url;
		private InputStream stream;

		public AsyncHttpImageTask(ImageView image, String url) {
			super();
			this.image = image;
			this.url = url;
		}

		@Override
		protected void onPreExecute() {
			// ここでキャッシュを確認したりすれば余計なスレッドを食わなくてすむ
			if (ImageUtils.hasImage(url)) {
				Log.i("FeatureManager", "use image cache:" + url);
				image.setImageBitmap(ImageUtils.getImage(url));
				setOnclickListener(image);
				cancel(true);
			}
		}

		/**
		 * バックグランドで行う処理
		 *
		 * @return
		 */
		@Override
		protected Bitmap doInBackground(Boolean... isConnected) {
			try {
				if (isConnected[0]) {
					try {
						URL bannerUrl = new URL(url);
						URLConnection conn = bannerUrl.openConnection();
						stream = conn.getInputStream();
					} catch (Exception e) {
						// URLアクセス時にエラーが起きた場合にダミーのイメージを表示する
						stream = baseContext.getResources().getAssets().open("img/top/tsc_top_feature_offline.png");
					}
				} else {
					stream = baseContext.getResources().getAssets().open("img/top/tsc_top_feature_offline.png");
				}
			} catch (IOException ioe) {
				// その他エラー処理
				Log.w("FeatureManager", "IOException ocurred when connecting Image File", ioe);
			}
			Bitmap bitmap = BitmapFactory.decodeStream(stream);
			try {
				stream.close();
				stream = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		/**
		 * バックグランド処理が完了
		 */
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			image.setImageBitmap(bitmap);
			setOnclickListener(image);
			ImageUtils.setImage(url, bitmap);
			Log.i("FeatureManager", "set image cache:" + url);
		}

		private void setOnclickListener(ImageView image) {
			image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					FeatureModel feature = features.get(viewFlipper.getDisplayedChild());
					Map<String, String> params = new HashMap<String, String>();
					List<String> rankingTypeList = new ArrayList<String>(3);
					rankingTypeList.add("daily");
					rankingTypeList.add("weekly");
					rankingTypeList.add("monthly");
					List<String> categoryCdList = new ArrayList<String>(4);
					categoryCdList.add("01");
					categoryCdList.add("02");
					categoryCdList.add("03");
					categoryCdList.add("04");
					List<String> rentalSalesSectionList = new ArrayList<String>(2);
					rentalSalesSectionList.add("1");
					rentalSalesSectionList.add("2");
					// pageTypeに応じて、遷移のさせ方を変える
					switch (feature.getPageType()) {

					case 1:
						// 外部ページ - ブラウザ起動で対象URLを表示
						linkToUrl(feature.getUrl());
						break;
					case 2:
						// ランキングTOP
						if (rankingTypeList.contains(feature.getRankingType())) {
							params.put("rankingType", feature.getRankingType());
							baseActivity.setContent(Utils.MENU_RANKING, params);
						} else {
							Log.w("FeatureManager", "Undefined Parameter: rankingType=" + feature.getRankingType());
						}
						break;
					case 3:
						// ランキングカテゴリ別
						if (rankingTypeList.contains(feature.getRankingType()) && categoryCdList.contains(feature.getCategoryCd())
								&& rentalSalesSectionList.contains(feature.getRentalSalesSection())) {
							params.put("categoryCd", feature.getCategoryCd());
							params.put("rentalSalesSection", feature.getRentalSalesSection());
							params.put("rankingType", feature.getRankingType());
							baseActivity.setContent(Utils.MENU_RANKING_GENRE, params);
						} else {
							Log.w("FeatureManager",
									new StringBuilder("Undefined Parameter: ").append("rankingType=" + feature.getRankingType()).append(", ")
											.append("categoryCd=" + feature.getCategoryCd()).append(", ")
											.append("rentalSalesSection=" + feature.getRentalSalesSection()).toString());
						}
						break;
					case 4:
						// ランキング順位ページ
						params.put("rankingConcentrationCd", feature.getRankingConcentrationCd());
						baseActivity.setContent(Utils.MENU_RANKING_DETAIL, params);
						break;
					case 5:
						// リリース情報
						Map<String, Integer> releaseCategoryCdMap = new HashMap<String, Integer>(5);
						releaseCategoryCdMap.put("01", Utils.MENU_RELEASE);
						releaseCategoryCdMap.put("02", Utils.MENU_RELEASE_DVD);
						releaseCategoryCdMap.put("03", Utils.MENU_RELEASE_CD);
						releaseCategoryCdMap.put("04", Utils.MENU_RELEASE_GAME);
						releaseCategoryCdMap.put("05", Utils.MENU_RELEASE_COMIC);
						if (releaseCategoryCdMap.containsKey(feature.getReleaseCategoryCd())) {
							baseActivity.setContent(releaseCategoryCdMap.get(feature.getReleaseCategoryCd()), params);
						} else {
							Log.w("FeatureManager", "Undefined Parameter: releaseCategoryCd=" + feature.getReleaseCategoryCd());
						}
						break;
					case 6:
						// 在庫ページ
						params.put("productKey", feature.getProductKey());
						baseActivity.setContent(Utils.MENU_PRODUCT_DETAIL, params);
						break;
					case 7:
						// 作品ページ
						params.put("urlCd", feature.getUrlCd());
						baseActivity.setContent(Utils.MENU_WORK_DETAIL, params);
						break;
					case 8:
						// 発掘良品
						baseActivity.setContent(Utils.MENU_SPECIAL_UPDATE, params);
						break;
					default:
						break;
					}
				}
			});
		}
	}

	/**
	 * 特集のXMLを読み込み、XMLファイルのInputStreamを返す。
	 *
	 * @param xmlUrl
	 *            XMLファイルのURL
	 * @return 取得したXMLのInputStream(取得に失敗した場合はnull)
	 */
	private List<FeatureModel> parse(String fileUrl, boolean isConnected) {
		List<FeatureModel> result = null;
		XmlPullParser xmlPullParser = Xml.newPullParser();
		InputStream inputstream = null;
		try {
			// XMLファイルを取得
			if (isConnected) {
				try {
					inputstream = new ByteArrayInputStream(HttpUtils.getAscii(fileUrl).getBytes());
				} catch (Exception e) {
					// 何らかの通信エラーが発生した場合はオフライン用を表示する
					Log.w("FeatureManager", "exception occured when xml parsing", e);
					inputstream = baseContext.getResources().getAssets().open(fileUrl);
				}
			} else {
				inputstream = baseContext.getResources().getAssets().open(fileUrl);
			}
			xmlPullParser.setInput(inputstream, null);
			int eventType = xmlPullParser.getEventType();
			FeatureModel feat = null;
			boolean is_finished = false;
			Date today = new Date();
			Class<? extends FeatureModel> c = null;
			XML: while (eventType != XmlPullParser.END_DOCUMENT && !is_finished) {
				String name = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					result = new ArrayList<FeatureModel>();
					break;
				case XmlPullParser.START_TAG:
					name = xmlPullParser.getName();
					Log.v("XML PRASE ", "name=" + name);
					if (name.equalsIgnoreCase("ELEMENT")) {
						feat = new FeatureModel();
						c = feat.getClass();
					} else if (feat != null) {
						try {
							String text = xmlPullParser.nextText();
							Method method = c.getMethod("set" + Character.toUpperCase(name.charAt(0)) + name.substring(1),
									new Class[] { String.class });
							method.invoke(feat, text);
							Log.v("XML PRASE ", "value=" + text);
						} catch (Exception e) {
							Log.e("FeatureManager", "invoke error", e);
						}
					}
					break;
				case XmlPullParser.END_TAG:
					name = xmlPullParser.getName();
					if (name.equalsIgnoreCase("ELEMENT") && feat != null) {
						// 期間外の場合は除外する
						if ((feat.getFrom() != null && today.before(feat.getFrom())) || (feat.getTo() != null && today.after(feat.getTo()))) {
							eventType = xmlPullParser.next();
							continue XML;
						}
						result.add(feat);
						if (result.size() >= 10) {
							break XML;
						}
					} else if (name.equalsIgnoreCase("RESULT")) {
						is_finished = true;
					}
					break;
				default:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException xe) {
			Log.w("FeatureSetting", "XML is Unparsable", xe);
		} catch (IOException ie) {
			Log.w("FeatureSetting", "XML File", ie);
		} finally {
			if (inputstream != null) {
				try {
					inputstream.close();
				} catch (IOException e) {
					Log.e("FeatureManager", "I/O Error when InputStream close");
				}
				inputstream = null;
			}
		}
		return result;
	}

	private void slideNext(View view) {
		int oldIndex = viewFlipper.getDisplayedChild();
		int newIndex;
		// isFlip = true;
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.move_in_right));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.move_out_left));
		if (oldIndex >= imageCount - 1) {
			newIndex = 0;
		} else {
			newIndex = oldIndex + 1;
		}
		setDisplayImage(oldIndex, newIndex);
		if (oldIndex != newIndex){
			fadeFeatureButton();
		}
	}

	private void slidePrev(View view) {
		int oldIndex = viewFlipper.getDisplayedChild();
		int newIndex;
		// isFlip = true;
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.move_in_left));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.move_out_right));
		if (oldIndex <= 0) {
			newIndex = imageCount - 1;
		} else {
			newIndex = oldIndex - 1;
		}
		setDisplayImage(oldIndex, newIndex);
		if (oldIndex != newIndex){
			fadeFeatureButton();
		}
	}

	private void setDisplayImage(int oldIndex, int newIndex) {
		Log.v("", "oldIndex: " + oldIndex + "  newIndex: " + newIndex);
		if (oldIndex == newIndex){
			return;
		}
		((ImageView) ll.findViewById(selectorIdList.get(oldIndex))).setImageResource(R.drawable.tsc_top_feature_circle_off);
		((ImageView) ll.findViewById(selectorIdList.get(newIndex))).setImageResource(R.drawable.tsc_top_feature_circle_on);
		viewFlipper.setDisplayedChild(newIndex);
		featureTitle.setText(features.get(newIndex).getTitle());
		featureText.setText(features.get(newIndex).getComment());
	}

	/**
	 * ブラウザを起動して、対象URLに遷移
	 *
	 * @param url
	 */
	private void linkToUrl(String url) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		baseContext.startActivity(intent);
	}

	public void fadeFeatureButton() {
		featureNext.clearAnimation();
		featurePrev.clearAnimation();
		handler.removeCallbacks(delayAnimation);
		handler.postDelayed(delayAnimation, 1500);
	}

	// アニメーションを2秒後に実行するため、Handlarを使用
	private final Runnable delayAnimation = new Runnable() {
		@Override
		public void run() {
			featureNext.startAnimation(alphaAnim);
			featurePrev.startAnimation(alphaAnim);
		}
	};

	public void resetFeatureView() {
		for (int i = 0; i < viewFlipper.getChildCount(); i++) {
			ImageView image = (ImageView) ((LinearLayout) viewFlipper.getChildAt(i)).getChildAt(0);
			image.setImageDrawable(null);
			Log.i("FeatureManager", "image drawable is null:" + i);
		}
		ImageUtils.clear();
		handler.removeCallbacks(null);
		stopAutoSlide();
	}

	public FrameLayout getBlankFeatureView() {
		return (FrameLayout) LayoutInflater.from(baseContext).inflate(R.layout.feature, null);
	}

	public void startAutoSlide() {
		stopAutoSlide();
		Log.i("FeatureManager", "startAutoSlide");
		slideTimerTask = new TimerTask() {
			@Override
			public void run() {
				slideHandler.post(new Runnable() {
					@Override
					public void run() {
						slideNext(new View(baseContext));
						Log.i("handler", "slideNext");
					}
				});
			}
		};
		slideTimer = new Timer();
		slideTimer.schedule(slideTimerTask, SLIDE_INTERVAL, SLIDE_INTERVAL);
	}

	public void stopAutoSlide() {
		Log.i("FeatureManager", "stopAutoSlide");
		if (slideTimer != null) {
			slideTimer.cancel();
			slideTimer = null;
		}
		//if (slideTimerTask != null){
	//		slideTimerTask.cancel();
//			slideTimerTask = null;
//		}
	//	slideHandler.removeCallbacks(null);
	}
}