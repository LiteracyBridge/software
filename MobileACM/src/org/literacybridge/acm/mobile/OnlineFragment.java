package org.literacybridge.acm.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.literacybridge.acm.mobile.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class OnlineFragment extends Fragment {

	List<ACMDatabaseInfo> deviceList;
    
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
	 
		View rootView = inflater.inflate(R.layout.fragment_online, container, false);
	    	
	    return rootView;
	    
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    
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
			listDataChild.put(deviceInf.getName(), deviceInf.getDeviceImagesNames());
			
			
		}
		        
		listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
		 
		// setting list adapter
		expListView.setAdapter(listAdapter);
	    
	}
	
	
}
