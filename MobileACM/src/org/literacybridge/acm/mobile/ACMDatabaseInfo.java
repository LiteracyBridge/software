package org.literacybridge.acm.mobile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ACMDatabaseInfo {
  private final String name;
  private final List<ACMDatabaseInfo.DeviceImage> deviceImages;

  public ACMDatabaseInfo(String name) {
    this.name = name;
    this.deviceImages = new ArrayList<ACMDatabaseInfo.DeviceImage>();
  }

  public String getName() {
    return name;
  }

  public void addDeviceImage(ACMDatabaseInfo.DeviceImage image) {
    this.deviceImages.add(image);
  }

  public List<ACMDatabaseInfo.DeviceImage> getDeviceImages() {
    return Collections.unmodifiableList(deviceImages);
  }

  public List<String> getDeviceImagesNames() {

    List<String> listNames = new ArrayList<String>();
    for (DeviceImage img : deviceImages) {
      listNames.add(img.getName());
    }

    return listNames;

  }

  public List<String> getDeviceImagesNamesStatesAndSize() {

    List<String> listNames = new ArrayList<String>();
    for (DeviceImage img : deviceImages) {
      listNames.add(img.getName() + "," + img.getStatus() + ","
          + img.getSizeInBytes());
    }

    return listNames;

  }

  @Override
  public String toString() {
    return name;
  }

  public static class DeviceImage {
    public static enum Status {
      Downloading, Downloaded, NotDownloaded, FailedDownload
    }

    private final ACMDatabaseInfo database;
    private final String name;
    private final String path;
    private long sizeInBytes;
    private Status status;

    public DeviceImage(ACMDatabaseInfo database, String name, String path, long sizeInBytes) {
      this.database = database;
      this.name = name;
      this.path = path;
      this.sizeInBytes = sizeInBytes;

      // Set status to not available by default
      this.status = Status.NotDownloaded;
    }

    public ACMDatabaseInfo getDatabaseInfo() {
      return database;
    }

    public String getName() {
      return name;
    }

    public String getPath() {
      return path;
    }

    public synchronized Status getStatus() {
      return status;
    }

    public synchronized void setStatus(Status status) {
      this.status = status;
    }

    public synchronized long getSizeInBytes() {
      return sizeInBytes;
    }

    @Override
    public String toString() {
      return name;
    }
  }
}