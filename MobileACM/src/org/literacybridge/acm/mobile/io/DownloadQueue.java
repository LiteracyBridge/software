package org.literacybridge.acm.mobile.io;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxInputStream;

public class DownloadQueue {
	private final BlockingQueue<DropboxAPI.Entry> queue;
	private final LocalRepositoryCache cache;
	
	public DownloadQueue(LocalRepositoryCache cache) {
		this.queue = new LinkedBlockingQueue<DropboxAPI.Entry>();
		this.cache = cache;
		
		new DownloadThread(queue, cache).start();
	}
	
	private static class DownloadThread extends Thread {
		private final BlockingQueue<DropboxAPI.Entry> queue;
		private final LocalRepositoryCache cache;
		
		private DownloadThread(BlockingQueue<DropboxAPI.Entry> queue, LocalRepositoryCache cache) {
			setDaemon(true);
			this.queue = queue;
			this.cache = cache;
		}
		
		@Override public void run() {
			try {
				while (true) {
					processDownload(queue.take());
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		
		private final void processDownload(DropboxAPI.Entry file) {
			DropboxInputStream in = null;
			
			try {
				in = IOHandler.getInstance().getInputStream(file.path);
				cache.storeFile(in);
			} catch (Exception e) {
				Log.e("DownloadQueue", "Exception in processDownload", e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						Log.e("DownloadQueue", "Exception in processDownload", e);
					}
				}
			}
		}
	}
	
}
