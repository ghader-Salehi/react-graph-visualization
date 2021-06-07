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
@CrossOrigin("*")
public class Controller {

    @Value("${file.upload-dir}")
    private String FILE_DIRECTORY;


    @PostMapping("/process-dataset")
    private DatasetEndpoint uploadDataset(@RequestParam("file") MultipartFile paramFile, @RequestParam("flag") boolean flag) {
        try {
            System.out.println("Processing file " + paramFile.getOriginalFilename());
            File file = new File(FILE_DIRECTORY + paramFile.getOriginalFilename());
            boolean fileExists = file.exists();

            if(!fileExists) {
                if(!file.createNewFile()) {
                    System.out.println("Something went wrong");
                    return null;
                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(paramFile.getBytes());
                fos.close();
                String uploadedFileDirectory = FILE_DIRECTORY + paramFile.getOriginalFilename();
                Processor processor = new Processor(uploadedFileDirectory, flag);
                return processor.process(false);
            }
            else {
                String uploadedFileDirectory = FILE_DIRECTORY + paramFile.getOriginalFilename();
                Processor processor = new Processor(uploadedFileDirectory, flag);
                return processor.process(true);
            }
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }//end uploadDataset

    @PostMapping("/process-custom")
    private ArrayList<CustomGraphLink> processCustom(@RequestBody CustomGraph customGraph) {
        com.jafar.wiring.custom.Processor processor = new com.jafar.wiring.custom.Processor(new ArrayList<>(Arrays.asList(customGraph.getLinks())), new ArrayList<>(Arrays.asList(customGraph.getNodes())));
        return processor.getLINKS();
    }//end processCustom
}//end class
