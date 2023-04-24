package com.ecommerce.core.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDownloadUtil {

    private Path foundFile;
    private Path foundExhFile;
    public Resource getFileAsResource(String path, String fileName) throws IOException {
        Path uploadDirectory = Paths.get(path);
        Files.list(uploadDirectory).forEach(file ->{
            if (file.getFileName().toString().startsWith(fileName)){
                foundFile = file;
                return;
            }
        });

        if (foundFile != null){
            return new UrlResource(foundFile.toUri());
        }
        return null;
    }

    public Resource getExhFileAsResource(String filePath, String fileName) throws IOException {
        Path uploadDirectory = Paths.get(filePath);
        Files.list(uploadDirectory).forEach(file ->{
            if (file.getFileName().toString().startsWith(fileName)){
                foundExhFile = file;
                return;
            }
        });

        if (foundExhFile != null){
            return new UrlResource(foundExhFile.toUri());
        }
        return null;
    }


}
