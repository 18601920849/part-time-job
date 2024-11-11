package com.entnews.rest;

import com.entnews.common.msg.Result;
import com.entnews.entity.Demo;
import com.entnews.service.DemoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    private DemoService demoService;

    @RequestMapping("/getDemo")
    public Result<List<Demo>> getDemo() {

        return Result.ok(demoService.getDemo());
    }
}
