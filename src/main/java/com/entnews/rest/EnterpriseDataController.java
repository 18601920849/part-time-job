package com.entnews.rest;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.entnews.common.msg.Result;
import com.entnews.entity.TCompanyDetailInfo;
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
    public Result<Object> enterpriseData(@RequestBody NewsVo newsVo) {

        Integer pageNum = newsVo.getPageNum();
        Integer pageSize = newsVo.getPageSize();
        String startTime = newsVo.getStartTime();
        String endTime = newsVo.getEndTime();
        if(ObjUtil.isNull(pageNum) || ObjUtil.isNull(pageSize) || StrUtil.isBlank(startTime) || StrUtil.isBlank(endTime)){
            return Result.fail("参数错误");
        }
        IPage<TCompanyDetailInfo> enterpriseByDate = enterpriseService.getEnterpriseByDate(newsVo.getStartTime(), newsVo.getEndTime(), (long) pageNum, (long) pageSize);
        return Result.ok(enterpriseByDate);
    }
}
