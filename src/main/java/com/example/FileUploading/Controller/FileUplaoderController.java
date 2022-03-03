package com.example.FileUploading.Controller;


import com.example.FileUploading.pojo.FileUploadResponse;
import com.example.FileUploading.service.FileUploaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class FileUplaoderController {

    private final FileUploaderService fileUploaderService ;
    @Autowired
    FileUplaoderController(FileUploaderService fileUploaderService){
        this.fileUploaderService = fileUploaderService ;
    }

    @PostMapping("/single/upload")
    public ResponseEntity<FileUploadResponse> addImage(@RequestParam("file") MultipartFile multipartFile){

        String fileName  = fileUploaderService.uploadFile(multipartFile);

        /***************       this will return http://localhost:8080/downlaod/fileName.jpeg        **************/
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(fileName)
                .toUriString();

        String contentType = multipartFile.getContentType();

        /***************        Creating the FileUploadResponse from Pojo Class         ***************/

        FileUploadResponse response = new FileUploadResponse(fileName,contentType,fileUrl);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }


    @GetMapping("download/{fileName}")
    ResponseEntity<Resource> downlaodSingleFile(@PathVariable String fileName){

       Resource resource =  fileUploaderService.downloadFile(fileName);
        MediaType contentType = MediaType.IMAGE_JPEG;



        return ResponseEntity.status(HttpStatus.OK)
                .contentType(contentType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;fileName="+resource.getFilename()
                ).body(resource);
    }
}
