package com.shilko.ru;

import java.util.Locale;
import java.util.ResourceBundle;

public class Resource {
    private ResourceBundle resourceBundle;
    private String path;
    public Resource(String path, String locale) {
        this.path = path;
        reLocale(locale);
    }
    public String getString(String key) {
        try {
            return new String(resourceBundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void reLocale(String locale) {
        resourceBundle = ResourceBundle.getBundle(path,Locale.forLanguageTag(locale));
    }
}
