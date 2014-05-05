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

@SuppressLint("NewApi")
public class MainActivity extends Activity {

  ExpandableListAdapter listAdapter;
  ExpandableListView expListView;
  List<String> listDataHeader;
  HashMap<String, List<String>> listDataChild;
  List<ACMDatabaseInfo> deviceList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("michael", "start");
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
        .permitAll().build();
    StrictMode.setThreadPolicy(policy);
    setContentView(R.layout.activity_main);

    // get the listview
    // expListView = (ExpandableListView) findViewById(R.id.lvExp);

    /*
     * listDataHeader = new ArrayList<String>();
     * listDataHeader.addAll(listDataChild.keySet());
     * 
     * 
     * listAdapter = new ExpandableListAdapter(this, listDataHeader,
     * listDataChild);
     */

    // setting list adapter
    // expListView.setAdapter(listAdapter);

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
