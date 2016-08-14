package com.javalab.util;

import org.apache.commons.fileupload.FileItem;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CustomFileUtils {

    private static Map<String, Integer> filePrefixMap = new HashMap<String, Integer>();

    private CustomFileUtils() {
    }

    /**
     * saves file on server (if file exists specifies a different file name with prefix)
     *
     * @param item     file to upload
     * @param path     path to upload file into
     * @param fileName desired file name
     * @throws Exception
     */
    public static void saveUploadedFile(FileItem item, String path, String fileName) throws Exception {

        fileName = fixFileName(item, fileName);
        File fileToSave = defineFile(fileName, path);

        if (!fileToSave.getParentFile().exists()) {               //create a directory if not exist
            fileToSave.getParentFile().mkdirs();
        }
        fileToSave.createNewFile();
        item.write(fileToSave);                                   //save file
    }

    /**
     * checks if the desired file name is empty, if so - returns the name of the original file
     *
     * @param item     original file
     * @param fileName desired name of file
     * @return
     */
    private static String fixFileName(FileItem item, String fileName) {
        if (fileName.equals("")) {
            return item.getName();
        } else {
            String fileExtension = item.getName().substring(item.getName().lastIndexOf('.'));
            return fileName + fileExtension;
        }
    }

    /**
     * defines a file, adds a prefix to file name if file already exists
     *
     * @param fileName name of file to save
     * @param path     path to save file into
     * @return
     */
    private static File defineFile(String fileName, String path) {
        File resultFile;

        do {
            String filePrefix = generatePrefix(fileName);
            String fullPath = path
                    + ((filePrefix.equals("(0) ")) ? "" : filePrefix)
                    + fileName;
            resultFile = new File(fullPath);
        } while (resultFile.exists());                      //generate prefixes while desired file name exists

        resetPrefixCounter(fileName);
        return resultFile;
    }

    /**
     * generates prefix for file name
     *
     * @param fileName
     * @return
     */
    private static String generatePrefix(String fileName) {
        if (!filePrefixMap.containsKey(fileName)) {
            filePrefixMap.put(fileName, 0);
        }
        Integer prefix = filePrefixMap.put(fileName, (filePrefixMap.get(fileName) + 1));
        return "(" + prefix + ") ";
    }

    /**
     * resets prefix counter
     *
     * @param fileName
     */
    private static void resetPrefixCounter(String fileName) {
        filePrefixMap.put(fileName, 0);
    }
}
