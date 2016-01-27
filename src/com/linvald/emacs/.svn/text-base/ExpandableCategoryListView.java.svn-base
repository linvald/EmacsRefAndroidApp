package com.linvald.emacs;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.linvald.emacs.data.Command;
import com.linvald.emacs.data.EmacsSubject;

/**
 * https://magazine-redesign08.collabnet.timeinc.net/servlets/tracking?id=MAGR1749
 * @author jlinvald8440
 *
 */
public class ExpandableCategoryListView extends ExpandableListActivity {

	private EmacsExpandableListAdapter adapter;
    private List<EmacsSubject> groups = null;
    private List<EmacsSubject> originalGroups = null;
    protected final static String LOG_CATEGORY = "EMACSAPP";
    private DataBaseHelper dbHelper = null;
    private SQLiteDatabase database = null;
    private String searchTerm = null;

    private String currentCategory = "emacs";
   
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   
        dbHelper = new DataBaseHelper(this);
        groups = new ArrayList<EmacsSubject>();
        database = dbHelper.getDatabase();
        try {
			dbHelper.createDataBase();
			dbHelper.openDataBase("emacs");
			database = dbHelper.getDatabase();
			loadDatabase();
		} catch (IOException e) {
			Log.e(LOG_CATEGORY, "Error getting database:" + e.getMessage());
			e.printStackTrace();
		}
		
        this.setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
        
        Intent intent = getIntent();
        
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        	String keyPressed = intent.getStringExtra(SearchManager.ACTION_KEY);
        	Log.d(SEARCH_SERVICE, "Key pressed:" + keyPressed);
        	String query = intent.getStringExtra(SearchManager.QUERY);
            
            doSearch(query);
        }else{
        	searchTerm = null;
        }
    }
          
    @Override
	protected void onDestroy() {
		super.onDestroy();
		dbHelper.close();
	}
    
    private void mapResultSetToGroups(Cursor cursor){
    	EmacsSubject currentSubject = null;
        Command cmd = null;
        groups = new ArrayList<EmacsSubject>();
        String subject = null;
        String command = null;
        String key = null;
        String category = null;
        while(cursor.moveToNext()){
        
        	subject = cursor.getString(0);
        	command = cursor.getString(1);
        	key = cursor.getString(2);
        	category = cursor.getString(3);
        	currentSubject = new EmacsSubject(subject);
        	currentSubject.setCategory(category);
        	//Log.d(LOG_CATEGORY, "Subject=" + subject + " command=" + command + " key=" + key);
        
        	if(groups.indexOf(currentSubject) >= 0){
        		currentSubject = groups.get(groups.indexOf(currentSubject)); 	
        	}else{
        		groups.add(currentSubject);
        	}
        	
        	cmd = new Command(); 
        	cmd.setCommand(key); 
        	cmd.setName(command);
        	
        	currentSubject.addKeyMapping(cmd);
        }
        cursor.close();
    }
    
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
    	Log.d(LOG_CATEGORY, "onChildClick");
		EmacsSubject group = groups.get(groupPosition);
		Command cmd = group.getKeyMappings().get(childPosition);
		
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        
        alertbox.setTitle(group.getSubjectName());
        alertbox.setMessage( cmd.getName() + "\n\n"+ cmd.getCommand());
        alertbox.setIcon(R.drawable.key);
        alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alertbox.show();
		
		Log.d(getPackageName(), "Info: " + cmd.getName());
		
		return true;
	}
    
    private void doSearch(String query) {
		Log.d(SEARCH_SERVICE, "Searching for Emacs Subjects");
		
		searchTerm = query;
		Cursor cursor = database.query("emacs", new String[]{"subject", "command", "key", "refcard"}, "refcard='" + currentCategory + "'" + " and command like '%" + query +"%' or command like '" + query +"%' or command like '%" + query + "' or subject  like '%" + query +"%'", null, null, null, null);
		mapResultSetToGroups(cursor);
		
		if(groups.size() == 0){
			Toast.makeText(this, "No matches",Toast.LENGTH_LONG).show();
		}else{
			
			EmacsExpandableListAdapter searchAdapter = new EmacsExpandableListAdapter(groups);
			this.adapter = searchAdapter;
	        adapter = new EmacsExpandableListAdapter(groups);
	        setListAdapter(adapter);
	        this.registerForContextMenu(getExpandableListView());
	        
	        for (int i = 0; i < searchAdapter.getGroupCount(); i++) {
	        	getExpandableListView().expandGroup(i);
			}
		}
	}
    
    private void loadDatabase(){
		Cursor cursor = database.query("emacs", new String[]{"subject", "command", "key", "refcard"},"refcard='" + currentCategory +"'" , null, null, null, null);
		mapResultSetToGroups(cursor);
		
		EmacsExpandableListAdapter searchAdapter = new EmacsExpandableListAdapter(groups);
		this.adapter = searchAdapter;
        adapter = new EmacsExpandableListAdapter(groups);
        setListAdapter(adapter);
        this.registerForContextMenu(getExpandableListView());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch(item.getItemId()){
    	case R.id.about_id:
    		showInfo();
            return true;
    	case R.id.emacs_id:
    		currentCategory = "emacs";
        	loadDatabase();
        	return true;
    	case R.id.dired:
    		currentCategory = "dired";
    		loadDatabase();
    		return true;
    	case R.id.calc:
    		currentCategory = "calc";
    		loadDatabase();
    		return true;
    	case R.id.org:
    		currentCategory = "org";
    		loadDatabase();
    		return true;
    	case R.id.viper:
    		currentCategory = "viper";
    		loadDatabase();
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }

	private void showInfo() {
		Log.d(LOG_CATEGORY, "showInfo");
		
		Cursor cursor = database.query("emacs_meta_data", new String[]{"version"}, null, null, null, null, null);
		cursor.moveToNext();
		String emacs_version = cursor.getString(0);
		
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
	        
        alertbox.setTitle("About");
        StringBuffer buf = new StringBuffer("Developed by Jesper Linvald\n");
        buf.append("Emacs version:" + emacs_version);
        
        alertbox.setMessage( buf.toString());
        alertbox.setIcon(R.drawable.key);
        alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
	        alertbox.show();
	}

	@Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();

        String title = ((TextView) info.targetView).getText().toString();

        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
            int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition); 
            Toast.makeText(this, title + ": Child " + childPos + " clicked in group " + groupPos,Toast.LENGTH_SHORT).show();

            return true;
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
            Toast.makeText(this, title + ": Group " + groupPos + " clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    /**
     * Adapter which maintains an ArrayList of emacs groups and children 
     */
    public class EmacsExpandableListAdapter extends BaseExpandableListAdapter {
     
    	List<EmacsSubject> groups = null;
    	public EmacsExpandableListAdapter(){
    		groups = originalGroups;
    	}
    	
    	public EmacsExpandableListAdapter(List<EmacsSubject> groups){
    		this.groups = groups;
    	}
    	
    	public List<EmacsSubject> getGroups(){
    		return groups;
    	}
    	
        public Object getChild(int groupPosition, int childPosition) {
        	EmacsSubject group = groups.get(groupPosition);
        	Command command = group.getKeyMappings().get(childPosition);
            return command;
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
        	EmacsSubject group = groups.get(groupPosition);
        	int count = group.getKeyMappings().size();
            return count;
        }

        public TextView getGenericView() {
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);
            TextView textView = new TextView(ExpandableCategoryListView.this);
            textView.setLayoutParams(lp);
            textView.setGravity(Gravity.BOTTOM | Gravity.LEFT);
            textView.setPadding(60, 0, 0, 0);
            return textView;
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        	TextView textView = getGenericView();
        	String txt = getChild(groupPosition, childPosition).toString();
            if(searchTerm != null){
	            String html = txt.replaceAll(searchTerm, "<i><b>" + searchTerm + "</b></i>");
	            textView.setText(Html.fromHtml(html));
            }else{
            	textView.setText(txt);
            }
            return textView;
        }

        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        public int getGroupCount() {
            return groups.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = getGenericView();
            String txt = getGroup(groupPosition).toString();
            if(searchTerm != null){
	            String html = txt.replaceAll(searchTerm, "<i><b>" + searchTerm + "</b></i>");
	            textView.setText(Html.fromHtml(html));
            }else{
            	textView.setText(txt);
            }
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }
    }
}