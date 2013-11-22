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

    private static final String USERS_COUNT_PATTERN_PROP = "users.count.pattern";

    private static final String NOT_FOUND_PATTERN_PROP = "404.pattern";

    private static final String USERS_THRESHOLD_PROP = "users.threshold";

    private static final String RATING_MIN_PROP = "rating.min";

    private static final String RATING_MAX_PROP = "rating.max";

    public static void main(String args[]) throws IOException {
        String urlSuffix = args[0];
        // load search properties
        Properties props = new Properties();
        props.load(Main.class.getResourceAsStream(PROPERTIES_FILE));

        double minRating = Double.parseDouble(props.getProperty(RATING_MIN_PROP));
        double maxRating = Double.parseDouble(props.getProperty(RATING_MAX_PROP));
        String urlPrefix = props.getProperty(IMDB_URL_PREFIX_PROP);
        String url = urlPrefix + urlSuffix;
        String ratingPattern = props.getProperty(RATING_PATTERN_PROP);
        String usersCountPattern = props.getProperty(USERS_COUNT_PATTERN_PROP);
        int usersThreshold = Integer.parseInt(props.getProperty(USERS_THRESHOLD_PROP));
        String notFoundPattern = props.getProperty(NOT_FOUND_PATTERN_PROP);
        PageParser parser = new PageParser(
                Pattern.compile(ratingPattern),
                Pattern.compile(usersCountPattern),
                Pattern.compile(notFoundPattern));
        if(!parser.parse(url))  {
            System.out.println("inexistant page " + url);
        }
        double rating = parser.getRating();
        if(minRating < rating && rating < maxRating
                && parser.getUserCount() > usersThreshold) {
            System.out.println(url);
        }
    }

}
