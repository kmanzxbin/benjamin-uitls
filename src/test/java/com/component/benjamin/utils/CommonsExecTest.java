package com.component.benjamin.utils;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;

public class CommonsExecTest {

    public static void main(String[] args) {
        String line = "netstat -an";
        CommandLine cmdLine = CommandLine.parse(line);
        DefaultExecutor executor = new DefaultExecutor();

        // 设置命令执行退出值为1，如果命令成功执行并且没有错误，则返回1
        executor.setExitValue(0);
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        ExecuteStreamHandler executeStreamHandler = new PumpStreamHandler();
        try {
            int exitValue = executor.execute(cmdLine);
            System.out.println(exitValue);
        } catch (ExecuteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
