package com.example.dynamicbuttons;
//import android.R.string;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper {
	public static final String IMG_ID = "id";
	public static final String IMG_NAME = "name";
	public static final String btncount = "count";
	public static final String IMG_PHOTO = "photo";
	public static final String TXT_VALUE = "content";
	public static final String X_POS = "posx";
	public static final String Y_POS = "posy";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String DATABASE_NAME = "ImageData.db";
	private static final int DATABASE_VERSION = 2;

	private static final String IMGBTN_TABLE = "ImageData";

	private static final String CREATE_IMGBTN_TABLE = "create table "
			+ IMGBTN_TABLE + " (" + IMG_ID
			+ " integer primary key autoincrement, " + IMG_PHOTO
			+ " text not null, " + IMG_NAME + " text not null unique, "
			+ TXT_VALUE + " text , "
			+ X_POS + " text , "
			+ Y_POS + " text , "
			+ btncount + " integer);";

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_IMGBTN_TABLE);
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + IMGBTN_TABLE);
			onCreate(db);
		}
	}

	public void Reset() {
		mDbHelper.onUpgrade(this.mDb, 1, 1);
	}

	public DBhelper(Context ctx) {
		mCtx = ctx;
		mDbHelper = new DatabaseHelper(mCtx);
	}

	public DBhelper open() throws SQLException {
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public long  insertEmpDetails(ImageData employee) {
		try {
			ContentValues cv = new ContentValues();
			cv.put(IMG_PHOTO, employee.getBitmap());
			cv.put(IMG_NAME, employee.getName());
			cv.put(btncount, employee.getCount());
			cv.put(TXT_VALUE, employee.getContent());
			cv.put(X_POS, employee.getPosx());
			cv.put(Y_POS, employee.getPosy());
			return mDb.insert(IMGBTN_TABLE, null, cv);

			
		} catch (Exception e) {
			// TODO: handle exception
			return -1;
		}
		
		
	}
	
	public void  updateEmpDetails(ImageData employee) {
		try {
			ContentValues cv = new ContentValues();
			cv.put(IMG_PHOTO, employee.getBitmap());
			cv.put(btncount, employee.getCount());
			cv.put(TXT_VALUE, employee.getContent());
			cv.put(X_POS, employee.getPosx());
			cv.put(Y_POS, employee.getPosy());
			mDb.update(IMGBTN_TABLE, cv, IMG_ID + "='" + employee.getId() + "'", null);

			
		} catch (Exception e) {
			
		}
		
		
	}
	
	public void deleteEmp(long id){
		mDb.delete(IMGBTN_TABLE, IMG_ID + "='" + id + "'", null);
	}

	public List<ImageData> retriveEmpDetails() throws SQLException {
		List<ImageData> listData = new ArrayList<ImageData>();
		Cursor cur = mDb.query(true, IMGBTN_TABLE, new String[] {IMG_ID, IMG_PHOTO,
				IMG_NAME, btncount, TXT_VALUE, X_POS, Y_POS }, null, null, null, null, null, null);
		if (cur.moveToFirst()) {
			for (int i = 0; i<cur.getCount(); i++){
				long id = cur.getLong(cur.getColumnIndex(IMG_ID));
				String filepath = cur.getString(cur.getColumnIndex(IMG_PHOTO));
				String name = cur.getString(cur.getColumnIndex(IMG_NAME));
				int count = cur.getInt(cur.getColumnIndex(btncount));
				String content = cur.getString(cur.getColumnIndex(TXT_VALUE));
				String x = cur.getString(cur.getColumnIndex(X_POS));
				String y = cur.getString(cur.getColumnIndex(Y_POS));
				ImageData data = new ImageData(filepath, name, count, content, x, y);
				data.setId(id);
				listData.add(data);
				cur.moveToNext();
			}
		}
		cur.close();
		return listData;
	}
}

