package filters.impl;

import filters.Filter;
import http.HttpRequest;
import http.HttpResponse;

public class FilterImpl extends Filter {

    public FilterImpl(Filter requestParesFilter) {
        super(requestParesFilter);
    }

    @Override
    public HttpRequest process(HttpRequest request) {

        if(request.hasHandled())
            return request;
        return this.filter.process(request);
    }
}
