package uk.co.acuminous.julez.mapper;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LiteralMapper implements TwoWayMapper {
    
    private final Map<String, String> map;    
    private final Map<String, String> inverseMap;

    public LiteralMapper(Map<String, String> map) {
        this.map = map;        
        this.inverseMap = inverse(map);  
    }

    @Override
    public String getKey(String value) {
        return inverseMap.get(value);
    }

    @Override
    public String getValue(String key) {
        return map.get(key);
    }   
    
    @Override
    public Set<String> getValues() {
        return inverseMap.keySet();
    }
    
    @Override
    public Collection<String> getKeys() {
        return map.keySet();
    }    
    
    private Map<String, String> inverse(Map<String, String> map) {
        Map<String, String> inverseMap = new LinkedHashMap<String, String>();
        for ( Map.Entry<String, String> entry : map.entrySet() ) {
            inverseMap.put(entry.getValue(), entry.getKey());
        }
        return inverseMap;        
    }
}
