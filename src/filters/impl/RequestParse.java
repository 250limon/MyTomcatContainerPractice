package filters.impl;

import filters.Filter;
import http.HttpRequest;
import http.HttpResponse;

import java.io.*;
import java.net.SocketException;

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
    private void parseRequest(HttpRequest httpRequest) throws IOException {
            String requestData = httpRequest.getRequestData();
            if (requestData == null || requestData.isEmpty()) {
                throw new IOException("Empty request");
            }

            // 分割请求为行
            String[] lines = requestData.split("\r\n");
            if (lines.length == 0) {
                throw new IOException("Invalid request format");
            }

            // 解析请求行
            String requestLine = lines[0];
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
            int bodyStartIndex = -1;
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.isEmpty()) {
                    // 空行表示请求头结束，请求体开始
                    bodyStartIndex = i + 1;
                    break;
                }

                int colonIndex = line.indexOf(':');
                if (colonIndex != -1) {
                    String headerName = line.substring(0, colonIndex).trim();
                    String headerValue = line.substring(colonIndex + 1).trim();
                    httpRequest.setHeader(headerName, headerValue);
                }
            }

            // 解析请求体
            if (bodyStartIndex > 0 && bodyStartIndex < lines.length) {
                // 构建请求体
                StringBuilder bodyBuilder = new StringBuilder();
                for (int i = bodyStartIndex; i < lines.length; i++) {
                    bodyBuilder.append(lines[i]);
                    if (i < lines.length - 1) {
                        bodyBuilder.append("\r\n");
                    }
                }

                String body = bodyBuilder.toString();
                if (!body.isEmpty()) {
                    httpRequest.setBody(body);

                    // 解析POST参数
                    if ("POST".equals(httpRequest.getMethod()) && httpRequest.getHeader("Content-Type") != null &&
                            httpRequest.getHeader("Content-Type").contains("application/x-www-form-urlencoded")) {
                        parsePostParameters(httpRequest);
                    }
                }
            }
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