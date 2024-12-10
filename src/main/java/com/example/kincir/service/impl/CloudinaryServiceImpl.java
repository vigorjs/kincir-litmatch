package com.example.kincir.service.impl;

import com.cloudinary.Cloudinary;
import com.example.kincir.service.CloudinaryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    @Resource
    private Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folderName) {
        try {
            HashMap<String, String> options = new HashMap<>();
            options.put("folder", folderName);
            Map uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
            String publicId = (String) uploadedFile.get("public_id");
            return cloudinary.url().secure(true).generate(publicId);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String updateFile(MultipartFile file, String folderName, String oldImageUrl) {
        try {
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                deleteImage(oldImageUrl, folderName);
            }

            HashMap<String, String> options = new HashMap<>();
            options.put("folder", folderName);
            Map uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
            String publicId = (String) uploadedFile.get("public_id");
            return cloudinary.url().secure(true).generate(publicId);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteImage(String url, String folderName) {
        try {
            String publicId = folderName + "/" + extractPublicIdFromUrl(url);
            HashMap<Object, Object> options = new HashMap<>();
            options.put("folder", "user_profile");
            cloudinary.uploader().destroy(publicId, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String extractPublicIdFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1].split("\\.")[0];
    }
}
