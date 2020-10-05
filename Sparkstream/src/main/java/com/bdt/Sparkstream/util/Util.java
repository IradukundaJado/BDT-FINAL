package com.bdt.Sparkstream.util;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util {


    private  static final Pattern HASHTAG= Pattern.compile("#\\w+");

    public  static Iterator<String> hashTagsFromTweet(String text) {
        List<String> hashTags = new ArrayList<>();
        Matcher matcher = HASHTAG.matcher(text);
        while (matcher.find()) {
            String res = matcher.group();
            hashTags.add(res);
        }
        return hashTags.iterator();
    }


}
