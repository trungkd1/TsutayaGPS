package jp.co.tsutaya.android.ranking.slide;

import java.util.ArrayList;

import jp.co.tsutaya.android.ranking.ContentsBaseActivity;
import jp.co.tsutaya.android.ranking.R;
import jp.co.tsutaya.android.ranking.util.Utils;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {
	private ArrayList<String> items;
	private LayoutInflater inflater;
	private ContentsBaseActivity activity;
	private int selectedMenuID;

	private static final int POS_TITLE = 0;
	private static final int POS_FILENAME = 1;
	private static final int POS_FILENAME_CURRENT = 2;
	private static final int POS_MENUID = 3;

	public MenuAdapter(Context context, ArrayList<String> items, ContentsBaseActivity act) {
		this.items = items;
		this.inflater = LayoutInflater.from(context);
		this.activity = act;
		this.selectedMenuID = Utils.getMenuID(act.getIntent().getExtras().getString(Utils.URL));
		Log.i("MenuAdapter", "initURL: " + act.getIntent().getExtras().getString(Utils.URL));
		Log.i("MenuAdapter", "selectedMenuID: " + selectedMenuID);
		Log.i("MenuAdapter", "Activity: " + act.getLocalClassName());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String info = getItem(position);
		String title = parseTitle(info);
		int menuID = parseMenuID(info);
		convertView = inflater.inflate(R.layout.sidemenu_item, null);

		// 文言の設定
		TextView text = (TextView) convertView.findViewById(R.id.menu_title);
		text.setText(title);
		text.setTextColor(Color.GRAY);

		// アイコンの設定
		ImageView icon = (ImageView) convertView.findViewById(R.id.menu_icon);
		icon.setImageResource(parseFileName(info));
		icon.setContentDescription(parseTitle(info));

		if (menuID == selectedMenuID) {
			convertView.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.bg_sidemenu_selected));
			icon.setImageResource(parseFileNameCurrent(info));
			text.setTextColor(Color.WHITE);
		} else {
			convertView.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.bg_sidemenu));
			icon.setImageResource(parseFileName(info));
			text.setTextColor(Color.GRAY);
		}

		convertView.setId(menuID);
		return convertView;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public String getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private String parseTitle(String info) {
		return this.activity.getString(Integer.parseInt(parseValue(info, POS_TITLE)));
	}

	private int parseFileName(String info) {
		return Integer.parseInt(parseValue(info, POS_FILENAME));
	}

	private int parseFileNameCurrent(String info) {
		return Integer.parseInt(parseValue(info, POS_FILENAME_CURRENT));
	}

	private int parseMenuID(String info) {
		return Integer.parseInt(parseValue(info, POS_MENUID));
	}

	private String parseValue(String info, int position) {
		String[] values = info.split(Utils.DS);
		return values[position];
	}

}