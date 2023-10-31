package org.kata.service;

import org.kata.dto.AvatarDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface AvatarService {
    AvatarDto createAvatarDto(String icp, MultipartFile file);
    ResponseEntity<AvatarDto> getAvatarDto(String icp);
    public ResponseEntity<List<AvatarDto>> getAllAvatarsDto(String icp);
    void deleteAvatars(String icp, List<Boolean> flags);
}
