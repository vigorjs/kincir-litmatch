package com.example.kincir.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadFile(MultipartFile file, String folderName);

    String updateFile(MultipartFile file, String folderName, String oldImageUrl);

    void deleteImage(String url, String folderName);
}
