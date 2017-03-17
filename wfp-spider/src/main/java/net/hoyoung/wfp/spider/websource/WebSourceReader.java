package net.hoyoung.wfp.spider.websource;

import java.util.List;

import net.hoyoung.wfp.spider.comweb.vo.ComVo;

public interface WebSourceReader {

	List<ComVo> read();
}
