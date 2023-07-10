package com.ly;

import com.alibaba.fastjson2.JSON;
import com.entity.FileInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Data
public class FileHandler {
    //所有文件的文件名
    private ArrayList<String> fileNames = new ArrayList<>();
    //所有文件的文件地址
    private ArrayList<String> filePaths = new ArrayList<>();
    //输出文件夹地址
    private String outDir = System.getProperty("user.dir") + File.separator + "decrypt";
    //类型
    private int decryType = 0;//0:简单 1:复杂

    //获取所有的
    private void handleAllFileMp4DirSimple(String baseDirPath) {
        File baseDir = new File(baseDirPath);
        //如果是文件夹，进行遍历
        if (baseDir.isDirectory()) {
            File[] fileDirs = baseDir.listFiles();
            for (int n = 0; n < fileDirs.length; n++) {
                //一级目录
                File dir1 = fileDirs[n];
                if (dir1.isDirectory()) {
                    System.out.println("找到文件夹:" + dir1.getAbsolutePath());
                    //遍历文件夹下的所有文件
                    File[] files = dir1.listFiles();
                    int numName = 0;
                    int numFile = 0;
                    String fileNameNew = "";
                    for (int m = 0; m < files.length; m++) {
                        File file = files[m];
                        String fileName = file.getName();
                        if (fileName.contains(".info")) {
                            //解析出文件名
                            fileNameNew = getFileName(file);
                            fileNames.add(fileNameNew);
                            numName++;
                        } else if (fileName.contains(".mp4")) {
                            //添加文件地址
                            filePaths.add(file.getAbsolutePath());
                            numFile++;
                        }
                    }
                    //保证是成对的
                    int differ = Math.abs(numName - numFile);
                    //如果存在差距
                    if (differ > 0) {
                        //如果解析的文件名较多
                        if (numName - numFile > 0) {
                            for (int x = 0; x < differ; x++) {
                                filePaths.add("");
                            }
                        } else {
                            //如果解析的文件较多
                            for (int x = 0; x < differ; x++) {
                                fileNames.add(fileNameNew);
                            }
                        }
                    }

                }
            }
        }
    }

    //获取所有的
    private void handleAllFileMp4Dir(String baseDirPath) {
        File baseDir = new File(baseDirPath);
        //如果是文件夹，进行遍历
        if (baseDir.isDirectory()) {
            File[] fileDirs = baseDir.listFiles();
            for (int n = 0; n < fileDirs.length; n++) {
                //一级目录
                File dir1 = fileDirs[n];
                if (dir1.isDirectory()) {
                    //二级目录
                    File dir2 = new File(dir1.getAbsolutePath() + File.separator + "1");
                    if (dir2.isDirectory()) {
                        System.out.println("找到文件夹:" + dir2.getAbsolutePath());
                        //遍历文件夹下的所有文件
                        File[] files = dir2.listFiles();
                        int numName = 0;
                        int numFile = 0;
                        String fileNameNew = "";
                        for (int m = 0; m < files.length; m++) {
                            File file = files[m];
                            String fileName = file.getName();
                            if (fileName.contains(".info")) {
                                //解析出文件名
                                fileNameNew = getFileName(file);
                                fileNames.add(fileNameNew);
                                numName++;
                            } else if (fileName.contains(".mp4")) {
                                //添加文件地址
                                filePaths.add(file.getAbsolutePath());
                                numFile++;
                            }
                        }
                        //保证是成对的
                        int differ = Math.abs(numName - numFile);
                        //如果存在差距
                        if (differ > 0) {
                            //如果解析的文件名较多
                            if (numName - numFile > 0) {
                                for (int x = 0; x < differ; x++) {
                                    filePaths.add("");
                                }
                            } else {
                                //如果解析的文件较多
                                for (int x = 0; x < differ; x++) {
                                    fileNames.add(fileNameNew);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private String getFileName(File file) {
        try {
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            FileInfo fileInfo = JSON.parseObject(content, FileInfo.class);
            String title = decryType == 0 ? fileInfo.getPartName() : fileInfo.getTitle();
            log.info("文件名为:" + title);
            return title;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }

    private boolean decryptFile(File file, String filePathNew) {
        boolean success = false;
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            int num = 0;
            //读取前三个字节,看是否是加密文件
            int read = inputStream.read();
            //只要没有到文件尾部,就继续读
            while (read != -1) {
                log.info("读到的字节:{}", read);
                if (read == 255) {
                    //读取了几个255
                    num++;
                    //如果读取了3个，就不继续读了
                    if (3 == num) {
                        break;
                    }
                    read = inputStream.read();
                } else {
                    break;
                }
            }
            //如果是加密文件
            if (3 == num) {
                log.info("正在处理加密文件");
                //如果读取了三个,就将输入流剩下的字节输出到另一个文件中
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePathNew));
                byte[] bytes = new byte[1024 * 200];
                //读取到的字节数
                int readLength = inputStream.read(bytes);
                int totalWrite = 0;
                long length = file.length();
                while (readLength != -1) {
                    outputStream.write(bytes, 0, readLength);
                    totalWrite += readLength;
                    log.info("{} 总共写入:{},文件大小:{},写入百分比:{}%", filePathNew, totalWrite, length, totalWrite * 100L / length);
                    readLength = inputStream.read(bytes);
                }
                outputStream.flush();
                //关闭输出流
                outputStream.close();
                //关闭输入流
                inputStream.close();
                //file.deleteOnExit();
                success = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return success;
    }

    public void handle(String baseDirPath) {
        this.handleAllFileMp4Dir(baseDirPath);

        int nameSize = fileNames.size();
        int pathSize = filePaths.size();
        int cpuNums = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(cpuNums * 2 * 2);

        //如果长度一致
        if (nameSize == pathSize) {
            for (int n = 0; n < nameSize; n++) {
                if (n == 0) {
                    File file = new File(outDir);
                    //如果存在这个目录
                    if (file.mkdirs()) {
                        log.info("文件夹创建成功");
                    }
                }

                String filePath = filePaths.get(n);
                String fileName = fileNames.get(n);
                File file = new File(filePath);
                String fileNameOld = file.getName();
                String fileNameNew = fileName + ".mp4";
                //如果同名,则添加时间轴
                if (fileNameOld.equals(fileNameNew)) {
                    fileNameNew = fileName + (new Date().getTime()) + ".mp4";
                }
                String filePathNew = outDir + File.separator + fileNameNew;//filePath.replaceFirst(fileNameOld, fileNameNew);
                log.info("文件地址: {} ,旧文件名：{}，新文件名: {} ,新文件地址：{}", filePath, fileNameOld,
                        fileName, filePathNew);
                executorService.submit(() -> {

                    boolean decryptSuccess = decryptFile(file, filePathNew);
                    if (decryptSuccess) {
                        log.info("解密成功: {}", filePathNew);
                    }
                });

            }
            executorService.shutdown();
        } else {
            log.info("文件名与文件地址没有一一对应!");
        }
    }

    private void printAllInfo() {
        int nameSize = fileNames.size();
        int pathSize = filePaths.size();
        //如果长度一致
        if (nameSize == pathSize) {
            for (int n = 0; n < nameSize; n++) {
                log.info("文件地址: {} ,新文件名: {} ", filePaths.get(n),
                        fileNames.get(n));
            }
        }
    }

/*    public static void main(String[] args) {
        log.info("当前目录:{}",System.getProperty("user.dir"));
        *//*long timeBegin = new Date().getTime();
        FileHandler fileHandler = new FileHandler();
        fileHandler.getAllFileMp4Dir("D:\\Users\\ly\\Documents\\git\\handle_blbl_wap\\vedio");
        fileHandler.handle();
        long timeEnd = new Date().getTime();
        log.info("耗时{}秒", (timeEnd - timeBegin) / 1000);*//*
    }*/
}
