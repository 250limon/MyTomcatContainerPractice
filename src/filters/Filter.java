package filters;

import http.HttpRequest;
import http.HttpResponse;

public abstract class Filter {
    protected Filter filter;
    public Filter(Filter filter) {
        this.filter = filter;
    }

    public Filter() {
    }

    public abstract HttpRequest process(HttpRequest request);
}
