package com.example.webappaccounting.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;


@Controller
public class UploadController {
    public static String UPLOAD_DIR = "src/main/java/com/example/webappaccounting/upload";
    //@Value("${upload.path}")
    //private String UPLOAD_DIR;

    @GetMapping("/upload")
    public String displayUploadForm(Map<String, Object> model) throws IOException {

        File file = new File(UPLOAD_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
        HashSet<String> filesInDir = (HashSet<String>) listFilesUsingDirectoryStream(UPLOAD_DIR);

        model.put("msg", filesInDir);
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file")MultipartFile file, Map<String, Object> model) throws IOException {
        StringBuilder fileNames = new StringBuilder();
        Path fileNameAndPath = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
        fileNames.append(file.getOriginalFilename());
        System.out.println(fileNames);
        Files.write(fileNameAndPath, file.getBytes());
        return "upload";
    }

    private Set<String> listFilesUsingDirectoryStream(String dir) throws IOException {
        Set<String> fileSet = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileSet.add(path.getFileName()
                            .toString());
                }
            }
        }
        return fileSet;
    }
}
