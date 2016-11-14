package net.hoyoung.wfp.parser.pdf;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

public class Main {

	public static void main(String[] args) throws IOException, TikaException {

		Tika tika = new Tika();
		String s = tika.parseToString(new File("/home/hoyoung/vm_share/pdf/2010_TCL集团_000100.pdf"));
		System.out.println(s);
	}

}
