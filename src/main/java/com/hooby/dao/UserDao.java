// Path: com.hooby.dao.UserDao.java
package com.hooby.dao;

import com.hooby.tx.TransactionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class UserDao {
    private TransactionManager txManager;

    public void setTransactionManager(TransactionManager txManager) {
        this.txManager = txManager;
    }

    public void insertUser(Map<String, Object> user) {
        try {
            Connection conn = txManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO users (id, name, age) VALUES (?, ?, ?)");
            ps.setString(1, (String) user.get("id"));
            ps.setString(2, (String) user.get("name"));
            ps.setInt(3, (Integer) user.get("age"));
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> selectUserById(String id) {
        try {
            Connection conn = txManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("id", rs.getString("id"));
                user.put("name", rs.getString("name"));
                user.put("age", rs.getInt("age"));
                return user;
            }
            return null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, Object>> selectUsers(String nameFilter, String ageFilter) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Connection conn = txManager.getConnection();
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

            PreparedStatement ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getString("id"));
                row.put("name", rs.getString("name"));
                row.put("age", rs.getInt("age"));
                result.add(row);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public void updateUser(String id, Map<String, Object> newUser) {
        try {
            Connection conn = txManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET name = ?, age = ? WHERE id = ?");
            ps.setString(1, (String) newUser.get("name"));
            ps.setInt(2, (Integer) newUser.get("age"));
            ps.setString(3, id);
            int affected = ps.executeUpdate();
            if (affected == 0) throw new NoSuchElementException("User not found");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void patchUser(String id, Map<String, Object> patchData) {
        try {
            Map<String, Object> user = selectUserById(id);
            if (user == null) throw new NoSuchElementException("User not found");

            String name = patchData.containsKey("name") ? (String) patchData.get("name") : (String) user.get("name");
            int age = patchData.containsKey("age") ? (Integer) patchData.get("age") : (Integer) user.get("age");

            Connection conn = txManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET name = ?, age = ? WHERE id = ?");
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(String id) {
        try {
            Connection conn = txManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id = ?");
            ps.setString(1, id);
            int affected = ps.executeUpdate();
            if (affected == 0) throw new NoSuchElementException("User not found");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAll() {
        try {
            Connection conn = txManager.getConnection();
            conn.prepareStatement("DELETE FROM users").executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}