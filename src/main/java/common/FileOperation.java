package common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileOperation {
    public static String writeToTmpFile(String fileName, String content) throws IOException {
        String tmpFilePath = getTmpDirectoryPath() + fileName;

        File file = new File(tmpFilePath);
        FileWriter fileWriterH = new FileWriter(file);
        fileWriterH.write(content);
        fileWriterH.close();

        return tmpFilePath;
    }


    public static void deleteTmpFile(String tmpFilePath){
        File tmpFile = new File(tmpFilePath);

        tmpFile.delete();
    }


    public static String getTmpDirectoryPath(){
        return System.getProperty("user.dir") + "/tmp/";
    }
}
