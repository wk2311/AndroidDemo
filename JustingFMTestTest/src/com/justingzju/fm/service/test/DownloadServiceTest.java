package com.justingzju.fm.service.test;

import java.util.ArrayList;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.justingzju.fm.service.DownloadRequest;
import com.justingzju.fm.service.DownloadService;

public class DownloadServiceTest extends ServiceTestCase<DownloadService> {

	public DownloadServiceTest() {
		super(DownloadService.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDownload1() {
		ArrayList<DownloadRequest> requests = new ArrayList<DownloadRequest>();
		requests.add(new DownloadRequest(
				"http://www.justing.com.cn:8081/free/podcastdownloadfree/73954.mp3?ut=free",
				"【双语】盗梦工厂18：能力是改革的敌人.mp3"));
		requests.add(new DownloadRequest(
				"http://www.justing.com.cn:8081/free/podcastdownloadfree/74116.mp3?ut=free",
				"姓刘的匈奴（1）.mp3"));
		Intent downloadIntent = new Intent(getContext(), DownloadService.class)
				.setAction(DownloadService.ACTION_SUBMIT_DOWNLOADS)
				.putParcelableArrayListExtra(
						DownloadService.EXTRA_DOWNLOAD_REQUEST, requests);
		startService(downloadIntent);
	}
	
	public void testDownload2() {
		ArrayList<DownloadRequest> requests = new ArrayList<DownloadRequest>();
		requests.add(new DownloadRequest(
				"http://www.justing.com.cn:8081/free/podcastdownloadfree/73976.mp3?ut=free",
				"小闲事02：蹩脚的心理医生.mp3"));
		requests.add(new DownloadRequest(
				"http://www.justing.com.cn:8081/free/podcastdownloadfree/73975.mp3?ut=free",
				"小闲事01：分享隐秘和艰难.mp3"));
		Intent downloadIntent = new Intent(getContext(), DownloadService.class)
				.setAction(DownloadService.ACTION_SUBMIT_DOWNLOADS)
				.putParcelableArrayListExtra(
						DownloadService.EXTRA_DOWNLOAD_REQUEST, requests);
		startService(downloadIntent);
	}

}
