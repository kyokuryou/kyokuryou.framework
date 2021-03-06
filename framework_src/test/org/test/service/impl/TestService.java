package org.test.service.impl;

import java.sql.SQLException;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.dao.TestDao;
import org.test.service.ITestService;

/**
 * Created by kyokuryou on 15-4-1.
 */
@Service
public class TestService implements ITestService {
	@Resource
	private TestDao testDao;

	public int getCount() {
		try {
			return testDao.getTestCountSql();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Transactional
	public Object insert1() {
		try {
			return testDao.getTestUpdate1();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
