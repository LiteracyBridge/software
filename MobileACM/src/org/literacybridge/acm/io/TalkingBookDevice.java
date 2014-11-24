package org.literacybridge.acm.io;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

public class TalkingBookDevice {
  private static volatile TalkingBookDevice mountedDevice;

  public static synchronized TalkingBookDevice getConnectedDevice(Context context) {
    if (mountedDevice == null) {
      try {
        mountedDevice = findAndMountDevice(context);
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

  private static TalkingBookDevice findAndMountDevice(Context context) throws IOException {
    File[] connectedDevices = DiskUtils.findConnectedDisks(context);
    for (File device : connectedDevices) {
      File mountPoint = DiskUtils.mountUSBDisk(context, device);
      if (mountPoint != null) {
        return new TalkingBookDevice(device, mountPoint);
      }
    }

    return null;
  }

  private final File devicePath;
  private File mountPoint;

  public TalkingBookDevice(File devicePath, File mountPoint) {
    this.devicePath = devicePath;
    this.mountPoint = mountPoint;
  }

  private boolean checkConnection() throws IOException {
    return mountPoint.exists();
  }

  public File getRootDir() {
    return mountPoint;
  }

  public File getDeviceDir() {
    return devicePath;
  }

  public void unmount() throws IOException {
    DiskUtils.unmount(mountPoint);
  }

  public void mount(Context context) throws IOException {
    mountPoint = DiskUtils.mountUSBDisk(context, devicePath);
  }

  public void checkIntegrity(boolean repair) throws IOException {
    DiskUtils.checkDisk(devicePath, repair);
  }

  public static final class TBInfo {

  }
}
