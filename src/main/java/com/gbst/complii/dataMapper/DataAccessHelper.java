package com.gbst.complii.dataMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Component
public class DataAccessHelper extends JdbcDaoSupport {

    @Autowired
    public void setDataSourceForComplii(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public List<Map<String, Object>> getData(String query) {
        return getJdbcTemplate().queryForList(query);
    }
}
