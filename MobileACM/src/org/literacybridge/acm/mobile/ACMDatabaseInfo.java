package org.literacybridge.acm.mobile;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.util.Log;

public class ACMDatabaseInfo {
  private final String name;
  private final List<ACMDatabaseInfo.DeploymentPackage> deviceImages;

  public ACMDatabaseInfo(String name) {
    this.name = name;
    this.deviceImages = new ArrayList<ACMDatabaseInfo.DeploymentPackage>();
  }

  public String getName() {
    return name;
  }

  public void addDeviceImage(ACMDatabaseInfo.DeploymentPackage image) {
    this.deviceImages.add(image);
  }

  public List<ACMDatabaseInfo.DeploymentPackage> getDeviceImages() {
    return Collections.unmodifiableList(deviceImages);
  }

  public List<String> getDeviceImagesNames() {
    List<String> listNames = new ArrayList<String>();
    for (DeploymentPackage img : deviceImages) {
      listNames.add(img.getName());
    }

    return listNames;
  }

  public List<String> getDeviceImagesNamesStatesAndSize() {
    List<String> listNames = new ArrayList<String>();
    for (DeploymentPackage img : deviceImages) {
      listNames.add(img.getName() + "," + img.getStatus() + ","
          + img.getTotalSizeInBytes());
    }

    return listNames;
  }

  public String getTBLoadersDropBoxPath() {
    return "/" + name + "/TB-Loaders";
  }

  public static boolean isACMDatabaseFolder(String path) {
    return path.startsWith("ACM-");
  }

  @Override
  public String toString() {
    return name;
  }

  public static class DeploymentPackage {
    public static enum Status {
      Downloading, Downloaded, NotDownloaded, FailedDownload
    }

    private final ACMDatabaseInfo database;
    private final String name;
    private final File localDir;
    private final long sizeInBytes;
    private Status status;
    private List<String> communities;

    public DeploymentPackage(ACMDatabaseInfo database, String name, long sizeInBytes, File localDir) {
      this.database = database;
      this.name = name;
      this.sizeInBytes = sizeInBytes;
      this.localDir = localDir;

      // Set status to not downloaded by default
      this.status = Status.NotDownloaded;
    }

    public ACMDatabaseInfo getDatabaseInfo() {
      return database;
    }

    public String getName() {
      return name;
    }

    public File getLocalZipFile() {
      return new File(localDir, getZipFileName(name));
    }

    public String getZipFileDropBoxPath() {
      return database.getTBLoadersDropBoxPath() + getZipFileName(name);
    }

    public static String getZipFileName(String imageName) {
      return "/content-" + imageName + ".zip";
    }

    public File getLocalContentFolder() {
      return new File(localDir, "content");
    }

    public synchronized Collection<String> getCommunities() {
      if (communities != null) {
        return communities;
      }

      if (status != Status.Downloaded) {
        return null;
      }

      communities = new ArrayList<String>();
      readCommunitiesFromDisk();
      return communities;
    }

    private synchronized void readCommunitiesFromDisk() {
      File[] subDir = getLocalContentFolder().listFiles();
      File communitiesDir = new File(subDir[0], "communities");
      Log.d("communities", "dir " + communitiesDir);
      communitiesDir.listFiles(new FileFilter() {
        @Override public boolean accept(File f) {
          if (f.isDirectory()) {
            communities.add(f.getName());
            Log.d("communities", "add " + f);
          }
          return true;
        }
      });
    }

    public synchronized Status getStatus() {
      return status;
    }

    public synchronized void setStatus(Status status) {
      this.status = status;
    }

    public synchronized long getTotalSizeInBytes() {
      return sizeInBytes;
    }

    @Override
    public String toString() {
      return name;
    }
  }
}