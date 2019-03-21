package com.sunrise.wcs.commons.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @项目名称:沃云购商城
 * @工程名称:SpringBootWeb
 * @类名称:DecodeRequestBodyAdvice
 * @类描述:对请求的数据进行统一的解密处理
 * @作者:yangjie
 * @创建时间:2019年3月19日 下午5:04:42
 * @当前版本:1.0
 */
@ControllerAdvice(basePackages = "com.sunrise.wcs.commons.web.action")
public class DecodeRequestBodyAdvice implements RequestBodyAdvice {

    private static final Logger logger = Logger.getLogger(DecodeRequestBodyAdvice.class);

    /**
     * 服务器端私钥
     */
    @Value("${server.private.key}")
    private String SERVER_PRIVATE_KEY;
    
    /**
     * 客户端公钥
     */
    @Value("${client.public.key}")
    private String CLIENT_PUBLIC_KEY;

    @Override
    public boolean supports(MethodParameter methodParameter, Type type,
            Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage httpInputMessage, MethodParameter methodParameter,
            Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return body;
    }

    /**
     * 对请求的数据进行解密
     */
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter methodParameter, Type type,
            Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        try {
            return new DecodeInputMessage(inputMessage);
        } catch (Exception e) {
            logger.error("对方法method :【" + methodParameter.getMethod().getName() + "】的请求数据进行解密出现异常：" + e.getMessage());
            return inputMessage;
        }
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage httpInputMessage, MethodParameter methodParameter,
            Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return body;
    }

    /**
     * @类名称:DecodeInputMessage
     * @类描述: 解密数据并封装成HttpInputMessage
     * @创建时间:2019年3月20日 上午10:56:04
     * @当前版本:1.0
     */
    class DecodeInputMessage implements HttpInputMessage {
        private HttpHeaders headers;

        private InputStream body;
        
        @SuppressWarnings("deprecation")
        public DecodeInputMessage(HttpInputMessage inputMessage) throws Exception {
            this.headers = inputMessage.getHeaders();
            this.body = IOUtils.toInputStream(decodeString(IOUtils.toString(inputMessage.getBody(), "utf-8")));
        }

        @Override
        public InputStream getBody() throws IOException {
            return body;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

        /**
         * @decodeString(解密数据) @创建时间:2019年3月20日 上午10:58:48
         * @param requestData
         * @return
         */
        public String decodeString(String requestData) {
            String content = null;
            String aesKey = null;
            boolean flag = false;
            Map<String, Object> result = new HashMap<String, Object>();
            //缺少请求参数
            if(requestData == null || requestData.equals("")){
                result.put("respCode", "0001");
                result.put("respMsg", "缺少请求参数");
                return new Gson().toJson(result);
            }
            Map<String, String> map = new Gson().fromJson(requestData, new TypeToken<Map<String, String>>(){}.getType());
            String data = map.get("data");//请求的密文数据
            String key = map.get("key");//请求的密文AES秘钥
            String sign = map.get("sign");//请求的签名
            //缺少请求参数
            if (StringUtils.isEmpty(data) || StringUtils.isEmpty(key) || StringUtils.isEmpty(sign)) {
                result.put("respCode", "0001");
                result.put("respMsg", "缺少请求参数");
                return new Gson().toJson(result);
            }
            
            try {
                //签名格式
                String signMac = key + "_WCS_SIGN_" + data;
                flag = RSAUtils.verify(signMac.getBytes(), CLIENT_PUBLIC_KEY, sign);
                if(!flag){
                    result.put("respCode", "0002");
                    result.put("respMsg", "签名校验失败");
                    return new Gson().toJson(result);
                }
                try {
                    aesKey = RSAUtils.decryptByPrivateKey(key, SERVER_PRIVATE_KEY);
                    content = AESUtils.decrypt(data, aesKey);
                } catch (Exception e) {
                    result.put("respCode", "0003");
                    result.put("respMsg", "数据解密异常");
                    return new Gson().toJson(result);
                }
                
                if (StringUtils.isEmpty(content) || StringUtils.isEmpty(aesKey)) {
                    result.put("respCode", "0003");
                    result.put("respMsg", "数据解密异常");
                    return new Gson().toJson(result);
                }
            } catch (Exception e) {
                result.put("respCode", "0002");
                result.put("respMsg", "签名校验失败");
                return new Gson().toJson(result);
            }
            
            return content;
        }

    }
}
