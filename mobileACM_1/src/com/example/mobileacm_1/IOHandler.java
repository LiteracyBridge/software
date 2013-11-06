package com.example.mobileacm_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

/*
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
*/

public class IOHandler {
    //private DropboxAPI<AndroidAuthSession> mApi;
	
    private static volatile IOHandler singleton;
    
    public static synchronized IOHandler getInstance() {
    	if (singleton == null) {
    		singleton = new IOHandler();
    	}
    	
    	return singleton;
    }
    
    public HashMap<String, List<String>> getDatabaseInfoMap()
    {
    	HashMap<String, List<String>> returnMap = new HashMap<String, List<String>>();
    	
    	List<String> db1_list = new ArrayList<String>();
    	db1_list.add("Contains not much data.");
    	db1_list.add("Really not much");
    	db1_list.add("Nothing to see here!");
    	
    	List<String> db2_list = new ArrayList<String>();
    	db2_list.add("Useful data");
    	db2_list.add("Very reusable");
    	db2_list.add("Valuable datasets");
    	
    	List<String> db3_list = new ArrayList<String>();
    	db3_list.add("Average data stack");
    	db3_list.add("Some good - some bad");
    	db3_list.add("Overall average!");
    	
    	
    	returnMap.put("Crappy data", db1_list);
    	returnMap.put("A+ data", db2_list);
    	returnMap.put("Standard stuff", db3_list);
    	
    	return returnMap;
    	
    }
    
    public ArrayList<ACMDatabaseInfo> getDatabaseInfos() {
    	
    	// Create dummy dbs
    	ACMDatabaseInfo db1 = new ACMDatabaseInfo("db1");
    	ACMDatabaseInfo db2 = new ACMDatabaseInfo("db2");
    	ACMDatabaseInfo db3 = new ACMDatabaseInfo("db3");
    	
    	ACMDatabaseInfo.DeviceImage dvImg1 = new ACMDatabaseInfo.DeviceImage("device image 1", "path1");
    	ACMDatabaseInfo.DeviceImage dvImg2 = new ACMDatabaseInfo.DeviceImage("device image 2", "path2");
    	ACMDatabaseInfo.DeviceImage dvImg3 = new ACMDatabaseInfo.DeviceImage("device image 3", "path3");
    	ACMDatabaseInfo.DeviceImage dvImg4 = new ACMDatabaseInfo.DeviceImage("device image 4", "path4");
    	ACMDatabaseInfo.DeviceImage dvImg5 = new ACMDatabaseInfo.DeviceImage("device image 5", "path5");
    	ACMDatabaseInfo.DeviceImage dvImg6 = new ACMDatabaseInfo.DeviceImage("device image 6", "path6");
    	ACMDatabaseInfo.DeviceImage dvImg7 = new ACMDatabaseInfo.DeviceImage("device image 7", "path7");
    	ACMDatabaseInfo.DeviceImage dvImg8 = new ACMDatabaseInfo.DeviceImage("device image 8", "path8");
    	ACMDatabaseInfo.DeviceImage dvImg9 = new ACMDatabaseInfo.DeviceImage("device image 9", "path9");
    	
    	db1.addDeviceImage(dvImg1);
    	db1.addDeviceImage(dvImg2);
    	db1.addDeviceImage(dvImg3);
    	db2.addDeviceImage(dvImg4);
    	db2.addDeviceImage(dvImg5);
    	db2.addDeviceImage(dvImg6);
    	db3.addDeviceImage(dvImg7);
    	db3.addDeviceImage(dvImg8);
    	db3.addDeviceImage(dvImg9);
    	
    	ArrayList<ACMDatabaseInfo> allDBs = new ArrayList<ACMDatabaseInfo>();
    	allDBs.add(db1);
    	allDBs.add(db2);
    	allDBs.add(db3);
    	
    	return allDBs;
    }
    
    public void copy(ACMDatabaseInfo.DeviceImage image) {
    	// TODO: copy
    }
    
    /*
	public void foo() {
	    try {
	        Entry dirent = mApi.metadata("/", 1000, null, true, null);
	
	        for (Entry ent: dirent.contents) {
	            if (ent.isDir) {
	            	Entry sub = mApi.metadata(ent.path, 1000, null, true, null);
	            	for (Entry ent1 : sub.contents) {
	            		if (ent1.fileName().equals("accessList.txt")) {
	            			
	            		}
	            	}
	            }
	        }
	    } catch (DropboxException e) {
	    	Log.d("Dropbox", "load", e);
	    }
	}
  */
}
