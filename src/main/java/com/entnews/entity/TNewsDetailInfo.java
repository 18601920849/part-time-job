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
 * 新闻信息详情信息
 * </p>
 *
 * @author author
 * @since 2024-11-14
 */
@Data
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("t_news_detail_info")
@ApiModel(value = "TNewsDetailInfo对象", description = "新闻信息详情信息")
public class TNewsDetailInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "新闻名称")
    private String newName;

    @ApiModelProperty(value = "新闻列表地址")
    private String url;

    @ApiModelProperty(value = "来源网址")
    private String sourceUrl;

    @ApiModelProperty(value = "关键字")
    private String keyWord;

    @ApiModelProperty(value = "新闻编号接口交互使用")
    private String newsNo;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String contant;

    @ApiModelProperty(value = "作者")
    private String auth;

    @ApiModelProperty(value = "来源")
    private String source;

    @ApiModelProperty(value = "文章发布日期")
    private Date newsDate;

    @ApiModelProperty(value = "数据加载时间")
    private Date etlDate;


}
