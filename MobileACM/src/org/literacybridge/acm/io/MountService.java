package org.literacybridge.acm.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class MountService {
  private static final long REFRESH_INTERVAL = 3000;

  private static MountService instance;

  public static synchronized void start(Activity activity) {
    if (instance == null) {
      instance = new MountService(activity);
      instance.startWatcher();
    }
  }

  private final Activity activity;
  private final Map<String, Device> connectedDevices;

  private MountService(Activity activity) {
    this.activity = activity;
    this.connectedDevices = new HashMap<String, MountService.Device>();
  }

  private void startWatcher() {
    new Watcher().start();
  }

  private final class Watcher extends Thread {
    private Watcher() {
      setDaemon(true);
    }

    @Override public void run() {
      while (true) {
        Log.d("MountService", "Main loop start.");
        try {
          Set<String> oldConnectedSet = new HashSet<String>(connectedDevices.keySet());
          Set<String> newConnectedSet = findConnectedTalkingBooks();
          if (newConnectedSet != null) {
            // first find devices that were disconnected since last round
            for (String device : oldConnectedSet) {
              if (!newConnectedSet.contains(device)) {
                showToast("Disconnected TalkingBook " + device);
                connectedDevices.remove(device);
              }
            }

            for (String device : newConnectedSet) {
              if (!oldConnectedSet.contains(device)) {
                File devicePath = new File(device);
                Log.d("MountService", "Found connected TalkingBook: " + device);
                final String label = DiskUtils.getLabel(devicePath);
                final File mntPoint = DiskUtils.mountUSBDisk(activity.getApplicationContext(), devicePath);
                Device deviceInfo = new Device(devicePath, true, mntPoint, label);
                connectedDevices.put(device, deviceInfo);
                showToast("Mounted " + deviceInfo);
              }
            }
          } else {
            for (String device : oldConnectedSet) {
              showToast("Disconnected TalkingBook " + device);
              connectedDevices.remove(device);
            }
          }
        } catch (Exception e) {
          Log.e("MountService", "Exception while looking for connected devices.", e);
        }
        try {
          sleep(REFRESH_INTERVAL);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }

  private Set<String> findConnectedTalkingBooks() throws IOException {
    // For each connected usb device there will be a file with a number as name
    // in /proc/scsi/usb-storage
    // We'll first try to find one that is a TalkingBook and remember its file number
    File[] files = Sudo.ls(new File("/proc/scsi/usb-storage"));
    if (files == null) {
      return null;
    }

    Set<String> ids = new HashSet<String>();
    for (File f : files) {
      String content = Sudo.cat(f);
      // Looking for this content:
      //     Host scsi4: usb-storage
      //     Vendor: GENERAL
      //     Product: GENERALPLUS-MSDC
      //     Serial Number: 123456789ABCDEFG
      //     Protocol: Transparent SCSI
      //     Transport: Bulk
      //     Quirks: IGNORE_RESIDUE
      if (content.contains("GENERALPLUS-MSDC")) {
        // found TB device
        ids.add(f.getName());
      }
    }

    if (ids.isEmpty()) {
      // no TB connected
      return null;
    }

    Set<String> talkingBooks = new HashSet<String>();

    // we don't need sudo to access files in /sys
    File[] blockDevices = new File("/sys/class/block").listFiles();
    for (File f : blockDevices) {
      // getCanonicalPath() returns the target of the symlink
      String symlinkPath = f.getCanonicalPath();
      // There will be symlinks like
      //      sda1 -> ../../devices/platform/tegra-ehci.0/usb2/2-1/2-1:1.0/host4/target4:0:0/4:0:0:0/block/sda/sda1
      // The number after 'target' corresponds to the file number in /proc/scsi/usb-storage that we found above
      for (String id : ids) {
        if (Pattern.compile(".*target" + id + ".*block/.*/.*").matcher(symlinkPath).matches()) {
          talkingBooks.add("/dev/block/" + f.getName());
        }
      }
    };

    return !talkingBooks.isEmpty() ? talkingBooks : null;
  }

  private void showToast(final String msg) {
    activity.runOnUiThread(new Runnable() {
      public void run() {
          Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
      }
    });
  }

  public static class Device {
    private File devicePath;

    private boolean isMounted;
    private File mntPoint;
    private String label;

    private Device(File devicePath, boolean isMounted, File mntPoint,
        String label) {
      this.devicePath = devicePath;
      this.isMounted = isMounted;
      this.mntPoint = mntPoint;
      this.label = label;
    }

    @Override public String toString() {
      return "TalkingBook[Device=" + devicePath + ", Label=" + label
          + ", Mountpoint=" + mntPoint + ", Mounted=" + isMounted;
    }
  }
}
