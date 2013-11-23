package com.fbratu.imdb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
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

    private static final String INDEX_NOTIFICATION_FREQ_PROP = "index.notification.frequency";

    private static final String TIMEOUT_RETRY_COUNT_PROP = "timeout.retry.count";

    private static final String TIMEOUT_RETRY_FREQ_PROP = "timeout.retry.frequency";

    private static final String READ_BUFFER_SIZE_PROP = "read.buffer.size";

    private static final String MAX_NOT_FOUND_PROP = "missing.pages.max";

    private static volatile boolean shutdownRequested = false;

    public static void main(String args[]) throws IOException {
        if(args.length!=1) {
            System.err.println("Missing mandatory parameter: the index from which to start the search!");
            System.exit(1);
        }
        String startIndex = args[0];
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
        int readBufferSize = Integer.parseInt(props.getProperty(READ_BUFFER_SIZE_PROP));
        PageParser parser = new PageParser(
                Pattern.compile(ratingPattern),
                Pattern.compile(usersCountPattern),
                Pattern.compile(notFoundPattern),
                readBufferSize);

        int indexNotificationFrequency = Integer.parseInt(props.getProperty(INDEX_NOTIFICATION_FREQ_PROP));
        String urlSuffix = startIndex;
        int counter = 0;
        int retryCount = Integer.parseInt(props.getProperty(TIMEOUT_RETRY_COUNT_PROP));
        int retries = retryCount;
        long retryFrequency = Long.parseLong(props.getProperty(TIMEOUT_RETRY_FREQ_PROP));
        boolean connectionTimedOut = false;
        boolean notFound = false;
        int notFoundCount = Integer.parseInt(props.getProperty(MAX_NOT_FOUND_PROP));
        int notFounds = notFoundCount;
        while(!shutdownRequested) {
            String url = urlPrefix + urlSuffix + "";
            try {
                notFound = !parser.parse(url);
                connectionTimedOut = false;
            } catch(ConnectException conne) {
                connectionTimedOut = true;
            } catch(FileNotFoundException fnfe) {
                notFound = true;
            }
            double rating = parser.getRating();
            if(minRating < rating && rating < maxRating
                    && parser.getUserCount() > usersThreshold) {
                System.out.println(url);
            }
            if(!connectionTimedOut && !notFound) {
                urlSuffix = nextIndex(urlSuffix);
                counter++;
                if(counter==indexNotificationFrequency) {
                    System.out.println("Now arriving at " + urlSuffix);
                    counter=0;
                }
                retries = retryCount;
                notFounds = notFoundCount;
            } else if(notFound) {
                if(notFounds>0) {
                    System.err.println("Inexistant page, dropping:" + url);
                    urlSuffix = nextIndex(urlSuffix);
                    retries = retryCount;
                    notFounds--;
                } else {
                    System.err.println(notFoundCount + " consecutive inexistant pages encountered. Aborting.");
                    break;
                }
            } else {
                if(retries == 0) {
                    System.err.println("Dropping " + url);
                    urlSuffix = nextIndex(urlSuffix);
                    retries = retryCount;
                } else {
                    System.err.println("Could not access " + url + " retrying in " + (retryFrequency / 1000) + " secs");
                    try {
                        Thread.sleep(retryFrequency);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    retries--;
                }
            }
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
