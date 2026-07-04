package com.self.cat.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@Component
public class DataSourceCheck implements CommandLineRunner {
    private final DataSource dataSource;

    // Inject the database source
    // 注入数据库源
    public DataSourceCheck(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Override
    public void run(String... args) throws Exception {


        try {
            dataSource.getConnection().close();

            log.info("DataSource Check: DataSource is Ok!");
        } catch (SQLException e) {
            log.error("DataSource Check: DataSource connection to database failed!", e);
            log.error("DataURL: " + dbUrl);
            log.error("Username: " + username);
            log.error("Password: " + password);

        }
    }
}
