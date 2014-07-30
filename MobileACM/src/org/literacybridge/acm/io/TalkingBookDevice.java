package org.literacybridge.acm.io;

import java.io.File;
import java.io.IOException;

import org.literacybridge.acm.io.DiskUtils.MountPoint;

import android.content.Context;
import android.util.Log;

public class TalkingBookDevice {
  private static volatile TalkingBookDevice mountedDevice;
  
  public static synchronized TalkingBookDevice getConnectedDevice(Context context) {
    if (mountedDevice == null) {
      try {
        mountedDevice = mountDevice(context);
      } catch (IOException e) {
        Log.d("TalkingBookDevice", "Exception while trying to mount TalkingBook.", e);
        return null;
      }
    }
    
    if (mountedDevice != null) {
      try {
        if (!mountedDevice.checkConnection()) {
          mountedDevice = null;
        }
      } catch (IOException e) {
        Log.d("TalkingBookDevice", "Exception while checking connection to mounted TalkingBook.", e);
        return null;
      }
    }
    
    return mountedDevice;
  }
  
  private static TalkingBookDevice mountDevice(Context context) throws IOException {
    MountPoint mountPoint = DiskUtils.mountUSBDisk(context);
    if (mountPoint != null) {
      return new TalkingBookDevice(mountPoint.devicePath, mountPoint.mountPoint);
    }
    
    return null;
  }
  
  private final File deviceDir;
  private final File mountPoint;
  
  public TalkingBookDevice(File deviceDir, File mountPoint) {
    this.deviceDir = deviceDir;
    this.mountPoint = mountPoint;
  }
  
  private boolean checkConnection() throws IOException {
    return mountPoint.exists();
  }
  
  public File getRootDir() {
    return mountPoint;
  }
  
  public File getDeviceDir() {
    return deviceDir;
  }
  
  public void unmount() throws IOException {
    DiskUtils.unmount(mountPoint);
  }
}
