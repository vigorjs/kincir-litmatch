package com.virgo.ecommerce.service;

import com.virgo.ecommerce.model.meta.User;
import com.virgo.ecommerce.utils.dto.RegisterRequestDTO;

import java.util.List;

public interface UserService {
    User create(RegisterRequestDTO req);
    List<User> getAll();
    User getById(Integer id);
    void delete(Integer id);
    User updateById(Integer id, RegisterRequestDTO req);
    //    User update (fitur)
    User update(RegisterRequestDTO req);
}
