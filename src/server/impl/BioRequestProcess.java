package server.impl;

import filters.FilterManager;
import http.HttpRequest;
import http.HttpResponse;
import http.impl.BioHttpResponse;
import server.Container;
import server.RequestProcess;

import java.net.Socket;

public class BioRequestProcess extends RequestProcess {
    private Container container;
    public BioRequestProcess(Container container) {
        super(container);
    }

    @Override
    public HttpRequest filter(HttpRequest request) {
        return FilterManager.getInstance().getFirstFilter().process(request);
    }

    @Override
    public HttpResponse createResponse(HttpRequest request)  {

        try{
            return new BioHttpResponse(request.getRequestData(),(Socket)request.getSource());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
