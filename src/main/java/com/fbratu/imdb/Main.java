package com.fbratu.imdb;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Author: Florin
 */
public class Main {

    private static final String IMDB_URL_PREFIX = "http://www.imdb.com/title/tt";

    private static final String RATING_PATTERN = "<div class=\"titlePageSprite star-box-giga-star\"> (\\d.\\d) </div>";

    public static void main(String args[]) {
        String url = IMDB_URL_PREFIX + args[0];
        PageParser parser = new PageParser(Pattern.compile(RATING_PATTERN));
        try {
            parser.parse(url);
            System.out.println("Rating for " + url + " is " + parser.getRating());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
