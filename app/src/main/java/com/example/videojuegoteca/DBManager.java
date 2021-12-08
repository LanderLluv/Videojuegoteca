package com.example.videojuegoteca;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {

    public static final String DB_NAME = "Videojuegoteca";
    public static final int DB_VERSION = 6;

    public static final String TABLE_USER = "user";
    public static final String USER_COL_LOGIN = "_id";
    public static final String USER_COL_PASSWD = "passwd";

    public static final String TABLE_GAME = "game";
    public static final String GAME_COL_NAME = "_id";
    public static final String GAME_COL_PLATFORM = "platform";
    public static final String GAME_COL_COMPLETED = "completed";
    public static final String GAME_COL_FAVOURITE = "favourite";
    public static final String GAME_COL_LOGIN = "login";

    public DBManager(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DBManager",
                "Creating DB" + DB_NAME + " v" + DB_VERSION);

        try{
            db.beginTransaction();
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USER + "( "
                    + USER_COL_LOGIN + " string(255) PRIMARY KEY NOT NULL, "
                    + USER_COL_PASSWD + " string(255) NOT NULL)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_GAME + "( "
                    + GAME_COL_NAME + " string(255) NOT NULL, "
                    + GAME_COL_PLATFORM + " string(255) NOT NULL, "
                    + GAME_COL_COMPLETED + " int DEFAULT 0, "
                    + GAME_COL_FAVOURITE + " int DEFAULT 0,"
                    + GAME_COL_LOGIN + " string(255) NOT NULL,"
                    + "PRIMARY KEY (" + GAME_COL_NAME + " ," + GAME_COL_LOGIN + "))");
            db.setTransactionSuccessful();
        }catch(SQLException exc){
            Log.e("DBManager.onCreate", exc.getMessage());
        }finally{
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("DBManager",
                "DB: " +  DB_NAME + ": v " +  oldVersion + " -> v" + newVersion);

        try{
            db.beginTransaction();
            db.execSQL("DROP TABLE IF EXISTS "+  TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);
            db.setTransactionSuccessful();
        }catch(SQLException exc){
            Log.e("DBManager.onUpgrade", exc.getMessage());
        }finally{
            db.endTransaction();
        }

        this.onCreate(db);
    }

    public boolean addGame(String name, String platform, int completed, int favourite, String login){
        Cursor cursor = null;
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(GAME_COL_NAME, name);
        values.put(GAME_COL_PLATFORM, platform);
        values.put(GAME_COL_COMPLETED, completed);
        values.put(GAME_COL_FAVOURITE, favourite);
        values.put(GAME_COL_LOGIN, login);

        try{
            db.beginTransaction();
            cursor = db.query(TABLE_GAME,
                    null,
                    GAME_COL_NAME + "=? AND " +  GAME_COL_LOGIN + "=?",
                    new String[]{name, login},
                    null, null, null, null);
            if(cursor.getCount() > 0){
                db.update(TABLE_GAME,
                        values, GAME_COL_NAME + "=? AND " + GAME_COL_LOGIN + "=?", new String[]{name, login});
            }else{
                db.insert(TABLE_GAME, null, values);
            }

            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){
            Log.e("DBManager.addGame", exc.getMessage());
        }finally{
            if(cursor != null) {
                cursor.close();
            }
            db.endTransaction();
        }
        return toret;
    }

    public boolean deleteGame(String id, String login){
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();
            db.delete(TABLE_GAME, GAME_COL_NAME + "=? AND " +  GAME_COL_LOGIN + "=?", new String[]{id, login});
            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){
            Log.e("DBManager.deleteGame", exc.getMessage());
        }finally {
            db.endTransaction();
        }

        return toret;
    }

    public Cursor getGamesPlatform(String platform, String login){
        return this.getReadableDatabase().query(TABLE_GAME,
                null, GAME_COL_PLATFORM + "=? AND " + GAME_COL_LOGIN + "=?", new String[]{platform, login}, null, null, null);
    }

    public Cursor getGames(String login){
        return this.getReadableDatabase().query(TABLE_GAME,
                null, GAME_COL_LOGIN + "=?", new String[]{login}, null, null, null);
    }

    public boolean addUser(String login, String passwd){
        Cursor cursor = null;
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(USER_COL_LOGIN, login);
        values.put(USER_COL_PASSWD, passwd);

        try{
            db.beginTransaction();
            cursor = db.query(TABLE_USER,
                    null,
                    USER_COL_LOGIN + "=?",
                    new String[]{login},
                    null, null, null, null);
            if(cursor.getCount() == 0){
                //En caso de que el usuario no exista
                db.insert(TABLE_USER, null, values);
                db.setTransactionSuccessful();
                toret = true;
            }

        }catch(SQLException exc){
            Log.e("DBManager.addUser", exc.getMessage());
        }finally{
            if(cursor != null){
                cursor.close();
            }
            db.endTransaction();
        }

        return toret;
    }

    public boolean checkLogin(String login, String passwd){
        Cursor cursor = null;
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();

            cursor = db.query(TABLE_USER,
                    null,
                    USER_COL_LOGIN + "=? AND " + USER_COL_PASSWD + "=?",
                    new String[]{login, passwd},
                    null, null, null, null);
            if(cursor.getCount() > 0){
                toret = true;
            }

            db.setTransactionSuccessful();
        }catch(SQLException exc){
            Log.e("DBManager.checkLogin", exc.getMessage());
        }finally{
            if(cursor != null){
                cursor.close();
            }
            db.endTransaction();
        }

        return toret;
    }
}
