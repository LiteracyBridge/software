package org.literacybridge.acm.mobile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.literacybridge.acm.io.DiskUtils;
import org.literacybridge.acm.mobile.ACMDatabaseInfo.DeploymentPackage.Status;

import android.content.Context;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

public class IOHandler {
  private final DropboxAPI<AndroidAuthSession> mApi;
  private final Map<String, ACMDatabaseInfo> databaseInfos;
  private final File filesDir;

  private static volatile IOHandler singleton;

  public static synchronized void init(DropboxAPI<AndroidAuthSession> mApi, Context context) {
    singleton = new IOHandler(mApi, context.getFilesDir());
  }

  public static synchronized IOHandler getInstance() {
    return singleton;
  }

  private IOHandler(DropboxAPI<AndroidAuthSession> mApi, File filesDir) {
    this.mApi = mApi;
    this.databaseInfos = new HashMap<String, ACMDatabaseInfo>();
    this.filesDir = filesDir;
  }

  public List<ACMDatabaseInfo> getDatabaseInfos() {
    return new ArrayList<ACMDatabaseInfo>(databaseInfos.values());
  }

  public File getLocalDownloadPath(ACMDatabaseInfo.DeploymentPackage image) {
    return getLocalDownloadPath(image.getDatabaseInfo().getName(), image.getName());
  }

  public File getLocalDownloadPath(String databaseName, String imageName) {
    final String localBasePath = "tbloaders/" + databaseName
        + "/" + imageName;
    return new File(filesDir, localBasePath);
  }

  public long getDownloadedSizeInBytes(ACMDatabaseInfo.DeploymentPackage image) {
    return getSizeInBytes(image.getLocalZipFile());
  }

  private static long getSizeInBytes(File file) {
    if (file.isDirectory()) {
      long sum = 0;
      for (File f : file.listFiles()) {
        sum += getSizeInBytes(f);
      }
      return sum;
    }

    return file.length();
  }

  public void store(ACMDatabaseInfo.DeploymentPackage image) {
    File localBaseDir = getLocalDownloadPath(image);
    boolean success = localBaseDir.exists() || localBaseDir.mkdirs();
    if (!success) {
      image.setStatus(Status.FailedDownload);
      Log.d("download", "Failed to create directory " + localBaseDir.getAbsolutePath());
      return;
    }
    image.setStatus(Status.Downloading);

    try {
      Entry entry = mApi.metadata(image.getZipFileDropBoxPath(), 1000, null, true, null);
      store(entry, localBaseDir);
      unzipContentFile(image);
      getSuccessFile(image.getLocalZipFile()).createNewFile();
      image.setStatus(Status.Downloaded);
    } catch (DropboxException e) {
      image.setStatus(Status.FailedDownload);
      Log.d("download", "Failed", e);
    } catch (IOException e) {
      image.setStatus(Status.FailedDownload);
      Log.d("download", "Failed", e);
    }
  }

  private void unzipContentFile(ACMDatabaseInfo.DeploymentPackage image) throws IOException {
    if (!image.getLocalZipFile().exists()) {
      throw new FileNotFoundException(image.getLocalZipFile().getAbsolutePath());
    }
    DiskUtils.unzip(image.getLocalZipFile());
    if (!image.getLocalContentFolder().exists() || !image.getLocalContentFolder().isDirectory()) {
      throw new IOException("Failed to unzip content file for image " + image);
    }
  }

  private void store(Entry entry, File localDir) throws DropboxException, IOException {
    Log.d("michael", "store " + entry.path);

    File file = new File(localDir, entry.fileName());

    if (entry.isDir) {
      Log.d("michael", "store dir " + entry.path);
      file.mkdirs();
      for (Entry ent : entry.contents) {
        Entry sub = mApi.metadata(ent.path, 1000, null, true, null);
        store(sub, file);
      }
    } else {
      OutputStream out = null;
      File successFile = getSuccessFile(file);
      if (successFile.exists()) {
        successFile.delete();
      }

      try {
        out = new BufferedOutputStream(new FileOutputStream(file));
        mApi.getFile(entry.path, null, out, null);
        Log.d("michael",
            "downloaded " + entry.path + " to " + file.getAbsolutePath());
      } finally {
        if (out != null) {
          try {
            out.close();
          } catch (IOException e) {
            // ignore
          }
        }
      }
    }
  }

  private File getSuccessFile(File file) {
    return new File(file.getParent(), file.getName() + ".success");
  }

  private void refreshLocal() {
    Log.d("REFRESH", "Refreshing DB list from local disk.");

    File databaseFolder = new File(filesDir, "tbloaders");
    if (databaseFolder.exists()) {
      File[] files = databaseFolder.listFiles();
      if (files != null) {
        for (File db : files) {
          if (db.isDirectory() && ACMDatabaseInfo.isACMDatabaseFolder(db.getName())) {
            ACMDatabaseInfo dbInfo = new ACMDatabaseInfo(db.getName());
            for (File image : db.listFiles()) {
              String imageName = image.getName();
              ACMDatabaseInfo.DeploymentPackage deviceImage =
                  new ACMDatabaseInfo.DeploymentPackage(dbInfo, imageName, getSizeInBytes(image), image);
              if (getSuccessFile(new File(image, ACMDatabaseInfo.DeploymentPackage.getZipFileName(imageName))).exists()) {
                deviceImage.setStatus(Status.Downloaded);
              } else {
                deviceImage.setStatus(Status.FailedDownload);
              }
              dbInfo.addDeviceImage(deviceImage);
            }
            if (dbInfo.getDeviceImages().size() > 0 && !databaseInfos.containsKey(dbInfo.getName())) {
              databaseInfos.put(dbInfo.getName(), dbInfo);
            }
          }
        }
      }
    }
  }

  public void refresh() {
    databaseInfos.clear();

    try {
      Entry dirent = mApi.metadata("/", 1000, null, true, null);

      for (Entry ent : dirent.contents) {
        if (ent.isDir && ACMDatabaseInfo.isACMDatabaseFolder(ent.fileName())) {
          ACMDatabaseInfo dbInfo = new ACMDatabaseInfo(ent.fileName());
          Entry tbloaderDir = null;
          try {
            tbloaderDir = mApi.metadata(dbInfo.getTBLoadersDropBoxPath(), 1000, null, true, null);
          } catch (DropboxException e) {
            // skip this folder
          }
          if (tbloaderDir != null) {
            for (Entry file : tbloaderDir.contents) {
              if (file.fileName().endsWith(".rev")) {
                Log.d("Dropbox", "Found " + file.fileName());
                String imageName = file.fileName().substring(0, file.fileName().length() - 4);
                String zipFilePath = tbloaderDir.path + ACMDatabaseInfo.DeploymentPackage.getZipFileName(imageName);
                try {
                  Entry zipFile = mApi.metadata(zipFilePath, 1000, null, true, null);
                  if (zipFile != null) {
                    ACMDatabaseInfo.DeploymentPackage deviceImage =
                        new ACMDatabaseInfo.DeploymentPackage(dbInfo, imageName, zipFile.bytes,
                            getLocalDownloadPath(dbInfo.getName(), imageName));
                    File localZipFile = deviceImage.getLocalZipFile();
                    if (localZipFile.exists()) {
                      if (getSuccessFile(localZipFile).exists()) {
                        deviceImage.setStatus(Status.Downloaded);
                      } else {
                        deviceImage.setStatus(Status.FailedDownload);
                      }
                    } else {
                      deviceImage.setStatus(Status.NotDownloaded);
                    }
                    dbInfo.addDeviceImage(deviceImage);
                  }
                } catch (DropboxException e) {
                  // ignore this image
                }
              }
            }
            if (dbInfo.getDeviceImages().size() > 0) {
              databaseInfos.put(dbInfo.getName(), dbInfo);
            }
          }
        }
      }

    } catch (DropboxException e) {
      Log.d("Dropbox", "Failed to load DBs from Dropbox.", e);
    }

    refreshLocal();
  }
}
