package com.inception.app.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ConditionalOnProperty(name = {"SPRING_DATASOURCE_URL_PRIMARY", "SPRING_DATASOURCE_URL_SECONDARY"})
public class CompareDataSourceConfig {

  @Bean(name = "primaryDataSource")
  public DataSource primaryDataSource(Environment env) {
    return build(env.getProperty("SPRING_DATASOURCE_URL_PRIMARY"), env);
  }

  @Bean(name = "secondaryDataSource")
  public DataSource secondaryDataSource(Environment env) {
    return build(env.getProperty("SPRING_DATASOURCE_URL_SECONDARY"), env);
  }

  @Bean(name = "primaryJdbcTemplate")
  public JdbcTemplate primaryJdbcTemplate(@Qualifier("primaryDataSource") DataSource ds) {
    return new JdbcTemplate(ds);
  }

  @Bean(name = "secondaryJdbcTemplate")
  public JdbcTemplate secondaryJdbcTemplate(@Qualifier("secondaryDataSource") DataSource ds) {
    return new JdbcTemplate(ds);
  }

  private DataSource build(String jdbcUrl, Environment env) {
    String user = env.getProperty("DB_USER");
    String pass = env.getProperty("DB_PASSWORD");

    HikariConfig cfg = new HikariConfig();
    cfg.setJdbcUrl(jdbcUrl);
    cfg.setUsername(user);
    cfg.setPassword(pass);
    cfg.setMaximumPoolSize(2);
    cfg.setConnectionTimeout(3000);

    return new HikariDataSource(cfg);
  }
}