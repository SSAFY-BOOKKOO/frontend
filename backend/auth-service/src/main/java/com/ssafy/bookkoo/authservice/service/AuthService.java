package com.ssafy.bookkoo.authservice.service;

import com.ssafy.bookkoo.authservice.dto.RequestLoginDto;
import com.ssafy.bookkoo.authservice.dto.ResponseLoginTokenDto;

public interface AuthService {

    ResponseLoginTokenDto login(RequestLoginDto requestLoginDto);

    ResponseLoginTokenDto getTokenDto(String refreshToken);
}
