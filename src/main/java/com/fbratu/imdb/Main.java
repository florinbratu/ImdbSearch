package com.fbratu.imdb;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Author: Florin
 */
public class Main {

    private static final String PROPERTIES_FILE = "search.properties";

    private static final String IMDB_URL_PREFIX_PROP = "imdb.url.prefix";

    private static final String RATING_PATTERN_PROP = "rating.pattern";

    private static final String RATING_MIN_PROP = "rating.min";

    private static final String RATING_MAX_PROP = "rating.max";

    public static void main(String args[]) throws IOException {
        String urlSuffix = args[0];
        Main main = new Main();
        main.search(urlSuffix);
    }

    private void search(String urlSuffix) throws IOException {
        // load search properties
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream(PROPERTIES_FILE));

        double minRating = Double.parseDouble(props.getProperty(RATING_MIN_PROP));
        double maxRating = Double.parseDouble(props.getProperty(RATING_MAX_PROP));
        String urlPrefix = props.getProperty(IMDB_URL_PREFIX_PROP);
        String url = urlPrefix + urlSuffix;
        String ratingPattern = props.getProperty(RATING_PATTERN_PROP);
        PageParser parser = new PageParser(Pattern.compile(ratingPattern));
        parser.parse(url);
        double rating = parser.getRating();
        if(minRating < rating && rating < maxRating) {
            System.out.println(url);
        }
    }

}
