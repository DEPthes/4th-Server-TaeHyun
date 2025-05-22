package com.hooby.db;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class JdbcUtils {

    public static Connection createConnection() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        conn.setAutoCommit(false);
        return conn;
    }

    public static void initSchema() {
        try (Connection conn = createConnection()) {
            initSchema(conn);
        } catch (Exception e) {
            throw new RuntimeException("❌ DB 초기화 실패", e);
        }
    }

    public static void initSchema(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            String sql = Files.readString(Paths.get("src/main/resources/schema.sql"), StandardCharsets.UTF_8);
            stmt.execute(sql);
            conn.commit();
            System.out.println("✔︎ schema.sql 실행 완료");
        } catch (Exception e) {
            throw new RuntimeException("❌ schema.sql 실행 실패", e);
        }
    }
}