package org.literacybridge.acm.mobile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.literacybridge.acm.io.DiskUtils;
import org.literacybridge.acm.io.MountService;
import org.literacybridge.acm.io.Sudo;
import org.literacybridge.acm.io.TalkingBookDevice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

public class TBFragment extends Fragment {

  private TextView status;
  private Button formatButton;
  private Button mountButton;
  private Button unMountButton;

  private TalkingBookDevice mountedDevice = null;
  private Tracker tracker;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_talkingbook, container,
        false);

    status = (TextView) rootView.findViewById(R.id.statusView);

    mountButton = (Button) rootView.findViewById(R.id.btnMount);
    mountButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
//        Toast.makeText(v.getContext(), "Connected devices: " + MountService.connectedDevices, Toast.LENGTH_SHORT).show();
//        List<File> tbs = MountService.connectedDevices;
//
//        if (tbs != null && !tbs.isEmpty()) {
//          try {
//            File device = tbs.get(0);
//            String label = DiskUtils.getLabel(device);
//            File mntPoint = DiskUtils.mountUSBDisk(v.getContext(), device);
//            Toast.makeText(v.getContext(), "Mounted '" + label + "' at " + mntPoint, Toast.LENGTH_LONG).show();
//            Sudo.sudo("echo '" + System.currentTimeMillis() + "' >> " + mntPoint.getAbsolutePath() + "/mtbl.test");
//            Toast.makeText(v.getContext(), Sudo.cat(new File(mntPoint, "mtbl.test")), Toast.LENGTH_LONG).show();
//          } catch (IOException e) {
//            Toast.makeText(v.getContext(), "Exception while mounting " + e, Toast.LENGTH_LONG).show();
//          }
//        }
      }
    });

    unMountButton = (Button) rootView.findViewById(R.id.btnUnmount);
    unMountButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if (mountedDevice == null) {
          Toast.makeText(v.getContext(), "No TalkingBook mounted", Toast.LENGTH_LONG).show();
          return;
        }

        try {
          mountedDevice.unmount();
        } catch (IOException e) {
          Log.e("DEBUG", "Exception while unmounting TalkingBook", e);
        }
        String msg = "Unmounted TB device " + mountedDevice.getDeviceDir() + " at " + mountedDevice.getRootDir();
        Log.d("DEBUG", msg);
        Toast.makeText(v.getContext(), msg, Toast.LENGTH_LONG).show();
        mountedDevice = null;
        status.setText("Connection status: No TalkingBook mounted");
      }
    });

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
