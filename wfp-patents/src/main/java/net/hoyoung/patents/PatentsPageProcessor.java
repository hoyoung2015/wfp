package net.hoyoung.patents;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.hoyoung.webmagic.downloader.htmlunit.HtmlUnitDownloader;
import net.hoyoung.wfp.core.service.CompanyPatentsService;

import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class PatentsPageProcessor implements PageProcessor{
	private static String[] config = { "classpath:spring-core.xml" };
	public static ApplicationContext APP_CONTEXT;
	
	
	private static String QUERY = "专利—专利权人:(\"武汉钢铁(集团)公司\") * Date:-2015";
	private static String URL_PATTERN = "http://librarian.wanfangdata.com.cn/Patent.aspx?dbhit=&q=[query]&db=patent&p=[page]";
	static {
		APP_CONTEXT = new FileSystemXmlApplicationContext(config);
		
		try {
			QUERY = URLEncoder.encode(QUERY,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		URL_PATTERN = URL_PATTERN.replace("[query]", QUERY);
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		long start = System.currentTimeMillis();
		int page = 1;
		String url = URL_PATTERN.replace("[page]", page+"");
		CompanyPatentsService companyPatentsService = APP_CONTEXT.getBean(CompanyPatentsService.class);
		
		
		Spider spider = Spider.create(new PatentsPageProcessor())
				.addPipeline(new PatentsPipeline(companyPatentsService))
				.setDownloader(new HtmlUnitDownloader())
				.addUrl(url);
		spider.thread(8).run();
		
		System.out.println("耗时>>>"+(System.currentTimeMillis()-start)/1000+"秒");
	}

	public void process(Page page) {
		
		
		
		Selectable pageLink = page.getUrl().regex("^http://d.g.wanfangdata.com.cn/Patent_CN.*.aspx$");
		if(pageLink.nodes().size()==0){//当前页是分页列表，提取详情链接和分页
			System.err.println("---------------------->>>列表页处理");
			//判断是否有下一页
			if(page.getHtml().xpath("//p[@class=pager_space]").get().contains("下一页")){
				String pageStr = this.getPage(page.getUrl().get());
				//加入下一页
				if(pageStr != null){
					page.addTargetRequest(URL_PATTERN.replace("[page]", (Integer.parseInt(pageStr)+1)+""));
				}
			}
			//详情链接
			page.addTargetRequests(this.getDetailUrl(page.getHtml()));
		}else{//当前页是详情
			System.err.println("---------------------->>>详情页处理");
			
			Html html = page.getHtml();
			
			String patInfo =  html.xpath("//div[@class=abstracts]/text()").get();
			page.putField("patInfo", patInfo);
//			System.err.println(patInfo);
			
			Selectable tbody = html.xpath("//table[@id=perildical2_dl]/tbody");
			
			String patName =  html.xpath("//h1/text()").get();
			page.putField("patName", patName);
//			System.err.println(patName);
			String comName =  tbody.xpath("/tbody/tr[8]/td/text()").get();
			page.putField("comName", comName);
//			System.err.println(comName);
			String patCode =  tbody.xpath("/tbody/tr[2]/td/text()").get();
			page.putField("patCode", patCode);
//			System.err.println(patCode);
			
			String patMainStdmode =  tbody.xpath("/tbody/tr[6]/td/text()").get();
			page.putField("patMainStdmode", patMainStdmode);
//			System.err.println(patMainStdmode);
			
			String patStdmode =  tbody.xpath("/tbody/tr[7]/td/text()").get();
			page.putField("patStdmode", patStdmode);
//			System.err.println(patStdmode);
			
			page.putField("source", page.getUrl().get());
		}
	}
	private Site site = Site.me().setRetryTimes(5).setSleepTime(200);
	public Site getSite() {
		return site;
	}
	private String getPage(String url){
		String s ="^http://librarian.wanfangdata.com.cn/Patent.aspx\\?dbhit=.*(&p=([0-9]+))$";
		
		Pattern p = Pattern.compile(s);
		Matcher m = p.matcher(url);
		if(m.find()){
			return m.group(2);
		}
		return null;
	}
	private List<String> getDetailUrl(Html html){
		List<Selectable> list = html.xpath("//ul[@class=list_ul]").nodes();
		List<String> urls = new ArrayList<String>();
		for(int i=0;i<list.size();i+=2){
			String a = list.get(i).xpath("/ul/li[1]/a[2]/@href").get();
			urls.add(a);
		}
		return urls;
	}
}
