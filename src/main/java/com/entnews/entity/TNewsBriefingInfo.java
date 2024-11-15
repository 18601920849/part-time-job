package com.entnews.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
@TableName("t_news_briefing_info")
@ApiModel(value = "TNewsBriefingInfo对象", description = "新闻简报信息表")
public class TNewsBriefingInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "新闻id")
    private String newsId;

    @ApiModelProperty(value = "批次id ")
    private String batchId;

    @ApiModelProperty(value = "是否推送")
    private String pushId;

    @ApiModelProperty(value = "标题")
    private String newsNo;

    @ApiModelProperty(value = "数据加载时间")
    private Date etlDate;


}
