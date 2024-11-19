package com.entnews.common.job;

import cn.hutool.core.util.StrUtil;
import com.entnews.common.msg.Result;
import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobFileAppender;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
@Log4j2
public class RunPythonJob {

    @XxlJob("xxlJobRunPython")
    public Result<String> xxlJobRunPython() {
        XxlJobContext xxlJobContext = XxlJobContext.getXxlJobContext();
        String jobLogFileName = xxlJobContext.getJobLogFileName();
        log.info("---------xxlJobRunPythont定时任务开始执行--------");
        XxlJobFileAppender.appendLog(jobLogFileName, "定时任务开始执行");
        String param = xxlJobContext.getJobParam();
        log.info("定时任务入参：" + param);
        XxlJobFileAppender.appendLog(jobLogFileName, "定时任务入参：" + param);
        if (StrUtil.isEmpty(param)) {
            return Result.fail("请输入参数");
        }
        try {
            String pyPath = "/home/admin/python/";
            // 构建包含参数的命令字符串
            String command = "python3 " + pyPath + param;
            log.info("定时任务执行命令：" + command);
            XxlJobFileAppender.appendLog(jobLogFileName, "定时任务执行命令：" + command);
            Process process = Runtime.getRuntime().exec(command);
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            log.info("python脚本执行返回结果:" + sb);
            XxlJobFileAppender.appendLog(jobLogFileName, "python脚本执行返回结果:" + sb);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("Python script executed successfully.");
                XxlJobFileAppender.appendLog(jobLogFileName, "Python script executed successfully.");
            } else {
                log.info("Python script execution failed with exit code: " + exitCode);
                XxlJobFileAppender.appendLog(jobLogFileName, "Python script execution failed with exit code: " + exitCode);
            }
        } catch (Exception e) {
            log.info("执行python脚本失败", e);
            XxlJobFileAppender.appendLog(jobLogFileName, "执行python脚本失败：" + e);
            return Result.fail("执行python脚本失败");
        }
        log.info("---------xxlJobRunPythont定时任务执行结束--------");
        XxlJobFileAppender.appendLog(jobLogFileName, "---------xxlJobRunPythont定时任务执行结束--------");
        return Result.ok();
    }
}
