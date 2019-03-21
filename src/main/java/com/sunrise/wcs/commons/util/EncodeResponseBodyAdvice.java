package com.sunrise.wcs.commons.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @项目名称:沃云购商城
 * @工程名称:SpringBootWeb
 * @类名称:DecodeRequestBodyAdvice
 * @类描述:对返回的数据进行统一的加密处理
 * @作者:yangjie
 * @创建时间:2019年3月19日 下午5:04:42
 * @当前版本:1.0
 */
@SuppressWarnings("rawtypes")
@ControllerAdvice(basePackages = "com.sunrise.wcs.commons.web.action")
public class EncodeResponseBodyAdvice implements ResponseBodyAdvice {

    private final static Logger logger = Logger.getLogger(EncodeResponseBodyAdvice.class);
    
    /**
     * 客户端公钥
     */
    @Value("${client.public.key}")
    private String CLIENT_PUBLIC_KEY;

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    /**
     * 对返回的数据进行加密
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass,
            ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
            // 生成aes秘钥
            String aesKey = AESUtils.getRandomString(16);
            // rsa加密
            String encrypted = RSAUtils.encryptByPublicKey(aesKey, CLIENT_PUBLIC_KEY);
            // aes加密
            String requestData = AESUtils.encrypt(result, aesKey);
            Map<String, String> map = new HashMap<String, String>();
            map.put("key", encrypted);
            map.put("data", requestData);
            return map;
        } catch (Exception e) {
            logger.error("对方法method :【" + methodParameter.getMethod().getName() + "】返回数据进行加密出现异常：" + e.getMessage());
        }

        return body;
    }

    

}
