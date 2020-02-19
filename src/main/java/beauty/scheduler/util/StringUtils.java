package beauty.scheduler.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringUtils {

    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.isEmpty();
    }

    public static int stringToInt(String str) {
        if (str.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String camelCase(String prefix, String str, boolean firstLetterUp) {
        StringBuffer sb = new StringBuffer();
        sb.append(prefix);
        if (firstLetterUp) {
            sb.append(str.substring(0, 1).toUpperCase());
        } else {
            sb.append(str.substring(0, 1).toLowerCase());
        }
        sb.append(str.substring(1));
        return sb.toString();
    }

    public static boolean matchesRegex(String regex, String value) {
        if ("".equals(regex)) {
            return true;
        }
        if (value == null) {
            return false;
        }

        Pattern pattern;
        try {
            pattern = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            return false;
        }

        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static int count(String str, String target) {
        return (str.length() - str.replace(target, "").length()) / target.length();
    }
}