package net.hoyoung.wfp.spider;

import java.io.File;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import com.gargoylesoftware.htmlunit.WebClient;

public class JEditorPaneTest {

	@Test
	public void test(){
		JEditorPane editorPane = new JEditorPane();
		try {
			editorPane.setPage("http://www.jisco.cn/structure/xwzx/gsxw");;
		    editorPane.setEditable(false);  
		    editorPane.setSize(200, 400);  
		    JScrollPane pane = new JScrollPane(editorPane);  
		    JFrame frame = new JFrame("PRINT");  
		    frame.setResizable(false);  
		    frame.setLocation(500, 400);  
		    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);  
		    frame.setContentPane(pane);  
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		    frame.setSize(200, 300);  
		    frame.setVisible(true);
		    
		    System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test2(){
		WebClient client = new WebClient();
	}
}
