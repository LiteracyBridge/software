package org.literacybridge.acm.mobile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;


public class IOHandler {
    private final DropboxAPI<AndroidAuthSession> mApi;
    private final List<ACMDatabaseInfo> databaseInfos;
	
    private static volatile IOHandler singleton;

    public static synchronized void init(DropboxAPI<AndroidAuthSession> mApi) {
    	singleton = new IOHandler(mApi);
    }
    
    public static synchronized IOHandler getInstance() {
    	return singleton;
    }
    
    public IOHandler(DropboxAPI<AndroidAuthSession> mApi) {
    	this.mApi = mApi;
    	this.databaseInfos = new ArrayList<ACMDatabaseInfo>();
    }
    
    public List<ACMDatabaseInfo> getDatabaseInfos() {
    	return Collections.unmodifiableList(databaseInfos);
    }
    
    public void copy(ACMDatabaseInfo.DeviceImage image) {
    	// TODO: copy
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
    
	public void refresh() {
		databaseInfos.clear();
	    try {
	        Entry dirent = mApi.metadata("/", 1000, null, true, null);
	
	        for (Entry ent: dirent.contents) {
	            if (ent.isDir) {
	            	Entry sub = mApi.metadata(ent.path, 1000, null, true, null);
	            	for (Entry ent1 : sub.contents) {
	            		if (ent1.fileName().equals("accessList.txt")) {
	            			ACMDatabaseInfo dbInfo = new ACMDatabaseInfo(ent.fileName());
	            			Log.d("michael", "ent.path=" + ent.path);
	            			String tbLoadersPath = ent.path + "/TB-Loaders/active";
	            			try {
		            			Entry images = mApi.metadata(tbLoadersPath, 1000, null, true, null);
		            			for (Entry image : images.contents) {
		            				dbInfo.addDeviceImage(new ACMDatabaseInfo.DeviceImage(image.fileName(), image.path));
		            			}
		            			databaseInfos.add(dbInfo);
	            			} catch (DropboxException e) {
	            				// ignore
	            			}
	            		}
	            	}
	            }
	        }
	    } catch (DropboxException e) {	
	    	Log.d("Dropbox", "load", e);
	    }
	}

}
