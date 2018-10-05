package jp.co.tsutaya.android.ranking.slide;

import jp.co.tsutaya.android.ranking.R;
import jp.co.tsutaya.android.ranking.util.Utils;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class ScrollerLinearLayout extends LinearLayout {

	private Context context;
	private Scroller scroller;
	private int scrollSizeWidth;
	private int displayWidth;

	private boolean isSearchMode = false;
	private boolean isMenuOpened = false;

	private float firstTouchX;
	private float firstTouchY;

	private final int FLIP_LENGTH = 50;

	public ScrollerLinearLayout(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public ScrollerLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	private void init() {
		this.scroller = new Scroller(context);
		DisplayMetrics metrics = Utils.getDisplayMetrics(context);
		this.displayWidth = metrics.widthPixels;
		this.scrollSizeWidth = displayWidth - (int) (getResources().getInteger(R.integer.slide_base_width) * metrics.density);
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			// Scrollerから移動位置を決定する
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			postInvalidate();
		}
	}

	public void slideAll() {
		Log.i("sidemenuSlide", "slideAll");
		scroller.startScroll(scroller.getCurrX(), scroller.getCurrY(), -displayWidth, 0, 200);
		invalidate();
		isSearchMode = true;
	}

	public void closeSidemenu() {
		Log.i("sidemenuSlide", "closeSidemnu");
		scroller.startScroll(scroller.getCurrX(), scroller.getCurrY(), -1 * scroller.getCurrX(), 0, 500);
		invalidate();
		isMenuOpened = false;
		isSearchMode = false;
	}

	public void openSidemenu() {
		Log.i("sidemenuSlide", "openSidemnu");
		if (!isSearchMode && isMenuOpened){
			return;
		}
		if (isSearchMode) {
			scroller.startScroll(scroller.getCurrX(), scroller.getCurrY(), displayWidth, 0, 500);
			isSearchMode = false;
		} else {
			scroller.startScroll(scroller.getCurrX(), scroller.getCurrY(), -scrollSizeWidth, 0, 500);
		}
		invalidate();
		isMenuOpened = true;
	}

	public boolean isMenuOpened() {
		return isMenuOpened;
	}

	public boolean isSearchMode() {
		return isSearchMode;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		int x = (int) event.getRawX();
		int y = (int) event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			firstTouchX = event.getRawX();
			firstTouchY = event.getRawY();
			break;

		case MotionEvent.ACTION_MOVE:
			// Y軸にぶれている場合は縦スクロールとして扱う
			if (Math.abs(y - firstTouchY) > FLIP_LENGTH) {
				return false;
			}
			if (x - firstTouchX > FLIP_LENGTH) { // 右フリップ
				openSidemenu();
				return true;
			} else if (firstTouchX - x > FLIP_LENGTH) { // 左フリップ
				closeSidemenu();
				return true;
			}
			break;
		}
		return false;
	}

}
