package com.entnews.vo;

import lombok.Data;

import java.util.List;

@Data
public class NewsVo {

    private String startTime;

    private String endTime;

    private List<String> ids;

    private Integer pageNum;

    private Integer pageSize;

    private String letterDate;

    private String issue;

    private String numberNo;

    private String year;

    private String code;
}
