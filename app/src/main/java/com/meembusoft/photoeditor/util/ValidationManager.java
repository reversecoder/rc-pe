package com.meembusoft.photoeditor.util;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.webkit.URLUtil;

import java.net.URL;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class ValidationManager {

    public static boolean isValidBangladeshiMobileNumber(String phoneNumber) {
        //https://stackoverflow.com/questions/15088518/validate-mobile-number-using-regular-expression
        try {
            String regex = "^(?:\\+?88)?0\\s?1[13-9]\\d{2}(?:\\+?-)?\\d{6}$";
//            String regex = "/(^(\\+8801|8801|01|008801))[1|5-9]{1}(\\d){8}$/";
//            String regex = "\\+?(88)?0?1[56789][0-9]{8}";
            Log.d("phoneNumberVerification", "phoneNumberVerification" + " >>> " + "phoneNumberVerification(isValidBangladeshiMobileNumber): " + phoneNumber);

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(phoneNumber);

            return matcher.matches();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("phoneNumberVerification", "phoneNumberVerification" + " >>> " + "phoneNumberVerification(isValidBangladeshiMobileNumber): " + ex.getMessage());
        }
        return false;
    }

    public static String getBangladeshNumber(String number) {
        String removeSpecialCharacter = number.replace("+", "").replace("-", "").trim();
        String remove88 = (removeSpecialCharacter.startsWith("88") ? removeSpecialCharacter.substring(2, removeSpecialCharacter.length()) : removeSpecialCharacter);
        String addHighfen = remove88.substring(0, 5) + "-" + remove88.substring(5, remove88.length());
        return addHighfen;
    }

    private static String getDigitsOnly(String s) {
        final StringBuffer digitsOnly = new StringBuffer();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (Character.isDigit(c)) {
                digitsOnly.append(c);
            }
        }
        return digitsOnly.toString();
    }

    public static boolean isValidEmail(final String text) {

        if (text.length() == 0) {
            return false;
        }

        // Input the string for validation
        // String email = "xyz@.com";
        // Set the email pattern string
        final Pattern p = Pattern
                .compile("^[a-z][a-z0-9_.]*@[a-z][a-z0-9_.]{1,}\\.[a-z][a-z0-9_]{1,}$");

        // Match the given string with the pattern
        final Matcher m = p.matcher(text);

        // check whether match is found
        final boolean matchFound = m.matches();

        final StringTokenizer st = new StringTokenizer(text, ".");
        String lastToken = null;
        while (st.hasMoreTokens()) {
            lastToken = st.nextToken();
        }

        if (matchFound && lastToken.length() >= 2 && text.length() - 1 != lastToken.length()) {

            // validate the country code
            return true;
        } else {
            return false;
        }
    }

    /*
     * Check pair key of this format: "5A38F3DAE0"
     * */
    public static boolean isValidPairKey(String pairKey) {
        int textLength = pairKey.length();
        if (textLength < 10 || textLength > 10) {
            return false;
        }
        final Pattern p = Pattern.compile("^([A-Z0-9]{10})$");
        // Match the given string with the pattern
        final Matcher m = p.matcher(pairKey);
        // check whether match is found
        boolean matchFound = m.matches();
        return matchFound;
    }

    /*
     * Check license key of this format: "AD6F2204-D591-4F9B-8215-5A38F3DDAAE0"
     * */
    public static boolean isValidLicenseKey(final String licenseKey) {
        int textLength = licenseKey.length();
        if (textLength < 36 || textLength > 36) {
            return false;
        }
        final Pattern p = Pattern.compile("^([A-Z0-9]{8})-([A-Z0-9]{4})-([A-Z0-9]{4})-([A-Z0-9]{4})-([A-Z0-9]{12})$");
        // Match the given string with the pattern
        final Matcher m = p.matcher(licenseKey);
        // check whether match is found
        boolean matchFound = m.matches();
        return matchFound;
    }

    public static boolean isValidUrl(String input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        Pattern URL_PATTERN = Patterns.WEB_URL;
        boolean isURL = URL_PATTERN.matcher(input).matches();
        if (!isURL) {
            String urlString = input + "";
            if (URLUtil.isNetworkUrl(urlString)) {
                try {
                    new URL(urlString);
                    isURL = true;
                } catch (Exception e) {
                }
            }
        }
        return isURL;
    }
}