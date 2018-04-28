package org.shop.service;

import org.shop.dao.UserDao;
import org.shop.serviceI.dto.User;
import org.shop.serviceI.dto.UserLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("org.shop.service.UserLoginServiceImpl")
public class UserLoginServiceImpl implements UserLoginService {

	@Autowired
	private UserDao userDao;

	private Logger logger = LoggerFactory.getLogger(UserLoginServiceImpl.class);

	@Cacheable(value = "hzMap", key = "#user.id")
	public User login(User user) {
		logger.info("login ...");
		user = userDao.getStudent(user.getId());
		try {
			Thread.sleep(350);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return user;
	}

	public void register(User user) {
		logger.info("register ...");
		userDao.save(user);
	}

}