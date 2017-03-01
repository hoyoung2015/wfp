package net.hoyoung.wfp.spider.comweb.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import net.hoyoung.wfp.core.utils.MongoUtil;
import net.hoyoung.wfp.spider.comweb.ComWebConstant;
import net.hoyoung.wfp.spider.comweb.bo.ComPage;
import us.codecraft.webmagic.utils.UrlUtils;

public class ExportThirdPartyDomain {

	static class DomainTree {
		String node;
		Map<String, DomainTree> childs;

		public DomainTree(String node) {
			super();
			this.node = node;
		}

		public DomainTree getChild(String nodeName) {
			if (this.childs == null)
				return null;
			return childs.get(nodeName);
		}

		private DomainTree addNode(String node) {
			if (this.childs == null) {
				this.childs = new HashMap<>();
			}
			DomainTree domainTree = this.childs.get(node);
			if (domainTree == null) {
				domainTree = new DomainTree(node);
				this.childs.put(node, domainTree);
			}
			return domainTree;
		}

		public void addDomain(String domain) {

			String[] split = domain.split("\\.");
			DomainTree curTree = this;
			for (int i = split.length - 1; i >= 0; i--) {
				curTree = curTree.addNode(split[i]);
			}
		}

		public void print() {
			System.out.print(this.node + ".");
			if (this.childs == null) {
				System.out.println();
				return;
			}
			for (Entry<String, DomainTree> entry : this.childs.entrySet()) {
				entry.getValue().print();
			}
		}

		@Override
		public String toString() {
			return "DomainTree [node=" + node + "]";
		}

		public boolean in(String domain) {
			String[] split = domain.split("\\.");
			DomainTree curTree = this;
			int i = 0;
			for (i = split.length - 1; i >= 0; i--) {
				curTree = curTree.getChild(split[i]);
				if (curTree == null) {
					break;
				}
			}
			if (i < 0)
				return true;
			int min = 2;
			if (Pattern.matches(".+(com\\.cn|gov\\.cn|org\\.cn)$", domain)) {
				min = 3;
			}
			if (split.length > min && i < 1)
				return true;
			return false;
		}
	}

	private static DomainTree rootDomainTree;
	private static DomainTree blackDomainTree;
	static {
		rootDomainTree = new DomainTree("");
		blackDomainTree = new DomainTree("");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream("src/main/shell/bin/web_source.txt")));
			String tmp = null;
			while ((tmp = br.readLine()) != null) {
				tmp = tmp.trim();
				if (tmp.startsWith("#") || StringUtils.isEmpty(tmp)) {
					continue;
				}
				String[] split = tmp.split("\t");
				rootDomainTree.addDomain(UrlUtils.getDomain(split[2]));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			br = new BufferedReader(new InputStreamReader(ExportThirdPartyDomain.class.getClassLoader()
					.getResourceAsStream("export_third_party_domain_blacklist.txt")));
			String tmp = null;
			while ((tmp = br.readLine()) != null) {
				tmp = tmp.trim();
				if (tmp.startsWith("#") || StringUtils.isEmpty(tmp)) {
					continue;
				}
				blackDomainTree.addDomain(tmp);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) {

//		String dbName = ComWebConstant.DB_NAME + "_test";
		String dbName = ComWebConstant.DB_NAME;

		List<String> list = MongoUtil.getCollectionNames(dbName, "\\d{6}");
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		Pattern pattern = Pattern.compile("^http(s?)://.+");

		Map<String, String> domainMap = new HashMap<>();

		for (String collectionName : list) {
			MongoCollection<Document> collection = MongoUtil.getCollection(dbName, collectionName);
			Bson filters = Filters.and(Filters.exists(ComPage.HTML));
			long total = collection.count(filters);
			System.out.println(String.format("process collection %s,total=%d", collectionName, total));
			MongoCursor<Document> iterator = collection.find(filters).projection(Projections.include(ComPage.HTML))
					.iterator();
			try {
				while (iterator.hasNext()) {
					Document doc = iterator.next();
					String html = doc.getString(ComPage.HTML);
					org.jsoup.nodes.Document document = Jsoup.parse(html);
					Elements elements = document.getElementsByTag("a");
					for (Element element : elements) {
						Element e = element.getElementsByAttributeValueMatching("href", pattern).first();
						if (e == null) {
							continue;
						}
						String link = e.attr("href");
						String domain = UrlUtils.getDomain(link);
						if (Pattern.matches("((\\d+\\.){3}\\d+.*|localhost.*)", domain) || rootDomainTree.in(domain)
								|| blackDomainTree.in(domain)) {
							continue;
						}
						if (domainMap.keySet().contains(domain)) {
							continue;
						}
						if (link.startsWith("https")) {
							link = "https://" + domain;
						} else {
							link = "http://" + domain;
						}
						domainMap.put(link, e.text());
					}
				}
			} finally {
				iterator.close();
			}
			// break;
		}
		BufferedWriter br = null;
		try {
			br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("third_party_site.txt")));
			for (Entry<String, String> entry : domainMap.entrySet()) {
				String line = entry.getKey() + "\t" + entry.getValue();
				System.out.println(line);
				br.write(line + "\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
