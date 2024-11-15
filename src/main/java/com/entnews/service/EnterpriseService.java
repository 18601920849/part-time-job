package com.entnews.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entnews.dao.EnterpriseDao;
import com.entnews.entity.TCompanyDetailInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EnterpriseService extends ServiceImpl<EnterpriseDao, TCompanyDetailInfo> {



    public List<TCompanyDetailInfo> getEnterpriseByDate(String startTime, String endTime) {
        Date startDate = DateUtil.parse(startTime, DatePattern.NORM_DATETIME_PATTERN);
        Date endDate = DateUtil.parse(endTime, DatePattern.NORM_DATETIME_PATTERN);
        LambdaQueryWrapper<TCompanyDetailInfo> queryWrapper = Wrappers.lambdaQuery();
        // 查询时间大于等于startTime，小于等于endTime的数据
        queryWrapper.between(TCompanyDetailInfo::getEtlDate, startDate, endDate);
        //return list(queryWrapper);
        return new ArrayList<TCompanyDetailInfo>(List.of(new TCompanyDetailInfo[]{
                new TCompanyDetailInfo("1", "test", "test", "test", "test", "test", "test", "test", "test", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, startDate),
                new TCompanyDetailInfo("2", "test", "test", "test", "test", "test", "test", "test", "test", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, endDate)
        }));
    }
}
