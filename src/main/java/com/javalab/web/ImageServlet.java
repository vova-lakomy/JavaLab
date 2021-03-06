package com.javalab.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.javalab.util.CustomImageUtils.*;


public class ImageServlet extends HttpServlet {

    private static final String IMAGE_PATH = System.getenv("OPENSHIFT_DATA_DIR") + "uploaded/";
    private static final String THUMBNAIL_PATH = System.getenv("OPENSHIFT_DATA_DIR") + "thumbs/";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);     //check if the encoding type
        if (!isMultipart) {                                                      //is 'multipart/form-data'
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(512 * 1024);                                     //max memory buffer, bytes
        File tempDir = new File(IMAGE_PATH);
        factory.setRepository(tempDir);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(1024 * 1024 * 5);                                       //max upload file size, bytes


        try {
            List items = upload.parseRequest(request);
            String fileName = "";
            FileItem fileToUpload = null;
            for (Object item : items) {                                           //get file and file-name fields
                FileItem fileItem = (FileItem) item;                              //from request
                if (fileItem.isFormField()) {
                    if (fileItem.getFieldName().equals("file-name")) {
                        fileName = fileItem.getString("UTF-8");
                    }
                } else{
                    fileToUpload = fileItem;
                }
            }

            if (fileToUpload.getSize() > 0) {
                checkContent(fileToUpload);
                saveUploadedFile(fileToUpload, IMAGE_PATH, THUMBNAIL_PATH, fileName);
            } else {
                throw new Exception("File is not chosen, please choose the file first");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
        response.sendRedirect("/images");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<String> fileNames = new ArrayList<String>();

        File[] files = new File(IMAGE_PATH).listFiles();                        //get list of files and folders

        for (File file : files) {
            if (file.isFile()) {                                                //save only files to list
                String fileName = file.getName();
                if (!".tmp".equals(fileName.substring(fileName.lastIndexOf('.')))) { // do not add *.tmp files to list
                    fileNames.add(fileName);
                }
            }
        }

        response.setCharacterEncoding("UTF-8");
        request.setAttribute("fileNames", fileNames);
        request.setAttribute("uploadPath", IMAGE_PATH);
        request.getRequestDispatcher("/list.jsp").forward(request, response);
    }

}
