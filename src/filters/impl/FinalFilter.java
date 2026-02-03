package filters.impl;

import filters.Filter;
import http.HttpRequest;

public class FinalFilter extends Filter {



    @Override
    public HttpRequest process(HttpRequest request) {
        return request;
    }
}
