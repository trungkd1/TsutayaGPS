package jp.co.tsutaya.android.ranking.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteのヘルパークラス。 現状StoreDataManagerでしか使用しないが今後の事を考えpublicクラスとしておく。
 * 
 * @author i_suyama
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "tsutaya_ranking_db";
	private static final int DATABASE_VERSION = 1;

	private static final String CREATE_STORE_DATA_TABLE = "create table STORE_DATA ("
			+ "    fcid text primary key,"
			+ "    fnam text,"
			+ "    tel1 text,"
			+ "    tel2 text,"
			+ "    tel3 text,"
			+ "    map_n integer," + "    map_e integer" + ")";

	private static final String CREATE_STORE_UPDATE_TABLE = "create table STORE_DATA_UPDATE ("
			+ "    last_update_date text primary key" + ")";

	private static final String CREATE_STOCK_LOCATION_TABLE = "create table STOCK_LOCATION_TABLE ("
			+ "    lat text, lon text, zoom_level text" + ")";

	private static final String CREATE_REGISTERED_STORE_TABLE = "create table if not exists REGISTERED_STORE ("
			+ "    fcid text primary key" + ")";

	private static final String DROP_STORE_DATA_TABLE = "drop table if exists STORE_DATA";

	private static final String DROP_STORE_DATA_UPDATE_TABLE = "drop table if exists STORE_DATA_UPDATE";

	private static final String DROP_STOCK_LOCATION_TABLE = "drop table if exists STOCK_LOCATION_TABLE";

	private static final String DROP_REGISTERED_STORE_TABLE = "drop table if exists REGISTERED_STORE";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_STORE_DATA_TABLE);
		db.execSQL(CREATE_STORE_UPDATE_TABLE);
		db.execSQL(CREATE_STOCK_LOCATION_TABLE);
		createRegisteredStoreTable(db);
	}

	public void createRegisteredStoreTable(SQLiteDatabase db){
		db.execSQL(CREATE_REGISTERED_STORE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		db.execSQL(DROP_STORE_DATA_TABLE);
		db.execSQL(DROP_STORE_DATA_UPDATE_TABLE);
		db.execSQL(DROP_STOCK_LOCATION_TABLE);
		db.execSQL(DROP_REGISTERED_STORE_TABLE);
		onCreate(db);
	}
}
