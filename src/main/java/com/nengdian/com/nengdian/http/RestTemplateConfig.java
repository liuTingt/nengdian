package com.nengdian.com.nengdian.http;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class RestTemplateConfig {
    private static final Logger logger = LoggerFactory.getLogger(RestTemplateConfig.class);

    // RestTemplate
    @Bean
    public RestTemplate createRestTemplate() throws Exception {

        //设置converter
        List<HttpMessageConverter<?>> messageConverters = Lists.newArrayList();
        ByteArrayHttpMessageConverter byteArrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        ResourceHttpMessageConverter resourceHttpMessageConverter = new ResourceHttpMessageConverter();
        SourceHttpMessageConverter sourceHttpMessageConverter = new SourceHttpMessageConverter();
        AllEncompassingFormHttpMessageConverter allEncompassingFormHttpMessageConverter = new AllEncompassingFormHttpMessageConverter();
        Jaxb2RootElementHttpMessageConverter messageConverter = new Jaxb2RootElementHttpMessageConverter();
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

        messageConverters.add(stringHttpMessageConverter);
        messageConverters.add(byteArrayHttpMessageConverter);
        messageConverters.add(resourceHttpMessageConverter);
        messageConverters.add(sourceHttpMessageConverter);
        messageConverters.add(byteArrayHttpMessageConverter);
        messageConverters.add(allEncompassingFormHttpMessageConverter);
        messageConverters.add(mappingJackson2HttpMessageConverter);
        messageConverters.add(messageConverter);
        RestTemplate rest = new RestTemplate(messageConverters);

        ClientHttpRequestFactory factory = ClientHttpRequestFactoryUtils.getHttpComponentsClientHttpRequestFactory();
        rest.setRequestFactory(factory);
        rest.setErrorHandler(new DefaultResponseErrorHandler());
        logger.info("RestTemplate 初始化完成.");
        return rest;
    }
}
