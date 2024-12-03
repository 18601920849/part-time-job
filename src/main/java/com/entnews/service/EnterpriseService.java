package com.entnews.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entnews.dao.EnterpriseDao;
import com.entnews.entity.TCompanyDetailInfo;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EnterpriseService extends ServiceImpl<EnterpriseDao, TCompanyDetailInfo> {


    public IPage<TCompanyDetailInfo> getEnterpriseByDate(String startTime, String endTime, Long current, Long size) {
        Page<TCompanyDetailInfo> page = new Page<>(current, size);
        Date startDate = DateUtil.parse(startTime, DatePattern.NORM_DATETIME_PATTERN);
        Date endDate = DateUtil.parse(endTime, DatePattern.NORM_DATETIME_PATTERN);
        LambdaQueryWrapper<TCompanyDetailInfo> queryWrapper = Wrappers.lambdaQuery();
        // 查询时间大于等于startTime，小于等于endTime的数据
        queryWrapper.between(TCompanyDetailInfo::getEtlDate, startDate, endDate);
        queryWrapper.orderByDesc(TCompanyDetailInfo::getEtlDate);
        queryWrapper.orderByAsc(TCompanyDetailInfo::getCompanyNo);
        return page(page, queryWrapper);
    }
}
