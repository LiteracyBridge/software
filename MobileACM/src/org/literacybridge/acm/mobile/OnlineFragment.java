package org.literacybridge.acm.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.literacybridge.acm.mobile.R;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

public class OnlineFragment extends Fragment implements View.OnClickListener {

	List<ACMDatabaseInfo> deviceList;
    
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ConnectivityManager connMgr;

    private Handler handler = new Handler();
    	
    private Button refreshButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
	 
		View rootView = inflater.inflate(R.layout.fragment_online, container, false);
	 
		refreshButton = (Button) rootView.findViewById(R.id.btnRefresh);
		refreshButton.setOnClickListener(this);
		
	    handler.postDelayed(runnable, 1000);

	    return rootView;
	    
	}
	
	private Runnable runnable = new Runnable() {
		   @Override
		   public void run() {
		      /* do what you need to do */
		      Log.d("bruno", "Timer fired!");
		      /* and here comes the "trick" */
		      handler.postDelayed(this, 1000);
		   }
		};
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    
	     connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);


	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

	    if (networkInfo != null && networkInfo.isConnected()) {

	    	// Hide Refresh button since network connectivity is available
	    	refreshButton.setVisibility(View.GONE);
	    	
	    	// Load device list including states
	    	this.loadDeviceList();
	    	
		    } else {
		    	Toast.makeText(
    					getActivity().getApplicationContext(),"No internet connectivity!",1).show();
		    	
		    			// Show Refresh button since network connectivity is available
		    			refreshButton.setVisibility(0);
		    			
		    }
		
	}


	private void loadDeviceList()
	{
		IOHandler.getInstance().refresh();
		deviceList = IOHandler.getInstance().getDatabaseInfos();
    	Log.d("bruno", "Size = " + deviceList.get(0).getName());
		
    	// Set ExpandeableListView to activity
    	expListView = (ExpandableListView) getActivity().findViewById(R.id.expListView);
	
	
    	listDataHeader = new ArrayList<String>(); 
    	listDataChild = new HashMap<String, List<String>>();
    	for (ACMDatabaseInfo deviceInf : deviceList)
    	{
    		// Adding header
    		listDataHeader.add(deviceInf.getName());
		
    		// Adding children
    		listDataChild.put(deviceInf.getName(), deviceInf.getDeviceImagesNamesStatesAndSize());
		
		
    	}
	        
    	listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
	 
    	
    	// setting list adapter
    	expListView.setAdapter(listAdapter);
    
    	expListView.setOnChildClickListener(new OnChildClickListener() {
    		@Override
    		public boolean onChildClick(ExpandableListView parent, View v,
    				int groupPosition, int childPosition, long id) {
            
    			// Make Toast to indicate which child was clicked
    			Toast.makeText(
    					getActivity().getApplicationContext(),
    					listDataHeader.get(groupPosition)
    					+ " : "
    					+ listDataChild.get(
    							listDataHeader.get(groupPosition)).get(
    									childPosition), Toast.LENGTH_SHORT).show();
                           
    			return false;
    		}
		
	    
	});
	}
	

	@Override
	public void onClick(View v) {
		//Toast.makeText(
			//	getActivity().getApplicationContext(),"Button clicked",1).show();
		
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

	    if (networkInfo != null && networkInfo.isConnected()) {
		
	    	refreshButton.setVisibility(View.GONE);
	    	this.loadDeviceList();
	    }
		
	}
	

	    
	
	
}
