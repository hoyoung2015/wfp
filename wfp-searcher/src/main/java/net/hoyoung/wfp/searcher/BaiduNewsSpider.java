package net.hoyoung.wfp.searcher;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.utils.HibernateUtils;
import net.hoyoung.wfp.searcher.savehandler.SaveHandler;
import net.hoyoung.wfp.searcher.savehandler.impl.DbSaveHandler;
import net.hoyoung.wfp.searcher.vo.NewItem;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v_huyang01 on 2016/6/13.
 */
public class BaiduNewsSpider {
    static Logger logger = LoggerFactory.getLogger(BaiduNewsSpider.class);
    private static final int OK = 0;
    private static final int WRONG_ARGS = 1;
    private static final int STOCK_CODE_NOT_EXISTS = 2;
    private static final int NEWS_HAS_CRAWLED = 3;
    public static void main(String[] args) {

        if(args==null||args.length!=2){
            logger.warn("正确参数为: <股票号> <关键词>");
            System.exit(WRONG_ARGS);
        }
        String stockCode = args[0];
        String keyword = args[1];
        int status = fetch(stockCode,keyword);
        System.exit(status);
    }
    public static int fetch(String stockCode,String keyword){
        if(StringUtils.isEmpty(stockCode)||StringUtils.isEmpty(keyword)){
            return WRONG_ARGS;
        }
        //创建下载器
        HtmlDownloader downloader = new HtmlDownloader();
        //创建存储器
        SaveHandler saveHandler = new DbSaveHandler();
        Searcher searcher = new Searcher(saveHandler,downloader);


        Session session = HibernateUtils.getCurrentSession();
        session.beginTransaction();
        CompanyInfo companyInfo = (CompanyInfo) session.get(CompanyInfo.class, stockCode);
        session.getTransaction().commit();
        if(companyInfo==null){
            logger.warn("股票号　"+stockCode+"　不存在");
            return STOCK_CODE_NOT_EXISTS;
        }
        SearchRequest sr = new SearchRequest();
        sr.putExtra("company", companyInfo);
        sr.putExtra("keyword",keyword);
        sr.addKeyword("+"+keyword)//加号表示这个关键词一定出现
                .addKeyword("\"" + companyInfo.getSname() + "\"");//加上双引号避免被分词
        /**
         * 校验股票号+关键词是否已经搜索
         */
        session = HibernateUtils.getCurrentSession();
        session.beginTransaction();
        Long count = (Long)session.createCriteria(NewItem.class)
                .setProjection(Projections.rowCount())
                .add(Restrictions.eq("stockCode", companyInfo.getStockCode()))
                .add(Restrictions.eq("keyword", keyword))
                .uniqueResult();
        session.getTransaction().commit();
        if(count>0){
            logger.info(sr.getQuery()+" 已经采集过");
            return NEWS_HAS_CRAWLED;
        }
        searcher.setSearchRequest(sr);
        searcher.run();
        searcher.close();
        return OK;
    }
}
