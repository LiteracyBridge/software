package org.literacybridge.acm.mobile.io;

import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

import com.dropbox.client2.DropboxAPI.DropboxInputStream;

public class LocalRepositoryCache {
	public void storeFile(DropboxInputStream in) throws IOException {
		String fileName = null;
		FileOutputStream out = openFileOutput(fileName, Context.MODE_WORLD_READABLE);

		
		byte[] buffer = new byte[1024];
		int len = in.read(buffer);
		while (len != -1) {
		    out.write(buffer, 0, len);
		    len = in.read(buffer);
		}
		
		fos.close();
	}
}
