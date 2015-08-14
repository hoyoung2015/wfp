package net.hoyoung.wfp.searcher.savehandler;
import java.util.List;

import net.hoyoung.wfp.searcher.entity.NewItem;
import us.codecraft.webmagic.selector.Html;

public interface SaveHandler {
	public List<NewItem> save(Html html);
}
