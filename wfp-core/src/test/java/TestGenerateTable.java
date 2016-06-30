import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Test;

/**
 * Created by hoyoung on 16-5-14.
 */
public class TestGenerateTable {
    @Test
    public void test(){
        Configuration conf = new Configuration().configure("user.cfg.xml");
        SchemaExport schemaExport = new SchemaExport(conf);
        schemaExport.drop(true, true);
        schemaExport.create(true, true);
    }
}
