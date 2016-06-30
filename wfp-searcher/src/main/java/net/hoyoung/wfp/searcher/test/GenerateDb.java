package net.hoyoung.wfp.searcher.test;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class GenerateDb {
	public static void main( String[] args )
    {
		Configuration conf = new Configuration().configure("hibernate.cfg.xml");
		SchemaExport schemaExport = new SchemaExport(conf);
		schemaExport.setOutputFile("/home/hoyoung/workspace/Intellij/wfp/wfp-searcher/sql/ddl.sql");
		schemaExport.create(true, false);
    }
}