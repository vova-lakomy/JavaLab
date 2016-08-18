package com.javalab.util;

import org.apache.commons.fileupload.FileItem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomImageUtils {

    private static Map<String, Integer> filePrefixMap = new HashMap<String, Integer>();

    private CustomImageUtils() {
    }

    /**
     * saves file on server (if file exists specifies a different file name with prefix)
     *
     * @param item     file to upload
     * @param imagePath     imagePath to upload file into
     * @param fileName desired file name
     * @throws Exception
     */
    public static void saveUploadedFile(FileItem item, String imagePath, String thumbnailPath, String fileName) throws Exception {

        fileName = fixFileName(item, fileName);
        File fileToSave = defineFile(fileName, imagePath);

        if (!fileToSave.getParentFile().exists()) {               //create a directory if not exist
            fileToSave.getParentFile().mkdirs();
        }
        fileToSave.createNewFile();
        item.write(fileToSave);                                   //save file

        createThumbnail(85, fileToSave.getPath(), thumbnailPath);
    }

    /**
     * checks if the selected file item content type is "image"
     *
     * @param item item to check
     * @throws Exception
     */
    public static void checkContent(FileItem item) throws Exception {
        if (!"image".equalsIgnoreCase(item.getContentType().split("/")[0])){
            throw new Exception("Chosen file is not an image. Choose another one. ");
        }
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
            return item.getName().replace(' ', '_');
        } else {
            String fileExtension = item.getName().substring(item.getName().lastIndexOf('.'));
            return fileName.replace(' ', '_') + fileExtension;
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

    /**
     * Creates a thumbnail of image with desired height,
     * saves to outputPath
     *
     * @param thumbnailHeight desired height
     * @param srcPath path to image source
     * @param outputPath path to save to
     * @throws IOException
     */
    private static void createThumbnail(int thumbnailHeight, String srcPath, String outputPath) throws IOException {
        BufferedImage img = ImageIO.read(new File(srcPath));                    //load image from file
        int thumbnailWidth = thumbnailHeight*img.getWidth()/img.getHeight();    //calculate proportional thumbnail width

        BufferedImage thumbnailImage = new BufferedImage( thumbnailWidth, thumbnailHeight, BufferedImage.TYPE_INT_RGB );

        Graphics2D graphics2D = createGraphicsObj(thumbnailWidth,thumbnailHeight,thumbnailImage);
        graphics2D.drawImage(img, 0, 0, thumbnailWidth, thumbnailHeight, null); //draw scaled image

        String fileName = new File(srcPath).getName();
        ImageIO.write( thumbnailImage, "PNG", new File(outputPath + fileName) ); //write image to a file
    }

    /**
     * creates Graphics2D object with selected params
     *
     * @param width width of graphics
     * @param height height of graphics
     * @param image clean buffered image
     * @return an instance of Graphics2D
     */
    private static Graphics2D createGraphicsObj(int width, int height, BufferedImage image){

        Graphics2D graphics = image.createGraphics(); //create a graphics object to paint to
        graphics.setBackground(Color.WHITE );         //and set properties
        graphics.setPaint(Color.WHITE );
        graphics.fillRect(0, 0, width, height);
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                                     RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
        return graphics;
    }
}
