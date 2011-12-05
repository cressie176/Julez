package uk.co.acuminous.julez.transformer;

import java.util.regex.Pattern;



public class SplitTransformer implements StringTransformer {

    private final Pattern pattern;
    private final String separator;

    public SplitTransformer(String regex, String separator) {
        this.pattern = Pattern.compile(regex);        
        this.separator = separator;
    }

    @Override
    public String transform(String source) {
        return pattern.matcher(source).replaceAll(separator);
    }

}
