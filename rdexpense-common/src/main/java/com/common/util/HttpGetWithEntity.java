package com.common.util;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;


/**
 * http请求体构造类
 * @Author: rdexpense
 * @Date: 2020/4/24 11:25
 */
public class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {
    public final static String METHOD_NAME = "GET";
    public HttpGetWithEntity() {
        super();
    }
    public HttpGetWithEntity(final URI uri) {
        super();
        setURI(uri);
    }
    /**
     * @throws IllegalArgumentException
     *             if the uri is invalid.
     */
    public HttpGetWithEntity(final String uri) {
        super();
        setURI(URI.create(uri));
    }
    @Override
    public String getMethod() {
// TODO Auto-generated method stub
        return METHOD_NAME;
    }
}