package com.zzy.minibo.Utils;

import java.io.File;

public final class FilesManager {

    public static boolean createFileDir(String path){
        boolean isSuccess = false;
        File file = new File(path);
        if (!file.exists()){
           isSuccess = file.mkdir();
        }
        return isSuccess;
    }
}
