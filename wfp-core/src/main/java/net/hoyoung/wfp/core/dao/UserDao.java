package net.hoyoung.wfp.core.dao;

import net.hoyoung.wfp.core.entity.User;
import org.springframework.stereotype.Repository;

/**
 * Created by hoyoung on 16-5-14.
 */
@Repository
public class UserDao extends BaseDao {
    public void insert(User user){
        this.getSession().save(user);
    }
}
