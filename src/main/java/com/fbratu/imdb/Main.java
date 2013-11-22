package com.fbratu.imdb;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Author: Florin
 */
public class Main {

    private static final String IMDB_URL_PREFIX_PROP = "imdb.url.prefix";

    private static final String RATING_PATTERN = "<div class=\"titlePageSprite star-box-giga-star\"> (\\d.\\d) </div>";

    private static final String PROPERTIES_FILE = "search.properties";

    public static void main(String args[]) throws IOException {
           String urlSuffix = args[0];
           Main main = new Main();
        main.search(urlSuffix);
    }

    private void search(String urlSuffix ) throws IOException {
        // load search properties
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream(PROPERTIES_FILE));

        String urlPrefix = props.getProperty(IMDB_URL_PREFIX_PROP);
        String url = urlPrefix + urlSuffix;
        PageParser parser = new PageParser(Pattern.compile(RATING_PATTERN));
        parser.parse(url);
        System.out.println("Rating for " + url + " is " + parser.getRating());
    }

}
