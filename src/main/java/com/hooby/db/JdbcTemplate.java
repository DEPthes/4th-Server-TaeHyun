package com.hooby.db;

import com.hooby.tx.TransactionManager;

import java.sql.*;
import java.util.List;

public class JdbcTemplate {
    private final TransactionManager txManager;

    public JdbcTemplate(TransactionManager txManager) {
        this.txManager = txManager;
    }

    public int update(String sql, List<Object> params) {
        try (PreparedStatement ps = prepareStatement(sql, params)) {
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("❌ update 실패: " + sql, e);
        }
    }

    public <T> T query(String sql, List<Object> params, ResultSetMapper<T> mapper) {
        try (PreparedStatement ps = prepareStatement(sql, params)) {
            try (ResultSet rs = ps.executeQuery()) {
                return mapper.map(rs);
            }
        } catch (Exception e) {
            throw new RuntimeException("❌ query 실패: " + sql, e);
        }
    }

    private PreparedStatement prepareStatement(String sql, List<Object> params) throws SQLException {
        Connection conn = txManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
        return ps;
    }

    @FunctionalInterface
    public interface ResultSetMapper<T> {
        T map(ResultSet rs) throws Exception;
    }
}