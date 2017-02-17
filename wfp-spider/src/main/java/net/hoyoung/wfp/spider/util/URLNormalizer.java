package net.hoyoung.wfp.spider.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

public class URLNormalizer {

	
	public static String normalize(String urlString) throws MalformedURLException{
		if ("".equals(urlString)) // permit empty
		      return urlString;

		    urlString = urlString.trim(); // remove extra spaces

		    URL url = new URL(urlString);

		    String protocol = url.getProtocol();
		    String host = url.getHost();
		    int port = url.getPort();
		    String file = url.getFile();

		    boolean changed = false;

		    if (!urlString.startsWith(protocol)) // protocol was lowercased
		      changed = true;

		    if ("http".equals(protocol) || "https".equals(protocol)
		        || "ftp".equals(protocol)) {

		      if (host != null) {
		        String newHost = host.toLowerCase(); // lowercase host
		        if (!host.equals(newHost)) {
		          host = newHost;
		          changed = true;
		        }
		      }

		      if (port == url.getDefaultPort()) { // uses default port
		        port = -1; // so don't specify it
		        changed = true;
		      }

		      if (file == null || "".equals(file)) { // add a slash
		        file = "/";
		        changed = true;
		      }

		      if (url.getRef() != null) { // remove the ref
		        changed = true;
		      }

		      // check for unnecessary use of "/../", "/./", and "//"
		      String file2 = getFileWithNormalizedPath(url);
		      if (!file.equals(file2)) {
		        changed = true;
		        file = file2;
		      }

		    }

		    if (changed)
		      urlString = new URL(protocol, host, port, file).toString();

		    return urlString;
	}
	private final static Pattern hasNormalizablePathPattern = Pattern
		      .compile("/[./]|[.]/");
	private static String getFileWithNormalizedPath(URL url)
		      throws MalformedURLException {
		    String file;

		    if (hasNormalizablePathPattern.matcher(url.getPath()).find()) {
		      // only normalize the path if there is something to normalize
		      // to avoid needless work
		      try {
		        file = url.toURI().normalize().toURL().getFile();
		        // URI.normalize() does not normalize leading dot segments,
		        // see also http://tools.ietf.org/html/rfc3986#section-5.2.4
		        int start = 0;
		        while (file.startsWith("/../", start)) {
		          start += 3;
		        }
		        if (start > 0) {
		          file = file.substring(start);
		        }
		      } catch (URISyntaxException e) {
		        file = url.getFile();
		      }
		    } else {
		      file = url.getFile();
		    }

		    // if path is empty return a single slash
		    if (file.isEmpty()) {
		      file = "/";
		    }

		    return file;
		  }
	public static void main(String args[]) throws IOException {
	    String line, normUrl;
	    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	    while ((line = in.readLine()) != null) {
	      try {
	        normUrl = URLNormalizer.normalize(line);
	        System.out.println(normUrl);
	      } catch (MalformedURLException e) {
	        System.out.println("failed: " + line);
	      }
	    }
	    System.exit(0);
	  }
}
