package org.kata.service;

import org.kata.dto.AvatarDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AvatarService {
    AvatarDto createAvatarDto(String icp, MultipartFile file, String conversationId);
    AvatarDto getAvatarDto(String icp, String conversationId);
    List<AvatarDto> getAllAvatarsDto(String icp, String conversationId);
    void deleteAvatars(String icp, List<Boolean> flags, String conversationId);
}
