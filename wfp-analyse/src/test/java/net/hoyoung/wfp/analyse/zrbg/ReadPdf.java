package net.hoyoung.wfp.analyse.zrbg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.regex.Pattern;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadPdf {
	protected static Logger LOG = LoggerFactory.getLogger(ReadPdf.class);

	public static void main(String[] args) {

		String inputDir = "/Users/baidu/tmp/zrbg_pdf/";
		String outputDir = "/Users/baidu/tmp/zrbg_txt/";
		File dir = new File(inputDir);

		Tika tika = new Tika();
		for (File file : dir.listFiles()) {
			BufferedWriter writer = null;
			BufferedReader br = null;
			try {
				Reader reader = tika.parse(file);
				br = new BufferedReader(reader);
				String tmp = null;
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(outputDir + file.getName().replaceAll("\\.pdf$", ".txt"))));
				while ((tmp = br.readLine()) != null) {
					if ("".equals(tmp) || Pattern.matches("\\s+", tmp)) {
						continue;
					}
					writer.write(tmp);
				}
			} catch (IOException e) {
				LOG.error("parse {} error {}", file.getName(), e);
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			break;
		}

	}

}
