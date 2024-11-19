package com.entnews.common.job;

import com.entnews.common.msg.Result;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
@Log4j2
public class RunPythonJob {

    @XxlJob("xxlJobRunPython")
    public Result<String> xxlJobRunPython(String param) {
        log.info("---------xxlJobTest定时任务执行成功--------");
        try {
            String pyPath = "/home/admin/python/3.py";
            // 构建包含参数的命令字符串
            String command = "python " + pyPath + " " + param;
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
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("Python script executed successfully.");
            } else {
                log.info("Python script execution failed with exit code: " + exitCode);
            }
        } catch (Exception e) {
            log.info("执行python脚本失败", e);
            return Result.fail("执行python脚本失败");
        }
        return Result.ok();
    }
}
