package jp.co.tsutaya.android.ranking.overscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

public class TopScrollView extends ScrollView {

	public interface ScrollViewListener {
		void onScrollChanged(TopScrollView scrollView, int x, int y, int oldx, int oldy);
	}

	public TopScrollView(Context context) {
		super(context);
	}

	public TopScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TopScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		// super.onSizeChanged(w, h, oldw, oldh);
		Log.v("TopScrollView", "w: " + w + "h:" + h);
	}

}