package com.zhituan.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserProfileSchemaInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public UserProfileSchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'user_profiles' AND column_name = 'avatar_data_url' AND data_type IN ('longtext', 'mediumtext')",
                    Integer.class
            );
            if (count != null && count > 0) {
                return;
            }
            jdbcTemplate.execute("ALTER TABLE user_profiles MODIFY COLUMN avatar_data_url LONGTEXT");
        } catch (Exception ignored) {
            // If the table does not exist yet or the dialect does not support the statement, keep startup non-fatal.
        }
    }
}