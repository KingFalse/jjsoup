package me.kagura;

import org.jsoup.Connection;
import org.jsoup.helper.Validate;

import javax.net.ssl.SSLSocketFactory;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 自动保持cookies
 * 借鉴Python的requests库
 */
public final class Session implements Serializable {

    //用于存放临时变量
    public Map<String, Object> ext = Collections.synchronizedMap(new HashMap<>());
    //设置用于序列化的key
    private String key;
    //超时时间
    private int timeoutMilliseconds = 30000; // 30 seconds
    //代理
    private KProxy proxy;
    //cookie
    private Map<String, String> cookies = Collections.synchronizedMap(new HashMap<>());
    //忽略请求错误
    private boolean ignoreHttpErrors = false;

    private SSLSocketFactory sslSocketFactory;
    //Response Content-Type校验
    private boolean ignoreContentType = true;

    private Session(String key) {
        this.key = key;
    }

    protected static Session newInstance(String key) {
        return new Session(key);
    }

    /**
     * 开始一个HTTP连接
     *
     * @param url
     * @return
     */
    public Connection connect(String url) {
        Connection instance = null;
        try {
            instance = (Connection) JJsoup.vClass.getConstructor(Session.class).newInstance(this);
            instance.url(url)
                    .proxy(proxy())
                    .ignoreContentType(ignoreContentType)
                    .timeout(timeoutMilliseconds)
                    .cookies(cookies)
                    .ignoreHttpErrors(ignoreHttpErrors)
                    .sslSocketFactory(sslSocketFactory);
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }


    public String key() {
        return key;
    }

    public Session key(String key) {
        this.key = key;
        return this;
    }

    public String cookie(String name) {
        return cookies.get(name);
    }

    public Map<String, String> cookies() {
        return cookies;
    }

    public Session cookies(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
        return this;
    }

    public Session cookie(String name, String val) {
        this.cookies.put(name, val);
        return this;
    }

    public Session proxy(Proxy proxy) {
        InetSocketAddress address = (InetSocketAddress) proxy.address();
        this.proxy = new KProxy(proxy.type(), address.getHostString(), address.getPort());
        return this;
    }

    public Session proxy(String host, int port) {
        this.proxy = new KProxy(Proxy.Type.HTTP, host, port);
        return this;
    }

    public Proxy proxy() {
        if (proxy == null) return null;
        return new Proxy(proxy.getType(), InetSocketAddress.createUnresolved(proxy.getHost(), proxy.getPort()));
    }

    public int timeout() {
        return timeoutMilliseconds;
    }

    public Session timeout(int millis) {
        Validate.isTrue(millis >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
        timeoutMilliseconds = millis;
        return this;
    }

    public boolean ignoreHttpErrors() {
        return ignoreHttpErrors;
    }

    public SSLSocketFactory sslSocketFactory() {
        return sslSocketFactory;
    }

    public Session sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    public Session ignoreHttpErrors(boolean ignoreHttpErrors) {
        this.ignoreHttpErrors = ignoreHttpErrors;
        return this;
    }

    public boolean ignoreContentType() {
        return ignoreContentType;
    }

    public Session ignoreContentType(boolean ignoreContentType) {
        this.ignoreContentType = ignoreContentType;
        return this;
    }

    @Override
    public String toString() {
        return "Session{" +
                "ext=" + ext +
                ", key='" + key + '\'' +
                ", timeoutMilliseconds=" + timeoutMilliseconds +
                ", proxy=" + proxy +
                ", cookies=" + cookies +
                ", ignoreHttpErrors=" + ignoreHttpErrors +
                ", sslSocketFactory=" + sslSocketFactory +
                ", ignoreContentType=" + ignoreContentType +
                '}';
    }

}

class KProxy implements Serializable {

    private Proxy.Type type;
    private String host;
    private int port;

    public KProxy(Proxy.Type type, String host, int port) {
        this.type = type;
        this.host = host;
        this.port = port;
    }

    public Proxy.Type getType() {
        return type;
    }

    public void setType(Proxy.Type type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "KProxy{" +
                "type=" + type +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

}