package uk.co.acuminous.julez.transformer;

import java.util.regex.Pattern;


public class SubstitutionTransformer implements StringTransformer {

    private final Pattern pattern;
    private final String substitution;

    public SubstitutionTransformer(String regex) {
        this(regex, "");
    }
    
    public SubstitutionTransformer(String regex, String substitution) {
        this.pattern = Pattern.compile(regex);        
        this.substitution = substitution;
    }
    
    @Override
    public String transform(String source) { 
        return pattern.matcher(source).replaceAll(substitution);
    }

}
