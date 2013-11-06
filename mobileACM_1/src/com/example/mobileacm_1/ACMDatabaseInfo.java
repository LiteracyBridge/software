package com.example.mobileacm_1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ACMDatabaseInfo {
	private final String name;
	private final List<ACMDatabaseInfo.DeviceImage> deviceImages;
	
	public ACMDatabaseInfo(String name) {
		this.name = name;
		this.deviceImages = new ArrayList<ACMDatabaseInfo.DeviceImage>();
	}

	
	public String getName() {
		return name;
	}
	
	public void addDeviceImage(ACMDatabaseInfo.DeviceImage image) {
		this.deviceImages.add(image);
	}
	
	public List<ACMDatabaseInfo.DeviceImage> getDeviceImages() {
		return Collections.unmodifiableList(deviceImages);
	}
			
	@Override
	public String toString() {
		return name;
	}
	
	public static class DeviceImage {
		private final String name;
		private final String path;
		
		public DeviceImage(String name, String path) {
			this.name = name;
			this.path = path;
		}
		
		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return name;
		}			
	}
}