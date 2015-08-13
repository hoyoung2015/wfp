package net.hoyoung.wfp.stockdown;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class TestWrite {

	public static void main(String[] args) throws IOException {
		File webSiteFile = new File("file/web_site.txt");
		FileOutputStream fos = new FileOutputStream(webSiteFile);
		OutputStreamWriter osw = new OutputStreamWriter(fos);
		BufferedWriter bfw = new BufferedWriter(osw);
		
		bfw.write("512354=honlwent.ng\n");
		bfw.write("512354=honlsdfwent.ng\n");
		bfw.write("512354=honlnt.ng\n");
		
		bfw.close();
		osw.close();
		fos.close();
	}

}
