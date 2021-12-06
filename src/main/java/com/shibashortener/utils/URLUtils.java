package com.shibashortener.utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLUtils {

    private static final int URL_LENGTH = 7;

    public static boolean isValid(String shibUrl) {

        try {
            URL url = new URL(shibUrl);
            url.toURI();

            return true;


        } catch (MalformedURLException | URISyntaxException e) {
            //e.printStackTrace();
            return false;
        }
    }

    public static String sanitizeURL(String shibUrl) {
        try {
            URL url = new URL(shibUrl);
            return url.toURI().toString();
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String checkProtocol(String url) {
        if(url.indexOf("https://") == -1 && url.indexOf("http://") == -1 ) url = "http://"+url;
        return url;
    }

    public static boolean isShibShortenedUrl(String id) {

        Pattern p = Pattern.compile("[A-Za-z0-9]");



        if(isValid(id)) {

            try {
                URI uri = new URI(id);
                String path = uri.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);

                if(idStr.length() > URL_LENGTH) {
                    return false;
                }

                return true;

            } catch (URISyntaxException e) {
                e.printStackTrace();
                return false;
            }

        }else if( p.matcher(id).find() && id.length() == URL_LENGTH) {
            return true;
        }

        return false;
    }


}
