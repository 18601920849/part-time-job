package com.entnews.rest;

import cn.hutool.core.lang.intern.InternUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
    public Result<Object> newsData(@RequestBody NewsVo newsVo){
        Integer pageNum = newsVo.getPageNum();
        Integer pageSize = newsVo.getPageSize();
        String startTime = newsVo.getStartTime();
        String endTime = newsVo.getEndTime();
        if(ObjUtil.isNull(pageNum) || ObjUtil.isNull(pageSize) || StrUtil.isBlank(startTime) || StrUtil.isBlank(endTime)){
            return Result.fail("参数错误");
        }
        IPage<TNewsDetailInfo> newsByDate = newsService.getNewsByDate(newsVo.getStartTime(), newsVo.getEndTime(), (long) pageNum, (long) pageSize);

        return Result.ok(newsByDate);
    }

    @PostMapping("/newsletter")
    public Result newsletter(@RequestBody NewsVo newsVo){
        newsVo.getIds().stream().forEach(item-> System.out.println(item));
        return Result.ok("成功");
    }
}
