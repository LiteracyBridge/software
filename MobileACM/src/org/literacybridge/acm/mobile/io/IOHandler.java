package org.literacybridge.acm.mobile.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.literacybridge.acm.mobile.ACMDatabaseInfo;

import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxInputStream;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;


public class IOHandler{
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
    
    public DropboxInputStream getInputStream(String path) throws DropboxException {
    	// setting revision to null to get latest version of the file
    	return mApi.getFileStream(path, null);
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
	            			String tbLoadersPath = ent.path + "/TB-Loaders/TB-Loaders/content";
	            			try {
		            			Entry images = mApi.metadata(tbLoadersPath, 1000, null, true, null);
		            			for (Entry image : images.contents) {
		            				long sizeInBytes = calculateSize(image.path + "/basic/messages/audio")
		            						+ calculateSize(image.path + "/basic/inbox/messages");;
		            				
		            				dbInfo.addDeviceImage(new ACMDatabaseInfo.DeviceImage(image.fileName(), image.path,
		            						sizeInBytes));
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
	
	private long calculateSize(String path) throws DropboxException {
		Log.d("size", path);
		List<Entry> files = mApi.metadata(path, 1000, null, true, null).contents;
		long size = 0;
		if (files != null) {
			for (Entry entry : files) {
				size += entry.bytes;
			}
		}
		return size;
	}
}
