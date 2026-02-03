package filters;

import filters.impl.FilterImpl;
import filters.impl.FinalFilter;
import filters.impl.RequestParse;

public class FilterManager {
    private static FilterManager filterManager=new FilterManager();
    private Filter filter;
    private FilterManager(){
    }
    public void setFirstFilter(Filter firstFilter){
        filter=firstFilter;
    }
    public Filter getFirstFilter()
    {
        return filter;
    }
    public static  FilterManager getInstance()
    {
        return filterManager;
    }
}
