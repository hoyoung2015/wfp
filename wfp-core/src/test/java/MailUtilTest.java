import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.TemplateException;
import net.hoyoung.wfp.core.utils.mail.MailUtil;

/**
 * 邮件工具测试类
 * 
 * @date 2014年4月26日 下午3:37:11
 * @author 曹志飞
 * @Description:
 * @project mailUtil
 */
public class MailUtilTest {
	private static Logger log = LoggerFactory.getLogger(MailUtilTest.class);

	@Test
	public void testMailTemplate() {
		String templateName = "template_1.ftl";
		Map<String, String> map = new HashMap<String, String>();
		map.put("content", "test");
		try {
			MailUtil.sendMailByTemplate("52935247955@qq.com", "test", map, templateName);
		} catch (IOException e) {
			log.error(e.toString(), e);
		} catch (TemplateException e) {
			log.error(e.toString(), e);
		} catch (MessagingException e) {
			log.error(e.toString(), e);
		}
	}

	@Test
	public void testMail() {
		try {
			MailUtil.sendMail("huyang09@baidu.com", "绿色形象研究", "普通邮件");
		} catch (IOException e) {
			log.error(e.toString(), e);
		} catch (MessagingException e) {
			log.error(e.toString(), e);
		}
	}

	@Test
	public void testMailAndFile() {
		try {
			String filePath = "D:/dat.txt";
			MailUtil.sendMailAndFile("52935247955@qq.com", "test", filePath, "ted");
		} catch (IOException e) {
			log.error(e.toString(), e);
		} catch (MessagingException e) {
			log.error(e.toString(), e);
		}
	}
}