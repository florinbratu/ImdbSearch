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

    private final Pattern ratingPattern;

    private String rating;

    public PageParser(Pattern ratingPattern) {
        this.ratingPattern = ratingPattern;
    }

    public void parse(String url) throws IOException {
        URL urlToOpen = new URL(url);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(
                        urlToOpen.openStream()));
        String s;
        StringBuilder builder = new StringBuilder();
        while ((s = bufferedReader.readLine()) != null) {
            builder.append(s);
        }
        Matcher matcher = ratingPattern.matcher(builder.toString());
        if(matcher.find()) {
            rating = matcher.group(1);
        } else {
            rating = null;
        }
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
}
