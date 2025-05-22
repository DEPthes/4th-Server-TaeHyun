package com.hooby.dao;

import com.hooby.db.JdbcTemplate;
import com.hooby.tx.TransactionManager;

import java.sql.ResultSet;
import java.util.*;

public class UserDao {
    private JdbcTemplate jdbc;

    public void setTransactionManager(TransactionManager txManager) {
        this.jdbc = new JdbcTemplate(txManager);
    }

    public void insertUser(Map<String, Object> user) {
        jdbc.update("INSERT INTO users (id, name, age) VALUES (?, ?, ?)",
                List.of(user.get("id"), user.get("name"), user.get("age")));
    }

    public Map<String, Object> selectUserById(String id) {
        return jdbc.query("SELECT * FROM users WHERE id = ?",
                List.of(id),
                rs -> {
                    if (!rs.next()) return null;
                    return toMap(rs);
                });
    }

    public List<Map<String, Object>> selectUsers(String nameFilter, String ageFilter) {
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (nameFilter != null) {
            sql.append(" AND name = ?");
            params.add(nameFilter);
        }
        if (ageFilter != null) {
            sql.append(" AND age = ?");
            params.add(Integer.parseInt(ageFilter));
        }

        return jdbc.query(sql.toString(), params, rs -> {
            List<Map<String, Object>> result = new ArrayList<>();
            while (rs.next()) {
                result.add(toMap(rs));
            }
            return result;
        });
    }

    public int updateUser(String id, Map<String, Object> newUser) {
        return jdbc.update("UPDATE users SET name = ?, age = ? WHERE id = ?",
                List.of(newUser.get("name"), newUser.get("age"), id));
    }

    public int updateUserNameAndAge(String id, String name, int age) {
        return jdbc.update("UPDATE users SET name = ?, age = ? WHERE id = ?",
                List.of(name, age, id));
    }

    public int deleteUser(String id) {
        return jdbc.update("DELETE FROM users WHERE id = ?", List.of(id));
    }

    public void deleteAll() {
        jdbc.update("DELETE FROM users", List.of());
    }

    private Map<String, Object> toMap(ResultSet rs) throws Exception {
        Map<String, Object> row = new HashMap<>();
        row.put("id", rs.getString("id"));
        row.put("name", rs.getString("name"));
        row.put("age", rs.getInt("age"));
        return row;
    }
}