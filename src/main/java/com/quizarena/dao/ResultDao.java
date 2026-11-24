package com.quizarena.dao;

import com.quizarena.database.Database;
import com.quizarena.model.PlayerAnswer; import com.quizarena.model.PlayerScore;
import java.sql.*;
import java.util.*;

/** Persists completed rooms and exposes the durable global leaderboard. */
public class ResultDao {
    public long save(String roomCode,String category,String difficulty,int questionCount,List<PlayerScore> players,Map<Long,List<PlayerAnswer>> answers) throws SQLException {
        try(Connection c=Database.open()){
            c.setAutoCommit(false);
            try(PreparedStatement s=c.prepareStatement("INSERT INTO quiz_sessions(room_code,category_id,difficulty,question_count,started_at,ended_at) VALUES(?,(SELECT id FROM categories WHERE name=?),?,?,NOW(),NOW())",Statement.RETURN_GENERATED_KEYS)){
                s.setString(1,roomCode);s.setString(2,category);s.setString(3,difficulty);s.setInt(4,questionCount);s.executeUpdate();
                long sessionId;try(ResultSet keys=s.getGeneratedKeys()){if(!keys.next())throw new SQLException("Could not create quiz session");sessionId=keys.getLong(1);}
                try(PreparedStatement p=c.prepareStatement("INSERT INTO session_players(session_id,user_id,score,correct_count,response_ms,final_rank) VALUES(?,?,?,?,?,?)")){for(PlayerScore row:players){p.setLong(1,sessionId);p.setLong(2,row.userId());p.setInt(3,row.score());p.setInt(4,row.correct());p.setLong(5,row.responseMs());p.setInt(6,row.rank());p.addBatch();}p.executeBatch();}
                try(PreparedStatement p=c.prepareStatement("INSERT INTO player_answers(session_id,user_id,question_id,selected_option,is_correct,response_ms) VALUES(?,?,?,?,?,?)")){for(Map.Entry<Long,List<PlayerAnswer>> entry:answers.entrySet())for(PlayerAnswer row:entry.getValue()){p.setLong(1,sessionId);p.setLong(2,entry.getKey());p.setLong(3,row.questionId());if(row.selectedOption()==null)p.setNull(4,Types.CHAR);else p.setString(4,row.selectedOption());p.setBoolean(5,row.correct());p.setLong(6,row.responseMs());p.addBatch();}p.executeBatch();}
                c.commit();
                return sessionId;
            }catch(SQLException e){c.rollback();throw e;}
        }
    }
    public List<String> latestAnalysis(long userId) throws SQLException {try(Connection c=Database.open();PreparedStatement p=c.prepareStatement("SELECT MAX(session_id) FROM session_players WHERE user_id=?")){p.setLong(1,userId);try(ResultSet r=p.executeQuery()){if(!r.next()||r.getObject(1)==null)return new ArrayList<>();return analysis(r.getLong(1),userId);}}}
    public List<String> analysis(long sessionId,long userId) throws SQLException {List<String> rows=new ArrayList<>();String sql="SELECT q.question_text,pa.selected_option,q.correct_option,pa.is_correct FROM player_answers pa JOIN questions q ON q.id=pa.question_id WHERE pa.session_id=? AND pa.user_id=? ORDER BY pa.question_id";try(Connection c=Database.open();PreparedStatement p=c.prepareStatement(sql)){p.setLong(1,sessionId);p.setLong(2,userId);try(ResultSet r=p.executeQuery()){while(r.next())rows.add(r.getString(1).replace("|","/")+"|"+(r.getString(2)==null?"UNANSWERED":r.getString(2))+"|"+r.getString(3)+"|"+(r.getBoolean(4)?"CORRECT":"WRONG"));}}return rows;}
    public int[] overallCounts(long userId) throws SQLException {try(Connection c=Database.open();PreparedStatement p=c.prepareStatement("SELECT COALESCE(SUM(is_correct),0),COUNT(*)-COALESCE(SUM(is_correct),0) FROM player_answers WHERE user_id=?")){p.setLong(1,userId);try(ResultSet r=p.executeQuery()){r.next();return new int[]{r.getInt(1),r.getInt(2)};}}}
    public List<String> globalRows() throws SQLException {List<String> rows=new ArrayList<>();try(Connection c=Database.open();PreparedStatement p=c.prepareStatement("SELECT username,total_score,correct_answers,quizzes_played,global_rank FROM global_leaderboard WHERE quizzes_played>0 ORDER BY global_rank,username");ResultSet r=p.executeQuery()){while(r.next())rows.add(r.getString(1)+","+r.getInt(2)+","+r.getInt(3)+","+r.getInt(4)+","+r.getInt(5));}return rows;}
}
