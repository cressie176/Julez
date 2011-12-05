package uk.co.acuminous.julez.mapper;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PatternMapper implements OneWayMapper {

    Map<Pattern, String> map;
    
    public PatternMapper(Map<String, String> map) {
        this.map = new LinkedHashMap<Pattern, String>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            this.map.put(Pattern.compile(entry.getKey()), entry.getValue());
        }        
    }
    
    @Override
    public String getValue(String key) {
        for (Pattern pattern : map.keySet()) {
            if (pattern.matcher(key).matches()) {
                return map.get(pattern);
            }            
        }
        return null;
    }

    @Override
    public Collection<String> getValues() {        
        return map.values();
    }

}
