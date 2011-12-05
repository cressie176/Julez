package uk.co.acuminous.julez.mapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.co.acuminous.julez.transformer.StringTransformer;

public class TransformingMapper implements TwoWayMapper {

    private LiteralMapper literalMapper;

    public TransformingMapper(StringTransformer keyTransformer, String... keys) {
        this(keyTransformer, Arrays.asList(keys));
    }
    
    public TransformingMapper(StringTransformer keyTransformer, Collection<String> keys) {        
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (String key : keys) {
            map.put(key, keyTransformer.transform(key));
        }
        this.literalMapper = new LiteralMapper(map);        
    }
    
    @Override
    public String getKey(String value) {
        return literalMapper.getKey(value);
    }

    @Override
    public String getValue(String key) {
        return literalMapper.getValue(key);
    }

    @Override
    public Collection<String> getKeys() {
        return literalMapper.getKeys();
    }

    @Override
    public Collection<String> getValues() {
        return literalMapper.getValues();
    }
}
