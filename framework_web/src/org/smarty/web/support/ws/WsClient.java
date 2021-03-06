package org.smarty.web.support.ws;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.transport.http.CommonsHttpMessageSender;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.smarty.core.utils.PathUtil;
import org.smarty.web.commons.WebBaseConstant;

/**
 * webService 子端实现
 * Created Date 2015/04/09
 *
 * @author quliang
 * @version 1.0
 */
public class WsClient {
    private URL wsdlUrl;
    private Handler handler;

    public WsClient(String wsdlUrl) throws MalformedURLException, FileNotFoundException {
        this(wsdlUrl, null);
    }

    public WsClient(String wsdlUrl, Handler handler) throws MalformedURLException, FileNotFoundException {
        this.wsdlUrl = getWsdlUrl(wsdlUrl);
        this.handler = handler;
    }

    public URL getWsdlUrl(String wsdl) throws MalformedURLException, FileNotFoundException {
        if (wsdl.startsWith("http://") || wsdl.startsWith("https://")) {
            return new URL(wsdl);
        } else {
            return PathUtil.getResourceAsURL(wsdl);
        }
    }

    /**
     * 调用客户端；Document做为请求，响应
     *
     * @param methodName 方法名
     * @param doc        参数
     * @return document
     * @throws Exception
     */
    public Document invokeForDocument(String methodName, Document doc) throws Exception {
        if (wsdlUrl == null) {
            return null;
        }
        // 创建客户端
        Client client = createClient();
        // 调用服务端接口并返回
        Object[] obj = client.invoke(methodName, getRequestParams(doc));
        client.close();
        return DocumentHelper.parseText(String.valueOf(obj[0]));
    }

    /**
     * 调用客户端；Object做为请求，响应
     *
     * @param methodName 方法名
     * @param params     参数
     * @return Object
     * @throws Exception
     */
    public Object invokeForObject(String methodName, Object[] params) throws Exception {
        if (wsdlUrl == null) {
            return null;
        }
        Client client = createClient();
        Object obj = client.invoke(methodName, params);
        client.close();
        return obj;
    }

    /**
     * 创建客户端
     *
     * @return 客户端
     * @throws Exception
     */
    protected Client createClient() throws Exception {
        // 请求服务端接口
        Client client = new Client(wsdlUrl);
        // 设置参数
        client.setProperty(CommonsHttpMessageSender.HTTP_CLIENT_PARAMS, createClientParams());
        // 设置校验xsdl报文
        if (handler != null) {
            client.addOutHandler(handler);
        }
        return client;
    }

    /**
     * 创建连接参数
     *
     * @return HttpClientParams
     */
    protected HttpClientParams createClientParams() {
        HttpClientParams params = new HttpClientParams();
        params.setParameter(HttpClientParams.USE_EXPECT_CONTINUE, Boolean.FALSE);
        params.setParameter(HttpClientParams.CONNECTION_MANAGER_TIMEOUT, 10 * 1000L);
        params.setParameter(HttpClientParams.SO_TIMEOUT, WebBaseConstant.TIME_OUT);
        return params;
    }

    protected Object[] getRequestParams(Object obj) {
        if (obj == null) {
            return new Object[]{};
        }
        byte[] bytes;
        if (obj instanceof Document) {
            bytes = ((Document) obj).asXML().getBytes();
        } else {
            bytes = obj.toString().getBytes();
        }
        if (bytes.length > WebBaseConstant.REQ_MAX_DATA) {
            throw new StringIndexOutOfBoundsException("request params expected less than:" + WebBaseConstant.REQ_MAX_DATA + ",reality is:" + bytes.length);
        }
        return new Object[]{obj};

    }
}
