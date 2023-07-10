# handle_blbl_wap
处理wap版的bilibili加密问题
(处理 ss21288\336713871\1\xxx.mp4 
 或336713871\1\xxx.mp4)这种结构的文件夹下的mp4
1\任意字符.mp4 为待解密视频文件
1\任意字符.info 中取文件名
# 使用
使用maven打包后，将*.jar复制粘贴到待解密的文件的根文件夹下(ss21288或336713871)，
然后通过命令行java -jar xxxx.jar 即可，会把解密后的mp4输出到 decrypt/目录下
