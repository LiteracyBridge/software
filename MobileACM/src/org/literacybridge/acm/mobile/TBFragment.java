package org.literacybridge.acm.mobile;

import java.io.IOException;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

public class TBFragment extends Fragment {
	
  private Tracker tracker;
  Button formatButton;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_talkingbook, container,
        false);

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

    // Set analytics
    this.tracker = EasyTracker.getInstance(getActivity());
    
    return rootView;

  }

  
  @Override
  public void onResume() {

      super.onResume();

      //this.tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
      this.tracker.set(Fields.SCREEN_NAME, "TalkingBook_Screen");
      this.tracker.send( MapBuilder.createAppView().build() );
  }
  
}
