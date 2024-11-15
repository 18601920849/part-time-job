package com.entnews.common.job;

import com.entnews.common.msg.Result;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class TestJob {

    @XxlJob("xxlJobTest")
    public Result<String> xxlJobTest(String date) {
        log.info("---------xxlJobTest定时任务执行成功--------");
        return Result.ok();
    }
}
