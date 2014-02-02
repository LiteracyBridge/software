package org.literacybridge.acm.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class CopyOfOnlineImages extends Activity {
	
	List<ACMDatabaseInfo> deviceList;
    
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("bruno", "OnlineImages Activity talks to you!");
		setContentView(R.layout.activity_online_images);
		
		deviceList = IOHandler.getInstance().getDatabaseInfos();
		
		Log.d("bruno", "Size = " + deviceList.get(0).getName());
		
		
		// Set ExpandeableListView to activity
		expListView = (ExpandableListView) findViewById(R.id.expListView);
		
		
		listDataHeader = new ArrayList<String>(); 
		listDataChild = new HashMap<String, List<String>>();
		for (ACMDatabaseInfo deviceInf : deviceList)
		{
			// Adding header
			listDataHeader.add(deviceInf.getName());
			
			// Adding children
			listDataChild.put(deviceInf.getName(), deviceInf.getDeviceImagesNames());
		}
		        
		listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
		 
		// setting list adapter
		expListView.setAdapter(listAdapter);
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.online_images, menu);
		return true;
	}

}
