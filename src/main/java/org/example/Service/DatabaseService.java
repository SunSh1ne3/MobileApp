package org.example.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<String> getAllDatabases() {
        String sql = "SHOW DATABASES;";
        logger.debug("Выполняем SQL-запрос: {}", sql);
        try {
            List<String> databases = jdbcTemplate.queryForList(sql, String.class);
            logger.debug("Получены базы данных: {}", databases);
            return databases;
        } catch (Exception e) {
            logger.error("Ошибка при получении списка баз данных: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось получить список баз данных", e);
        }
    }
}
