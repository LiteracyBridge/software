package org.literacybridge.acm.mobile;

import java.util.List;
import java.util.ListIterator;

import org.literacybridge.acm.mobile.ACMDatabaseInfo.DeviceImage;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

public class OnlineFragment extends Fragment implements View.OnClickListener {

	List<ACMDatabaseInfo> deviceList;
    
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
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

			  // Refresh device list
			  //loadDeviceList(); 
			  Log.d("UI", "Refreshing device list");
			  
			  //updateDownloadingStates();
			  listAdapter.notifyDataSetChanged();
			  
		      handler.postDelayed(this, 1000);
		   }
		};
		
	private void updateDownloadingStates()
	{
        // Add image
        ImageView imgListView = (ImageView) getView()
        		.findViewById(R.id.imgListItem);
        
        // Add ProgressBar
        ProgressBar proProgress = (ProgressBar) getView()
        		.findViewById(R.id.progressBar1);
        
		deviceList = IOHandler.getInstance().getDatabaseInfos();
		
		 for (ACMDatabaseInfo dbInfo : deviceList)
		 {
			 for (DeviceImage dImg : dbInfo.getDeviceImages())
			 {
				 
				 String StateName = dImg.getStatus().name();
				
				 //Log.d("UI", "DeviceImage=" + dImg.getName() + " StateName=" + StateName);
					
				 
				 if (StateName.equals("Downloaded"))
			        {
			        	proProgress.setVisibility(View.GONE);
			        	imgListView.setVisibility(0); //To set visible
			        	imgListView.setImageResource(R.drawable.download);
			        }
			        else if(StateName.equals("Downloading"))
			        {
			        	proProgress.setVisibility(0);
			        	imgListView.setVisibility(View.GONE); 
			        }
			        else if(StateName.equals("NotDownloaded"))
			        {
			           	proProgress.setVisibility(View.GONE);
			        	imgListView.setVisibility(View.GONE); 
			        }
			        else if(StateName.equals("FailedDownload"))
			        {
			           	proProgress.setVisibility(View.GONE);
			        	imgListView.setVisibility(0);
			        	imgListView.setImageResource(R.drawable.failed);
			        	
			        }
				 
			 }
		 }
		 
		
	}
	
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
    	Log.d("UI", "Size = " + deviceList.get(0).getName());
		
    	// Set ExpandeableListView to activity
    	expListView = (ExpandableListView) getActivity().findViewById(R.id.expListView);
	
	
    	listAdapter = new ExpandableListAdapter(getActivity(), deviceList);
	 
    	
    	// setting list adapter
    	expListView.setAdapter(listAdapter);
    
    	expListView.setOnChildClickListener(new OnChildClickListener() {
    		@Override
    		public boolean onChildClick(ExpandableListView parent, View v,
    				int groupPosition, int childPosition, long id) {
            
    			ACMDatabaseInfo.DeviceImage image = 
    					deviceList.get(groupPosition).getDeviceImages().get(childPosition);
    			IOHandler.getInstance().store(getActivity().getBaseContext(), image);
                
    			
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
