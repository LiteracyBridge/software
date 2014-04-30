package org.literacybridge.acm.mobile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.literacybridge.acm.io.DiskUtils;

import android.util.Log;

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
//		copyStatsFromDevice();
//		formatDevice();
//		checkDisk();
//		formatDevice();
		
		return Result.SUCCESS;
	}
	
	private void copyStatsFromDevice() throws IOException {
		
	}
	
	private void copyImageToDevice() throws IOException {
		// First check if device is healthy
		boolean healthy = DiskUtils.checkDisk(false);

		// Not healthy? Try formatting
		if (!healthy) {
			DiskUtils.formatDevice();
			healthy = DiskUtils.checkDisk(false);
		}

		// Still not? Then try repairing with checkdisk 
		if (!healthy) {
			healthy = DiskUtils.checkDisk(true);
		}
		
		// Still not? Give up here.
		if (!healthy) {
			throw new IOException("The device appears to be corrupted.");
		}
		
		
		DiskUtils.rsync(source, targetPath);
		
//		File deviceRoot = new File("/storage/UsbDriveA");
//		File test = new File(deviceRoot, "test.txt");
//		
//		FileWriter writer = new FileWriter(test);
//		writer.append("Michael hat einen noch kleineren Penis.");
//		writer.flush();
//		writer.close();
	}
	

}
