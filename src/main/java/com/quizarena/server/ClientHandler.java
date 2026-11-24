package com.quizarena.server;
import com.quizarena.dao.QuestionDao; import com.quizarena.dao.ResultDao; import com.quizarena.model.*; import com.quizarena.service.AuthService; import java.io.*; import java.net.*; import java.util.*;
final class ClientHandler implements Runnable {
 final Socket socket; final BufferedReader in; final PrintWriter out; final Set<String> answers=Collections.synchronizedSet(new HashSet<>()); User user; Room room;
 ClientHandler(Socket s)throws IOException{socket=s;in=new BufferedReader(new InputStreamReader(s.getInputStream()));out=new PrintWriter(s.getOutputStream(),true);}
 public void run(){try{String line;while((line=in.readLine())!=null)handle(line);}catch(IOException ignored){}finally{if(room!=null)room.remove(this);try{socket.close();}catch(IOException ignored){}}}
 void handle(String line){String[] p=line.split("\\|",-1);try{switch(p[0]){case "AUTH"->auth(p);case "REGISTER"->register(p);case "ADMIN_ADD"->adminAdd(p);case "CREATE"->create(p);case "JOIN"->join(p);case "GLOBAL"->global();case "ANALYSIS"->analysis();case "START"-> {if(room!=null)room.start();}case "ANSWER"-> {if(room!=null)room.answer(this,Long.parseLong(p[1]),p[2].charAt(0),Long.parseLong(p[3]));}default->send("ERROR|Unknown request");}}catch(Exception e){send("ERROR|"+e.getMessage());}}
 void auth(String[] p)throws Exception{if(p.length<3){send("ERROR|Email and password are required");return;}User found=new AuthService().login(p[1],p[2]);if(found==null){send("ERROR|Invalid email or password");return;}user=found;send("AUTH_OK|"+user.username()+"|"+user.role());}
 void register(String[] p)throws Exception{if(p.length<4){send("ERROR|Username, email and password are required");return;}new AuthService().register(p[1],p[2],p[3]);send("REGISTERED|Account created");}
 void adminAdd(String[] p)throws Exception{if(user==null||!"ADMIN".equals(user.role())){send("ERROR|Administrator access required");return;}if(p.length<9){send("ERROR|All question fields are required");return;}new QuestionDao().add(p[1],p[2],p[3],p[4],p[5],p[6],p[7],p[8].charAt(0));send("ADMIN_OK|Question added");}
 void global()throws Exception{send("GLOBAL|"+String.join(";",new ResultDao().globalRows()));}
 void analysis()throws Exception{if(user==null){send("ERROR|Sign in first");return;}ResultDao dao=new ResultDao();send("ANALYSIS|"+String.join(";",dao.latestAnalysis(user.id())));int[] totals=dao.overallCounts(user.id());send("ANALYSIS_TOTAL|"+totals[0]+"|"+totals[1]);}
 void create(String[] p)throws Exception{if(user==null){send("ERROR|Sign in first");return;}List<Question> qs=new QuestionDao().random(p[2],p[3],Integer.parseInt(p[4]));if(qs.isEmpty()){send("ERROR|No questions match the selected filters");return;}String code=QuizServer.createRoom(p[1],p[2],p[3],Integer.parseInt(p[4]),Integer.parseInt(p[5]),Integer.parseInt(p[6]),qs);join(new String[]{"JOIN",code});send("ROOM_CREATED|"+code);}
 void join(String[] p){if(user==null){send("ERROR|Sign in first");return;}Room r=QuizServer.room(p[1]);if(r==null){send("ERROR|Invalid room code");return;}r.add(this);room=r;send("JOINED|"+r.code+"|"+r.scores.size());}
 void send(String s){out.println(s);}
}
