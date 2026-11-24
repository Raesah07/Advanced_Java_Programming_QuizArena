package com.quizarena.dao;
import com.quizarena.database.Database; import com.quizarena.model.User; import java.sql.*;
public class UserDao {
 public User findByEmail(String email) throws SQLException {try(Connection c=Database.open();PreparedStatement p=c.prepareStatement("SELECT id,username,email,role FROM users WHERE email=?")){p.setString(1,email);try(ResultSet r=p.executeQuery()){return r.next()?new User(r.getLong(1),r.getString(2),r.getString(3),r.getString(4)):null;}}}
 public String passwordHash(String email) throws SQLException {try(Connection c=Database.open();PreparedStatement p=c.prepareStatement("SELECT password_hash FROM users WHERE email=?")){p.setString(1,email);try(ResultSet r=p.executeQuery()){return r.next()?r.getString(1):null;}}}
 public void create(String username,String email,String hash) throws SQLException {try(Connection c=Database.open();PreparedStatement p=c.prepareStatement("INSERT INTO users(username,email,password_hash) VALUES(?,?,?)")){p.setString(1,username);p.setString(2,email);p.setString(3,hash);p.executeUpdate();}}
}
