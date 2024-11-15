package com.entnews.rest;

import com.entnews.common.msg.Result;
import com.entnews.entity.TNewsDetailInfo;
import com.entnews.service.NewsService;
import com.entnews.vo.NewsVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsDataController {

    @Resource
    private NewsService newsService;

    @PostMapping("/newsData")
    public Result<List<TNewsDetailInfo>> newsData(@RequestBody NewsVo newsVo){
        return Result.ok(newsService.getNewsByDate(newsVo.getStartTime(), newsVo.getEndTime()));
    }

    @PostMapping("/newsletter")
    public Result newsletter(@RequestBody NewsVo newsVo){
        newsVo.getIds().stream().forEach(item-> System.out.println(item));
        return Result.ok("成功");
    }
}
