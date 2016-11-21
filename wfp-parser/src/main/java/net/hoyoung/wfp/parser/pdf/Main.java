package net.hoyoung.wfp.parser.pdf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.io.input.CharSequenceReader;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

public class Main {

	public static void main(String[] args) throws IOException, TikaException {

		String inputPath = "/home/hoyoung/vm_share/pdf";
		String outputPath = "/home/hoyoung/vm_share/txt/";

		File inputFile = new File(inputPath);
		if(!inputFile.exists()||!inputFile.isDirectory()){
			System.err.println("input path dosens't exists");
			System.exit(1);
		}
		if(!outputPath.endsWith(File.separator)){
			outputPath += File.separator;
		}
		File outputFile = new File(outputPath);
		if(!outputFile.exists()){
			outputFile.mkdirs();
		}
		
		
		File[] files = inputFile.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".pdf") || name.endsWith(".PDF");
			}
		});
		Tika tika = new Tika();
		File errorFile = new File(outputPath+"error.txt");
		BufferedWriter bwerror = new BufferedWriter(new FileWriterWithEncoding(errorFile, "UTF-8"));
		for (File file : files) {
			String name = file.getName();
			System.out.println("parse "+name);
			String ext = ".txt";
			try {
				String textFileName = outputPath+name.subSequence(0, name.lastIndexOf("."))+ext;
				File textFile = new File(textFileName);
				if(textFile.exists()){
					System.out.println("exists "+textFileName);
					continue;
				}
				String s = tika.parseToString(file);
				
				BufferedReader br = new BufferedReader(new CharSequenceReader(s));
				FileWriterWithEncoding fw = new FileWriterWithEncoding(textFile, "UTF-8");
				BufferedWriter bw = new BufferedWriter(fw);
				String tmp = null;
				while((tmp = br.readLine() )!=null){
//					if(Pattern.matches("\\s+", tmp)){
//						continue;
//					}
					bw.write(tmp);
				}
				br.close();
				fw.flush();
				bw.flush();
				fw.close();
				bw.close();
				
				
			} catch (Exception e) {
				System.err.println("parse "+file.getName()+" error");
				e.printStackTrace();
				bwerror.write("parse "+file.getName()+" error\n"+e.getMessage()+"\n"+e.getStackTrace().toString()+"\n");
			}
			
//			break;
		}
		bwerror.flush();
		bwerror.close();
	}

}
