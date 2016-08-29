import java.io.*;
import java.net.*;

public class server{
  public static void main(String[] args)
  {
    //for(int i = 0;i<args.length;i++){
      try{
        InetAddress address = InetAddress.getLocalHost();
        System.out.println("ip:"+address.getHostAddress());
      }
      catch(Exception e){
        System.out.println("inetaddress error");
      }
    //}
    try{
      ServerSocket server = new ServerSocket(8888);

      while(true){
          Socket socket = server.accept();
          System.out.println("connection ok!");
          Thread thread = new serverthread(socket);
          thread.start();
      }
    }
    catch(Exception e){
      System.out.println(e);
    }
  }
}

class serverthread extends Thread
{

    //static ServerSocket server = new ServerSocket(8888);
    Socket socket = null;
    public void serverthread(Socket socket){
      this.socket = socket;
    }

    public void getUsername(){

    }

    public void run(){
      try{


          BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
          //PrintWriter out = new PrintWriter(socket.getOutputStream());
          //BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
          String s;
          while(true){
            s = in.readLine();
              System.out.println("client说:"+s);
            if(s.equals("bye")) break;
              //out.println(sin.readLine());
              //out.flush();
          }
          System.out.println("end");
          in.close();
          //out.close();
          socket.close();
          //server.close();
      }
      catch(Exception e){
          System.out.println(e);

      }
    }
}
