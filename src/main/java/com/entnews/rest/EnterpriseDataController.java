package com.entnews.rest;

import com.entnews.common.msg.Result;
import com.entnews.entity.Enterprise;
import com.entnews.service.EnterpriseService;
import com.entnews.vo.NewsVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/enterprise")
public class EnterpriseDataController {


    @Resource
    private EnterpriseService enterpriseService;


    @PostMapping("/enterpriseData")
    public Result<List<Enterprise>> enterpriseData(@RequestBody NewsVo newsVo) {
        return Result.ok(enterpriseService.getEnterpriseByDate(newsVo.getStartTime(), newsVo.getEndTime()));
    }
}
