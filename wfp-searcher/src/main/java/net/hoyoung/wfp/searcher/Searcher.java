package net.hoyoung.wfp.searcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.entity.NewItem;
import net.hoyoung.wfp.searcher.savehandler.SaveHandler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.selector.Html;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
@Component
public class Searcher {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private CompanyInfo companyInfo;
	private static String BAIDU_NEWS_URL = "http://news.baidu.com/advanced_news.html";
	private Set<String> keywords;
	private WebClient webClient;
	private List<NewItem> newItemList;
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	private SaveHandler saveHandler;
	@Autowired
	private HtmlDownloader htmlDownloader;
	public Searcher() {
		super();
		keywords = new TreeSet<String>();
		newItemList = new ArrayList<NewItem>();
	}
	public Searcher addKeywords(List<String> keys){
		for (String key : keys) {
			keywords.add(key);
		}
		return this;
	}
	public Searcher addKeyword(String key){
		keywords.add(key);
		return this;
	}
	
	public void setCompanyInfo(CompanyInfo companyInfo) {
		this.companyInfo = companyInfo;
	}
	public void run(){
		String query = getQurey();
		if(query==null){
			logger.warn("关键词是空的");
			System.exit(0);
		}
		logger.info("搜索关键字："+query);
		webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		try {
			HtmlPage htmlPage = webClient.getPage(BAIDU_NEWS_URL);
			HtmlForm form = htmlPage.getFormByName("f");
			HtmlInput kwInput = form.getInputByName("q1");
			kwInput.setValueAttribute(query);
			
			HtmlSelect pageSelect = form.getSelectByName("rn");
			pageSelect.setSelectedAttribute("50", true);
			
			HtmlInput submitButton = form.getInputByName("submit");
			
			HtmlPage resultPage = submitButton.click();
			
			if(resultPage.getWebResponse().getStatusCode()==200){
				String htmlContent = new String(resultPage.getWebResponse().getContentAsString());
				Html resultHtml = new Html(htmlContent);
				newItemList.addAll(saveHandler.save(resultHtml));
				boolean hasNextPage = true;
				HtmlAnchor nextPageBtn = null;
				do{
					try{
						//查找“下一页”的按钮
						nextPageBtn = resultPage.getAnchorByText("下一页>");
					}catch(ElementNotFoundException e){
						logger.warn("没有下一页了");
						hasNextPage = false;
					}
					if(hasNextPage){//存在下一页，触发链接
						resultPage = nextPageBtn.click();
						if(resultPage.getWebResponse().getStatusCode()==200){
							htmlContent = new String(resultPage.getWebResponse().getContentAsString());
							final Html nextHtml = new Html(htmlContent);
							newItemList.addAll(saveHandler.save(nextHtml));
						}
					}
				}while(hasNextPage);
			}
			webClient.closeAllWindows();
			//开始爬取新闻内容
			for(NewItem news : newItemList){
				htmlDownloader.addUrl(news.getTargetUrl());
//				break;
			}
			htmlDownloader.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private String getQurey(){
		if(keywords.size()==0){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (String key : keywords) {
			if(StringUtils.isEmpty(key)){
				continue;
			}
			sb.append(" "+key.replace(" ", ""));
		}
		String s = sb.toString();
		return StringUtils.isEmpty(s)?null:s;
	}
}