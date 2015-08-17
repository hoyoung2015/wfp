package net.hoyoung.wfp.searcher.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.hoyoung.wfp.core.entity.CompanyInfo;

public class CompanyFileReaderUtil {
	private File file;

	public CompanyFileReaderUtil(String filePath) {
		super();
		this.file = new File(filePath);
	}
	public List<String> read(){
		List<String> list = new ArrayList<String>();
		
		try {
			FileInputStream fs = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fs, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String temp = null;
			
			while ((temp = br.readLine()) != null) {
				list.add(temp.replace(" ", ""));
			}
			
			fs.close();
			isr.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
