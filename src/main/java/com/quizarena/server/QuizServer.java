package com.quizarena.server;
import com.quizarena.model.Question; import java.io.*; import java.net.*; import java.util.*; import java.util.concurrent.*;
public class QuizServer {
 static final ScheduledExecutorService SCHEDULER=Executors.newScheduledThreadPool(4); private static final ExecutorService CLIENTS=Executors.newCachedThreadPool(); private static final Map<String,Room> ROOMS=new ConcurrentHashMap<>(); private static final Random RANDOM=new Random();
 public static void main(String[] a)throws IOException{int port=a.length==0?5050:Integer.parseInt(a[0]);try(ServerSocket server=new ServerSocket(port)){System.out.println("QuizArena server listening on "+port);while(true)CLIENTS.execute(new ClientHandler(server.accept()));}}
 static String createRoom(String host,String category,String difficulty,int count,int seconds,int max,List<Question> q){String code;do{code=String.format("%06d",RANDOM.nextInt(1_000_000));}while(ROOMS.containsKey(code));ROOMS.put(code,new Room(code,category,difficulty,count,seconds,max,q));return code;}
 static Room room(String code){return ROOMS.get(code);}
}
