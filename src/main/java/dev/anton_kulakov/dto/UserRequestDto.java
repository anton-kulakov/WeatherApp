package dev.anton_kulakov.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserRequestDto {
    private int id;
    private String login;
}
