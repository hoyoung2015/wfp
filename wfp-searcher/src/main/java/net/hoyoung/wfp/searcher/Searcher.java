package net.hoyoung.wfp.searcher;

import net.hoyoung.wfp.searcher.savehandler.SaveHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
public class Searcher {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private SearchRequest searchRequest;
	private static String BAIDU_NEWS_URL = "http://news.baidu.com/advanced_news.html";
	private WebClient webClient;
	private SaveHandler saveHandler;
	private HtmlDownloader htmlDownloader;
	public Searcher(SaveHandler saveHandler,HtmlDownloader htmlDownloader) {
		super();
		this.saveHandler = saveHandler;
		this.htmlDownloader = htmlDownloader;
		webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
	}

	public SaveHandler getSaveHandler() {
		return saveHandler;
	}

	public void setSaveHandler(SaveHandler saveHandler) {
		this.saveHandler = saveHandler;
	}

	public HtmlDownloader getHtmlDownloader() {
		return htmlDownloader;
	}

	public void setHtmlDownloader(HtmlDownloader htmlDownloader) {
		this.htmlDownloader = htmlDownloader;
	}

	public SearchRequest getSearchRequest() {
		return searchRequest;
	}

	public void setSearchRequest(SearchRequest searchRequest) {
		this.searchRequest = searchRequest;
	}
	public void close(){
		if(webClient!=null){
			webClient.closeAllWindows();
			webClient = null;
		}
	}
	public void run(){
		//组合关键词
		String query = searchRequest.getQuery();
		if(query==null){
			logger.warn("关键词是空的");
			return;
		}
		webClient.closeAllWindows();
		logger.info("搜索关键字："+query);

		try {
			HtmlPage htmlPage = webClient.getPage(BAIDU_NEWS_URL);
			//构造表单
			HtmlForm form = htmlPage.getFormByName("f");
			HtmlInput kwInput = form.getInputByName("q1");
			kwInput.setValueAttribute(query);
			
			HtmlSelect pageSelect = form.getSelectByName("rn");
			pageSelect.setSelectedAttribute("50", true);
			
			HtmlInput submitButton = form.getInputByName("submit");
			//获取表单提交按钮
			//第一次搜索请求
			HtmlPage resultPage = submitButton.click();
			//等待结果
			if(resultPage.getWebResponse().getStatusCode()==200){
				String htmlContent = new String(resultPage.getWebResponse().getContentAsString());
				
				searchRequest.setHtml(htmlContent);
				saveHandler.save(searchRequest);
				
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

					logger.info("休息3秒>>>>>>>>>>>>>>>>>>>>>>>>>");
					Thread.sleep(3000);

					if(hasNextPage){//存在下一页，触发链接
						resultPage = nextPageBtn.click();
						if(resultPage.getWebResponse().getStatusCode()==200){
							htmlContent = new String(resultPage.getWebResponse().getContentAsString());
							searchRequest.setHtml(htmlContent);
							saveHandler.save(searchRequest);
						}
					}

				}while(hasNextPage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}