package uk.co.acuminous.julez.mapper;

import java.util.Collection;

public interface OneWayMapper {
    String getValue(String key);    
    Collection<String> getValues();           
}
