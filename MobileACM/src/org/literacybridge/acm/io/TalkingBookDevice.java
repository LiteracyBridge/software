package org.literacybridge.acm.io;

import java.io.File;
import java.io.IOException;

import android.content.Context;

public class TalkingBookDevice {
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
