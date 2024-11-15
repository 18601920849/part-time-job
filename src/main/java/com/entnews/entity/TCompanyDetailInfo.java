package com.entnews.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 新闻简报信息表
 * </p>
 *
 * @author author
 * @since 2024-11-14
 */
@Data
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("t_company_detail_info")
@ApiModel(value="TCompanyDetailInfo对象", description="新闻简报信息表")
public class TCompanyDetailInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "公司编号")
    private String companyNo;

    @ApiModelProperty(value = "公司简称 ")
    private String companyAbbrName;

    @ApiModelProperty(value = "公司全名")
    private String companyFullName;

    @ApiModelProperty(value = "行业")
    private String industry;

    @ApiModelProperty(value = "是否上市 0 否 1 是")
    private String isListedFlag;

    @ApiModelProperty(value = "股票代码")
    private String stockCode;

    @ApiModelProperty(value = "是否提供季度财报 0 否 1 是")
    private String financialReportFlag;

    @ApiModelProperty(value = "财报类型")
    private String financialReportType;

    @ApiModelProperty(value = "年份")
    private String year;

    @ApiModelProperty(value = "营业收入(亿元)")
    private Double Revenue;

    @ApiModelProperty(value = "净利润(亿元)")
    private Double netProfit;

    @ApiModelProperty(value = "研发投入(亿元)")
    private Double investment;

    @ApiModelProperty(value = "研发人员数量")
    private Double investmentPeonum;

    @ApiModelProperty(value = "市场规模(市值亿元)")
    private Double marketSize;

    @ApiModelProperty(value = "占据市场份额")
    private Double marketShare;

    @ApiModelProperty(value = "数据加载时间")
    private Date etlDate;


}
