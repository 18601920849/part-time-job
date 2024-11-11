package com.entnews.rest;

import com.entnews.common.msg.Result;
import com.entnews.entity.Enterprise;
import com.entnews.entity.News;
import com.entnews.service.NewsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsDataController {

    @Resource
    private NewsService newsService;

    @PostMapping("/newsData")
    public Result<List<News>> newsData(String startTime, String endTime){
        return Result.ok(newsService.getNewsByDate(startTime, endTime));
    }

    @PostMapping("/newsletter")
    public Result newsletter(Integer id){
        return Result.ok("成功");
    }
}
