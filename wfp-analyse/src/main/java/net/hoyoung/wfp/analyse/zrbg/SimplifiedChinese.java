package net.hoyoung.wfp.analyse.zrbg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.beust.jcommander.internal.Lists;
import com.hankcs.hanlp.HanLP;

public class SimplifiedChinese {

	public static void main(String[] args) throws IOException {
		String s = FileUtils
				.readFileToString(new File("/Users/baidu/workspace/wfp/wfp-python/zrbg/pdfminer/word_seg_result.txt"));
		s = HanLP.convertToSimplifiedChinese(s);
		String[] split = s.split("\n");
		Map<String, Integer> map = new HashMap<>();
		for (String sp : split) {
			if (StringUtils.isEmpty(sp)) {
				continue;
			}
			String word = sp.split("\t")[0];
			Integer count = Integer.valueOf(sp.split("\t")[1]);
			Integer mapCount = map.get(word);
			if (mapCount != null) {
				map.put(word, mapCount + count);
			} else {
				map.put(word, count);
			}
		}
		// 这里将map.entrySet()转换成list
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
		// 然后通过比较器来实现排序
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			// 升序排序
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return -1 * o1.getValue().compareTo(o2.getValue());
			}
		});

		List<String> rs = Lists.newArrayList();
		for (Map.Entry<String, Integer> mapping : list) {
			rs.add(mapping.getKey() + "\t" + mapping.getValue());
		}
		FileUtils.writeLines(new File("word_seg.txt"), rs);
	}

}
