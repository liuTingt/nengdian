package com.nengdian.com.nengdian.http;

import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultServiceUnavailableRetryStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;


public class ClientHttpRequestFactoryUtils {
    private static Integer maxConnTotal = 200000;

    private static Integer maxConnPerRoute = 5000;

    private static Integer connectTimeOut = 1000;

    private static Integer readTimeout = 30000;

    public static ClientHttpRequestFactory getHttpComponentsClientHttpRequestFactory() throws Exception {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setMaxConnPerRoute(maxConnPerRoute);
        builder.setMaxConnTotal(maxConnTotal);
        builder.setRetryHandler((exception, executionCount, context) -> {
            if (executionCount >= 3) {
                return false;
            }
            if (exception instanceof InterruptedIOException) {
                // Timeout
                return false;
            }
            if (exception instanceof UnknownHostException) {
                // Unknown host
                return false;
            }
            if (exception instanceof SSLException) {
                // SSL handshake exception
                return false;
            }
            return true;
        });
        ServiceUnavailableRetryStrategy serviceUnavailableRetryStrategy = new DefaultServiceUnavailableRetryStrategy();
        builder.setServiceUnavailableRetryStrategy(serviceUnavailableRetryStrategy);
        builder.setConnectionReuseStrategy(new NoConnectionReuseStrategy());
        //忽略https证书认证
        TrustStrategy trustStrategy = (((x509Certificates, s) -> true));
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(trustStrategy).build();
        SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        builder.setSSLSocketFactory(connectionSocketFactory);
        // 设置重试一次
        CloseableHttpClient request = builder.build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(
                request);
        factory.setConnectTimeout(connectTimeOut);
        factory.setReadTimeout(readTimeout);
        // 连接不够用的等待时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
        factory.setConnectionRequestTimeout(50);
        return factory;
    }
}
