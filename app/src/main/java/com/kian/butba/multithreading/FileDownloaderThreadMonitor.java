package com.kian.butba.multithreading;

/**
 * Created by Kian Mistry on 01/03/17.
 */

public class FileDownloaderThreadMonitor {

	private boolean isRunning = true;
	private int filesDownloaded = 0;

	public boolean isRunning() {
		return isRunning;
	}

	public int getFilesDownloaded() {
		return filesDownloaded;
	}

	public void setRunning(boolean running) {
		isRunning = running;
	}

	public void incrementFilesDownloaded() {
		this.filesDownloaded++;
	}
}