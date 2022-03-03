package com.example.FileUploading.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLOutput;

@Service
public class FileUploaderService {

    Path fileStoragePath;
    String fileStoragePathFromAppProperties ;

    public FileUploaderService(@Value("${file.storage-location}") String fileStoragePathFromAppProperties) {
        System.out.println(fileStoragePathFromAppProperties);
        this.fileStoragePathFromAppProperties=fileStoragePathFromAppProperties;
        /*********************     get dynamic path from paths         ********************/
        this.fileStoragePath = Paths.get(fileStoragePathFromAppProperties)
                .toAbsolutePath().normalize();
        /*********************     Now we need to create the directory in which we want to
         *********************     Store the files                   ********************/
        try{
            Files.createDirectories(fileStoragePath);
        }catch (IOException e){
            throw new RuntimeException("issue in creating the Directory ");
        }
    }

    public String uploadFile (MultipartFile multipartFile){

        /***************** StringUtils.cleanPath will return clean file name removing
         ***************** all the extra symbols etc            *******************/

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        /***************** creating the file path   **************/
        Path filePath = Paths.get(fileStoragePath+"/"+fileName);
        /****               now copying the file to directory    ***/
        /**   .getInputStraeam() get all the file attributes and data and copy it to
               the filePath we provided                            **/
        try{
            Files.copy(multipartFile.getInputStream(),filePath, StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException e){
            throw new RuntimeException("internal server error while uploading file ");
        }

        return fileName;
    }



    //downloading the single file
    public Resource downloadFile(String fileName){
        Path path = Paths.get(fileStoragePathFromAppProperties).toAbsolutePath().resolve(fileName);
        System.out.println("from downlaod file" +path.toUri());

        Resource resource ;
        try{
          resource  = new UrlResource(path.toUri());
        }catch (MalformedURLException e){
            throw new RuntimeException("Issue in reading the file ");
        }

        if(resource.isReadable() && resource.exists()){
            return resource;
        }else{
            throw new RuntimeException("file Doesnot Exist or not readable ");
        }

    }

}
