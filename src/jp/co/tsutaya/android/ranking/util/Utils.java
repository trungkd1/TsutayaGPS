package jp.co.tsutaya.android.ranking.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jp.co.tsutaya.android.ranking.ClimorCouponActivity;
import jp.co.tsutaya.android.ranking.FavoriteActivity;
import jp.co.tsutaya.android.ranking.R;
import jp.co.tsutaya.android.ranking.RankingActivity;
import jp.co.tsutaya.android.ranking.RelatedLinkActivity;
import jp.co.tsutaya.android.ranking.ReleaseCalendarActivity;
import jp.co.tsutaya.android.ranking.SettingActivity;
import jp.co.tsutaya.android.ranking.SpecialActivity;
import jp.co.tsutaya.android.ranking.StockSearchActivity;
import jp.co.tsutaya.android.ranking.StoreSearchActivity;
import jp.co.tsutaya.android.ranking.TopMenuActivity;
import jp.co.tsutaya.android.ranking.TsutayaLogActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Menu;
import android.view.WindowManager;

public class Utils {

	/**
	 * メニューのリソースID
	 */
	/** ホーム */
	public static final int MENU_HOME = R.integer.menu_home;
	/** TOPページ **/
	public static final int MENU_TOP = R.integer.menu_top;
	/** 在庫検索 obsoleted. */
	public static final int MENU_STOCKSEARCH = R.integer.menu_stocksearch;
	/** 商品ページ */
	public static final int MENU_PRODUCT_DETAIL = R.integer.menu_product_detail;
	/** 作品ページ */
	public static final int MENU_WORK_DETAIL = R.integer.menu_work_detail;
	/** ランキング */
	public static final int MENU_RANKING = R.integer.menu_ranking;
	public static final int MENU_RANKING_GENRE = R.integer.menu_ranking_genre;
	public static final int MENU_RANKING_DETAIL = R.integer.menu_ranking_detail;
	// /** 発掘良品 */
	// public static final int MENU_SPECIAL = 30;
	/** リリース情報 **/
	public static final int MENU_RELEASE = R.integer.menu_release;
	public static final int MENU_RELEASE_DVD = R.integer.menu_release_dvd;
	public static final int MENU_RELEASE_CD = R.integer.menu_release_cd;
	public static final int MENU_RELEASE_GAME = R.integer.menu_release_game;
	public static final int MENU_RELEASE_COMIC = R.integer.menu_release_comic;
	/** お気に入り */
	public static final int MENU_FAVORITE = R.integer.menu_favorite;
	/** 設定 */
	public static final int MENU_SETTINGS = R.integer.menu_settings;
	/** クリモルクーポン */
	public static final int MENU_COUPON = R.integer.menu_coupon;
	/** 店舗検索 **/
	public static final int MENU_STORESEARCH = R.integer.menu_storesearch;
	/** 履歴 */
	public static final int MENU_TLOG = R.integer.menu_tlog;
	/** 関連リンク集 */
	public static final int MENU_RELATED = R.integer.menu_related;
	/** TSUTAYA AR */
	public static final int MENU_TSUTAYAAR = R.integer.menu_tsutayaar;
	/** TSUTAYA Facebook */
	public static final int MENU_FACEBOOK = R.integer.menu_facebook;

	// ページURL
	private static final String MENU_TOP_URL = "top.html";
	private static final String MENU_STOCKSEARCH_URL = "search_result.html";
	private static final String MENU_PRODUCT_DETAIL_URL = "product_detail.html";
	private static final String MENU_WORK_DETAIL_URL = "work_detail.html";
	private static final String MENU_REVIEW_URL = "review_detail.html";
	private static final String MENU_TUNELIST_URL = "tune_list.html";
	private static final String MENU_RANKING_URL = "ranking_top.html";
	private static final String MENU_RANKING_GENRE_URL = "ranking_genre_list.html";
	private static final String MENU_RANKING_DETAIL_URL = "ranking.html";
	private static final String MENU_SPECIAL_URL = "http://www.tsutaya.co.jp/movie/ms/t-hr/iphone/index_for_tsutaya_ranking.html";
	private static final String MENU_FAVORITE_URL = "favorites.html";
	private static final String MENU_SETTINGS_URL = "setting.html";
	private static final String MENU_COUPON_URL = "climorcoupon.html";
	private static final String MENU_RELEASE_URL = "http://www.tsutaya.co.jp/smartphone/ap/search/rels.html";
	private static final String MENU_RELEASE_DVD_URL = "http://www.tsutaya.co.jp/smartphone/ap/search/rels/rels_dvd.html";
	private static final String MENU_RELEASE_CD_URL = "http://www.tsutaya.co.jp/smartphone/ap/search/rels/rels_cd.html";
	private static final String MENU_RELEASE_GAME_URL = "http://www.tsutaya.co.jp/smartphone/ap/search/rels/rels_game.html";
	private static final String MENU_RELEASE_COMIC_URL = "http://www.tsutaya.co.jp/smartphone/ap/search/rels/rels_comic.html";
	private static final String MENU_STORESEARCH_URL = "store_search.html";
	private static final String MENU_STORESEARCH_RESULT_URL = "store_search_result.html";
	private static final String MENU_STORE_DETAIL_URL = "store_detail.html";
	private static final String MENU_STORE_INFO_URL = "store_info.html";
	private static final String MENU_TLOG_URL = "tsutayalogapp.html";
	private static final String MENU_RELATED_URL = "http://www.tsutaya.co.jp/smartphone/ap/search/service.html";
	

	// 機能特有メニュー（ランキングの右隣に表示）
	/** 追加 */
	public static final int MENU_ADD = 31;
	/** 更新(ランキング用) */
	public static final int MENU_RANKING_UPDATE = 32;
	/** 更新 */
	public static final int MENU_SPECIAL_UPDATE = 33;
	/** 現在地 */
	public static final int MENU_MYLOCATION = 34;
	/** 更新 */
	public static final int MENU_RELCAL_UPDATE = 35;
	public static final int MENU_TOP_UPDATE = 36;
	public static final int MENU_TLOG_UPDATE = 37;
	public static final int MENU_RELLINK_UPDATE = 38;

	/**
	 * 定数
	 */
	/** Intentのキー */
	public static final String URL = "URL";
	public static final String TOLID = "TOLID";
	public static final String COUPON = "COUPON";

	/** 在庫あり */
	public static final int STOCK_EXISTS = 2;
	/** 在庫なし */
	public static final int STOCK_NONE = 1;
	/** 取扱いなし */
	public static final int STOCK_NG = 0;
	/** 検索中 */
	public static final int STOCK_SEARCHING = -1;

	/** 在庫あり(String) */
	public static final String STOCK_EXISTS_STRING = "○";
	/** 在庫なし(String) */
	public static final String STOCK_NONE_STRING = "×";
	/** 取扱いなし(String) */
	public static final String STOCK_NG_STRING = "－";
	/** ログイン状態 */
	public static final int USER_LOGINED = 1;
	public static final int USER_IS_NOT_LOGIN = 0;
	/** 店舗おまとめ状態 */
	public static final int APP_MYFC_MERGED = 1;
	public static final int APP_MYFC_NOT_MERGED = 0;
	/** height or width **/
	public static final int DISP_HEIGHT = 0;
	public static final int DISP_WIDTH = 1;

	/** CCC,TCom関連アプリ */
	private static HashMap<String, String> APP_MAP;

	/** 文字列の区切りコード */
	public static final String DS = "__&__";

	/** 店舗在庫状態 */
	private static HashMap<String, Integer> STOCK_RESULT_MAP;
	/** クラスとMenuの対応表 */
	private static HashMap<Class<?>, Integer> MENU_MAP;

	/** RTメトリクス用のキー・バリュー対応表 */
	private static HashMap<String, String> RT_KEY_MAP;
	private static HashMap<String, String> RT_VALUE_MAP;
	public static final int RT_KEY = 0;
	public static final int RT_VALUE = 1;

	static {
		STOCK_RESULT_MAP = new HashMap<String, Integer>();
		STOCK_RESULT_MAP.put(STOCK_EXISTS_STRING, STOCK_EXISTS);
		STOCK_RESULT_MAP.put(STOCK_NONE_STRING, STOCK_NONE);
		STOCK_RESULT_MAP.put(STOCK_NG_STRING, STOCK_NG);

		MENU_MAP = new HashMap<Class<?>, Integer>();
		// MENU_MAP.put(StockSearchActivity.class, MENU_STOCKSEARCH);
		MENU_MAP.put(TopMenuActivity.class, MENU_HOME);
		MENU_MAP.put(ClimorCouponActivity.class, MENU_COUPON);
		MENU_MAP.put(FavoriteActivity.class, MENU_FAVORITE);
		MENU_MAP.put(ReleaseCalendarActivity.class, MENU_RELEASE);
		MENU_MAP.put(StoreSearchActivity.class, MENU_STORESEARCH);
		MENU_MAP.put(RankingActivity.class, MENU_RANKING);
		// MENU_MAP.put(SpecialActivity.class, MENU_SPECIAL);
		MENU_MAP.put(SettingActivity.class, MENU_SETTINGS);
		MENU_MAP.put(TsutayaLogActivity.class, MENU_TLOG);
		MENU_MAP.put(RelatedLinkActivity.class, MENU_RELATED);
		/* 以下のアプリのみ現在は起動可能とする */
		APP_MAP = new HashMap<String, String>();
		APP_MAP.put("d5a971eecbf5630ff5366e115a3a4673366e086f", "TSUTAYA AR");
		APP_MAP.put("30cd6025e0c48ad06937586eac26279b9f083d62", "TSUTAYA FREE Magazine");
		APP_MAP.put("a9c0db42e9f446559d61a5668de88ddcabcc1681", "TSUTAYA DISCAS");
		APP_MAP.put("470cc3e4f5fde0d3bfb6d66b940ad755608c5113", "T-Site");
		APP_MAP.put("c227819eff9733fe481bd190628990801568d66b", "TSUTAYA eBooks");

		RT_KEY_MAP = new HashMap<String, String>();
		RT_KEY_MAP.put(MENU_TOP_URL, "TS01");                // トップ
		RT_KEY_MAP.put(MENU_STOCKSEARCH_URL, "TS02");        // 検索結果(商品)
		RT_KEY_MAP.put(MENU_PRODUCT_DETAIL_URL, "TS19");     // 商品情報
		RT_KEY_MAP.put(MENU_WORK_DETAIL_URL, "TS20");        // 作品情報
		RT_KEY_MAP.put(MENU_REVIEW_URL, "TS26");             // レビュー
		RT_KEY_MAP.put(MENU_TUNELIST_URL, "TS27");           // 収録曲一覧
		RT_KEY_MAP.put(MENU_STORESEARCH_URL, "TS14");        // 店舗検索TOP
		RT_KEY_MAP.put(MENU_STORESEARCH_RESULT_URL, "TS15"); // 店舗検索結果
		RT_KEY_MAP.put(MENU_STORE_DETAIL_URL, "TS16");       // 店舗詳細
		RT_KEY_MAP.put(MENU_STORE_INFO_URL, "TS24");         // 店舗からのお得情報
		RT_KEY_MAP.put(MENU_FAVORITE_URL, "TS07");           // お気に入り
		RT_KEY_MAP.put(MENU_TLOG_URL, "TS16");               // 利用履歴
		RT_KEY_MAP.put(MENU_SETTINGS_URL, "TS18");           // 設定
		RT_KEY_MAP.put(MENU_COUPON_URL, "TS11");             // クーポン
		RT_KEY_MAP.put(MENU_RANKING_URL, "TS08");            // ランキングTOP

		RT_VALUE_MAP = new HashMap<String, String>();
		RT_VALUE_MAP.put(MENU_TOP_URL, "top");
		RT_VALUE_MAP.put(MENU_STOCKSEARCH_URL, "item_search");
		RT_VALUE_MAP.put(MENU_PRODUCT_DETAIL_URL, "item");
		RT_VALUE_MAP.put(MENU_WORK_DETAIL_URL, "works");
		RT_VALUE_MAP.put(MENU_REVIEW_URL, "review");
		RT_VALUE_MAP.put(MENU_TUNELIST_URL, "songlist");
		RT_VALUE_MAP.put(MENU_STORESEARCH_URL, "storelocator");
		RT_VALUE_MAP.put(MENU_STORESEARCH_RESULT_URL, "storelocator_result");
		RT_VALUE_MAP.put(MENU_STORE_DETAIL_URL, "store_detail");
		RT_VALUE_MAP.put(MENU_STORE_INFO_URL, "store_otoku");
		RT_VALUE_MAP.put(MENU_FAVORITE_URL, "favarite");
		RT_VALUE_MAP.put(MENU_TLOG_URL, "log");
		RT_VALUE_MAP.put(MENU_SETTINGS_URL, "settings");
		RT_VALUE_MAP.put(MENU_COUPON_URL, "coupon");
		RT_VALUE_MAP.put(MENU_RANKING_URL, "rank");

	}

	/**
	 * 在庫状況をINTで返します。
	 *
	 * @param stock
	 * @return
	 */
	public static int castStockInfo(String stock) {
		return STOCK_RESULT_MAP.get(stock);
	}

	/**
	 * assets領域からテンプレートとなるファイル（HTML）を読み込む
	 *
	 * @param assets
	 * @param path
	 * @return 読み込んだファイルの中身
	 */
	public static String getTemplateHtml(AssetManager assets, String path) {
		InputStream in = null;
		try {
			in = assets.open(path);

			byte[] readBytes = new byte[in.available()];
			in.read(readBytes);
			in.close();
			return new String(readBytes);

		} catch (IOException e) {
			// パスは固定値を指定するため、ファイルI/Oの失敗は基本的にはあり得ない。
			// 失敗した場合はActivity終了
			throw new RuntimeException(e);
		}
	}

	/**
	 * デフォルトオプションメニューを追加する（Map用）
	 */
	public static boolean createDefaultOptionsMenu(Menu menu, Context context, Class<?> c) {
		return createDefaultOptionsMenu(menu, context, false, c);
	}

	/**
	 * デフォルトオプションメニューを追加する（Web画面用）
	 */
	public static boolean createDefaultOptionsMenu(Menu menu, Context context, boolean homeFlag) {
		return createDefaultOptionsMenu(menu, context, homeFlag, context.getClass());
	}

	/**
	 * デフォルトオプションメニューを追加する
	 */
	public static boolean createDefaultOptionsMenu(Menu menu, Context context, boolean homeFlag, Class<?> c) {
		/**
		 * menu.add(0, MENU_STOCKSEARCH, MENU_STOCKSEARCH,
		 * context.getString(R.string.menu_stocksearch)).setIcon(
		 * R.drawable.ic_search);
		 */
		menu.add(0, MENU_TOP, 5, context.getString(R.string.menu_top)).setIcon(R.drawable.ic_menu_top);
		menu.add(0, MENU_RELEASE, 20, context.getString(R.string.menu_release)).setIcon(R.drawable.ic_menu_release);
		menu.add(0, MENU_FAVORITE, 30, context.getString(R.string.menu_favorite)).setIcon(R.drawable.ic_menu_favorite);
		menu.add(0, MENU_COUPON, 40, context.getString(R.string.menu_coupon)).setIcon(R.drawable.ic_menu_coupon);
		menu.add(0, MENU_RANKING, 50, context.getString(R.string.menu_ranking)).setIcon(R.drawable.ic_menu_rank);
		menu.add(0, MENU_STORESEARCH, 60, context.getString(R.string.menu_storesearch)).setIcon(R.drawable.ic_menu_storesearch);
		// menu.add(0, MENU_SPECIAL, 80,
		// context.getString(R.string.menu_special)).setIcon(R.drawable.ic_menu_goods);
		menu.add(0, MENU_TLOG, 85, context.getString(R.string.menu_tlog)).setIcon(R.drawable.ic_menu_rireki);
		menu.add(0, MENU_RELATED, 87, context.getString(R.string.menu_related)).setIcon(R.drawable.ic_menu_related);
		menu.add(0, MENU_SETTINGS, 90, context.getString(R.string.menu_settings)).setIcon(R.drawable.ic_menu_setting);

		// ホームボタンの出力
		if (!homeFlag) {
			Integer myMenuId = MENU_MAP.get(c);
			if (myMenuId != null) {
				menu.removeItem(myMenuId);
				if (myMenuId == MENU_TOP) { // TOPページ向けアイコン差し替え
					menu.add(0, MENU_HOME, myMenuId, context.getString(R.string.menu_top)).setIcon(R.drawable.ic_menu_top);
				} else {
					// menu.add(0, MENU_HOME, myMenuId,
					menu.add(0, MENU_HOME, 10, context.getString(R.string.menu_home)).setIcon(android.R.drawable.ic_menu_revert);
				}
			}
		}

		return true;
	}

	/**
	 * URLからメニューIDを返す
	 */
	public static int getMenuID(String url) {

		if (url == null || "".equals(url) || url.equals(MENU_TOP_URL)) {
			return MENU_TOP;
		} else if (url.equals(MENU_RANKING_URL)) {
			return MENU_RANKING;
		} else if (url.equals(MENU_FAVORITE_URL)) {
			return MENU_FAVORITE;
		} else if (url.equals(MENU_SETTINGS_URL)) {
			return MENU_SETTINGS;
		} else if (url.equals(MENU_COUPON_URL)) {
			return MENU_COUPON;
		} else if (url.equals(MENU_RELEASE_URL)) {
			return MENU_RELEASE;
		} else if (url.equals(MENU_STORESEARCH_URL)) {
			return MENU_STORESEARCH;
		} else if (url.equals(MENU_TLOG_URL)) {
			return MENU_TLOG;
		} else if (url.equals(MENU_RELATED_URL)) {
			return MENU_RELATED;
		} else {
			return MENU_HOME;
		}
	}

	/**
	 * メニューIDからURLを返す
	 */
	public static String getUrl(int menuID) {
		switch (menuID) {
		case MENU_TOP:
			return MENU_TOP_URL;
		case MENU_STOCKSEARCH:
			return MENU_STOCKSEARCH_URL;
		case MENU_STORESEARCH:
			return MENU_STORESEARCH_URL;
		case MENU_RELEASE:
			return MENU_RELEASE_URL;
		case MENU_RELEASE_DVD:
			return MENU_RELEASE_DVD_URL;
		case MENU_RELEASE_CD:
			return MENU_RELEASE_CD_URL;
		case MENU_RELEASE_GAME:
			return MENU_RELEASE_GAME_URL;
		case MENU_RELEASE_COMIC:
			return MENU_RELEASE_COMIC_URL;
		case MENU_RANKING:
			return MENU_RANKING_URL;
		case MENU_RANKING_GENRE:
			return MENU_RANKING_GENRE_URL;
		case MENU_RANKING_DETAIL:
			return MENU_RANKING_DETAIL_URL;
		case MENU_COUPON:
			return MENU_COUPON_URL;
		case MENU_FAVORITE:
			return MENU_FAVORITE_URL;
		case MENU_TLOG:
			return MENU_TLOG_URL;
		case MENU_SETTINGS:
			return MENU_SETTINGS_URL;
		case MENU_RELATED:
			return MENU_RELATED_URL;
		default:
			return null;
		}
	}

	/**
	 * 指定されたURLがHome画面のものかどうか判定します。
	 *
	 * @param url
	 * @return
	 */
	public static boolean checkHomeUrl(String url) {
		if (url == null) {
			return false;
		}
		boolean ret = false;
		ret |= url.endsWith(MENU_STOCKSEARCH_URL);
		ret |= url.endsWith(MENU_RANKING_URL);
		ret |= url.endsWith(MENU_SPECIAL_URL);
		ret |= url.endsWith(MENU_FAVORITE_URL);
		ret |= url.endsWith(MENU_SETTINGS_URL);
		ret |= url.endsWith(MENU_COUPON_URL);
		ret |= url.endsWith(MENU_STORESEARCH_URL);
		ret |= url.endsWith(MENU_RELEASE_URL);
		ret |= url.endsWith(MENU_TLOG_URL);
		ret |= url.endsWith(MENU_RELATED_URL);
		return ret;
	}

	public static int getMenuId(int menuNum, Resources res) {
		SparseIntArray array = new SparseIntArray();
		array.append(res.getInteger(MENU_HOME), MENU_HOME);
		array.append(res.getInteger(MENU_TOP), MENU_TOP);
		array.append(res.getInteger(MENU_STOCKSEARCH), MENU_STOCKSEARCH);
		array.append(res.getInteger(MENU_RANKING), MENU_RANKING);
		array.append(res.getInteger(MENU_RELEASE), MENU_RELEASE);
		array.append(res.getInteger(MENU_FAVORITE), MENU_FAVORITE);
		array.append(res.getInteger(MENU_SETTINGS), MENU_SETTINGS);
		array.append(res.getInteger(MENU_COUPON), MENU_COUPON);
		array.append(res.getInteger(MENU_STORESEARCH), MENU_STORESEARCH);
		array.append(res.getInteger(MENU_TLOG), MENU_TLOG);
		array.append(res.getInteger(MENU_RELATED), MENU_RELATED);
		array.append(res.getInteger(MENU_TSUTAYAAR), MENU_TSUTAYAAR);
		return array.get(menuNum);
	}

	/**
	 * デフォルトのメニューが選択された際の動作を定義します。
	 *
	 * @param itemId
	 * @param activity
	 * @return
	 */
	public static boolean defaultMenuItemSelected(int itemId, Activity activity) {
		return defaultMenuItemSelected(itemId, activity, activity.getClass());
	}

	/**
	 * デフォルトのメニューが選択された際の動作を定義します。
	 *
	 * @param itemId
	 * @param activity
	 * @return
	 */
	public static boolean defaultMenuItemSelected(int itemId, Activity activity, Class<?> c) {
		return defaultMenuItemSelected(itemId, activity, activity.getClass(), null);
	}

	/**
	 * デフォルトのメニューが選択された際の動作を定義します。
	 *
	 * @param itemId
	 *            メニューID
	 * @param activity
	 *            呼び出したActivityのインスタンス
	 * @return
	 */
	public static boolean defaultMenuItemSelected(int itemId, Activity activity, Class<?> c, Map<String, String> urlParams) {

		// 該当するアクティビティを起動
		// Intent intent = new Intent(this, ContentsBaseActivity.class);
		Intent intent = null;
		String goUrl = null;
		Class<?> newClass = null;
		boolean forceClearFlg = false;
		boolean clearTopFlg = false;
		boolean resetStackFlg = false;

		if (itemId == MENU_STOCKSEARCH) {
			forceClearFlg = true;
		}
		if (urlParams != null && "1".equals(urlParams.get("clearTop"))) {
			clearTopFlg = true;
			urlParams.remove("clearTop");
		}

		switch (itemId) {
		/*
		 * case Utils.MENU_STOCKSEARCH: newClass = StockSearchActivity.class;
		 * goUrl = MENU_STOCKSEARCH_URL; break;
		 */
		case Utils.MENU_TOP:
			newClass = TopMenuActivity.class;
			goUrl = MENU_TOP_URL;
			resetStackFlg = true;
			break;
		case Utils.MENU_RANKING:
			newClass = RankingActivity.class;
			goUrl = MENU_RANKING_URL;
			break;
		case Utils.MENU_RANKING_GENRE:
			newClass = RankingActivity.class;
			goUrl = MENU_RANKING_GENRE_URL;
			break;
		case Utils.MENU_RANKING_DETAIL:
			newClass = RankingActivity.class;
			goUrl = MENU_RANKING_DETAIL_URL;
			break;
		case Utils.MENU_FAVORITE:
			newClass = FavoriteActivity.class;
			goUrl = MENU_FAVORITE_URL;
			break;
		case Utils.MENU_SETTINGS:
			newClass = SettingActivity.class;
			goUrl = MENU_SETTINGS_URL;
			break;
		case Utils.MENU_STOCKSEARCH:
			newClass = StockSearchActivity.class;
			goUrl = MENU_STOCKSEARCH_URL;
			break;
		case Utils.MENU_STORESEARCH:
			newClass = StoreSearchActivity.class;
			goUrl = MENU_STORESEARCH_URL;
			break;
		case Utils.MENU_COUPON:
			newClass = ClimorCouponActivity.class;
			goUrl = MENU_COUPON_URL;
			break;
		case Utils.MENU_RELEASE:
			newClass = ReleaseCalendarActivity.class;
			goUrl = MENU_RELEASE_URL;
			break;
		case Utils.MENU_RELEASE_DVD:
			newClass = ReleaseCalendarActivity.class;
			goUrl = MENU_RELEASE_DVD_URL;
			break;
		case Utils.MENU_RELEASE_CD:
			newClass = ReleaseCalendarActivity.class;
			goUrl = MENU_RELEASE_CD_URL;
			break;
		case Utils.MENU_RELEASE_GAME:
			newClass = ReleaseCalendarActivity.class;
			goUrl = MENU_RELEASE_GAME_URL;
			break;
		case Utils.MENU_RELEASE_COMIC:
			newClass = ReleaseCalendarActivity.class;
			goUrl = MENU_RELEASE_COMIC_URL;
			break;
		case Utils.MENU_TLOG:
			newClass = TsutayaLogActivity.class;
			goUrl = MENU_TLOG_URL;
			break;
		case Utils.MENU_RELATED:
			newClass = RelatedLinkActivity.class;
			goUrl = MENU_RELATED_URL;
			break;
		case Utils.MENU_PRODUCT_DETAIL:
			newClass = StockSearchActivity.class;
			goUrl = MENU_PRODUCT_DETAIL_URL;
			break;
		case Utils.MENU_WORK_DETAIL:
			newClass = StockSearchActivity.class;
			goUrl = MENU_WORK_DETAIL_URL;
			break;
		case Utils.MENU_SPECIAL_UPDATE:
			newClass = SpecialActivity.class;
			goUrl = MENU_SPECIAL_URL;
			break;
		}
		goUrl += createGetParam(urlParams);
		Log.i("Utils", goUrl);

		// homeFlgがTrueか、または自身のクラスが遷移先クラスと一致しない場合のみActivity起動
		// = 各機能のTOP画面で現在の機能のメニューが押下された場合はstartActivityしない
		if (newClass != null && (forceClearFlg || clearTopFlg || !newClass.equals(activity.getClass()))) {
			Log.i("Utils", activity.getLocalClassName());
			intent = new Intent(activity.getApplication(), newClass);
			// アクティビティ起動
			// 起動済みクラスが指定された場合は
			// インスタンス生成せずフォワード処理のみ行う
			if (forceClearFlg) {
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				Log.i("Utils", "newIntent FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET:" + newClass.getSimpleName());
			} else if (clearTopFlg) {
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Log.i("Utils", "newIntent FLAG_ACTIVITY_CLEAR_TOP:" + newClass.getSimpleName());
			} else if (resetStackFlg){
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				Log.i("Utils", "newIntent FLAG_ACTIVITY_CLEAR_TOP:" + newClass.getSimpleName());
			} else {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Log.i("Utils", "newIntent FLAG_ACTIVITY_NEW_TASK:" + newClass.getSimpleName());
			}
			intent.putExtra(URL, goUrl);
			activity.startActivity(intent);
		} else {
			Log.w("Utils", "newIntent doesn't create");
			Log.w("Utils", "newClass is Null:" + (newClass == null));
			Log.w("Utils", "forceClearFlg:" + forceClearFlg);
			Log.w("Utils", "clearTopFlg:" + clearTopFlg);
			Log.w("Utils", "currentClass:" + activity.getClass().getSimpleName());
			Log.w("Utils", "newClass:" + ((newClass == null) ? "null" : newClass.getSimpleName()));
		}
		return true;
	}

	private static String createGetParam(Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return "";
		}
		Iterator<String> it = params.keySet().iterator();
		StringBuilder paramString = new StringBuilder("?");
		while (it.hasNext()) {
			String key = it.next();
			paramString.append(key);
			paramString.append("=");
			paramString.append(params.get(key));
			paramString.append("&");
		}
		return paramString.toString();
	}

	/**
	 * 現在日時、時刻を文字列化して返却します。
	 *
	 * @return 時刻文字列化したもの
	 */
	public static String getDateString() {
		Time time = new Time("Asia/Tokyo");
		time.setToNow();
		String date = time.format("%Y%m%d");
		return date;
	}

	/**
	 * 現在時取得
	 *
	 * @return 現在時の文字列
	 */
	public static int getCurrentHour() {
		Time time = new Time("Asia/Tokyo");
		time.setToNow();
		// String date = time.format("H");
		return time.hour;
	}

	/**
	 * インターネットの接続状態を確認します。
	 *
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null) {
			return cm.getActiveNetworkInfo().isConnected();
		}
		return false;
	}

	/**
	 * パッケージ名を指定して、該当するアプリから起動可能なIntentを取得する。
	 *
	 * @param pm
	 *            PackegeManagerのインスタンス
	 * @param appid
	 *            アプリのパッケージ名
	 * @return 指定したアプリの呼び出し用Intentを返却、見つからない場合はNULLを返却する。
	 */
	public static Intent getCallableIntent(PackageManager pm, String appid) {
		if (appid == null || pm == null)
			return null;
		Intent targetIntent = null;
		try {
			String hashstr = getDigest(appid, "SHA-1"); // 現時点では固定
			if (APP_MAP.get(hashstr) == null) {
				return null;
			}
			targetIntent = pm.getLaunchIntentForPackage(appid);
			return targetIntent;
		} catch (Exception ne) {
			return null;
		}
	}

	public static String getDigest(String idstr, String type) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance(type);
		md.update(idstr.getBytes("iso-8859-1"), 0, idstr.length());
		byte[] hashstr = md.digest();
		return toHexStr(hashstr);
	}

	/**
	 * 16進数文字列に変換
	 *
	 * @param datastring
	 * @return 引数の文字列を16進数に変換した文字列
	 */
	private static String toHexStr(byte[] datastring) {
		StringBuilder buf = new StringBuilder();
		for (byte b : datastring) {
			int hb = (b >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				buf.append((0 <= hb) && (hb <= 9) ? (char) ('0' + hb) : (char) ('a' + (hb - 10)));
				hb = b & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	/**
	 * ディスプレイ情報を返す
	 *
	 * @param cont
	 * @return
	 */
	public static DisplayMetrics getDisplayMetrics(Context cont) {
		Display display = ((WindowManager) cont.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		return displayMetrics;
	}

	/**
	 * RTメトリクス用パラメータを返す。 <br>
	 * params[0] = キー<br>
	 * params[1] = 値
	 *
	 * @param url
	 *            取得するページのキーURL
	 * @return キーと値の配列（不明なURLの場合はNULL）
	 */
	public static String[] getRTMetricsParams(String url) {
		if (url == null || "".equals(url)) {
			return null;
		}
		String fileName = url;
		if (url.indexOf("?") > 0) {
			fileName = url.substring(0, url.indexOf("?"));
		}
		if (!RT_VALUE_MAP.containsKey(fileName)) {
			Log.w("RTMetrics", "Undefined fileName:" + fileName);
			return null;
		}
		String[] params = new String[2];
		params[RT_KEY] = RT_KEY_MAP.get(fileName);
		params[RT_VALUE] = RT_VALUE_MAP.get(fileName);
		return params;
	}

	/**
	 * GETパラメータをMAPにして返す
	 *
	 * @param url
	 *             putExtraでセットしたパラメータ付URL
	 * @return GETパラメータのキー・バリューのMap
	 */
	public static Map<String, String> getUrlParams(String url) {
		if (!url.contains("?")) {
			return null;
		}
		String getString = url.substring(url.indexOf("?") + 1);
		String[] keyValues = getString.split("&");
		Map<String, String> params = new HashMap<String, String>(keyValues.length);
		for (int i = 0; i < keyValues.length; i++) {
			int splitCount = keyValues[i].indexOf("=");
			if (splitCount < 0) {
				continue;
			}
			params.put(keyValues[i].substring(0, splitCount), keyValues[i].substring(splitCount + 1));
		}

		return params;
	}
	
	public static String BANNER_URL = "";
	public static String TOP_POPUP_URL = "";
	public static String BANNER_ONCLICK_URL = "";
    /** Check build mode is release or debug
     * @param ctx
     * @return build mode
     */
    public static boolean isDebuggable(Context ctx) {
        boolean debuggable = false;
        PackageManager pm = ctx.getPackageManager();
        try {
            ApplicationInfo appinfo = pm.getApplicationInfo(
                    ctx.getPackageName(), 0);
            debuggable = (0 != (appinfo.flags &= ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (NameNotFoundException e) {
            /* debuggable variable will remain false */
        }
        return debuggable;
    }

    public static void init(Context ctx)
    {
        if (!isDebuggable(ctx))
        {
        	BANNER_URL = ctx.getResources().getString(R.string.banner_url_release);
        	TOP_POPUP_URL = ctx.getResources().getString(R.string.top_popup_url_release);
        } else
        {
        	BANNER_URL = ctx.getResources().getString(R.string.banner_url_debug);
        	
        	TOP_POPUP_URL = ctx.getResources().getString(R.string.top_popup_url_debug);
        }
        BANNER_ONCLICK_URL = ctx.getResources().getString(R.string.banner_onclick_url_release);
    }

}
