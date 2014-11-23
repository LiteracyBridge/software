package org.literacybridge.acm.mobile;

import java.io.IOException;

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

public class TBFragment extends Fragment {

  private TextView status;
  private Button formatButton;
  private Button mountButton;
  private Button unMountButton;

  private TalkingBookDevice mountedDevice = null;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_talkingbook, container,
        false);

    status = (TextView) rootView.findViewById(R.id.statusView);

    mountButton = (Button) rootView.findViewById(R.id.btnMount);
    mountButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        mountedDevice = TalkingBookDevice.getConnectedDevice(v.getContext());
        if (mountedDevice != null) {
          String msg = "Mounted TB device " + mountedDevice.getDeviceDir() + " at " + mountedDevice.getRootDir();
          Log.d("DEBUG", msg);
          Toast.makeText(v.getContext(), msg, Toast.LENGTH_LONG).show();
          status.setText("Connection status: TalkingBook mounted: " + mountedDevice.getDeviceDir());
        } else {
          Toast.makeText(v.getContext(), "No TalkingBook connected", Toast.LENGTH_LONG).show();
        }
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

    return rootView;

  }

}
