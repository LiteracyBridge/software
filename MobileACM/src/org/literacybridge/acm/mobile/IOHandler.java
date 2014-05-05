package org.literacybridge.acm.mobile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.literacybridge.acm.mobile.ACMDatabaseInfo.DeviceImage.Status;

import android.content.Context;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

public class IOHandler {
  private final DropboxAPI<AndroidAuthSession> mApi;
  private final List<ACMDatabaseInfo> databaseInfos;

  private static volatile IOHandler singleton;

  public static synchronized void init(DropboxAPI<AndroidAuthSession> mApi) {
    singleton = new IOHandler(mApi);
  }

  public static synchronized IOHandler getInstance() {
    return singleton;
  }

  public IOHandler(DropboxAPI<AndroidAuthSession> mApi) {
    this.mApi = mApi;
    this.databaseInfos = new ArrayList<ACMDatabaseInfo>();
  }

  public List<ACMDatabaseInfo> getDatabaseInfos() {
    return Collections.unmodifiableList(databaseInfos);
  }

  public void store(Context context, ACMDatabaseInfo.DeviceImage image) {
    final String localBasePath = "dbs/" + image.getDatabaseInfo().getName()
        + "/" + image.getName();
    File localBaseDir = new File(context.getFilesDir(), localBasePath);
    image.setStatus(Status.Downloading);

    try {
      Entry entry = mApi.metadata(image.getPath(), 1000, null, true, null);
      store(context, entry, entry.path, localBaseDir);
      image.setStatus(Status.Downloaded);
    } catch (DropboxException e) {
      image.setStatus(Status.FailedDownload);
      Log.d("download", "Failed", e);
    } catch (IOException e) {
      image.setStatus(Status.FailedDownload);
      Log.d("download", "Failed", e);
    }
  }

  private void store(Context context, Entry entry, String dropboxBasePath,
      File localBasePath) throws DropboxException, IOException {
    Log.d("michael", "store " + entry.path);

    String relativePath = entry.path.substring(dropboxBasePath.length());
    File file = new File(localBasePath, relativePath);

    if (entry.isDir) {
      Log.d("michael", "store dir " + entry.path);
      file.mkdirs();
      for (Entry ent : entry.contents) {
        Entry sub = mApi.metadata(ent.path, 1000, null, true, null);
        store(context, sub, dropboxBasePath, localBasePath);
      }
    } else {
      OutputStream out = null;

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

  public void refresh() {
    databaseInfos.clear();
    try {
      Entry dirent = mApi.metadata("/", 1000, null, true, null);

      for (Entry ent : dirent.contents) {
        if (ent.isDir) {
          Entry sub = mApi.metadata(ent.path, 1000, null, true, null);
          for (Entry ent1 : sub.contents) {
            if (ent1.fileName().equals("accessList.txt")) {
              ACMDatabaseInfo dbInfo = new ACMDatabaseInfo(ent.fileName());
              Log.d("michael", "ent.path=" + ent.path);
              String tbLoadersPath = ent.path + "/TB-Loaders/active";
              try {
                Entry images = mApi.metadata(tbLoadersPath, 1000, null, true,
                    null);
                for (Entry image : images.contents) {
                  dbInfo.addDeviceImage(new ACMDatabaseInfo.DeviceImage(dbInfo,
                      image.fileName(), image.path));
                }
                databaseInfos.add(dbInfo);
              } catch (DropboxException e) {
                // ignore
              }
            }
          }
        }
      }

    } catch (DropboxException e) {
      Log.d("Dropbox", "load", e);
    }
  }
}
