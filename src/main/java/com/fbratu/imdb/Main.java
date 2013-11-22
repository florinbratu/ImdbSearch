package com.fbratu.imdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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

    private static final String START_INDEX_PROP = "index.start";

    private static volatile boolean shutdownRequested = false;

    public static void main(String args[]) throws IOException {
        final Thread mainThread = Thread.currentThread();
        // register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdownRequested = true;
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        // load search properties
        Properties props = new Properties();
        props.load(Main.class.getResourceAsStream(PROPERTIES_FILE));

        double minRating = Double.parseDouble(props.getProperty(RATING_MIN_PROP));
        double maxRating = Double.parseDouble(props.getProperty(RATING_MAX_PROP));
        String urlPrefix = props.getProperty(IMDB_URL_PREFIX_PROP);
        String ratingPattern = props.getProperty(RATING_PATTERN_PROP);
        String usersCountPattern = props.getProperty(USERS_COUNT_PATTERN_PROP);
        int usersThreshold = Integer.parseInt(props.getProperty(USERS_THRESHOLD_PROP));
        String notFoundPattern = props.getProperty(NOT_FOUND_PATTERN_PROP);
        PageParser parser = new PageParser(
                Pattern.compile(ratingPattern),
                Pattern.compile(usersCountPattern),
                Pattern.compile(notFoundPattern));
        String startIndex = props.getProperty(START_INDEX_PROP);
        String urlSuffix = startIndex;
        while(!shutdownRequested) {
            String url = urlPrefix + urlSuffix + "";
            if(!parser.parse(url))  {
                System.out.println("inexistant page " + url);
                break;
            }
            double rating = parser.getRating();
            if(minRating < rating && rating < maxRating
                    && parser.getUserCount() > usersThreshold) {
                System.out.println(url);
            }
            urlSuffix = nextIndex(urlSuffix);
        }
        System.err.println("Search stopped at:" + urlSuffix);
    }

    private static String nextIndex(String index) {
        if(cornerCases.containsKey(index))
            return cornerCases.get(index);
        int firstNonZero;
        for(firstNonZero=0;firstNonZero<index.length() && index.charAt(firstNonZero)=='0';firstNonZero++);
        // now, i has the number of zeros
        int value = Integer.parseInt(index);
        value++;
        return index.substring(0,firstNonZero) + value;
    }

    public static void testNextIndex(String args[]) {
        String testStrings[] = new String[] {
          "0000009",
                "0000099",
                "0000999",
                "0009999",
                "0099999",
                "0999999",
                "0098786",
                "0919999",
                "0976598"
        };
        for(String index:testStrings) {
            System.out.println(nextIndex(index));
        }
    }

    private static final Map<String, String> cornerCases = new HashMap<String, String>();

    static {
        cornerCases.put("0000009","0000010");
        cornerCases.put("0000099","0000100");
        cornerCases.put("0000999","0001000");
        cornerCases.put("0009999","0010000");
        cornerCases.put("0099999","0100000");
        cornerCases.put("0999999","1000000");
    }

}
