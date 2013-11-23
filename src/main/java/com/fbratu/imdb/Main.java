package com.fbratu.imdb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Author: Florin
 */
public class Main {

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

        // init configuration
        Configuration conf = new Configuration();
        conf.init();

        PageParser parser = new PageParser(
                Pattern.compile(conf.getRatingPattern()),
                Pattern.compile(conf.getUsersCountPattern()),
                Pattern.compile(conf.getNotFoundPattern()),
                conf.getReadBufferSize());

        String urlSuffix = startIndex;
        int counter = 0;
        int retries = conf.getRetryCount();
        boolean connectionError = false;
        boolean notFound = false;
        int notFounds = conf.getNotFoundCount();
        while(!shutdownRequested) {
            String url = conf.getUrlPrefix() + urlSuffix + "";
            try {
                notFound = !parser.parse(url);
                connectionError = false;
            } catch(ConnectException conne) {
                connectionError = true;
            } catch(SocketException socke) {
                connectionError = true;
            } catch(FileNotFoundException fnfe) {
                notFound = true;
            }
            double rating = parser.getRating();
            if(conf.getMinRating() < rating && rating < conf.getMaxRating()
                    && parser.getUserCount() > conf.getUsersThreshold()) {
                System.out.println(url);
            }
            if(!connectionError && !notFound) {
                urlSuffix = nextIndex(urlSuffix);
                counter++;
                if(counter==conf.getIndexNotificationFrequency()) {
                    System.out.println("Now arriving at " + urlSuffix);
                    counter=0;
                }
                retries = conf.getRetryCount();
                notFounds = conf.getNotFoundCount();
            } else if(notFound) {
                if(notFounds>0) {
                    System.err.println("Inexistant page, dropping:" + url);
                    urlSuffix = nextIndex(urlSuffix);
                    retries = conf.getRetryCount();
                    notFounds--;
                } else {
                    System.err.println(conf.getNotFoundCount() + " consecutive inexistant pages encountered. Aborting.");
                    break;
                }
            } else {
                if(retries == 0) {
                    System.err.println("Dropping " + url);
                    urlSuffix = nextIndex(urlSuffix);
                    retries = conf.getRetryCount();
                } else {
                    System.err.println("Connection error while accessing " + url
                            + " retrying in " + (conf.getRetryFrequency() / 1000) + " secs");
                    try {
                        Thread.sleep(conf.getRetryFrequency());
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
