package com.example.crm.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 从 db.json 读取数据库连接信息并创建数据源（host/port/database/user/password/ssl）。
 * 文件路径可通过 crm.db-config-file 配置，默认相对后端工作目录的 ../docs/db.json。
 */
@Configuration
public class DbJsonConfig {

    private static final Logger log = LoggerFactory.getLogger(DbJsonConfig.class);

    @Bean
    public DataSource dataSource(@Value("${crm.db-config-file}") String configFile, ObjectMapper objectMapper) {
        Path path = Paths.get(configFile).toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            throw new IllegalStateException("数据库配置文件不存在: " + path);
        }
        try {
            JsonNode node = objectMapper.readTree(Files.readAllBytes(path));
            String host = node.path("host").asText();
            int port = node.path("port").asInt(5432);
            String database = node.path("database").asText();
            String user = node.path("user").asText();
            String password = node.path("password").asText();
            boolean ssl = node.path("ssl").asBoolean(false);

            String url = "jdbc:postgresql://" + host + ":" + port + "/" + database
                    + (ssl ? "?sslmode=require" : "");

            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(url);
            ds.setUsername(user);
            ds.setPassword(password);
            ds.setDriverClassName("org.postgresql.Driver");
            ds.setMaximumPoolSize(10);
            ds.setMinimumIdle(1);
            ds.setPoolName("crm-db");
            log.info("DataSource initialized from {} -> {}", path, host + ":" + port + "/" + database);
            return ds;
        } catch (Exception e) {
            throw new IllegalStateException("读取数据库配置文件失败: " + path, e);
        }
    }
}
