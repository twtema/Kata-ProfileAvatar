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

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/avatar")
public class AvatarController {

    private final AvatarService avatarService;

    @PostMapping("/createAvatar")
    public ResponseEntity<AvatarDto> uploadAvatar(@RequestParam("icp") String icp, @RequestParam("file") MultipartFile file) {
        return new ResponseEntity<>(
                avatarService.createAvatarDto(icp, file), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AvatarException.class)
    public ErrorMessage getAvatarHandler(AvatarNotFoundException e) {
        return new ErrorMessage(e.getMessage());
    }
}
