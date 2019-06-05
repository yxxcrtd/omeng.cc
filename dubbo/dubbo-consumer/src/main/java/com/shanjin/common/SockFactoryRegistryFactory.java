package com.shanjin.common;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.FactoryBean;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/21
 * @desc TODO
 */
public class SockFactoryRegistryFactory implements FactoryBean<Registry<ConnectionSocketFactory>> {

    @Override
    public Registry<ConnectionSocketFactory> getObject() throws Exception {
        SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null,
                new TrustSelfSignedStrategy())
                .build();
        HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, hostnameVerifier); 
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslsf)
                .build();
    }

    @Override
    public Class<?> getObjectType() {
        return Registry.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
