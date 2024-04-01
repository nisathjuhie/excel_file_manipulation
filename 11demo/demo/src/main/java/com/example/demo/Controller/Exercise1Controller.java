package com.example.demo.Controller;
import com.example.demo.Service.Exercise1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import  org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/excel")
public class Exercise1Controller {

    @Autowired
    Exercise1Service exercise1Service;

     //   private static final String UPLOAD_DIR = "C:\\Users\\2106791\\OneDrive - Cognizant\\Desktop\\Demo.xlsx";




        @PostMapping("/process")
        public String processExcelFile(@RequestParam("filePath") String filePath) throws IOException{
            try{
                return exercise1Service.processExcelFile(filePath);
               //  return "file sorted and saved successfully";
            }
            catch(Exception e)
            {
              return e.getMessage();
            }
        }


}
