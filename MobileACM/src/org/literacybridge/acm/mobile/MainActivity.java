package org.literacybridge.acm.mobile;

import java.util.HashMap;


import java.util.List;

import org.literacybridge.acm.mobile.dropbox.DropboxClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.widget.ExpandableListView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

  ExpandableListAdapter listAdapter;
  ExpandableListView expListView;
  List<String> listDataHeader;
  HashMap<String, List<String>> listDataChild;
  List<ACMDatabaseInfo> deviceList;

  private EasyTracker easyTracker = null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("michael", "start");
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
        .permitAll().build();
    StrictMode.setThreadPolicy(policy);
    setContentView(R.layout.activity_main);
    
    easyTracker = EasyTracker.getInstance(MainActivity.this);
   
    
    
  }

  
  @Override
  public void onStart() {
    super.onStart();

    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
  }
  
  @Override
  public void onStop() {
    super.onStop();

    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
  }
  
  
  @Override
  protected void onResume() {
    super.onResume();
    Intent intent = new Intent(this, DropboxClient.class);
    startActivity(intent);

    // try {
    // IOHandler.getInstance().refresh();
    // deviceList = IOHandler.getInstance().getDatabaseInfos();
    // } catch (Exception e) {
    // Log.d("michael", "x", e);
    // }
    //
    // Log.d("michael", "DeviceList Size=" + deviceList.size());

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

}
