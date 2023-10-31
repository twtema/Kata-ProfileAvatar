package org.kata.controller;

import lombok.RequiredArgsConstructor;
import org.kata.dto.AvatarDto;
import org.kata.exception.AvatarException;
import org.kata.exception.AvatarNotFoundException;
import org.kata.service.AvatarService;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/avatar")
public class AvatarController {

    private final AvatarService avatarService;
    @Operation(summary = "Создать новый Avatar", description = "Сохраняет и возвращает DTO нового аватара")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avatar успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос")
    })
    @PostMapping("/createAvatar")
    public ResponseEntity<AvatarDto> uploadAvatar(@RequestParam("icp") String icp, @RequestParam("file") MultipartFile file) {
        return new ResponseEntity<>(
                avatarService.createAvatarDto(icp, file), HttpStatus.OK);
    }
    @Operation(summary = "Получить Avatar по icp",
            description= "Возвращает DTO Avatar по ICP")
    @GetMapping("/getAvatar")
    public ResponseEntity<AvatarDto> getAvatar(@Parameter(description = "ICP для получения") @RequestParam("icp") String icp) {
        return avatarService.getAvatarDto(icp);
    }
    @Operation(summary = "Получить список Avatar по icp",
            description= "Возвращает список DTO Avatar по ICP")
    @GetMapping("/getAllAvatars")
    public ResponseEntity<List<AvatarDto>> getAllAvatars(@Parameter(description = "ICP для получения List<Avatar>") @RequestParam("icp") String icp) {
        return avatarService.getAllAvatarsDto(icp);
    }
    @Operation(summary = "Запрос на удаление аватаров по icp и списку флагов",
            description = "Запрос на удаление одного или нескольких Avatar по icp и списку boolean (галочки) в соответствии со списком getAllAvatars(String icp)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Avatar успешно удалён"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос")
    })
    @DeleteMapping("/deleteAvatars")
    public ResponseEntity<HttpStatus> deleteAvatars(@Parameter(description = "ICP для удаления") @RequestParam("icp") String icp,
                                                    @Parameter(description = "Флаги для удаления") @RequestParam("flags") List<Boolean> flags) {
        avatarService.deleteAvatars(icp, flags);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @Operation(summary = "Обновляет активный аватар",
            description= "Обновляет аватар по icp и файлу-изображению")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Avatar успешно обновлён"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос")
    })
    @PatchMapping("/setActiveAvatar")
    public ResponseEntity<AvatarDto> setActiveAvatar(@Parameter(description = "ICP для обновления") @RequestParam("icp") String icp,
                                                     @Parameter(description = "Изображение для обновления") @RequestParam("file") MultipartFile file) {
        return new ResponseEntity<>(
                avatarService.createAvatarDto(icp, file), HttpStatus.ACCEPTED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AvatarException.class)
    public ErrorMessage getAvatarHandler(AvatarNotFoundException e) {
        return new ErrorMessage(e.getMessage());
    }
}
