package org.literacybridge.acm.io;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.util.Pair;

public class DiskUtils {

  private final static String TBMountDirectory = "/data/media/0/usbStorage/sda1";
  private final static String TBDevDirectory = "/dev/block/sda1";

  /**
   * Performs an "rsync", which means that this method will try to copy the
   * current state of source to the target by making the least amount of
   * IOOperations.
   * 
   * @param source
   *          The source folder on the Phone's/Tablet's local storage.
   * @param targetPath
   *          Relative path on the connected external USB device.
   */
  public static void rsync(File source, String targetPath) throws IOException {

  }

  private static void copy(List<Pair<File, File>> filesToCopy)
      throws IOException {
    for (Pair<File, File> file : filesToCopy) {
      copy(file.first, file.second);
    }
  }

  private static void copy(File from, File to) throws IOException {
    if (to.exists()) {
      throw new IOException("Target file already exists.");
    }

  }

  public static void formatDevice() throws IOException {
    runAsRoot("mkdir" + TBMountDirectory, "/system/bin/umount "
        + TBMountDirectory, "/system/xbin/mkfs.vfat -v " + TBDevDirectory,
        "/system/bin/mount -t vfat -o rw " + TBDevDirectory + " "
            + TBMountDirectory);

  }

  public static boolean checkDisk(boolean repair) throws IOException {
    // TODO: use repair flag
    return runAsRoot("/system/bin/fsck.exfat -R " + TBDevDirectory);
  }

  public static void unmount() throws IOException {

    runAsRoot("/system/bin/umount " + TBMountDirectory);

  }

  public static boolean runAsRoot(String... cmds) throws IOException {
    Process p = Runtime.getRuntime().exec("su");
    DataOutputStream os = new DataOutputStream(p.getOutputStream());
    for (String tmpCmd : cmds) {
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
