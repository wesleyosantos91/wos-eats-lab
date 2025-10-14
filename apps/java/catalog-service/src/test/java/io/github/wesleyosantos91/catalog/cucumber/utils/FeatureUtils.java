package io.github.wesleyosantos91.catalog.cucumber.utils;

/**
 * Utilitários para construção de URLs em testes de features.
 */
public class FeatureUtils {

    private static final String URL = "http://localhost";

    /**
     * Constrói a URL base do host com a porta aleatória do servidor de testes.
     *
     * @param randomServerPort porta aleatória gerada pelo Spring Boot Test
     * @return URL completa (ex: http://localhost:8080)
     */
    public static String getHost(int randomServerPort) {
        return URL + ":" + randomServerPort;
    }

    private FeatureUtils() {
        // Utility class
    }
}
