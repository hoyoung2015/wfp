import net.hoyoung.wfp.core.dao.UserDao;
import net.hoyoung.wfp.core.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-core.xml")
public class SpringTestApp{

    @Autowired
    UserDao userDao;
    @Test
    public void test(){
        User user = new User();
        user.setId("123456");
        user.setName("liubei");
        userDao.insert(user);
    }

}