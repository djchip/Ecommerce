package com.ecommerce.core.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUploadUtil {

	public static void saveFiles(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
		Path uploadDirectory = Paths.get(uploadDir);
		if(!Files.exists(uploadDirectory)) {
			Files.createDirectories(uploadDirectory);
		}
		try (InputStream inputStream = multipartFile.getInputStream()) {
			Path filePath = uploadDirectory.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IOException("Could not save file: " + fileName, e);
		}
	}
	
	public static void deleteFile(String uploadDir, String fileName) throws IOException {
		try {
			File file = new File(uploadDir + "/" + fileName);
			if(file != null) {
				file.delete();
			}
		}catch(Exception e) {}
	}

	public static void cleanDir(String dir) {
		Path dirPath = Paths.get(dir);
		try {
			Files.list(dirPath).forEach(file -> {
				if(!Files.isDirectory(file)) {
					try {
						Files.delete(file);
					} catch (IOException e) {
						log.error("Could not delete file: "+file);
//						System.out.println("Could not delete file: "+file);
					}
				}
			});
		} catch (IOException e) {
			log.error("Could not list directory: "+dirPath);
//			System.out.println("Could not list directory: "+dirPath);
		}
	}

	public static void removeDir(String dir){
		cleanDir(dir);
		try {
			Files.delete(Paths.get(dir));
		} catch (IOException e){
			log.error("Could not remove directory: "+dir);
		}
	}

	public static int getCharCount(String path) throws IOException {
		BufferedReader reader = null;
		File file = new File(path);
		FileInputStream fileInputStream = new FileInputStream(file);
		InputStreamReader input = new InputStreamReader(fileInputStream);
		reader = new BufferedReader(input);

		int charCount = 0;
		String data;
		while((data = reader.readLine()) != null){
			charCount += data.length();
		}
		return charCount;
	}

	public static void createFolder(String path){
		File theDir = new File(path);
		if (!theDir.exists()){
			theDir.mkdirs();
		}
	}
}
