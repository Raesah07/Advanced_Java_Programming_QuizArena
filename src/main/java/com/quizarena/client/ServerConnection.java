package com.quizarena.client;
import java.io.*; import java.net.*; import java.util.function.Consumer;
public class ServerConnection implements AutoCloseable {
 private final Socket socket; private final BufferedReader in; private final PrintWriter out;
 public ServerConnection(String host,int port,Consumer<String> listener)throws IOException{socket=new Socket(host,port);in=new BufferedReader(new InputStreamReader(socket.getInputStream()));out=new PrintWriter(socket.getOutputStream(),true);Thread t=new Thread(()->{try{String s;while((s=in.readLine())!=null)listener.accept(s);}catch(IOException ignored){}});t.setDaemon(true);t.start();}
 public void send(String s){out.println(s);} public void close()throws IOException{socket.close();}
}
