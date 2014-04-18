package org.literacybridge.acm.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DeviceImageLoader {
	private static volatile DeviceImageLoader singleton;
	
	public static synchronized DeviceImageLoader getInstance() {
		if (singleton == null) {
			singleton = new DeviceImageLoader();
		}
    	return singleton;
    }
	
	public enum Result {
		SUCCESS,
		CORRUPT_MEMORY_CARD,
		FAILED
	}

	public Result imageDevice() throws IOException {
		copyStatsFromDevice();
		formatDevice();
		checkDisk();
		copyImageToDevice();
		
		return Result.SUCCESS;
	}
	
	private void copyStatsFromDevice() throws IOException {
		
	}
	
	private void formatDevice() throws IOException {
		// umount /storage/UsbDriveA  
		// mkfs.exfat -t fat32 -c 0 /dev/block/vold/8:1
		// mount -t vfat -o rw,users /dev/block/vold/8:1 /mnt/UsbDriveA
		executeCommand("./fsck.exfat -R /dev/block/vold/8:1", false);
		
	}
	
	private void checkDisk() throws IOException {
	}
	
	private void copyImageToDevice() throws IOException {
		
	}
	
	static String executeCommand(String cmd, boolean listenToStdErr) {
		StringBuilder responseBuilder = new StringBuilder();
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
	
			InputStream stderr = listenToStdErr ? proc.getErrorStream() : proc.getInputStream(); 
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
		
			while ((line = br.readLine()) != null) {
				responseBuilder.append(line);
				responseBuilder.append('\n');
			}
			
			// check for converter error
			if (!(proc.waitFor() == 0)) {
				throw new RuntimeException("Error while executing command " + cmd);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error while executing command " + cmd, e);
		}
			
		return responseBuilder.toString();
	}
	
	static void consumeProcessOutput(Process proc, boolean listenToStdErr) throws IOException {
		InputStream stderr = listenToStdErr ? proc.getErrorStream() : proc.getInputStream(); 
		InputStreamReader isr = new InputStreamReader(stderr);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
	
		while ((line = br.readLine()) != null);
	}

}
