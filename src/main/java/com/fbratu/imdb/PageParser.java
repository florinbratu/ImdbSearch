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

    public PageParser(Pattern ratingPattern, Pattern usersCountPattern, Pattern notFoundPattern, int readBufferSize) {
        this.ratingPattern = ratingPattern;
        this.usersCountPattern = usersCountPattern;
        this.notFoundPattern = notFoundPattern;
        readBuffer = new char[readBufferSize];
    }

    public ParseResult parse(String url) throws IOException {
        URL urlToOpen = new URL(url);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(
                        urlToOpen.openStream()));
        StringBuilder builder = new StringBuilder();
        while (bufferedReader.read(readBuffer, 0 , readBuffer.length) != -1) {
            builder.append(readBuffer);
        }
        String content = builder.toString();
        // check for 404
        Matcher notFoundMatcher = notFoundPattern.matcher(content);
        boolean notFound = false;
        if(notFoundMatcher.find()) {
            notFound = true;
        }
        Matcher ratingMatcher = ratingPattern.matcher(content);
        String rating;
        if(ratingMatcher.find()) {
            rating = ratingMatcher.group(1);
        } else {
            rating = null;
        }
        Matcher usersCountMatcher = usersCountPattern.matcher(content);
        String usersCount;
        if(usersCountMatcher.find()) {
            usersCount = usersCountMatcher.group(1);
        } else {
            usersCount = null;
        }
        ParseResult result = new ParseResult(url,usersCount,rating);
        result.setNotFound(notFound);
        return result;
    }
}
