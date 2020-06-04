package com.wyc.ser;

import com.wyc.until.DBUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataSer implements Runnable{

    private Socket socket=null;
    private int width;
    private int height;
    private int tStart;
    private int tEnd;
    public DataSer(Socket socket){
        this.socket=socket;
    }

    public void run() {

        InputStream inputStream=null;
        OutputStream outputStream=null;

        try {
            inputStream=socket.getInputStream();
            outputStream=socket.getOutputStream();
            byte[] bytes=new byte[1024*30];
            int len=inputStream.read(bytes);
            String str=new String(bytes,0,len);
            if (str.contains("-")){
                str=str.trim();
                System.out.println(str);
                width=Integer.parseInt(str.split("-")[0]);
                height=Integer.parseInt(str.split("-")[1]);
                tStart=Integer.parseInt(str.split("-")[2]);
                tEnd=Integer.parseInt(str.split("-")[3]);
                System.out.println(width+"-"+height);
            }
           // String sql="select t,v from data join (select round(?*(t-?)/(?-?)) as k,min(v) as v_min,max(v) as v_max,min(t) as t_min,max(t) as t_max from data group by k) as QA on k=round(?*(t-?)/(?-?)) and (v=v_min or v=v_max or t=t_min or t=t_max)";
            //String sql2="select t,v from data join (select round(200*(t-1)/9999) as k,min(v) as v_min,max(v) as v_max,min(t) as t_min,max(t) as t_max from data group by k) as QA on k=round(200*(t-1)/9999) and (v=v_min or v=v_max or t=t_min or t=t_max)";
            String re=getData();
            byte[] bytes1=re.getBytes();
            System.out.println("sen"+bytes1.length);
            outputStream.write(bytes1,0,bytes1.length);
            outputStream.flush();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void openServer()throws IOException {
        System.out.println("数据服务器启动成功！");
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        ServerSocket serverSocket=new ServerSocket(6000);
        while(true){
            Socket socket=serverSocket.accept();
            executorService.execute(new DataSer(socket));
        }
    }

    public String getData(){
        Connection connection=null;
        StringBuffer result=new StringBuffer();
        try {
            connection= DBUtil.getConnection();
            String sql="select t,v from data join (select round(?*(t-?)/(?-?)) as k,min(v) as v_min,max(v) as v_max,min(t) as t_min,max(t) as t_max from data group by k) as QA on k=round(?*(t-?)/(?-?)) and (v=v_min or v=v_max or t=t_min or t=t_max)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,width);
            preparedStatement.setInt(2,tStart);
            preparedStatement.setInt(3,tEnd);
            preparedStatement.setInt(4,tStart);
            preparedStatement.setInt(5,width);
            preparedStatement.setInt(6,tStart);
            preparedStatement.setInt(7,tEnd);
            preparedStatement.setInt(8,tStart);
            ResultSet resultSet = preparedStatement.executeQuery();
            int flag=0;
            int count=0;
            result.append("");
            while(resultSet.next()){
                if(flag==0){
                    result.append(resultSet.getInt(1)+"+");
                    result.append(resultSet.getInt(2)+"");
                    flag=1;
                }else{
                    result.append("*"+resultSet.getInt(1)+"+");
                    result.append(resultSet.getInt(2)+"");
                }
                count++;
            }
            String re=result.toString();
            re=count+"*"+re;
            return re;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    public static void main(String[] args) {
        try {
            openServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
