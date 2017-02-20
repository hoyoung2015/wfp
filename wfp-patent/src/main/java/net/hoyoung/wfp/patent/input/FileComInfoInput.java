package net.hoyoung.wfp.patent.input;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.collect.Lists;

import net.hoyoung.wfp.core.bo.ComInfo;

public class FileComInfoInput implements ComInfoInput {

	private String fileName = null;

	public FileComInfoInput(String fileName) {
		super();
		this.fileName = fileName;
	}

	@Override
	public List<ComInfo> getComInfo() {
		List<ComInfo> list = Lists.newArrayList();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");
				ComInfo comInfo = new ComInfo();
				comInfo.setName(split[1]);
				comInfo.setStockCode(split[0]);
				list.add(comInfo);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("read file " + fileName + " error");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

}
