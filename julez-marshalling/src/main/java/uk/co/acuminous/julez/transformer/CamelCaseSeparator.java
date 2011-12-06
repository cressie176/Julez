package uk.co.acuminous.julez.transformer;

import java.util.Arrays;

import uk.co.acuminous.julez.util.StringUtils;

public class CamelCaseSeparator extends SplitTransformer {

    private static String REGEX_1 = "(?<=[A-Z])(?=[A-Z][a-z])";
    private static String REGEX_2 = "(?<=[^A-Z])(?=[A-Z])";
    private static String REGEX_3 = "(?<=[A-Za-z])(?=[^A-Za-z])";
    private static String CAMEL_CASE_REGEX = or(REGEX_1, REGEX_2, REGEX_3);

    public CamelCaseSeparator() {
        this(" ");
    }

    public CamelCaseSeparator(String separator) {
        super(CAMEL_CASE_REGEX, separator);
    }

    private static String or(String... regex) {
        return StringUtils.join(Arrays.asList(regex), "|");
    }

}
