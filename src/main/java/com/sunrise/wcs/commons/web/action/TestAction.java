package com.sunrise.wcs.commons.web.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sunrise.wcs.commons.util.SecurityParameter;

@RestController
public class TestAction {
    
    private static Logger logger = Logger.getLogger(TestAction.class);
    
    //@GetMapping("getUserInfo")
    @RequestMapping("getUserInfo")
    @SecurityParameter
    @ResponseBody
    public Map<String,Object> getUserInfo(@RequestBody String data){
        logger.info("进入getUserInfo");
        Map<String,Object> map = new HashMap<String,Object>();
        logger.info("data:"+data);
        return map;
    }
}
