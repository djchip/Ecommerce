package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.service.ContentType;
import com.ecommerce.core.service.FilesStorageService;
import com.ecommerce.core.service.impl.content_type.*;
import com.ecommerce.core.dto.ContentTypeDTO;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {
    private ContentTypeDTO dto;
    private final Path root = Paths.get(ConstantDefine.FILE_PATH.COLLECTION);
    private static final Map<String, ContentType> mapContentType;

    static {
        Map<String, ContentType> map = new HashMap<>();
        map.put("text/html; charset=utf-8", new HtmlType());
        map.put("application/pdf", new PdfType());
        map.put("image/png", new PngType());
        map.put("image/jpeg", new JpegType());
        map.put("application/msword", new MicrosoftWord());
        map.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", new MicrosoftWordOpenXML());
        map.put("text/csv", new CsvType());
        map.put("text/plain", new TextType());
        map.put("application/vnd.ms-excel", new MicrosoftExcel());
        map.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new MicrosoftExcelOpenXML());
        map.put("video/mp4", new MP4Video());
        map.put("audio/mpeg", new MP3Audio());
        mapContentType = Collections.unmodifiableMap(map);
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public void save(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    @Override
    public ContentTypeDTO setLinkOrFile(String encodeURI, Boolean isLink) throws Exception {
        String result = URLDecoder.decode(encodeURI, StandardCharsets.UTF_8.toString());
        System.out.println(result + " CHIEN");
        URL url = new URL(result);
        detectLinkOrFile(url);
        return dto;
    }

    private void detectLinkOrFile(URL url) throws Exception {
        URLConnection connection = url.openConnection();
        ContentType contentType = distributeType(connection.getContentType());
        contentType = detectTextHtml(contentType, url);
        System.out.println("contentType " + contentType);
        getAndSetFile(url, contentType);
    }

    private ContentType distributeType(String type) {
        return mapContentType.getOrDefault(type, null);
    }

    private void getAndSetFile(URL url, ContentType contentType) throws Exception {
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        // String FILE_NAME = "D:\\nong_nghiep\\hoc-vien-nong-nghiep\\DocumentManagement\\FILE_NAME" + contentType.getType();
        String FILE_NAME = "/app/exhibitionmanagement/DocumentManagement/FILE_NAME" + contentType.getType();
        FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME);
        // write data
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileOutputStream.getChannel()
                .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        // set Content from File

        InputStream is = new FileInputStream(FILE_NAME);
        contentType.handleContentType(is);
        // set path and content for ContentTypeDTO then return it
        dto = new ContentTypeDTO();
        dto.setFileName(FILE_NAME);
        dto.setContent(contentType.getContent());
        dto.setType(contentType.getType());
    }

    private ContentType detectTextHtml(ContentType contentType, URL url){
        ContentType.class.isAssignableFrom(HtmlType.class);
        if(url.getPath().contains(".pdf")){
            contentType = new PdfType();
        } else if(url.getPath().contains(".docx")){
            contentType = new MicrosoftWordOpenXML();
        } else if (url.getPath().contains(".doc")){
            contentType = new MicrosoftWordOpenXML();
        }
        return contentType;
    }
}
