package com.connectly.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ImageService {

    String uploadImage(MultipartFile contactImage, String filename);

    String getUrlFromPublicId(String publicId);

}
