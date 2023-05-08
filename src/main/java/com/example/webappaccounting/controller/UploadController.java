package com.example.webappaccounting.controller;

import com.example.webappaccounting.repository.ShiftRepo;
import com.example.webappaccounting.service.ParseHelper;
import com.example.webappaccounting.service.ShiftServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;


@Controller
public class UploadController {
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private ShiftRepo shiftRepo;

    @Autowired
    private ShiftServiceImpl service;
    //private static String UPLOAD_DIR = "src/main/java/com/example/webappaccounting/upload";
    @Value("${upload.path}")
    private String UPLOAD_DIR;

    @GetMapping("/upload")
    public String displayUploadForm(Map<String, Object> model) throws IOException {

        File file = new File(UPLOAD_DIR);
        if (!file.exists()) {
            file.mkdir();
        }

        String listFiles = listFiles(UPLOAD_DIR);
        if (!listFiles.isEmpty()) {
            model.put("msg", listFiles);
        } else {
            model.put("msg", "No files in folder");
        }

        return "upload";
    }

    @PostMapping("/upload")
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Выберите файл для загрузки");
            return "redirect:uploadStatus";
        }

        try {

            // Получить файл и сохранить его где-нибудь
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("message",
                    "Успешно загружен файл '" + file.getOriginalFilename() + "'");
            //shiftRepo.deleteAll();
            ParseHelper helper = new ParseHelper(shiftRepo, service);
            helper.ParseRecordCsv(path.toString());
            logger.debug("загружен файл");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

    private String listFiles(String dir) {
        StringBuilder builder = new StringBuilder();
        File folder = new File(dir);
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            builder.append(file.getName())
                    .append('\n');
        }
        return builder.toString();
    }
}
