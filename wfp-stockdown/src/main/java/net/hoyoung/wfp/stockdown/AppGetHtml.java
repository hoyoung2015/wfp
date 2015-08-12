package net.hoyoung.wfp.stockdown;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

/**
 * Hello world!
 *
 */
public class AppGetHtml 
{
    public static void main( String[] args ) throws IOException
    {
    	File file = new File("E:\\workspace\\eclipse-jee\\wfp\\wfp-stockdown\\src\\main\\resource\\szmb000001.html");
    	
    	FileInputStream fs = new FileInputStream(file);
    	InputStreamReader isr = new InputStreamReader(fs,"UTF-8");
    	BufferedReader br = new BufferedReader(isr);
    	String temp = null;
    	StringBuffer sb = new StringBuffer();
    	while((temp=br.readLine()) != null){
    		sb.append(temp);
    	}
    	
    	Html html = new Html(sb.toString());
    	
    	String stockSymbol = html.xpath("///table[1]/tbody/tr/td/text()").get().split(" ")[0];
    	System.out.println(stockSymbol);
    	
    	Selectable table = html.xpath("//table").nodes().get(1);
    	
    	String name = table.xpath("/table/tbody/tr[1]/td[2]/text()").get();
    	System.out.println(name);
    	
    	String ename = table.xpath("/table/tbody/tr[2]/td[2]/text()").get();
    	System.out.println(ename);
    	
    	String addr = table.xpath("/table/tbody/tr[3]/td[2]/text()").get();
    	System.out.println(addr);
    	
    	String sname = table.xpath("/table/tbody/tr[4]/td[2]/text()").get();
    	System.out.println(sname);
    	
    	String regCapitalStr = table.xpath("/table/tbody/tr[7]/td[2]/text()").get();
    	long regCapital = (long) (Float.parseFloat(regCapitalStr.replace(",", ""))*10000);
    	System.out.println(regCapital);
    	
    	String industry = table.xpath("/table/tbody/tr[8]/td[2]/text()").get();
    	System.out.println(industry);
    	
    	String webSite = table.xpath("/table/tbody/tr[12]/td[2]/text()").get();
    	System.out.println(webSite);
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	
    	Date listingDate = null;
    	Date offerDate = null;
    	String listingDateStr = table.xpath("/table/tbody/tr[13]/td[2]/text()").get();
    	String offerDateStr = table.xpath("/table/tbody/tr[14]/td[2]/text()").get();
    	try {
			listingDate = sdf.parse(listingDateStr);
			offerDate = sdf.parse(offerDateStr);
			System.out.println(listingDate);
			System.out.println(offerDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
    	fs.close();
    	isr.close();
    	br.close();
    }
}
