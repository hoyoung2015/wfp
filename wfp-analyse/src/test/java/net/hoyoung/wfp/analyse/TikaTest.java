package net.hoyoung.wfp.analyse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TikaTest {

	@Test
	public void test() throws IOException, SAXException, TikaException{
		//构建InputStream来读取数据  
		   InputStream  input=new FileInputStream(new File("/Users/baidu/tmp/zrbg_pdf/000001.pdf"));//可以写文件路径，pdf，word，html等  
		   BodyContentHandler textHandler=new BodyContentHandler();  
		   Metadata matadata=new Metadata();//Metadata对象保存了作者，标题等元数据  
		   Parser parser=new  AutoDetectParser();//当调用parser，AutoDetectParser会自动估计文档MIME类型，此处输入pdf文件，因此可以使用PDFParser  
		   ParseContext context=new ParseContext();  
		   parser.parse(input, textHandler, matadata, context);//执行解析过程  
		   input.close();  
		   System.out.println("Title: "+matadata.get(TikaCoreProperties.TITLE));  
		   System.out.println("Type: "+matadata.get(TikaCoreProperties.TYPE));  
		   System.out.println("Body: "+textHandler.toString());//从textHandler打印正文 
	}
	@Test
	public void test2() throws IOException{
		Tika tika = new Tika();
		Reader reader = tika.parse(new File("/Users/baidu/tmp/zrbg_pdf/000006.pdf"));
		String s = IOUtils.toString(reader);
		System.out.println(s);
	}
}
