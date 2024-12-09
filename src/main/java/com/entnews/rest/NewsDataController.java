package com.entnews.rest;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<String> ids = newsVo.getIds();
        if (CollectionUtil.isEmpty(ids)){
            return Result.fail("参数错误");
        }
        String fileName = null;
        Map commonDataMap = new HashMap();
        try {
            commonDataMap.put("date", newsVo.getLetterDate());
            commonDataMap.put("issue", newsVo.getIssue());
            commonDataMap.put("numberno", newsVo.getNumberNo());
            commonDataMap.put("year", newsVo.getYear());
            fileName = newsService.getNewsLetter(ids,commonDataMap);
            Map map = new HashMap();
            map.put("fileName",fileName);
            map.put("filePath","http://8.152.6.134:8080/entnews/download/"+fileName);
            return Result.ok("成功",map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("失败");
        }
    }
}
