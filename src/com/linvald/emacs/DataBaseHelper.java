package com.linvald.emacs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper{
	 
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.linvald.emacs/databases/";
    private static String DB_NAME_EMACS = "emacs";
    private SQLiteDatabase myDataBase; 
    private final Context myContext;

    public DataBaseHelper(Context context) {
    	super(context, DB_NAME_EMACS, null, 1);
        this.myContext = context;
    }	
 
    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() throws IOException{
    	boolean dbExist = checkDataBase("emacs");
    	if(dbExist){
    		//do nothing - database already exist
    		Log.d(ExpandableCategoryListView.LOG_CATEGORY, "Database exists");
    	}else{
    		//By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
        	Log.d(ExpandableCategoryListView.LOG_CATEGORY, "Database did not exist - creating empty");
        	try {
    			copyDataBase("emacs");
    		} catch (IOException e) {
    			Log.e(ExpandableCategoryListView.LOG_CATEGORY, e.getMessage());
        		throw new Error("Error copying database");
        	}
    	}
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(String tableName){
    	Log.d(ExpandableCategoryListView.LOG_CATEGORY, "Check Database " + tableName);
    	SQLiteDatabase checkDB = null;
    	try{
    		String myPath = DB_PATH + tableName;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    		Log.d(ExpandableCategoryListView.LOG_CATEGORY, checkDB.toString());
    	}catch(SQLiteException e){
    		Log.d(ExpandableCategoryListView.LOG_CATEGORY, "DB does NOT exist:" + e.getMessage());
    	}
 
    	if(checkDB != null){
    		checkDB.close();
    	}
    	return checkDB != null ? true : false;
    }
 
    /**
     * Copies the database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase(String tableName) throws IOException{
    	Log.d(ExpandableCategoryListView.LOG_CATEGORY, "Copying database");
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(tableName);
    	Log.d(ExpandableCategoryListView.LOG_CATEGORY, "Asset database:" + myInput.available());
    	// Path to the just created empty db
    	String outFileName = DB_PATH + tableName;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
    }
 
    public void openDataBase(String tableName){
        String myPath = DB_PATH + tableName;
        try{
        	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(Exception e){
        	Log.d(ExpandableCategoryListView.LOG_CATEGORY, e.getMessage());
        }
    }
    
    public SQLiteDatabase getDatabase(){
    	return myDataBase;
    }
    
    @Override
	public synchronized void close() {
	    if(myDataBase != null){
		    myDataBase.close();
	    }
	    super.close();
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	 	try {
			copyDataBase("emacs");
		} catch (IOException e) {
			Log.e(ExpandableCategoryListView.LOG_CATEGORY, e.getMessage());
    		throw new Error("Error copying database");
    	}
	}
}