package com.sunrise.wcs.commons.util;
import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;
 
/**
 * 
 * @项目名称:沃云购商城
 * @类名称:SecurityParameter
 * @类描述: 自定义数据加密解密的注解
 * @创建时间:2019年3月20日 上午10:52:53
 * @当前版本:1.0
 */
@Target({ElementType.METHOD,ElementType.TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Mapping
@Documented
public @interface SecurityParameter {
 
}
