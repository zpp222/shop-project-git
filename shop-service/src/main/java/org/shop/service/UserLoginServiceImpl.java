package org.shop.service;

import java.util.Map;

import javax.annotation.Resource;

import org.shop.aop.ServiceLog;
import org.shop.dao.UserDao;
import org.shop.service.asy.ShopAsyJob;
import org.shop.serviceI.dto.User;
import org.shop.serviceI.dto.UserLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("org.shop.service.UserLoginServiceImpl")
public class UserLoginServiceImpl extends BaseService implements UserLoginService {

	private Logger logger = LoggerFactory.getLogger(UserLoginServiceImpl.class);

	@Autowired
	private UserDao userDao;

	@Resource(name = "taskExecutor")
	private ThreadPoolTaskExecutor taskExecutor;

	private ShopAsyJob getShopAsyJob() {
		return beanFactory.getBean(ShopAsyJob.class);
	}

	@Cacheable(value = "hzMap", key = "#user.name")
	public User login(User user) {
		String userName = user.getName();
		logger.info("login user'name {} !", userName);
		User local = userDao.getStudent(userName);
		if (null == local)
			local = user;
		logger.info("login user is {} !", local);
		return local;
	}

	@ServiceLog
	@Transactional
	public void register(User user) {
		logger.info("register ...");
		userDao.save(user);

		/** do some thing */
		ShopAsyJob asyJob = this.getShopAsyJob();
		asyJob.setData("hello");
		try {
			taskExecutor.execute(asyJob);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Cacheable(value = "hzMap", key = "#id")
	public User getUserById(String id) {
		logger.info("getUserById {}", id);
		User local = userDao.getUserById(id);
		if (null == local) {
			local = new User();
		}
		return local;
	}

	@Cacheable(cacheManager = "caffeCacheManager", value = "userCache", key = "#root.methodName",unless="#result==null")
	@Override
	public Map<String, String> getUserCount() {
		Map<String, String> result = userDao.getUserCount();
		logger.info("实际执行userDao.getUserCount...");
		return result;
	}

}
