package com.better_computer.habitaid.util;

import com.better_computer.habitaid.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    private static final String IPV4_REGEX =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    // This function checks if the IP address supplied is valid
    // @returns -1 in case of a valid IP Address
    public static int isIpAddressValid(String ipAddress) {
        // Let us assume all is good with IP Address
        boolean isValidIP = true;
        // So set the error code to negative
        int errorCode = -1;

        // CHECK - 1:
        // If the IP address supplied is blank or has spaces
        // it is not valid
        if(ipAddress == null || ipAddress.isEmpty()) {
            errorCode = R.string.errTxt_Ip_has_spaces;
        } else {
            // CHECK - 2:
            // If IP address cannot be parsed by the IPAddress class
            // then it is not valid
            Pattern IPv4_PATTERN = Pattern.compile(IPV4_REGEX);
            Matcher matcher = IPv4_PATTERN.matcher(ipAddress);
            if(!matcher.matches()) {
                errorCode = R.string.errTxt_Ip_invalid;
            }
        }

        return errorCode;
    }

}
