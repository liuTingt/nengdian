package com.nengdian.com.nengdian.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Component
public class HttpUtil {
    private  final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 发送POST请求
     *
     * @param url        请求url
     * @param returnType 返回类型,必须重写toString()方法,否则不能正确记录日志信息
     * @return 指定的返回类型
     */
    public  final <T> T doPostByJson(String url, Class<T> returnType) {
        return doPostByJson(url, null, returnType);
    }

    /**
     * 发送POST请求
     *
     * @param url        请求url
     * @param data       发送的数据,必须重写toString()方法,否则不能正确记录日志信息
     * @param returnType 返回类型,必须重写toString()方法,否则不能正确记录日志信息
     * @return 指定的返回类型
     */
    public  final <T, E> T doPostByJson(String url, E data, Class<T> returnType) {
        return doPost(url, data, MediaType.APPLICATION_JSON_UTF8, returnType);
    }

    /**
     * 发送POST请求
     *
     * @param url         请求url
     * @param data        发送的数据,必须重写toString()方法,否则不能正确记录日志信息
     * @param headers 请求头信息
     * @param returnType  返回类型,必须重写toString()方法,否则不能正确记录日志信息
     * @return 指定的返回类型
     */
    public  final <T, E> T doPostByJson(String url, E data, Map<String, String> headers, Class<T> returnType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (!CollectionUtils.isEmpty(headers)) {
            httpHeaders.setAll(headers);
        }
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return doPost(url, data, httpHeaders, returnType);
    }

    /**
     * 发送POST请求
     *
     * @param url        请求url
     * @param data       发送的数据,必须重写toString()方法,否则不能正确记录日志信息
     * @param returnType 返回类型,必须重写toString()方法,否则不能正确记录日志信息
     * @return 指定的返回类型
     */
    public  final <T> T doPostByFormData(String url, MultiValueMap<String, String> data, Class<T> returnType) {
        return doPost(url, data, MediaType.APPLICATION_FORM_URLENCODED, returnType);
    }

    /**
     * 发送GET请求
     *
     * @param url   请求url
     * @param clazz 返回类型,必须重写toString()方法,否则不能正确记录日志信息
     * @return 指定的返回类型
     */
    public  final <T> T doGet(String url, Class<T> clazz) {
        log.info("GET_REQUEST: {}, {}", url, clazz.getName());
        T result = restTemplate.getForObject(url, clazz);
        log.info("GET_RESPONSE: {}", result);

        return result;
    }

    /**
     * 发送POST请求
     *
     * @param url         请求url
     * @param data        发送的数据,必须重写toString()方法,否则不能正确记录日志信息
     * @param requestType 请求头类型
     * @param returnType  返回类型,必须重写toString()方法,否则不能正确记录日志信息
     * @return 指定的返回类型
     */
    public  final <T, E> T doPost(String url, E data, MediaType requestType, Class<T> returnType) {
        log.info("POST_REQUEST: {}, {}, {}, {}", url, data, requestType, returnType.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(requestType);
        HttpEntity<E> entity = new HttpEntity<>(data, headers);
        T result = restTemplate.postForObject(url, entity, returnType);
        log.info("POST_RESPONSE: {}", result);
        return result;
    }

    /**
     * 发送POST请求
     *
     * @param url         请求url
     * @param data        发送的数据,必须重写toString()方法,否则不能正确记录日志信息
     * @param headers 请求头信息
     * @param returnType  返回类型,必须重写toString()方法,否则不能正确记录日志信息
     * @return 指定的返回类型
     */
    public  final <T, E> T doPost(String url, E data, HttpHeaders headers, Class<T> returnType) {
        log.info("POST_REQUEST: {}, {}, {}, {}", url, data, headers, returnType.getName());
        HttpEntity<E> entity = new HttpEntity<>(data, headers);
        T result = restTemplate.postForObject(url, entity, returnType);
        log.info("POST_RESPONSE: {}", result);
        return result;
    }
}
