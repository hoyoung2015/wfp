package net.hoyoung.webmagic.downloader;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

public class ErrorHttpClientDownloader extends HttpClientDownloader {

	@Override
	protected void onError(Request request) {
		System.err.println("error----------------");
	}
}
