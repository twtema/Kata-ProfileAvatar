package org.kata.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class AvatarDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "ICP", example = "1234567890")
    private String icp;

    @Schema(description = "Имя файла", example = "avatar.png")
    private String filename;

    @Schema(description = "Данные изображения, максимум 5МБ")
    private byte[] imageData;
}
