package com.example.webappaccounting.service;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class AuthenticationSuccessListenerImpl implements ApplicationListener<AuthenticationSuccessEvent> {
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent authenticationSuccessEvent) {
        UserDetails userDetails = (UserDetails) authenticationSuccessEvent.getAuthentication().getPrincipal();
        String pathToFile =
        //"src/main/java/com/example/webappaccounting/upload/app.log";
        "/root/tmp/upload/app.log";
        String log = String.format(" вход пользователя %s", userDetails.getUsername());
        try {
            recordLog(pathToFile, log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recordLog(String pathToFile, String log) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        File file = new File(pathToFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String logDateTime = now.format(formatter);
        log = String.format("%s %s", logDateTime, log);
        try(FileWriter writer = new FileWriter(pathToFile, true))
        {
            writer.write(log);
            writer.append('\n');
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
}
