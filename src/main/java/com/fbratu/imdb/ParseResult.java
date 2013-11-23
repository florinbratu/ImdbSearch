package com.fbratu.imdb;

/**
 * Author: Florin
 */
public class ParseResult {

    private double rating;

    private int usersCount;

    private final String url;

    private boolean notFound;

    public ParseResult(String url, String usersCount, String rating) {
        this.url = url;
        // read users count
        if(usersCount == null) {
            this.usersCount = PageParser.USER_COUNT_NOT_FOUND;
        } else {
            try {
                this.usersCount = Integer.parseInt(usersCount.replaceAll("[\\,\\.\\s]+",""));
            } catch(NumberFormatException e) {
                this.usersCount = PageParser.USER_COUNT_NOT_FOUND;
            }
        }
        // read ratings
        if(rating == null) {
            this.rating = PageParser.RATING_NOT_FOUND;
        } else {
            try {
                this.rating = Double.parseDouble(rating);
            } catch(NumberFormatException e) {
                this.rating = PageParser.RATING_NOT_FOUND;
            }
        }
    }

    public double getRating() {
        return rating;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public String getUrl() {
        return url;
    }

    public boolean isNotFound() {
        return notFound;
    }

    public void setNotFound(boolean notFound) {
        this.notFound = notFound;
    }
}
