package com.quizarena.database;
import java.sql.*;
public final class Database {
 private Database(){}
 public static Connection open() throws SQLException { String url=env("QUIZ_DB_URL","jdbc:mysql://localhost:3306/quizarena?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true"); return DriverManager.getConnection(url,env("QUIZ_DB_USER","root"),env("QUIZ_DB_PASSWORD","")); }
 private static String env(String key,String fallback){String value=System.getenv(key); return value==null||value.isBlank()?fallback:value;}
}
