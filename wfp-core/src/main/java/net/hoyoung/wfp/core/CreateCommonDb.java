package net.hoyoung.wfp.core;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class CreateCommonDb {

	public static void main(String[] args) {
		Configuration conf = new Configuration().configure();
		SchemaExport schemaExport = new SchemaExport(conf);
		schemaExport.drop(true, true);
		schemaExport.create(true, true);		
	}

}
