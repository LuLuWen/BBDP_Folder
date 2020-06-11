/*
This code is generated from JMMA (Java Microservices migration assistant).
JMMA is a tiny widget that can help to create Controller and Feign templates.
The users only add some annotations.
More information and source code, you are able to find their on Github.
(https://github.com/570gina/JMMA)

Author: CHIAYU LI
National Taiwan Ocean University - SOSELab
*/
package com.folder.controller;

import java.io.*;
import java.sql.SQLException;
import java.util.Date;

import com.folder.model.FolderServer;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

// Source: FolderServer.java
@RestController
public class FolderController {
    @Autowired
    DataSource datasource;

    @Autowired
    ThreadPoolTaskExecutor executor;

    private static final int BUFFER_SIZE = 4096;

    // Original method: getVideo
    @RequestMapping(value = "/getVideo", method = RequestMethod.GET)
    public void getVideo(HttpServletResponse response, @RequestHeader("User-Agent") String agent, @RequestHeader("Range") String range, @RequestParam String videoPath) throws IOException {
        String videoType = videoPath.substring(videoPath.lastIndexOf(46) + 1, videoPath.length());
        if(videoType.equals("mov")||videoType.equals("MOV")){
            response.setContentType("video/quicktime");
        }
        else{
            response.setContentType("video/" + videoPath.substring(videoPath.lastIndexOf(46) + 1, videoPath.length()));
        }

        FileInputStream inputStream = null;
        ServletOutputStream outputStream = response.getOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;

        String outputType = "firstType";
        String browserDetails = agent.toLowerCase();

        if(browserDetails.indexOf("iphone") >= 0 || browserDetails.indexOf("ipad") >= 0){
            outputType = "secondType";		//iphone or ipad
        }
        else if(browserDetails.indexOf("macintosh") >= 0 && !browserDetails.contains("chrome")){
            outputType = "secondType";		//mac + safari
        }
        else if (browserDetails.indexOf("windows") >= 0 || browserDetails.toLowerCase().indexOf("android") >= 0){
            outputType = "firstType";		//windows or android
        }
        else if(browserDetails.indexOf("macintosh") >= 0 && browserDetails.contains("chrome")){
            outputType = "firstType";		//mac + chrome
        }

        try {
            inputStream = FolderServer.getVideo(videoPath);

            if(outputType.equals("firstType") && inputStream!=null){
                if(inputStream!=null){
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }
            else if(outputType.equals("secondType") && inputStream!=null){
                if( range != null && !range.equals("bytes=0-")) {
                    String[] ranges = range.split("=")[1].split("-");
                    int from = Integer.parseInt(ranges[0]);
                    int to = Integer.parseInt(ranges[1]);
                    int len = to - from + 1 ;

                    response.setStatus(206);
                    response.setHeader("Accept-Ranges", "bytes");
                    File f = new File(videoPath);
                    String responseRange = String.format("bytes %d-%d/%d", from, to, f.length());
                    response.setHeader("Connection", "close");
                    response.setHeader("Content-Range", responseRange);
                    response.setDateHeader("Last-Modified", new Date().getTime());
                    response.setContentLength(len);

                    byte[] buf = new byte[4096];
                    inputStream.skip(from);
                    while( len != 0) {
                        int read = inputStream.read(buf, 0, len >= buf.length ? buf.length : len);
                        if( read != -1) {
                            outputStream.write(buf, 0, read);
                            len -= read;
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("發生FileNotFoundException : " + e);
        }
        finally{
            if(inputStream!=null){
                inputStream.close();
                System.out.println("顯示影片inputStream.close()");
            }
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
                System.out.println("顯示影片outStream.close()");
            }
       }
    }

    // Original method: getPhoto
    @RequestMapping(value = "/getPhoto", method = RequestMethod.GET)
    public byte[] getPhoto(@RequestParam String patientID, @RequestParam String time) throws SQLException, IOException {
        InputStream in = FolderServer.getPhoto(datasource.getConnection(), patientID, time);
        return IOUtils.toByteArray(in);
    }

    // Original method: getSmallPhoto
    @RequestMapping(value = "/getSmallPhoto", method = RequestMethod.GET)
    public byte[] getSmallPhoto(@RequestParam String patientID, @RequestParam String time) throws SQLException, IOException {
        InputStream in = FolderServer.getSmallPhoto(datasource.getConnection(), patientID, time);
        return IOUtils.toByteArray(in);
    }

    // Original method: editPhoto
    @RequestMapping(value = "/editPhoto", method = RequestMethod.POST)
    public String editPhoto(@RequestParam String patientID, @RequestParam String time, @RequestParam String description) throws SQLException {
        return FolderServer.editPhoto(datasource.getConnection(), patientID, time, description);
    }

    // Original method: deletePhoto
    @RequestMapping(value = "/deletePhoto", method = RequestMethod.POST)
    public String deletePhoto(@RequestParam String patientID, @RequestParam String time, @RequestParam String videoPath) throws SQLException {
        FolderServer.deletePhoto(datasource.getConnection(), patientID, time);

        System.out.println(executor.getKeepAliveSeconds());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                File file = new File(videoPath);
                if (!file.exists()) {
                    System.out.println("影片不存在");
                }
                else {
                    System.out.println("刪除影片中...");
                    while(!file.delete()){}
                    System.out.println("已刪除"+videoPath);
                }
            }
        });

        return FolderServer.deletePhoto(datasource.getConnection(), patientID, time);
    }

    // Original method: getSelectFileInfo
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/getSelectFileInfo", method = RequestMethod.GET)
    public String getSelectFileInfo(@RequestParam String patientID, @RequestParam String time) throws SQLException {
        return FolderServer.getSelectFileInfo(datasource.getConnection(), patientID, time);
    }

    // Original method: getAllFileInfo
    @RequestMapping(value = "/getAllFileInfo", method = RequestMethod.POST)
    public String getAllFileInfo(@RequestParam String patientID) throws SQLException {
        return FolderServer.getAllFileInfo(datasource.getConnection(), patientID);
    }

    // Original method: getDoctorFileInfo
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/getDoctorFileInfo", method = RequestMethod.GET)
    public String getDoctorFileInfo(@RequestParam String patientID, @RequestParam String doctorID) throws SQLException {
        return FolderServer.getDoctorFileInfo(datasource.getConnection(), patientID, doctorID);
    }
}
