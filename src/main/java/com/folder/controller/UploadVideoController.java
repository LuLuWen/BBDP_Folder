package com.folder.controller;

import com.folder.model.FolderServer;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

// Original class: UploadVideoServlet
@RestController
public class UploadVideoController {
    @Autowired
    DataSource dataSource;

    @Autowired
    ServletContext context;

    @RequestMapping(value = "/uploadVideo", method = RequestMethod.POST)
    public void uploadVideo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String videoRootPath = context.getRealPath("resources/patientVideo");
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        Connection conn = null;
        //System.out.println("videoRootPath為 : "+videoRootPath);
        if(isMultipart){
            try {
                //Create a factory for disk-based file items
                FileItemFactory factory = new DiskFileItemFactory();

                //Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(factory);

                //Parse the request
                List<FileItem> items = upload.parseRequest(request);

                conn = dataSource.getConnection();

                FolderServer.uploadVideo(items, conn ,videoRootPath);
            }
            catch (FileUploadException e) {
                System.out.println("發生FileUploadException");
                e.printStackTrace();

            }
            catch (SQLException | IOException e) {
                e.printStackTrace();
            }
            finally {
                if (conn!=null) try {conn.close();}catch (Exception ignore) {}
            }
        }
    }
}
