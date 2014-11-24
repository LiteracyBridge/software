package org.literacybridge.acm.io;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

public class DiskUtils {
  private static void copy(List<Pair<File, File>> filesToCopy)
      throws IOException {
    for (Pair<File, File> file : filesToCopy) {
      copy(file.first, file.second);
    }
  }

  public static void copy(File from, File to) throws IOException {
    if (from.isDirectory()) {
      if (!to.exists()) {
        runAsRoot("mkdir -p " + to.getAbsolutePath());
      }

      File[] subFiles = from.listFiles();
      for (File f : subFiles) {
        copy(f, new File(to, f.getName()));
      }
    } else {
      if (to.exists()) {
        if (to.length() == from.length()
            && to.lastModified() == from.lastModified()) {
          // avoid copying - files are identical
          return;
        }

        runAsRoot("rm " + to.getAbsolutePath());
      }

      runAsRoot("cp " + from.getAbsolutePath() + " " + to.getAbsolutePath());
    }
  }

  public static void formatDevice(Context context, TalkingBookDevice device) throws IOException {
    device.unmount();
    runAsRoot("/system/xbin/mkfs.vfat -v " + device.getDeviceDir());
    device.mount(context);

  }

  public static File[] findConnectedDisks(Context context) throws IOException {
    File blockDevices = new File("/dev/block");
    if (!blockDevices.exists()) {
      return null;
    }

    File[] devices = blockDevices.listFiles(new FilenameFilter() {
      @Override public boolean accept(File dir, String filename) {
        return filename.toLowerCase().matches("sda\\d+");
      }
    });

    return devices;
  }

  public static File mountUSBDisk(Context context, File devicePath) throws IOException {
    File mntRootDir = new File(context.getFilesDir(), "mnt");

    File mountPoint = new File(mntRootDir, devicePath.getName());
    if (mountPoint.exists()) {
      String[] files = mountPoint.list();
      if (files != null && files.length > 0) {
        // assume it's already mounted
        return mountPoint;
      }
    } else {
      mountPoint.mkdirs();
    }

    if (runAsRoot("mount -t vfat -o rw -o nosuid " + devicePath.getAbsolutePath() + " " + mountPoint.getAbsolutePath())) {
      // mount return code 0 - assume we successfully mounted this device if mountPoint exists
      if (mountPoint.exists()) {
        return mountPoint;
      }
    }

    return null;
  }

  public static boolean checkDisk(File dir, boolean repair) throws IOException {
    String repairParam = "n";
    if (repair == true){
      repairParam = "y";
    }
    return runAsRoot("/system/bin/fsck_msdos -" + repairParam + " " + dir);
  }

  public static void unmount(File mountPoint) throws IOException {
    runAsRoot("/system/bin/umount " + mountPoint.getAbsolutePath());
  }

  public static boolean runAsRoot(String... cmds) throws IOException {
    Process p = Runtime.getRuntime().exec("su");
    DataOutputStream os = new DataOutputStream(p.getOutputStream());
    for (String tmpCmd : cmds) {
      Log.d("ROOT", tmpCmd);
      os.writeBytes(tmpCmd + "\n");
    }
    os.writeBytes("exit\n");
    os.flush();
    try {
      return p.waitFor() == 0;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException(e);
    }
  }

  static void consumeProcessOutput(Process proc, boolean listenToStdErr)
      throws IOException {
    InputStream stderr = listenToStdErr ? proc.getErrorStream() : proc
        .getInputStream();
    InputStreamReader isr = new InputStreamReader(stderr);
    BufferedReader br = new BufferedReader(isr);
    String line = null;

    while ((line = br.readLine()) != null)
      ;
  }
}
