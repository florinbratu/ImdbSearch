package com.fbratu.imdb;

import java.io.IOException;
import java.util.Properties;

/**
 * Author: Florin
 */
public class Configuration {

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

    private double minRating;
    private double maxRating;
    private String urlPrefix;
    private String ratingPattern;
    private String usersCountPattern;
    private int usersThreshold;
    private String notFoundPattern;
    private int readBufferSize;

    private int indexNotificationFrequency;
    private int retryCount;
    private long retryFrequency;
    private int notFoundCount;

    public Configuration() {
    }

    // load search properties
    public void init() throws IOException {
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream(PROPERTIES_FILE));
        minRating = Double.parseDouble(props.getProperty(RATING_MIN_PROP));
        maxRating = Double.parseDouble(props.getProperty(RATING_MAX_PROP));
        urlPrefix = props.getProperty(IMDB_URL_PREFIX_PROP);
        ratingPattern = props.getProperty(RATING_PATTERN_PROP);
        usersCountPattern = props.getProperty(USERS_COUNT_PATTERN_PROP);
        usersThreshold = Integer.parseInt(props.getProperty(USERS_THRESHOLD_PROP));
        notFoundPattern = props.getProperty(NOT_FOUND_PATTERN_PROP);
        readBufferSize = Integer.parseInt(props.getProperty(READ_BUFFER_SIZE_PROP));
        indexNotificationFrequency = Integer.parseInt(props.getProperty(INDEX_NOTIFICATION_FREQ_PROP));
        retryCount = Integer.parseInt(props.getProperty(TIMEOUT_RETRY_COUNT_PROP));
        retryFrequency = Long.parseLong(props.getProperty(TIMEOUT_RETRY_FREQ_PROP));
        notFoundCount = Integer.parseInt(props.getProperty(MAX_NOT_FOUND_PROP));
    }

    public double getMinRating() {
        return minRating;
    }

    public double getMaxRating() {
        return maxRating;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public String getRatingPattern() {
        return ratingPattern;
    }

    public String getUsersCountPattern() {
        return usersCountPattern;
    }

    public int getUsersThreshold() {
        return usersThreshold;
    }

    public String getNotFoundPattern() {
        return notFoundPattern;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public int getIndexNotificationFrequency() {
        return indexNotificationFrequency;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public long getRetryFrequency() {
        return retryFrequency;
    }

    public int getNotFoundCount() {
        return notFoundCount;
    }
}
