package com.media.db;

import java.io.File;

import com.media.info.Account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AccountInfoService {
	private DBHelper dbOpenHelper;

	public AccountInfoService(Context context) {
		DBHelper.init(context);
		this.dbOpenHelper = DBHelper.dbHelper();
	}

	public void save(String userId, String name, String role, String tags,
			String username, String key) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL(
				"insert into user(_id, username, name, role, tags, userId, key) values(?,?,?,?,?,?,?)",
				new Object[] { 2012, username, name, role, tags, userId, key });
		dbOpenHelper.close();
	}
	
	public void saveOrUpdate(Account account){
		if(getAccount()==null){
			save(account.getId(),account.getName(),account.getRole(),account.getTags().toString(),account.getUsername(),"");
		}else{
			setUserInfo(account.getId(),account.getName(),account.getRole(),account.getTags().toString(),account.getUsername(),"");
		}
	}

	public void setUserInfo(String userId, String name, String role,
			String tags, String username, String key) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("userId", userId);
		cv.put("username", username);
		if (key != null) {
			cv.put("key", key);
		}
		cv.put("name", name);
		cv.put("role", role);
		cv.put("tags", tags);
		db.update("user", cv, "_id=?", new String[] { "2012" });
		dbOpenHelper.close();
	}

	public String getSessionId() {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("user", new String[] { "key" }, "_id=?",
				new String[] { "2012" }, null, null, null);
		String result = null;
		if (cursor.moveToFirst()) {
			result = cursor.getString(0);
		}
		cursor.close();
		dbOpenHelper.close();
		return result;
	}

	public Account getAccount() {
		Account account = new Account();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("user", new String[] { "userId", "name",
				"role", "username" }, "_id=?", new String[] { "2012" }, null,
				null, null);
		if (cursor.moveToFirst()) {
			account.setId(cursor.getString(0));
			account.setName(cursor.getString(1));
			account.setUsername(cursor.getString(3));
			account.setRole(cursor.getString(2));
		}else{
			account = null;
		}
		cursor.close();
		dbOpenHelper.close();
		return account;
	}

	public String getTags() {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("user", new String[] { "tags" }, "_id=?",
				new String[] { "2012" }, null, null, null);
		String result = null;
		if (cursor.moveToFirst()) {
			result = cursor.getString(0);
			cursor.close();
			dbOpenHelper.close();
			return result;
		}
		return result;
	}

	public String getUserInfo() {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select username from user where _id=?",
				new String[] { "2012" });
		cursor.moveToFirst();
		String username = cursor.getString(0);
		cursor.close();
		dbOpenHelper.close();
		return username;
	}
	
	public boolean isExist(){
		boolean flag = false;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select username from user where _id=?",
				new String[] { "2012" });
		flag = cursor.moveToFirst();
		cursor.close();
		dbOpenHelper.close();
		return flag;
	}

	public void update() {

	}

	public void delete() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from user where _id=2012");
		dbOpenHelper.close();
	}

	public String getBindId(File uploadFile) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select sourceid from uploadlog where uploadfilepath=?",
				new String[] { uploadFile.getAbsolutePath() });
		String result = null;
		if (cursor.moveToFirst()) {
			result = cursor.getString(0);
			cursor.close();
			dbOpenHelper.close();
			return result;
		}
		return result;
	}

}
