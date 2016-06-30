package net.hoyoung.patents;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;


/**
 * Hello world!
 *
 */
public class CreatePatentsTables 
{
    public static void main( String[] args )
    {
    	Configuration conf = new Configuration().configure("hibernate-patents.xml");
		SchemaExport schemaExport = new SchemaExport(conf);
		schemaExport.drop(true, true);
		schemaExport.create(true, true);
    }
}
