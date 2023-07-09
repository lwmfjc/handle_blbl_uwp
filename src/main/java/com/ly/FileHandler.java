package com.ly;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class FileHandler {
    //所有文件的文件名
    private static ArrayList<String> fileNames=new ArrayList<>();
    //所有文件的文件地址
    private static ArrayList<String> filePaths=new ArrayList<>();

    //获取所有的
    public static void getAllFileMp4Dir(String baseDirPath) {
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
                        File[] files  = dir2.listFiles();
                        boolean hasName=false;
                        boolean hasFile=false;
                        for(int m=0;m<files.length;m++){
                            File file=files[m];
                            String fileName = file.getName();
                            if(fileName.contains(".info")){
                                //解析出文件名
                                fileNames.add("文件名");
                                hasName=true;
                            }else if(fileName.contains(".mp4")){
                                //添加文件地址
                                filePaths.add(file.getAbsolutePath());
                                hasFile=true;
                            }
                        }
                        //如果有一个不存在
                        //保证文件名和文件路径成对出现
                        if(!hasName||!hasFile){
                            if(!hasName){
                                fileNames.add("");
                            }
                            if(!hasFile){
                                filePaths.add("");
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        getAllFileMp4Dir("D:\\Users\\ly\\Documents\\git\\handle_blbl_wap\\vedio");
    }
}
