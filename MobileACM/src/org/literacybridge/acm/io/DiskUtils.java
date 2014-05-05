package org.literacybridge.acm.io;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.List;

import android.util.Pair;

public class DiskUtils {

  public final static String TBMountDirectory = "/data/media/0/usbStorage/sda1";
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

  public static void copy(File from, File to) throws IOException {
    if (from.isDirectory()) {
      if (!to.exists()) {
        to.mkdirs();
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
  
        to.delete();
      }
  
      if (!to.exists()) {
        to.createNewFile();
      }
  
      FileChannel source = null;
      FileChannel destination = null;
  
      try {
        source = new FileInputStream(to).getChannel();
        destination = new FileOutputStream(from).getChannel();
        destination.transferFrom(source, 0, source.size());
      } finally {
        if (source != null) {
          source.close();
        }
        if (destination != null) {
          destination.close();
        }
      }
    }
  }

  public static void formatDevice() throws IOException {
    runAsRoot("mkdir" + TBMountDirectory, "/system/bin/umount "
        + TBMountDirectory, "/system/xbin/mkfs.vfat -v " + TBDevDirectory,
        "/system/bin/mount -t vfat -o rw " + TBDevDirectory + " "
            + TBMountDirectory);

  }

  public static boolean checkDisk(boolean repair) throws IOException {
    String repairParam = "n";
    if (repair == true){
      repairParam = "y";
    }
    return runAsRoot("/system/bin/fsck_msdos -" + repairParam + " " + TBDevDirectory);
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
