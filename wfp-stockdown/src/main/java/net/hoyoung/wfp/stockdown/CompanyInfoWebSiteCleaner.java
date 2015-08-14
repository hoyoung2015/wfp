package net.hoyoung.wfp.stockdown;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import net.hoyoung.wfp.core.entity.CompanyInfo;
import net.hoyoung.wfp.core.utils.HibernateUtils;
import net.hoyoung.wfp.core.utils.RegexUtils;

import org.hibernate.Session;
/**
 * 上市公司网址整理主类
 * @author hoyoung
 *1.删除网址为空串的记录
 *2.从web_site_fix.txt中读取修正过的网址，修正数据库中的网址
 *3.校验网址是否符合标准url，不符合的写入web_site.txt文件进行人工检验，检验修正后的录入web_site_fix.txt文件中
 */
public class CompanyInfoWebSiteCleaner {
	public static void main(String[] args) throws IOException {
		Session session = HibernateUtils.openSession();
		
		//剔除没有网址的
		session.beginTransaction();
		session.createQuery("delete from CompanyInfo c where c.webSite=''").executeUpdate();
		session.getTransaction().commit();
		
		//多网址的只保留一个
		File file = new File("file/web_site_fix.txt");
		FileInputStream fs = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fs, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		String temp = null;
		while ((temp = br.readLine()) != null) {
			int index = temp.indexOf("=");
			String stockCode = temp.substring(0, index);
			String webSite = temp.substring(index + 1);
			System.out.println(stockCode + "|" + webSite);
			session.beginTransaction();
			CompanyInfo c = (CompanyInfo) session.createQuery("from CompanyInfo c where c.stockCode='"+stockCode+"'")
			.uniqueResult();
			if(c!=null){
				c.setWebSite(webSite);
			}
			session.getTransaction().commit();
		}
		fs.close();
		isr.close();
		br.close();
//		System.exit(0);
		
		//检查不合法的url并将结果写入web_site.txt文件中
		File webSiteFile = new File("file/web_site.txt");
		FileOutputStream fos = new FileOutputStream(webSiteFile);
		OutputStreamWriter osw = new OutputStreamWriter(fos);
		BufferedWriter bfw = new BufferedWriter(osw);
		
		List<CompanyInfo> list = session.createQuery("select new CompanyInfo(id,stockCode,webSite) from CompanyInfo").list();
		for (CompanyInfo companyInfo : list) {
			System.err.println(RegexUtils.checkURL(companyInfo.getWebSite())+" | "+companyInfo.getWebSite());
			if(!RegexUtils.checkURL(companyInfo.getWebSite())){
				bfw.write(companyInfo.getStockCode()+"="+companyInfo.getWebSite()+"\n");
			}
		}
		
		bfw.close();
		osw.close();
		fos.close();
	}

}
