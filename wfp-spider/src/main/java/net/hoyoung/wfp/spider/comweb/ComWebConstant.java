package net.hoyoung.wfp.spider.comweb;

import net.hoyoung.wfp.core.utils.WFPContext;

public interface ComWebConstant {
	String REDIS_PREFIX = "comweb_";
	String DOC_REGEX = "pdf|PDF|doc|DOC|docx|DOCX";
	String LANDING_PAGE_KEY = "landingPageUrl";
	String REMOVE_URL_KEY = "removeUrl";
	String URL_LIST_KEY = "urlList";
	String STOCK_CODE_KEY = "stockCode";
	String CONTENT_LENGTH_KEY = "contentLength";
	String DB_NAME = WFPContext.getProperty("compage.dbname", String.class);
	String DB_NAME_TEST = "wfp_spider_test";
	String COLLECTION_NAME = "com_page";
	String COLLECTION_PREFIX = "com_page_";
	String COLLECTION_NAME_TMP = "com_page_tmp";
}
