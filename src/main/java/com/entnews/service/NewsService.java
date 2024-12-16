package com.entnews.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entnews.common.utils.CozeHttpClient;
import com.entnews.common.utils.WordFileGenerator;
import com.entnews.dao.NewsDao;
import com.entnews.entity.TNewsDetailInfo;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class NewsService extends ServiceImpl<NewsDao, TNewsDetailInfo> {

    @Resource
    private CozeHttpClient cozeHttpClient;

    private final String templatePath = "template/newsfile.docx";

    //private final String outputPath = "/home/entnews/newsfiles/";

    private final String outputPath = "/home/entnews/newsfiles/";

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

    public String getNewsLetter(List<String> ids,Map commonDataMap) throws IOException {
        LambdaQueryWrapper<TNewsDetailInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.in(TNewsDetailInfo::getNewsNo, ids);
        List<TNewsDetailInfo> list = list(wrapper);
        String fileName = "";
        if (CollectionUtil.isEmpty(list)) {
            return fileName;
        }
        String token = cozeHttpClient.getToken();
        String chatJson = cozeHttpClient.createChat(token);
        Gson gson = new Gson();
        Map map = gson.fromJson(chatJson, Map.class);
        String conversationId = (String) map.get("id");
        List wordList = new ArrayList();
        String keyword = "";
        for (TNewsDetailInfo tNewsDetailInfo : list) {
            String sourceUrl = tNewsDetailInfo.getSourceUrl();
            String chatId = cozeHttpClient.sendMsg(token, conversationId, sourceUrl);
            if (StrUtil.isNotEmpty(chatId)){
                boolean isSuccess = false;
                while (!isSuccess){
                    String status = cozeHttpClient.getRetrieve(token, chatId, conversationId);
                    if ("completed".equals(status)){
                        isSuccess = true;
                        String result = cozeHttpClient.getMsg(token, chatId, conversationId);
                        Map resultMap = gson.fromJson(result, Map.class);
                        List dataList = (List) resultMap.get("data");
                        for (Object data : dataList){
                            Map answerMap = (Map) data;
                            String type = (String) answerMap.get("type");
                            if ("answer".equals(type)){
                                Map answerContent = new HashMap();
                                String content = (String) answerMap.get("content");
                                String title = content.substring(content.indexOf("标题：")+3, content.indexOf("关键字："));
                                keyword = keyword+content.substring(content.indexOf("关键字：")+4, content.indexOf("摘要："))+"、";
                                String summary = content.substring(content.indexOf("摘要：")+3);
                                answerContent.put("title", ""+title);
                                answerContent.put("sourceurl", sourceUrl);
                                answerContent.put("summary", summary);
                                wordList.add(answerContent);
                            }
                        }
                    }else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        keyword = keyword.substring(0, keyword.length()-1);
        commonDataMap.put("keyword", keyword);
        fileName = "newsletter" + System.currentTimeMillis() + RandomUtil.randomString(5) + ".docx";
        WordFileGenerator.generateWord(templatePath, outputPath + fileName, wordList, commonDataMap);
        return fileName;
    }

}
