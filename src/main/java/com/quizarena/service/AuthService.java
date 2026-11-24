package com.quizarena.service;
import com.quizarena.dao.UserDao; import com.quizarena.model.User; import java.nio.charset.StandardCharsets; import java.security.*; import java.sql.SQLException;
public class AuthService {
 private final UserDao dao=new UserDao();
 public User login(String email,String password) throws Exception {String stored=dao.passwordHash(email); if(stored==null||!stored.equals(hash(password)))return null;return dao.findByEmail(email);}
 public void register(String username,String email,String password) throws SQLException {dao.create(username,email,hash(password));}
 public static String hash(String value){try{byte[] b=MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));StringBuilder s=new StringBuilder();for(byte x:b)s.append(String.format("%02x",x));return s.toString();}catch(GeneralSecurityException e){throw new IllegalStateException(e);}}
}
