package org.test.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.dao.TestDao;

import javax.annotation.Resource;
import java.sql.SQLException;

/**
 * Created by kyokuryou on 15-4-1.
 */
@Service
public class TestService {
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

    public int getCountMapper() {
        try {
            return testDao.getTestCountFile();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Transactional
    public Object insert1() {
        try {
            testDao.getTestInsert1();
            testDao.getTestInsert1();
            testDao.getTestInsert1();
            testDao.getTestInsert1();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
