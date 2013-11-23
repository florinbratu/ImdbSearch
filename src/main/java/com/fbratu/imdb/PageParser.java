package com.fbratu.imdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for IMDB movie title pages
 *
 * Author: Florin
 */
public class PageParser {

    public static final double RATING_NOT_FOUND = -1;

    public static final int USER_COUNT_NOT_FOUND = -1;

    private final Pattern ratingPattern;

    private final Pattern usersCountPattern;

    private final Pattern notFoundPattern;

    private final char readBuffer[];

    private String rating;

    private String usersCount;

    public PageParser(Pattern ratingPattern, Pattern usersCountPattern, Pattern notFoundPattern, int readBufferSize) {
        this.ratingPattern = ratingPattern;
        this.usersCountPattern = usersCountPattern;
        this.notFoundPattern = notFoundPattern;
        readBuffer = new char[readBufferSize];
    }

    // return false if 404 page
    public boolean parse(String url) throws IOException {
        URL urlToOpen = new URL(url);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(
                        urlToOpen.openStream()));
        String s;
        StringBuilder builder = new StringBuilder();
        while (bufferedReader.read(readBuffer, 0 , readBuffer.length) != -1) {
            builder.append(readBuffer);
        }
        String content = builder.toString();
        // check for 404
        Matcher notFoundMatcher = notFoundPattern.matcher(content);
        if(notFoundMatcher.find()) {
            return false;
        }
        Matcher ratingMatcher = ratingPattern.matcher(content);
        if(ratingMatcher.find()) {
            rating = ratingMatcher.group(1);
        } else {
            rating = null;
        }
        Matcher usersCountMatcher = usersCountPattern.matcher(content);
        if(usersCountMatcher.find()) {
            usersCount = usersCountMatcher.group(1);
        } else {
            usersCount = null;
        }
        return true;
    }

    public double getRating() {
         if(rating == null)
              return RATING_NOT_FOUND;
        try {
            return Double.parseDouble(rating);
        } catch(NumberFormatException e) {
            return RATING_NOT_FOUND;
        }
    }

    public int getUserCount() {
        if(usersCount == null)
            return USER_COUNT_NOT_FOUND;
        try {
            return Integer.parseInt(usersCount.replaceAll("[\\,\\.\\s]+",""));
        } catch(NumberFormatException e) {
            return USER_COUNT_NOT_FOUND;
        }
    }
}
