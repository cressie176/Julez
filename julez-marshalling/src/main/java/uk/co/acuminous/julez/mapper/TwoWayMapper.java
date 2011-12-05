package uk.co.acuminous.julez.mapper;

import java.util.Collection;

public interface TwoWayMapper extends OneWayMapper {
    String getKey(String value);    
    Collection<String> getKeys();
}
