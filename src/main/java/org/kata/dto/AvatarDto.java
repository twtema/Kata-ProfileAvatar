package org.kata.dto;

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
    private String icp;

    private String filename;

    @Schema(maxLength = 5000000)
    private byte[] imageData;
}
