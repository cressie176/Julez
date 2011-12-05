package uk.co.acuminous.julez.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class StringUtils {

    @SuppressWarnings("rawtypes")
    public static String join(Collection items) {
        return join(items, "");
    }    

    @SuppressWarnings("rawtypes")    
    public static String join(Collection items, String separator) {
        
        items = items == null ? Collections.emptySet() : items;
        separator = separator == null ? "" : separator;
        
        StringBuilder sb = new StringBuilder();
        int count = 1;
        Iterator it = items.iterator();
        while(it.hasNext()) {
            sb.append(it.next());
            if (count < items.size()) {
                sb.append(separator);
            }
            count++;
        }
        return sb.toString();
    }
}
