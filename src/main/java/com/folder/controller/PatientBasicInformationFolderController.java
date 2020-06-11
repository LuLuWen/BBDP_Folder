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

import com.folder.model.PatientBasicInformationFolderNumServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;

// Source: PatientBasicInformationFolderNumServer.java
@RestController
public class PatientBasicInformationFolderController {

    @Autowired
    DataSource dataSource;

    // Original method: getRecentFolder
    @RequestMapping(value = "/getTheAmountOfFiles", method = RequestMethod.POST)
    public String getTheAmountOfFiles(@RequestParam String patientID, @RequestParam String doctorID) {
        return PatientBasicInformationFolderNumServer.getRecentFolder(dataSource, patientID, doctorID);
    }
}
