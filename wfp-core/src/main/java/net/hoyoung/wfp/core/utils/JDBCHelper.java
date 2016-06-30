package net.hoyoung.wfp.core.utils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class JDBCHelper {

    public static HashMap<String, JdbcTemplate> templateMap = new HashMap<String, JdbcTemplate>();

    public static JdbcTemplate createMysqlTemplate(String templateName, String url, String username, String password, int initialSize, int maxActive) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        templateMap.put(templateName, template);
        return template;
    }
    public static JdbcTemplate getJdbcTemplate(String templateName){
        return templateMap.get(templateName);
    }

}