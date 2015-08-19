package net.hoyoung.webmagic.pipeline;

import java.util.List;
import java.util.regex.Pattern;

import net.hoyoung.wfp.core.entity.SocialReportSyn;
import net.hoyoung.wfp.core.service.SocialReportService;
import net.hoyoung.wfp.core.service.SocialReportSynService;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class SocialReportPipeline implements Pipeline {
	private SocialReportService socialReportService;
	private SocialReportSynService socialReportSynService;
	@Override
	public void process(ResultItems resultItems, Task task) {
		if(Pattern.matches(".*zrbg/data/zrbList.aspx.*", resultItems.getRequest().getUrl())){
			List<SocialReportSyn> srsynList = resultItems.get("srsynList");
			for (SocialReportSyn socialReportSyn : srsynList) {
				socialReportSynService.add(socialReportSyn);
			}
		}
	}
	
	public SocialReportPipeline(SocialReportService socialReportService,
			SocialReportSynService socialReportSynService) {
		super();
		this.socialReportService = socialReportService;
		this.socialReportSynService = socialReportSynService;
	}

	public SocialReportService getSocialReportService() {
		return socialReportService;
	}
	public void setSocialReportService(SocialReportService socialReportService) {
		this.socialReportService = socialReportService;
	}
	public SocialReportSynService getSocialReportSynService() {
		return socialReportSynService;
	}
	public void setSocialReportSynService(
			SocialReportSynService socialReportSynService) {
		this.socialReportSynService = socialReportSynService;
	}

}
