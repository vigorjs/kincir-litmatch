package com.example.kincir.service.impl;

import com.cloudinary.Cloudinary;
import com.example.kincir.config.security.advisers.exception.NotFoundException;
import com.example.kincir.model.meta.User;
import com.example.kincir.repository.UserRepository;
import com.example.kincir.service.AuthenticationService;
import com.example.kincir.service.UserService;
import com.example.kincir.utils.dto.request.RegisterRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final Cloudinary cloudinary;

    @Override
    public User create(User req) {
        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(req.getPassword().isEmpty() || req.getPassword() == null ? null : passwordEncoder.encode(req.getPassword()))
                .build();
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(Integer id) {
        return userRepository.findById(id).orElseThrow( () -> new NotFoundException("User not Found") );
    }

    @Override
    public void delete(Integer id) {
        if (userRepository.findById(id).isPresent()){
            userRepository.deleteById(id);
        }else {
            throw new NotFoundException("Category dengan ID " + id + "tidak ditemukan");
        }
    }

    @Override
    public User updateById(Integer id, RegisterRequestDTO req) {
        User target = getById(id);
        User user = updateUserDetails(target, req);
        userRepository.update(user);

        return target;
    }

    //Axel ganti bagian update
    @Override
    public User update(RegisterRequestDTO req, MultipartFile multipartFile) throws IOException {
        User user = authenticationService.getUserAuthenticated();

        if(user.getPhoto() != null && multipartFile != null && !multipartFile.isEmpty()){
            String oldPhoto = user.getPhoto();
            String oldCloudId;
            if(user.getCloudinaryImageId() == null){
                oldCloudId = oldPhoto.substring(oldPhoto.lastIndexOf('/') + 1, oldPhoto.lastIndexOf('.'));
            } else {
                oldCloudId = user.getCloudinaryImageId();
            }

            cloudinary.uploader().destroy(oldCloudId, Map.of());

            File convfile = new File(multipartFile.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(convfile);
            fos.write(multipartFile.getBytes());
            fos.close();

            String photo = cloudinary.uploader()
                    .upload(convfile, Map.of("public_id", "profile" + user.getUsername() + "-" + UUID.randomUUID()
                    ))
                    .get("url")
                    .toString();

            convfile.delete();
            user.setPhoto(photo);
            user.setCloudinaryImageId(photo.substring(photo.lastIndexOf('/') + 1, photo.lastIndexOf('.')));

        } else {
        File convFile = new File(multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream( convFile );
        fos.write(multipartFile.getBytes());
        fos.close();

        String photo = cloudinary.uploader()
                        .upload(convFile, Map.of("public_id", "profile" + user.getUsername() + "-" + UUID.randomUUID()
                                ))
                .get("url")
                .toString();

        convFile.delete();

        user.setPhoto(photo);
        user.setCloudinaryImageId(photo.substring(photo.lastIndexOf('/') + 1, photo.lastIndexOf('.')));
    }

        updateUserDetails(user, req);
        userRepository.update(user);

        return user;
    }

    private User updateUserDetails(User user, RegisterRequestDTO req) {
        if (req.getUsername() != null && !req.getUsername().isEmpty()) {
            user.setUsername(req.getUsername());
        }
        if (req.getEmail() != null && !req.getEmail().isEmpty()) {
            user.setEmail(req.getEmail());
        }
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        return user;
    }
}
