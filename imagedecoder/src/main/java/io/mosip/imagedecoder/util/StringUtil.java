package io.mosip.imagedecoder.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtil {
	private static Logger LOGGER = LoggerFactory.getLogger(StringUtil.class);
	
	public static int stringCompare(String str1, String str2)
    { 
		int l1 = str1.length();
        int l2 = str2.length();
        int lmin = Math.min(l1, l2);
  
        for (int i = 0; i < lmin; i++) {
            int str1_ch = (int)str1.charAt(i);
            int str2_ch = (int)str2.charAt(i);
  
            if (str1_ch != str2_ch) {
                return str1_ch - str2_ch;
            }
        }
  
        // Edge case for strings like
        // String 1="Geeks" and String 2="Geeksforgeeks"
        if (l1 != l2) {
            return l1 - l2;
        }
  
        // If none of the above conditions is true,
        // it implies both the strings are equal
        else {
            return 0;
        }
    }
	
	public static int atoi(char[] str)
    {
        int sign = 1, base = 0, i = 0;
 
        // if whitespaces then ignore.
        while (str[i] == ' ')
        {
            i++;
        }
 
        // sign of number
        if (str[i] == '-' || str[i] == '+')
        {
            sign = 1 - 2 * (str[i++] == '-' ? 1 : 0);
        }
 
        // checking for valid input
        while (i < str.length
               && str[i] >= '0'
               && str[i] <= '9') {
 
            // handling overflow test case
            if (base > Integer.MAX_VALUE / 10
                || (base == Integer.MAX_VALUE / 10
                    && str[i] - '0' > 7))
            {
                if (sign == 1)
                    return Integer.MAX_VALUE;
                else
                    return Integer.MIN_VALUE;
            }
            base = 10 * base + (str[i++] - '0');
        }
        return base * sign;
    }
	
	/**
     * Returns the double value of s.
     * 
     * @param s
     *            the string containing the floating point number
     * @return the value of the floating point number in s
     */
    public static double atof(String s) {
        int i, numMinuses = 0, numDots = 0;

        s = s.trim();
        for (i = 0; i < s.length()
                && (s.charAt(i) == '-' || s.charAt(i) == '.' || Character.isDigit(s.charAt(i))); i++)
            if (s.charAt(i) == '-')
                numMinuses++;
            else if (s.charAt(i) == '.')
                numDots++;

        if (i != 0 && numMinuses < 2 && numDots < 2)
            return Double.parseDouble(s.substring(0, i));

        return 0.0;
    }
}
