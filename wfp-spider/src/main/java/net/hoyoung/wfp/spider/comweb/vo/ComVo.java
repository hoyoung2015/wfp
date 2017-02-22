package net.hoyoung.wfp.spider.comweb.vo;
public class ComVo {
		private String stockCode;
		private String sname;
		private String webSite;
		private Integer sleepTime;

		public ComVo(String stockCode, String sname, String webSite, Integer sleepTime) {
			super();
			this.stockCode = stockCode;
			this.sname = sname;
			this.webSite = webSite;
			this.sleepTime = sleepTime;
		}

		public String getStockCode() {
			return stockCode;
		}

		public void setStockCode(String stockCode) {
			this.stockCode = stockCode;
		}

		public String getSname() {
			return sname;
		}

		public void setSname(String sname) {
			this.sname = sname;
		}

		public String getWebSite() {
			return webSite;
		}

		public void setWebSite(String webSite) {
			this.webSite = webSite;
		}

		public Integer getSleepTime() {
			return sleepTime;
		}

		public void setSleepTime(Integer sleepTime) {
			this.sleepTime = sleepTime;
		}

	}