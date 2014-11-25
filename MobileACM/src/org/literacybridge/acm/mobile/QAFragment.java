package org.literacybridge.acm.mobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;


public class QAFragment extends Fragment {

	private Tracker tracker;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_qa, container, false);

    if (rootView.findViewById(R.id.fragment_container) != null) {

      // However, if we're being restored from a previous state,
      // then we don't need to do anything and should return or else
      // we could end up with overlapping fragments.
      if (savedInstanceState != null) {
        return null;
      }

      // Create a new Fragment to be placed in the activity layout
      LibraryFragment firstFragment = new LibraryFragment();

      // In case this activity was started with special instructions from an
      // Intent, pass the Intent's extras to the fragment as arguments
      firstFragment.setArguments(getActivity().getIntent().getExtras());

      // Add the fragment to the 'fragment_container' FrameLayout
      getActivity().getSupportFragmentManager().beginTransaction()
          .add(R.id.fragment_container, firstFragment).commit();

      // Report analytics
      this.tracker = EasyTracker.getInstance(getActivity());

    }

    return rootView;

  }

  @Override
  public void onResume() {

      super.onResume();

      //this.tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
      this.tracker.set(Fields.SCREEN_NAME, "Library_Screen");
      this.tracker.send( MapBuilder.createAppView().build() );
  }

}
