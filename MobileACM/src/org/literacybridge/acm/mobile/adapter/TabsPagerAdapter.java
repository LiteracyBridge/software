package org.literacybridge.acm.mobile.adapter;

import org.literacybridge.acm.mobile.OnlineFragment;
import org.literacybridge.acm.mobile.TBFragment;
import org.literacybridge.acm.mobile.LibraryFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

  public TabsPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public Fragment getItem(int index) {

    switch (index) {
    case 0:
      return new OnlineFragment();
    case 1:
      // Fragment retFrag = LibraryFragment.newInstance();
      return new LibraryFragment();
      //return new QAFragment();
    case 2:
      return new TBFragment();
    }

    return null;
  }

  @Override
  public int getCount() {
    // get item count - equal to number of tabs
    return 3;
  }
}
