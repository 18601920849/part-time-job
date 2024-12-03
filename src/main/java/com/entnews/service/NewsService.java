package com.entnews.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entnews.dao.NewsDao;
import com.entnews.entity.TCompanyDetailInfo;
import com.entnews.entity.TNewsDetailInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class NewsService extends ServiceImpl<NewsDao, TNewsDetailInfo> {

    @Autowired
    private WebClient webClient;

    public IPage<TNewsDetailInfo> getNewsByDate(String startTime, String endTime, Long current, Long size) {
        Page<TNewsDetailInfo> page = new Page<>(current, size);
        Date startDate = DateUtil.parse(startTime, DatePattern.NORM_DATETIME_PATTERN);
        Date endDate = DateUtil.parse(endTime, DatePattern.NORM_DATETIME_PATTERN);
        LambdaQueryWrapper<TNewsDetailInfo> queryWrapper = Wrappers.lambdaQuery();
        // 查询时间大于等于startTime，小于等于endTime的数据
        queryWrapper.between(TNewsDetailInfo::getEtlDate, startDate, endDate);
        queryWrapper.orderByDesc(TNewsDetailInfo::getEtlDate);
        queryWrapper.orderByAsc(TNewsDetailInfo::getNewsNo);
        return page(page, queryWrapper);
    }

    public String getDataFromInterface() {
        // 发送GET请求到指定接口路径，这里假设接口路径是 /your-api-path
        Mono<String> stringMono = webClient.get()
                .uri("/your-api-path")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);

        return "";
    }

}
