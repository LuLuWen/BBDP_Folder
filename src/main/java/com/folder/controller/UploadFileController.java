package com.folder.controller;

import com.folder.model.FolderServer;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

// Original class: UploadFileServlet
@RestController
public class UploadFileController {
    @Autowired
    DataSource dataSource;

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public void uploadFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        Connection conn = null;
        if (isMultipart) {
            try {
                //List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

                //Create a factory for disk-based file items
                FileItemFactory factory = new DiskFileItemFactory();

                //Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(factory);

                //Parse the request
                List<FileItem> items = upload.parseRequest(request);
                conn = dataSource.getConnection();

                FolderServer.uploadPhoto(items, conn);
            } catch (FileUploadException e) {
                System.out.println("發生FileUploadException");
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) try {
                    conn.close();
                } catch (Exception ignore) {
                }
            }
        }
    }
}
