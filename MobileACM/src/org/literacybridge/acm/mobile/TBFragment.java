package org.literacybridge.acm.mobile;

import java.io.File;
import java.io.IOException;

import org.literacybridge.acm.mobile.R;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TBFragment extends Fragment {

	Button formatButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
	 
		View rootView = inflater.inflate(R.layout.fragment_talkingbook, container, false);
	         
		formatButton = (Button) rootView.findViewById(R.id.btnFormat);
		formatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


            	try {
					DeviceImageLoader.getInstance().imageDevice();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            	
            }
        });
		
		
	    return rootView;
	    
	}
	

	
}
