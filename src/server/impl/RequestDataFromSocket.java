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
           return bufferedReader.lines().collect(Collectors.joining("\n"));
       }catch(Exception e){
           e.printStackTrace();
       }
       return null;

    }
}
