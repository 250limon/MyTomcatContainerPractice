package server.impl;

import server.RequestDataString;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.stream.Collectors;

public class RequestDataFromSocket implements RequestDataString {
    @Override
    public String getRequestData(Object dataSource) {
        Socket socket = (Socket) dataSource;
       try{
           BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           StringBuilder requestData = new StringBuilder();
           String line;
           // 读取请求头，直到遇到空行（HTTP请求头结束标志）
           while((line=bufferedReader.readLine())!=null){
               requestData.append(line).append("\r\n");
               // 检查是否遇到空行（请求头结束标志）
               if(line.trim().isEmpty()){
                   break;
               }
           }
           return requestData.toString();
       }catch(Exception e){
           e.printStackTrace();
       }
       return null;

    }
}
