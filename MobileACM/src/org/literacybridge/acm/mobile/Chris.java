package org.literacybridge.acm.mobile;

import java.util.List;

public class Chris {
	public void foo() {
		IOHandler conn = IOHandler.getInstance();
		List<ACMDatabaseInfo> dbs = conn.getDatabaseInfos();
		
		for (ACMDatabaseInfo db : dbs) {
			// show name
			db.getName();			
		}
		
		// let user select db
		ACMDatabaseInfo selectedDB = null;
		
		List<ACMDatabaseInfo.DeviceImage> images = selectedDB.getDeviceImages();
		
		for (ACMDatabaseInfo.DeviceImage image : images) {
			// show second list
			image.getName();
			//...
		}
		
		// TODO: find out which image the user selected
		ACMDatabaseInfo.DeviceImage selectedImage = null;
		
		conn.copy(selectedImage);
	}
}
