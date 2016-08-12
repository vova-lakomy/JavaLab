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

import static com.javalab.util.CustomFileUtils.*;


public class ImageServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);     //check if the encoding type
        if (!isMultipart) {                                                      //is 'multipart/form-data'
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String uploadDirPath = getServletContext().getRealPath("/uploaded/");     //set directory to upload

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(512 * 1024);                                     //max memory buffer, bytes
        File tempDir = (File) getServletContext().getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(tempDir);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(1024 * 1024 * 5);                                       //max upload file size, bytes


        try {
            List items = upload.parseRequest(request);
            String fileName = "";
            for (Object item : items) {                                           //searching for file name in request
                FileItem fileItem = (FileItem) item;
                if (fileItem.isFormField()) {
                    if (fileItem.getFieldName().equals("file-name")) {
                        fileName = fileItem.getString("UTF-8");
                    }
                }
            }

            for (Object item : items) {                                      //searching for file to upload in request
                FileItem fileItem = (FileItem) item;
                if (!fileItem.isFormField()) {
                    if (fileItem.getSize() > 0) {
                        saveUploadedFile(fileItem, uploadDirPath, fileName);
                    } else {
                        throw new Exception("File is not chosen");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
        response.sendRedirect("/image");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String uploadPath = getServletContext().getRealPath("/uploaded/");     //contents directory
        List<String> fileNames = new ArrayList<>();

        File[] files = new File(uploadPath).listFiles();                        //get list of files and folders

        for (File file : files) {
            if (file.isFile()) {                                                //save only files to list
                fileNames.add(file.getName());
            }
        }

        response.setCharacterEncoding("UTF-8");
        request.setAttribute("fileNames", fileNames);
        request.setAttribute("uploadPath", "./uploaded/");
        request.getRequestDispatcher("/list.jsp").forward(request, response);
    }

}
