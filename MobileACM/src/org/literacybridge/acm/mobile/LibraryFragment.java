package org.literacybridge.acm.mobile;

import java.util.ArrayList;
import java.util.HashMap;

import org.literacybridge.acm.mobile.R;
import org.literacybridge.acm.mobile.adapter.ListViewLibraryAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LibraryFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
	 
		View rootView = inflater.inflate(R.layout.fragment_library, container, false);    
	    return rootView;
	    
	}
		
}

