package net.hoyoung.wfp.stockdown;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.hoyoung.webmagic.pipeline.DBPipeline;
import net.hoyoung.wfp.core.service.CompanyInfoService;
import net.hoyoung.wfp.core.utils.StringUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 企业基础信息爬取主类
 * @author hoyoung
 *
 */
public class CompanyInfoSpiderPageProcessor implements PageProcessor
{
	private static String[] config = { "classpath:spring-core.xml" };
	public static ApplicationContext APP_CONTEXT;
	private static String COM_BRIEF_ADDSON = "http://www.cninfo.com.cn/information/brief/";
	private static String[] STOCKLIST_URL = {
		"http://www.cninfo.com.cn/information/sz/mb/szmblclist.html" //深市主板
		,"http://www.cninfo.com.cn/information/sh/mb/shmblclist.html" //沪市主板
		,"http://www.cninfo.com.cn/information/sz/sme/szsmelclist.html" //中小企业版
	};
	static {
		APP_CONTEXT = new FileSystemXmlApplicationContext(config);
	}
    public static void main( String[] args )
    {
    	long start = System.currentTimeMillis();
        DBPipeline dBPipeline = new DBPipeline(APP_CONTEXT.getBean(CompanyInfoService.class));
        Spider.create(new CompanyInfoSpiderPageProcessor())
        .addPipeline(dBPipeline)
        .addUrl(STOCKLIST_URL[0])
        .addUrl(STOCKLIST_URL[1])
        .thread(10)
        .run();
        System.out.println("耗时:"+(System.currentTimeMillis()-start)/1000+"秒");
    }
    private int count = 0;
	@Override
	public void process(Page page) {
		//判断来源
		if(page.getRequest().getUrl().endsWith("list.html")){//股票列表
			Selectable trs = page.getHtml().xpath("/html/body/div/table/tbody/tr");
	    	for (Selectable node : trs.nodes()) {
	    		Selectable tds = node.xpath("/tr/td");
	    		for (Selectable nodetd : tds.nodes()) {
	    			String str = nodetd.xpath("/td/a/text()").get();
	    			int emptyIndex = str.indexOf(" ");
	    			String stockCode = str.substring(0,emptyIndex);
	    			String market = null;
	    			String sc = stockCode.substring(0,1);
	    			if("6".equals(sc)||"9".equals(sc)){
	    				market = "shmb";
	    			}else if("3".equals(sc)){
	    				market = "szcn";
	    			}else if("002".equals(stockCode.substring(0,3))){
	    				market = "szsme";
	    			}else{
	    				market = "szmb";
	    			}
//	    			if(count++>0){
//	    				return;
//	    			}
	    			Request request = new Request(COM_BRIEF_ADDSON+market+stockCode+".html");
	    			request.putExtra("market", market);
	    			request.putExtra("stockCode", stockCode);
	    			page.addTargetRequest(request);
//	    			System.err.println(COM_BRIEF_ADDSON+market+stockCode+".html");
	    		}
			}
			return;
		}
//		String stockCode = page.getHtml().xpath("///table[1]/tbody/tr/td/text()").get().split(" ")[0];
		page.putField("stockCode", page.getRequest().getExtra("stockCode"));
		page.putField("market", page.getRequest().getExtra("market"));
//    	System.out.println(stockSymbol);
		
    	Selectable table = page.getHtml().xpath("//table").nodes().get(1);
    	
    	String name = table.xpath("/table/tbody/tr[1]/td[2]/text()").get();
    	page.putField("name", name);
//    	System.out.println(name);
    	
    	String ename = table.xpath("/table/tbody/tr[2]/td[2]/text()").get();
    	page.putField("ename", ename);
//    	System.out.println(ename);
    	
    	String addr = table.xpath("/table/tbody/tr[3]/td[2]/text()").get();
    	page.putField("addr", addr);
//    	System.out.println(addr);
    	
    	String sname = table.xpath("/table/tbody/tr[4]/td[2]/text()").get();
    	page.putField("sname", sname);
//    	System.out.println(sname);
    	
    	String regCapitalStr = table.xpath("/table/tbody/tr[7]/td[2]/text()").get();
    	long regCapital = (long) (Float.parseFloat(regCapitalStr.replace(",", ""))*10000);
    	page.putField("regCapital", regCapital);
    	System.out.println(regCapital);
    	
    	String industry = table.xpath("/table/tbody/tr[8]/td[2]/text()").get();
    	page.putField("industry", industry);
//    	System.out.println(industry);
    	
    	String webSite = table.xpath("/table/tbody/tr[12]/td[2]/text()").get();
    	page.putField("webSite", StringUtils.clearEmptyStr(webSite));
//    	System.out.println(webSite);
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	
    	Date listingDate = null;
    	Date offerDate = null;
    	String listingDateStr = table.xpath("/table/tbody/tr[13]/td[2]/text()").get();
    	String offerDateStr = table.xpath("/table/tbody/tr[14]/td[2]/text()").get();
    	try {
			listingDate = sdf.parse(listingDateStr);
			offerDate = sdf.parse(offerDateStr);
			page.putField("listingDate", listingDate);
//			System.out.println(listingDate);
			page.putField("offerDate", offerDate);
//			System.out.println(offerDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	@Override
	public Site getSite() {
		return site;
	}
	private Site site = Site.me().setRetryTimes(5).setSleepTime(200);
}
