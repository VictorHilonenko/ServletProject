package beauty.scheduler.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static beauty.scheduler.util.AppConstants.MESSAGES_BUNDLE_NAME;

public class LocaleUtils {
    private static final Map<String, Locale> enabledLangs = new HashMap<>();
    private static final Map<Locale, ResourceBundle> messagesBundles = new HashMap<>();

    public static final Locale LOCALE_ENGLISH = Locale.ENGLISH;
    public static final Locale LOCALE_UKRAINIAN = Locale.forLanguageTag("uk"); //there is no Locale for "uk" in java.util.Locale yet

    private static final Locale DEFAULT_LOCALE = LOCALE_ENGLISH;

    static {
        enabledLangs.put(LOCALE_ENGLISH.getLanguage(), LOCALE_ENGLISH);
        messagesBundles.put(LOCALE_ENGLISH, ResourceBundle.getBundle(MESSAGES_BUNDLE_NAME, LOCALE_ENGLISH));

        enabledLangs.put(LOCALE_UKRAINIAN.getLanguage(), LOCALE_UKRAINIAN);
        messagesBundles.put(LOCALE_UKRAINIAN, ResourceBundle.getBundle(MESSAGES_BUNDLE_NAME, LOCALE_UKRAINIAN));
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