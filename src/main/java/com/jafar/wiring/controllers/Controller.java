package com.jafar.wiring.controllers;

import com.jafar.wiring.imageProcessing.Processor;
import com.jafar.wiring.models.CustomGraph;
import com.jafar.wiring.models.CustomGraphLink;
import com.jafar.wiring.models.DatasetEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
public class Controller {

    @Value("${file.upload-dir}")
    private String FILE_DIRECTORY;

    private String UPLOADED_FILE_DIRECTORY;

    @PostMapping("/upload-dataset")
    private String uploadDataset(@RequestParam("File") MultipartFile paramFile) {
        try {
            File file = new File(FILE_DIRECTORY + paramFile.getOriginalFilename());
            if(!file.createNewFile())
                return "Error: File could not be created!!!";
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(paramFile.getBytes());
            fos.close();
            UPLOADED_FILE_DIRECTORY = FILE_DIRECTORY + paramFile.getOriginalFilename();
            return "File uploaded successfully. :D";
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
        return "Something went wrong.";
    }//end uploadDataset

    @GetMapping("/process-dataset")
    private DatasetEndpoint processDataset() {
        try {
            return Processor.process(UPLOADED_FILE_DIRECTORY);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }//end processDataset

    @PostMapping("/process-custom")
    private ArrayList<CustomGraphLink> processCustom(@RequestBody CustomGraph customGraph) {
        com.jafar.wiring.custom.Processor processor = new com.jafar.wiring.custom.Processor(new ArrayList<>(Arrays.asList(customGraph.getLinks())), new ArrayList<>(Arrays.asList(customGraph.getNodes())));
        return processor.getLINKS();
    }//end processCustom



}//end class
