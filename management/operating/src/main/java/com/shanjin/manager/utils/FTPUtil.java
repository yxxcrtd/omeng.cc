package com.shanjin.manager.utils;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.shanjin.manager.constant.Constant;

public class FTPUtil {
    private FTPClient ftp;

    /**
     * 
     * @param path
     *            上传到ftp服务器哪个路径下
     * @param addr
     *            地址
     * @param port
     *            端口号
     * @param username
     *            用户名
     * @param password
     *            密码
     * @return
     * @throws Exception
     */
    private boolean connect(String path, String addr, int port, String username, String password) throws Exception {
        boolean result = false;
        ftp = new FTPClient();
        int reply;
        ftp.connect(addr, port);
        ftp.login(username, password);
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return result;
        }
        String[] paths = path.split("/");
        for (int i = 0; i < paths.length; i++) {
            if (i == 0) {
                ftp.changeWorkingDirectory("/");
            } else {
                ftp.changeWorkingDirectory(paths[i - 1]);
            }
            ftp.makeDirectory(paths[i]);
        }
        int index = paths.length - 1;
        if (paths[index].equals("/")) {
            index--;
        }
        ftp.changeWorkingDirectory(paths[index]);
        result = true;
        return result;
    }

    /**
     * 
     * @param file
     *            上传的文件或文件夹
     * @throws Exception
     */
    private boolean upload(File file,String fileName) throws Exception {
        boolean result = false;
        if (file.isDirectory()) {
            ftp.makeDirectory(file.getName());
            ftp.changeWorkingDirectory(file.getName());
            String[] files = file.list();
            for (int i = 0; i < files.length; i++) {
                File file1 = new File(file.getPath() + "\\" + files[i]);
                if (file1.isDirectory()) {
                    upload(file1,file1.getName());
                    ftp.changeToParentDirectory();
                } else {
                    File file2 = new File(file.getPath() + "\\" + files[i]);
                    FileInputStream input = new FileInputStream(file2);
                    result = ftp.storeFile(file2.getName(), input);
                    input.close();
                }
            }
        } else {
            File file2 = new File(file.getPath());
            FileInputStream input = new FileInputStream(file2);
            result = ftp.storeFile(fileName, input);
            input.close();
        }
        return result;
    }

    public static boolean uploadFile(String path, File file,String fileName) throws Exception {
        FTPUtil t = new FTPUtil();
        t.connect(path, Constant.FTPConfig.ADDR, Constant.FTPConfig.PORT, Constant.FTPConfig.USERNAME,
                Constant.FTPConfig.PASSWORD);
       // t.connect(path, "ftpserver", 21, "shanjin01","shanjin01");
        return t.upload(file,fileName);
    }
}
