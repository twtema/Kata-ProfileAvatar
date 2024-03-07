package org.kata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.AvatarDto;
import org.kata.exception.AvatarNotFoundException;
import org.kata.service.AvatarService;
import org.kata.service.util.ImageProcessor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

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

    @Override
    public AvatarDto createAvatarDto(String icp, MultipartFile file, String conversationId) {
        AvatarDto avatarDto;
        imageProcessor.process(file);
        avatarDto = AvatarDto.builder()
                .icp(icp)
                .filename(imageProcessor.getFilename())
                .imageData(imageProcessor.getImage())
                .uploadDate(ZonedDateTime.now())
                .build();
        if (!avatarDto.getIcp().isEmpty()) {
            sendAvatarDto(avatarDto, imageProcessor.getHex(), conversationId);
            log.info("Success load Avatar for icp:{}", avatarDto.getIcp());
        }
        return avatarDto;
    }

    @Override
    public AvatarDto getAvatarDto(String icp, String conversationId) {
        return Optional.ofNullable(loaderWebClient.get().
                        uri(uriBuilder -> uriBuilder
                                .path(urlProperties.getProfileLoaderGetAvatar())
                                .queryParam("id", icp)
                                .build())
                        .header("conversationId", conversationId)
                        .retrieve()
                        .toEntity(AvatarDto.class)
                        .block())
                .orElseThrow(() -> new AvatarNotFoundException("Avatar for icp " + icp + " not found"))
                .getBody();
    }

    @Override
    public List<AvatarDto> getAllAvatarsDto(String icp, String conversationId) {
        return loaderWebClient.get().
                uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderGetAllAvatars())
                        .queryParam("icp", icp)
                        .build())
                .header("conversationId", conversationId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AvatarDto>>() {
                })
                .block();
    }

    @Override
    public void deleteAvatars(String icp, List<Boolean> flags, String conversationId) {
        loaderWebClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderDeleteAvatar())
                        .queryParam("icp", icp)
                        .queryParam("flags", flags)
                        .build())
                .header("conversationId", conversationId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private void sendAvatarDto(AvatarDto dto, String hex, String conversationId) {
        loaderWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderPostAvatar())
                        .queryParam("icp", dto.getIcp())
                        .queryParam("hex", hex)
                        .build())
                .header("conversationId", conversationId)
                .body(Mono.just(dto), AvatarDto.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
