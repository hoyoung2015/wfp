package net.hoyoung.wfp;

import java.io.IOException;
import java.util.List;

public interface StockCodeReader {
	List<String> getStockCodes() throws IOException;
}
