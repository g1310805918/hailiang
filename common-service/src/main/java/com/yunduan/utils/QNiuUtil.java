package com.yunduan.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.yunduan.entity.Setting;
import com.yunduan.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;

/**
 * 2021/1/21
 * 七牛云工具类
 */
public class QNiuUtil {

    /* 海量 */
    public static String accessKey = "Pk61rdUWJhidicNQR7o-mo5T-CPCl0MvJt-L3zdM";
    public static String secretKey = "nnqTY8mgkifplcgUC15k-vIZfzWyO0etdiuwfGcL";
    //空间名
    public static String bucket = "repairorder";
    //空间绑定的域名
    public static String domain = "http://mvs.vastdata.com.cn";



    /**
     * 获取图片上传凭证
     */
    public static String getUpToken() {
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        return upToken;
    }


    /**
     * 上传MultipartFile  (图片、视频)
     */
    public static String uploadMultipartFile(MultipartFile file) {
        if (file.isEmpty()){
            return "文件内容不能为空";
        }
        String extName = FileUtil.extName(file.getOriginalFilename());
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Region.huabei());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //获取文件名(随机数 + 文件名)
        String fileName = RandomUtil.randomNumbers(16) + "." + extName;
        //把文件转化为字节数组
        InputStream is = null;
        ByteArrayOutputStream bos = null;
        try {
            is = file.getInputStream();
            bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len = -1;
            while ((len = is.read(b)) != -1){
                bos.write(b, 0, len);
            }
            byte[] uploadBytes= bos.toByteArray();
            // 默认不指定key的情况下，以文件内容的hash值作为文件名
            String key = fileName;
            //进行上传操作，传入文件的字节数组，文件名，上传空间，得到回复对象
            Response response = uploadManager.put(uploadBytes,key, getUpToken());
            //默认上传接口回复对象
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            //返回访问路径
            return publicFile(putRet.key, domain);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 上传本地文件
     */
    public static String uploadLocalImg(String filePath){
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Region.huabei());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //把文件转化为字节数组
        InputStream is = null;
        ByteArrayOutputStream bos = null;
        try {
            File file = new File(filePath);
            is = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len = -1;
            while ((len = is.read(b)) != -1){
                bos.write(b, 0, len);
            }
            byte[] uploadBytes= bos.toByteArray();
            // 默认不指定key的情况下，以文件内容的hash值作为文件名
            String key = file.getName();
            //进行上传操作，传入文件的字节数组，文件名，上传空间，得到回复对象
            Response response = uploadManager.put(uploadBytes,key, getUpToken());
            //默认上传接口回复对象
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            //返回访问路径
            return publicFile(putRet.key, domain);
        }catch (QiniuException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 公有空间返回文件URL
     * @param fileName  文件名
     * @param domainOfBucket  测试域名
     */
    public static String publicFile(String fileName,String domainOfBucket) {
        String encodedFileName=null;
        try {
            encodedFileName = URLEncoder.encode(fileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String finalUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
        return finalUrl;
    }



    /**
     * 私有空间返回文件URL
     */
    public static String privateFile(Auth auth,String fileName,String domainOfBucket,long expireInSeconds) {
        String encodedFileName=null;
        try {
            encodedFileName = URLEncoder.encode(fileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String publicUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
        String finalUrl = auth.privateDownloadUrl(publicUrl, expireInSeconds);
        System.out.println(finalUrl);
        return finalUrl;
    }



    /**
     * 获取文件下载地址
     */
    public static String getDownLoadUrl(String targetPath){
        return Auth.create(accessKey,secretKey).privateDownloadUrl(targetPath);
    }



    //删除文件
    public static int deleteFileFromQNiu(String fileName) throws QiniuException {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone1());
        String key = fileName;
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        Response delete = bucketManager.delete(bucket, key);
        return delete.statusCode;
    }

}
