package com.ly;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Main {
    public static void main(String[] args) {
        String curDir = System.getProperty("user.dir");
        log.info("当前目录:{}", curDir);
        long timeBegin = new Date().getTime();
        FileHandler fileHandler = new FileHandler();
        //只解析在这个修改时间之前的mp4
        fileHandler.setMyDateStart("2023-07-12 00:40:00");
        fileHandler.setMyDateEnd("2023-07-12 08:17:00");
        //fileHandler.setMySetDate("2023-07-12 03:00:00");
        //判断以什么模式解密
        int index = curDir.lastIndexOf(File.separator);
        if (index != -1) {
            String substring = curDir.substring(index + 1);
            Pattern pattern = Pattern.compile("\\d+");
            Matcher isNum = pattern.matcher(substring);
            log.info("是否是全数字?{}", isNum.matches() ? "是" : "否");
            log.info("路径最后一部分{}", substring);
            fileHandler.setDecryType(isNum.matches() ? 0 : 1);
        } else {
            return;
        }
        //fileHandler.handle("E:\\vedio\\3674173");
        fileHandler.handle(curDir);
        long timeEnd = new Date().getTime();
        log.info("耗时{}秒", (timeEnd - timeBegin) / 1000);
        //fileHandler.printAllInfo();
    }
}
