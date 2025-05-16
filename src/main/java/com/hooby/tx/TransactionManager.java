package com.hooby.tx;

import com.hooby.db.JdbcUtils;

import java.sql.Connection;

public class TransactionManager {

    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    public void begin() {
        try {
            if (connectionHolder.get() != null) throw new IllegalStateException("이미 트랜잭션이 시작됨");
            Connection conn = JdbcUtils.createConnection();
            connectionHolder.set(conn);
            System.out.println("👍🏻 Transaction 시작");
        } catch (Exception e) {
            throw new RuntimeException("🖕🏻 트랜잭션 시작 실패", e);
        }
    }

    public void commit() {
        try {
            Connection conn = getConnection();
            conn.commit();
            System.out.println("✅ Transaction 커밋");
        } catch (Exception e) {
            throw new RuntimeException("🖕🏻 커밋 실패", e);
        }
    }

    public void rollback() {
        try {
            Connection conn = getConnection();
            conn.rollback();
            System.out.println("🖕🏻 Transaction 롤백");
        } catch (Exception e) {
            throw new RuntimeException("🖕🏻 롤백 실패", e);
        }
    }

    public void close() {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                throw new RuntimeException("🖕🏻 커넥션 닫기 실패", e);
            } finally {
                connectionHolder.remove();
            }
        }
    }

    public Connection getConnection() {
        Connection conn = connectionHolder.get();
        if (conn == null) throw new IllegalStateException("🖕🏻 트랜잭션이 시작되지 않음");
        return conn;
    }

    public boolean isActive() {
        return connectionHolder.get() != null;
    }
}