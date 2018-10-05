package jp.co.tsutaya.android.ranking.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class HttpUtils {
	/**
	 * HTTP通信で、画像データを取得する
	 *
	 * @param url
	 *            画像ファイルのURL
	 * @return 画像データのビットマップオブジェクト
	 */
	public static Bitmap getImage(String url) {
		try {
			byte[] byteArray = getByteArrayFromURL(url);
			return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 *
	 * HTTP通信で、文章データを取得する
	 *
	 * @param url
	 *            テキストファイルのURL
	 * @param encoding
	 *            エンコードの指定
	 * @return テキストのString
	 * @throws UnsupportedEncodingException
	 */
	public static String getAscii(String url, String encoding) throws Exception {
		return new String(getByteArrayFromURL(url), encoding);
	}

	public static String getAscii(String url) throws Exception {
		return getAscii(url, "UTF-8");
	}

	/**
	 * 指定したURLから、バイナリコードを取得する。<br>
	 * 外部サーバにある画像やデータなどを取得するための通信基盤
	 *
	 * @param strUrl
	 *            ファイルのURL
	 * @return ファイルのbyte配列
	 */
	public static byte[] getByteArrayFromURL(String strUrl) throws Exception {
		byte[] byteArray = new byte[1024];
		byte[] result = null;
		HttpURLConnection con = null;
		InputStream in = null;
		ByteArrayOutputStream out = null;
		int size = 0;
		try {
			URL url = new URL(strUrl);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			in = con.getInputStream();

			out = new ByteArrayOutputStream();
			while ((size = in.read(byteArray)) != -1) {
				out.write(byteArray, 0, size);
			}
			result = out.toByteArray();
		} finally {
			if (con != null)
				con.disconnect();
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
		return result;
	}
}