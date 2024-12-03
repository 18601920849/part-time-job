package com.entnews.rest;

import com.entnews.common.msg.Result;
import com.entnews.common.utils.CozeHttpClient;
import com.entnews.service.DemoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    private DemoService demoService;

    @Resource
    private CozeHttpClient cozeHttpClient;

    @RequestMapping("/getDemo")
    public Result<String> getDemo() {
        try {
            String token = cozeHttpClient.getToken();
            return Result.ok(token);
        } catch (UnsupportedEncodingException e) {
            return Result.fail(e.getMessage());
        }

    }

}
