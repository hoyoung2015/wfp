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
public class AppGetHtmlList 
{
    public static void main( String[] args ) throws IOException
    {
    	File file = new File("E:\\szmblist.html");
    	
    	FileInputStream fs = new FileInputStream(file);
    	InputStreamReader isr = new InputStreamReader(fs,"UTF-8");
    	BufferedReader br = new BufferedReader(isr);
    	String temp = null;
    	StringBuffer sb = new StringBuffer();
    	while((temp=br.readLine()) != null){
    		sb.append(temp);
    	}
    	
    	Html html = new Html(sb.toString());
//    	System.out.println(html.get());
    	Selectable trs = html.xpath("/html/body/div/table/tbody/tr");
    	System.out.println(trs.nodes().size());
    	for (Selectable node : trs.nodes()) {
    		Selectable tds = node.xpath("/tr/td");
    		for (Selectable nodetd : tds.nodes()) {
    			String str = nodetd.xpath("/td/a/text()").get();
    			int emptyIndex = str.indexOf(" ");
    			String stockCode = str.substring(0,emptyIndex);
    			System.out.println(stockCode);
    			
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
    			System.out.println(market);
    		}
		}
    	fs.close();
    	isr.close();
    	br.close();
    }
}
