package net.hoyoung.wfp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.collect.Lists;

public class FileStockCodeReader implements StockCodeReader {
	
	private String filename;
	

	public FileStockCodeReader(String file) {
		super();
		this.filename = file;
	}


	@Override
	public List<String> getStockCodes() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
		String tmp = null;
		List<String> list = Lists.newArrayList();
		while ((tmp=reader.readLine())!=null) {
			tmp = tmp.trim().replace("\n", "");
			if("".equals(tmp)){
				continue;
			}
			list.add(tmp);
		}
		reader.close();
		return list;
	}

}
