package org.kata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.AvatarDto;
import org.kata.service.AvatarService;
import org.kata.service.util.ImageProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@Service
@Slf4j
public class AvatarServiceImpl implements AvatarService {
    private final UrlProperties urlProperties;
    private final WebClient loaderWebClient;
    private final ImageProcessor imageProcessor;

    public AvatarServiceImpl(UrlProperties urlProperties, ImageProcessor imageProcessor) {
        this.urlProperties = urlProperties;
        this.loaderWebClient = WebClient.create(urlProperties.getProfileLoaderBaseUrl());
        this.imageProcessor = imageProcessor;
    }

    public AvatarDto createAvatarDto(String icp, MultipartFile file) {
        AvatarDto avatarDto;
        imageProcessor.process(file);
        avatarDto = AvatarDto.builder()
                .icp(icp)
                .filename(imageProcessor.getFilename())
                .imageData(imageProcessor.getImage())
                .build();
        if (!avatarDto.getIcp().isEmpty()) {
            sendAvatarDto(avatarDto, imageProcessor.getHex());
            log.info("Success load Avatar for icp:{}", avatarDto.getIcp());
        }
        return avatarDto;
    }

    public ResponseEntity<AvatarDto> getAvatarDto(String icp) {
        return loaderWebClient.get().
                uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderGetAvatar())
                        .queryParam("icp", icp)
                        .build())
                .retrieve()
                .toEntity(AvatarDto.class)
                .block();
    }

    @Override
    public ResponseEntity<List<AvatarDto>> getAllAvatarsDto(String icp) {
        return loaderWebClient.get().
                uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderGetAllAvatars())
                        .queryParam("icp", icp)
                        .build())
                .retrieve()
                .toEntityList(AvatarDto.class)
                .block();
    }

    @Override
    public void deleteAvatars(String icp, List<Boolean> flags) {
        loaderWebClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderDeleteAvatar())
                        .queryParam("icp", icp)
                        .queryParam("flags", flags)
                        .build())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private void sendAvatarDto(AvatarDto dto, String hex) {
        loaderWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderPostAvatar())
                        .queryParam("icp", dto.getIcp())
                        .queryParam("hex", hex)
                        .build())
                .body(Mono.just(dto), AvatarDto.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
