package com.entnews.rest;

import com.entnews.common.msg.Result;
import com.entnews.entity.Enterprise;
import com.entnews.service.EnterpriseService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/enterprise")
public class EnterpriseDataController {


    @Resource
    private EnterpriseService enterpriseService;


    @PostMapping("/enterpriseData")
    public Result<List<Enterprise>> enterpriseData(String startTime, String endTime) {
        return Result.ok(enterpriseService.getEnterpriseByDate(startTime, endTime));
    }
}
