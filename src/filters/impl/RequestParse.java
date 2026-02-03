package filters.impl;

import filters.Filter;
import http.HttpRequest;
import http.HttpResponse;
import java.net.SocketException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RequestParse extends Filter {
    public RequestParse(Filter finalFilter) {
        super(finalFilter);
    }

    @Override
    public HttpRequest process(HttpRequest request) {

        try {
            parseRequest(request);
        }catch (SocketException e) {
            // 处理客户端连接关闭的情况
            if (e.getMessage().contains("Software caused connection abort") || 
                e.getMessage().contains("你的主机中的软件中止了一个已建立的连接")) {
                System.out.println("客户端主动关闭连接，这是正常现象");
            } else {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return filter.process(request);
    }


    /**
     * 解析HTTP请求
     */
    private HttpRequest parseRequest(HttpRequest httpRequest) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(httpRequest.getInputStream()));

        // 解析请求行
        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IOException("Empty request");
        }

        String[] requestParts = requestLine.split(" ", 3);
        if (requestParts.length < 3) {
            throw new IOException("Invalid request line: " + requestLine);
        }

        httpRequest.setMethod(requestParts[0]);
        httpRequest.setUrl(requestParts[1]);
        httpRequest.setProtocol(requestParts[2]);

        // 解析URL参数
        parseUrlParameters(httpRequest);

        // 解析请求头
        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            int colonIndex = headerLine.indexOf(':');
            if (colonIndex != -1) {
                String headerName = headerLine.substring(0, colonIndex).trim();
                String headerValue = headerLine.substring(colonIndex + 1).trim();
                httpRequest.setHeader(headerName, headerValue);
            }
        }

        // 解析请求体（添加异常处理，处理客户端关闭连接的情况）
        if (httpRequest.getHeader("Content-Length") != null) {
            try {
                int contentLength = Integer.parseInt(httpRequest.getHeader("Content-Length"));
                char[] bodyChars = new char[contentLength];
                int bytesRead = 0;
                while (bytesRead < contentLength) {
                    int read = reader.read(bodyChars, bytesRead, contentLength - bytesRead);
                    if (read == -1) {
                        // 客户端可能已关闭连接，记录日志并返回
                        System.out.println("客户端关闭连接，请求体未完全读取");
                        break;
                    }
                    bytesRead += read;
                }
                if (bytesRead > 0) {
                    httpRequest.setBody(new String(bodyChars, 0, bytesRead));
                    // 解析POST参数
                    if ("POST".equals(httpRequest.getMethod()) && httpRequest.getHeader("Content-Type") != null &&
                            httpRequest.getHeader("Content-Type").contains("application/x-www-form-urlencoded")) {
                        parsePostParameters(httpRequest);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("无效的Content-Length格式");
            } catch (SocketException e) {
                System.out.println("客户端关闭连接，无法读取请求体");
            }
        }


        return httpRequest;
    }
    /**
     * 解析URL参数
     */
    private void parseUrlParameters(HttpRequest httpRequest) {
        int queryIndex = httpRequest.getUrl().indexOf('?');
        if (queryIndex != -1) {
            String queryString = httpRequest.getUrl().substring(queryIndex + 1);
            httpRequest.setUrl(httpRequest.getUrl().substring(0, queryIndex));
            parseParameters(queryString, httpRequest);
        }
    }

    /**
     * 解析POST参数
     */
    private void parsePostParameters(HttpRequest httpRequest) {
        if (httpRequest.getBody() != null && !httpRequest.getBody().isEmpty()) {
            parseParameters(httpRequest.getBody(), httpRequest);
        }
    }

    /**
     * 解析参数字符串
     */
    private void parseParameters(String paramString, HttpRequest httpRequest) {
        String[] pairs = paramString.split("&");
        for (String pair : pairs) {
            int equalsIndex = pair.indexOf('=');
            if (equalsIndex != -1) {
                String name = pair.substring(0, equalsIndex);
                String value = pair.substring(equalsIndex + 1);
                httpRequest.setParameter(name, value);
            } else {
                httpRequest.setParameter(pair, "");
            }
        }
    }

}