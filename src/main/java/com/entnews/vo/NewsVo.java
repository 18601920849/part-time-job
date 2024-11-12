package com.entnews.vo;

import lombok.Data;

import java.util.List;

@Data
public class NewsVo {

    private String startTime;

    private String endTime;

    private List<String> ids;
}
