package net.hoyoung.wfp.spider.comweb.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.apache.tika.Tika;

public class Ano {

	public static void main(String[] args) {

		Tika tika = new Tika();
		BufferedReader br = null;
		try {
			Reader reader = tika.parse(new File("/home/hoyoung/0c955883e13ee12106163375dc670ac70c556a23.doc"));
			br = new BufferedReader(reader);
			String tmp = null;
			while ((tmp = br.readLine()) != null) {
				System.out.println(tmp);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}

}
