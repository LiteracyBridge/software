package org.literacybridge.acm.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

public class DiskUtils {
  private static final int BUFFER_SIZE = 2048;

  private static void copy(List<Pair<File, File>> filesToCopy)
      throws IOException {
    for (Pair<File, File> file : filesToCopy) {
      copy(file.first, file.second);
    }
  }

  public static void copy(File from, File to) throws IOException {
    if (from.isDirectory()) {
      if (!to.exists()) {
        Sudo.sudo("mkdir -p " + to.getAbsolutePath());
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

        Sudo.sudo("rm " + to.getAbsolutePath());
      }

      Sudo.sudo("cp " + from.getAbsolutePath() + " " + to.getAbsolutePath());
    }
  }

  public static void unzip(File zipFile) throws IOException {
    BufferedOutputStream dest = null;
    FileInputStream fis = new FileInputStream(zipFile);
    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
    ZipEntry entry;
    while ((entry = zis.getNextEntry()) != null) {
      File target = new File(zipFile.getParent(), entry.getName());
      if (entry.isDirectory()) {
        Log.d("unzip", "mkdir " + entry);
        target.mkdirs();
      } else {
        Log.d("unzip", "extracting " + entry);
        int count;
        byte data[] = new byte[BUFFER_SIZE];
        // write the files to the disk
        FileOutputStream fos = new FileOutputStream(target);
        dest = new BufferedOutputStream(fos, BUFFER_SIZE);
        while ((count = zis.read(data, 0, BUFFER_SIZE)) != -1) {
          dest.write(data, 0, count);
        }
        dest.flush();
        dest.close();
      }
    }
    zis.close();
  }

  public static void formatDevice(Context context, TalkingBookDevice device) throws IOException {
    device.unmount();
    Sudo.sudo("/system/xbin/mkfs.vfat -v " + device.getDeviceDir());
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
      boolean success = mountPoint.mkdirs();
    }

    Sudo.Output output = Sudo.sudo("mount -t vfat -o rw -o nosuid "
        + devicePath.getAbsolutePath() + " " + mountPoint.getAbsolutePath());

    if (output.returnCode == 0) {
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
    return Sudo.sudo("/system/bin/fsck_msdos -" + repairParam + " " + dir).returnCode == 0;
  }

  public static void unmount(File mountPoint) throws IOException {
    Sudo.sudo("/system/bin/umount " + mountPoint.getAbsolutePath());
  }

  public static String getLabel(File device) throws IOException {
    String output = Sudo.sudo("blkid").stdout;
    String[] lines = output.split("\n");

    for (String line : lines) {
      String pattern = "^" + device.getAbsolutePath() + ": LABEL=\"([^\"]*)\".*";
      Matcher matcher = Pattern.compile(pattern).matcher(line);

      if (matcher.matches()) {
        return matcher.group(1);
      }
    }

    return null;
  }
}
