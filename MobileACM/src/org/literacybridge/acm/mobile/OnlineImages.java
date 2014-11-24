package org.literacybridge.acm.mobile;

import org.literacybridge.acm.mobile.adapter.TabsPagerAdapter;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class OnlineImages extends FragmentActivity implements
    ActionBar.TabListener {

  /*
   * List<ACMDatabaseInfo> deviceList;
   * 
   * ExpandableListAdapter listAdapter; ExpandableListView expListView;
   * List<String> listDataHeader; HashMap<String, List<String>> listDataChild;
   */

  private ViewPager viewPager;
  private TabsPagerAdapter mAdapter;
  private ActionBar actionBar;
  // Tab titles
  private String[] tabs = { "Downloads", "Library", "TalkingBook" };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_online_images);

    // Initialization
    viewPager = (ViewPager) findViewById(R.id.pager);
    actionBar = getActionBar();
    mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

    viewPager.setAdapter(mAdapter);
    // actionBar.setHomeButtonEnabled(false);
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    // Adding Tabs
    for (String tab_name : tabs) {
      actionBar.addTab(actionBar.newTab().setText(tab_name)
          .setTabListener(this));
    }

    viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

      @Override
      public void onPageSelected(int position) {
        // on changing the page
        // make respected tab selected
        actionBar.setSelectedNavigationItem(position);
      }

      @Override
      public void onPageScrolled(int arg0, float arg1, int arg2) {
      }

      @Override
      public void onPageScrollStateChanged(int arg0) {
      }
    });

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.online_images, menu);
    return true;
  }

  @Override
  public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
    viewPager.setCurrentItem(arg0.getPosition());

  }

  @Override
  public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
    // TODO Auto-generated method stub

  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    // action with ID action_refresh was selected
    case R.id.action_sync:
      Toast.makeText(this, "Sync selected", Toast.LENGTH_SHORT)
          .show();
      
      throw new RuntimeException("This is a crash");
            
      
    case R.id.action_debug:
    	Intent myIntent = new Intent(this, DebugActivity.class);
    	this.startActivity(myIntent);
    	break;
      
    // action with ID action_settings was selected
    case R.id.action_settings:
      Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
          .show();
      break;
    default:
      break;
    }

    return true;
  } 

}
