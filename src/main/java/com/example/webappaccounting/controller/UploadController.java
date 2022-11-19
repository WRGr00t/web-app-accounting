package com.example.webappaccounting.controller;

import com.example.webappaccounting.repository.ShiftRepo;
import com.example.webappaccounting.service.ParseHelper;
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

    @Autowired
    private ShiftRepo shiftRepo;
    public static String UPLOAD_DIR = "src/main/java/com/example/webappaccounting/upload";
    /*@Value("${upload.path}")
    private String UPLOAD_DIR;*/

    @GetMapping("/upload")
    public String displayUploadForm(Map<String, Object> model) throws IOException {

        File file = new File(UPLOAD_DIR);
        if (!file.exists()) {
            file.mkdir();
        }

        model.put("msg", listFiles(UPLOAD_DIR));
        return "upload";
    }

   /* @PostMapping("/upload")
    public String uploadFile(@RequestParam("file")MultipartFile file, Map<String, Object> model) throws IOException {
        if (file != null) {
            Path fileNameAndPath = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
            Files.write(fileNameAndPath, file.getBytes());
        }
        model.put("msg", getUploadDirList(UPLOAD_DIR));
        return "upload";
    }*/

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
            shiftRepo.deleteAll();
            ParseHelper helper = new ParseHelper(shiftRepo);
            helper.ParseRecordCsv(path.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

    private String listFiles(String dir) throws IOException {
        StringBuilder builder = new StringBuilder();
        File folder = new File(dir);
        for (File file : folder.listFiles()) {
            builder.append(file.getName())
                    .append('\n');
        }
        return builder.toString();
    }
}
