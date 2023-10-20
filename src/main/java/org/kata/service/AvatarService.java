package org.kata.service;

import org.kata.dto.AvatarDto;
import org.springframework.web.multipart.MultipartFile;

public interface AvatarService {
    AvatarDto createAvatarDto(String icp, MultipartFile file);
}
