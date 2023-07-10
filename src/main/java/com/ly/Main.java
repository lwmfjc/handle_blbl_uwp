package com.ly;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class Main {
    public static void main(String[] args) {
        String curDir=System.getProperty("user.dir");
        log.info("当前目录:{}",curDir);
       long timeBegin = new Date().getTime();
        FileHandler fileHandler = new FileHandler();
        fileHandler.handle("D:\\Users\\ly\\Documents\\git\\handle_blbl_wap\\vedio");
        //fileHandler.handle(curDir);
        long timeEnd = new Date().getTime();
        log.info("耗时{}秒", (timeEnd - timeBegin) / 1000);
    }
}
