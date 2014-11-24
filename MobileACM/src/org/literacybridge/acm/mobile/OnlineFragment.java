package org.literacybridge.acm.mobile;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

public class OnlineFragment extends Fragment implements View.OnClickListener {

  ExpandableListAdapter listAdapter;
  ExpandableListView expListView;
  ConnectivityManager connMgr;
  private Tracker tracker;

  private Handler handler = new Handler();

  private Button refreshButton;
  private ProgressBar progressBar;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater
        .inflate(R.layout.fragment_online, container, false);

    refreshButton = (Button) rootView.findViewById(R.id.btnRefresh);
    refreshButton.setOnClickListener(this);
    progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarLoading);

    handler.postDelayed(runnable, 1000);

    this.tracker = EasyTracker.getInstance(getActivity());

    return rootView;

  }

  private Runnable runnable = new Runnable() {
    @Override
    public void run() {
      listAdapter.notifyDataSetChanged();

      handler.postDelayed(this, 1000);
    }
  };



  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    // Set ExpandeableListView to activity
    expListView = (ExpandableListView) getActivity().findViewById(
        R.id.expListView);

    listAdapter = new ExpandableListAdapter(getActivity());

    // setting list adapter
    expListView.setAdapter(listAdapter);

    connMgr = (ConnectivityManager) getActivity().getSystemService(
        Context.CONNECTIVITY_SERVICE);

    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

    if (networkInfo != null && networkInfo.isConnected()) {

      // Hide Refresh button since network connectivity is available
      refreshButton.setVisibility(View.GONE);

      // Load device list including states
      this.loadDeviceList();



    } else {
      Toast.makeText(getActivity().getApplicationContext(),
          "No internet connectivity!", 1).show();

      // Show Refresh button since network connectivity is available
      refreshButton.setVisibility(0);

    }
  }

  private void loadDeviceList() {

    progressBar.setVisibility(0);

	this.tracker.send(MapBuilder.createEvent("Image_clicked",
			"Image_Downloaded", "Image_Selected", null).build());

    new DownloadLibraryInfosTask().execute();
  }

  @Override
  public void onClick(View v) {
    // Toast.makeText(
    // getActivity().getApplicationContext(),"Button clicked",1).show();

    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

    if (networkInfo != null && networkInfo.isConnected()) {

      refreshButton.setVisibility(View.GONE);
      this.loadDeviceList();
    }

  }

  @Override
  public void onResume() {

      super.onResume();

      //this.tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
      this.tracker.set(Fields.SCREEN_NAME, "Download_Screen");
      this.tracker.send( MapBuilder.createAppView().build() );
  }

  private class DownloadLibraryInfosTask extends AsyncTask<Void, Void, List<ACMDatabaseInfo>> {
    /** The system calls this to perform work in a worker thread and
      * delivers it the parameters given to AsyncTask.execute() */
    @Override
    protected List<ACMDatabaseInfo> doInBackground(Void... nothing) {
      IOHandler.getInstance().refresh();
      return IOHandler.getInstance().getDatabaseInfos();
    }

    /** The system calls this to perform work in the UI thread and delivers
      * the result from doInBackground() */
    @Override
    protected void onPostExecute(final List<ACMDatabaseInfo> result) {
      listAdapter.setListData(result);

      expListView.setOnChildClickListener(new OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v,
            int groupPosition, int childPosition, long id) {

          final ACMDatabaseInfo.DeviceImage image = result.get(groupPosition)
              .getDeviceImages().get(childPosition);
          new Thread(new Runnable() {
            @Override public void run() {
              IOHandler.getInstance().store(image);
            }
          }).start();

          return false;
        }

      });

      listAdapter.notifyDataSetChanged();
      progressBar.setVisibility(View.GONE);
    }
  }
}
