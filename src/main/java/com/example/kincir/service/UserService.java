package com.example.kincir.service;

import com.example.kincir.model.meta.User;
import com.example.kincir.utils.dto.request.RegisterRequestDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface UserService {
    User create(User req);
    List<User> getAll();
    User getById(Integer id);
    void delete(Integer id);
    User updateById(Integer id, RegisterRequestDTO req);
    //    User update (fitur)
    User update(RegisterRequestDTO req, MultipartFile multipartFile) throws IOException;
}
