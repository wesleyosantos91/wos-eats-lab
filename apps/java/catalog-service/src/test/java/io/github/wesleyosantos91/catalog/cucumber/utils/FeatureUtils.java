package io.github.wesleyosantos91.catalog.cucumber.utils;

public class FeatureUtils {

    private static final String URL = "http://localhost";

    public static String getHost(int randomServerPort) {
        return URL + ":" + randomServerPort;
    }

    private FeatureUtils() {
    }
}
