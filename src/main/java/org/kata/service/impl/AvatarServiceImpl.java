package org.kata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.AvatarDto;
import org.kata.exception.AvatarException;
import org.kata.service.AvatarService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
@Slf4j
public class AvatarServiceImpl implements AvatarService {
    private final UrlProperties urlProperties;
    private final WebClient loaderWebClient;

    public AvatarServiceImpl(UrlProperties urlProperties) {
        this.urlProperties = urlProperties;
        this.loaderWebClient = WebClient.create(urlProperties.getProfileLoaderBaseUrl());
    }

    public AvatarDto createAvatarDto(String icp, MultipartFile file) {
        AvatarDto avatarDto;
        try {
            avatarDto = AvatarDto.builder()
                    .icp(icp)
                    .filename(file.getOriginalFilename())
                    .imageData(file.getBytes())
                    .build();
        } catch (IOException e) {
            throw new AvatarException("Error create Avatar");
        }
        if (!avatarDto.getIcp().isEmpty()) {
            sendAvatarDto(avatarDto);
            log.info("Succsess load Avatar for icp:{}", avatarDto.getIcp());
        }
        return avatarDto;
    }

    private void sendAvatarDto(AvatarDto dto) {
        loaderWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderPostAvatar())
                        .queryParam("icp", dto.getIcp())
                        .build())
                .body(Mono.just(dto), AvatarDto.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
