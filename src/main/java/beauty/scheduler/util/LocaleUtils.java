package beauty.scheduler.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static beauty.scheduler.util.AppConstants.MESSAGES_BUNDLE_NAME;

public class LocaleUtils {
    private static final Map<String, Locale> enabledLangs = new HashMap<>();
    private static final Map<Locale, ResourceBundle> messagesBundles = new HashMap<>();
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    static {
        Locale enLocale = Locale.ENGLISH;
        enabledLangs.put(enLocale.getLanguage(), Locale.ENGLISH);
        messagesBundles.put(enLocale, ResourceBundle.getBundle(MESSAGES_BUNDLE_NAME, enLocale));

        Locale ukLocale = Locale.forLanguageTag("uk"); //there is no Locale for "uk" yet
        enabledLangs.put(ukLocale.getLanguage(), ukLocale);
        messagesBundles.put(ukLocale, ResourceBundle.getBundle(MESSAGES_BUNDLE_NAME, ukLocale));
    }

    public static String getLocalizedMessage(String message, String lang) {
        Locale locale = enabledLangs.getOrDefault(lang, DEFAULT_LOCALE);
        ResourceBundle bundle = messagesBundles.get(locale);

        return bundle.getString(message);
    }

    public static boolean getLangIsEnabled(String lang) {
        return enabledLangs.containsKey(lang.toLowerCase());
    }

    public static Locale getDefaultLocale() {
        return DEFAULT_LOCALE;
    }
}