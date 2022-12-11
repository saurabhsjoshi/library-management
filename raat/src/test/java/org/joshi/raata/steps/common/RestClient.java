package org.joshi.raata.steps.common;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.message.BasicHeader;

import java.util.List;

public class RestClient {
    private static CloseableHttpClient client = null;

    public static CloseableHttpClient getClient() {
        if (client == null) {
            client = HttpClientBuilder.create()
                    .setDefaultHeaders(List.of(
                            new BasicHeader("Accept", "application/json"),
                            new BasicHeader("Content-type", "application/json")
                    ))
                    .build();
        }
        return client;
    }
}
