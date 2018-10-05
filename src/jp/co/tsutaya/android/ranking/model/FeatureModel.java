package jp.co.tsutaya.android.ranking.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.TextUtils;
import android.util.Log;

public class FeatureModel implements Serializable {
	private static final long serialVersionUID = -6483702508723277840L;

	private int pageType;

	/** バナー画像URL */
	private String banner;
	/** 特集タイトル */
	private String title;
	/** 特集テキスト */
	private String comment;
	/** 遷移先URL */
	private String url;
	/** ランキングタイプ */
	private String rankingType;
	/** カテゴリーコード */
	private String categoryCd;
	/** レンタル・セールスセクションコード */
	private String rentalSalesSection;
	/** ランキングコード */
	private String rankingConcentrationCd;
	private String releaseCategoryCd;
	private String productKey;
	private String urlCd;
	/** 公開開始日 */
	private Date from;
	/** 公開終了日 */
	private Date to;

	/** 日付フォーマット */
	private final String DATE_FORMAT_STRING = "yyyy/MM/dd";
	private SimpleDateFormat DATE_FORMAT = (SimpleDateFormat) SimpleDateFormat.getDateInstance();

	public FeatureModel() {
		DATE_FORMAT.applyPattern(DATE_FORMAT_STRING);
	}

	/**
	 *
	 * @return pageType
	 */
	public int getPageType() {
		return pageType;
	}

	/**
	 * ページタイプをセットする
	 *
	 * @param pageType
	 */
	public void setPageType(String pageType) {
		this.pageType = Integer.parseInt(pageType);
	}

	/**
	 * バナー画像URLを取得する
	 * @return banner
	 */
	public String getBanner() {
		return banner;
	}

	/**
	 * @param banner
	 *            セットする banner
	 */
	public void setBanner(String banner) {
		this.banner = banner;
	}

	/**
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            セットする title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            セットする comment
	 */
	public void setComment(String text) {
		this.comment = text;
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return rankingType
	 */
	public String getRankingType() {
		return rankingType;
	}

	/**
	 * @param rankingType
	 *            セットする rankingType
	 */
	public void setRankingType(String rankingType) {
		this.rankingType = rankingType;
	}

	/**
	 *
	 * @return categoryCd
	 */
	public String getCategoryCd() {
		return categoryCd;
	}

	/**
	 * @param categoryCd
	 *            セットする categoryCd
	 */
	public void setCategoryCd(String categoryCd) {
		this.categoryCd = categoryCd;
	}

	/**
	 * @return rentalSalesSection
	 */
	public String getRentalSalesSection() {
		return rentalSalesSection;
	}

	/**
	 * @param rentalSalesSection
	 *            セットする rentalSalesSection
	 */
	public void setRentalSalesSection(String rentalSalesSection) {
		this.rentalSalesSection = rentalSalesSection;
	}

	/**
	 * @return rankingConcentrationCd
	 */
	public String getRankingConcentrationCd() {
		return rankingConcentrationCd;
	}

	/**
	 * @param rankingConcentrationCd
	 *            セットする rankingConcentrationCd
	 */
	public void setRankingConcentrationCd(String rankingConcentrationCd) {
		this.rankingConcentrationCd = rankingConcentrationCd;
	}

	/**
	 * @return releaseCategoryCd
	 */
	public String getReleaseCategoryCd() {
		return releaseCategoryCd;
	}

	/**
	 * @param releaseCategoryCd
	 *            セットする releaseCategoryCd
	 */
	public void setReleaseCategoryCd(String releaseCategoryCd) {
		this.releaseCategoryCd = releaseCategoryCd;
	}

	/**
	 * @return productKey
	 */
	public String getProductKey() {
		return productKey;
	}

	/**
	 * @param productKey
	 *            セットする productKey
	 */
	public void setProductKey(String productKey) {
		this.productKey = productKey;
	}

	/**
	 * @return urlCd
	 */
	public String getUrlCd() {
		return urlCd;
	}

	/**
	 * @param urlCd
	 *            セットする urlCd
	 */
	public void setUrlCd(String urlCd) {
		this.urlCd = urlCd;
	}

	/**
	 * @param url
	 *            セットする url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	public Date getFrom() {
		return from;
	}

	/**
	 * 公開開始日をセット
	 *
	 * @param from
	 *            公開開始日の日付文字列(yyyy/MM/dd) <br>
	 *            日付形式が間違っていた場合は指定なしとして扱う
	 */
	public void setFrom(String from) {
		if (!TextUtils.isEmpty(from)) {
			Date date = null;
			try {
				date = DATE_FORMAT.parse(from);
			} catch (Exception e) {
				Log.w("Feature", "Unparsable Date Format: " + from);
			}
			this.from = date;
		}
	}

	public Date getTo() {
		return to;
	}

	/**
	 * 公開終了日をセット
	 *
	 * @param to
	 *            公開終了日の日付文字列(yyyy/MM/dd)<br>
	 *            日付形式が間違っていた場合は指定なしとして扱う
	 */
	public void setTo(String to) {
		if (!TextUtils.isEmpty(to)) {
			Date date = null;
			try {
				date = DATE_FORMAT.parse(to);
			} catch (Exception e) {
				Log.w("Feature", "Unparsable Date Format: " + to);
			}
			this.to = date;
		}
	}
}