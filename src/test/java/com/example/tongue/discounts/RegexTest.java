package com.example.tongue.discounts;

import java.util.regex.Pattern;

public class RegexTest {
    public void testProductRegex(){
        String handleRegex = "[a-zA-z0-9]{1,50}";
        String tagsRegex = "([a-zA-Z0-9]{1,10},){1,12}[a-zA-Z0-9]{1,10}";
        //Boolean matches = Pattern.matches(regex,product.getTags());
        String statusRegex = "(active|draft|archived)";
        String[] regexSet = {handleRegex,tagsRegex,statusRegex};
        String[][] testStrings = {{"a  asds24","09asdasd","0292 asdsd"},
                {"asdb,123","asd,sdsds,2323a,ADAD","a,s,d,d,d,d,d,d6,d,d,d,d,d55, 2 2- 5 2 , ","123456789012-ABB"},
                {"proactive","active","activedraft","archived"}};
        for (int i = 0; i < regexSet.length; i++) {
            String regex = regexSet[i];
            String[] testSet = testStrings[i];
            for (int j = 0; j < testSet.length; j++) {
                Boolean matches = Pattern.matches(regex,testSet[j]);
                System.out.println("Test: "+testSet[j]);
                System.out.println("Regex: "+regex);
                System.out.println("Result: "+matches);
            }
        }
    }
}
