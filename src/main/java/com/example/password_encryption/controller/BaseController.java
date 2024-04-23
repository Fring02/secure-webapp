package com.example.password_encryption.controller;

import com.example.password_encryption.util.JwtUtilService;
import io.jsonwebtoken.JwtException;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BaseController {
    protected final Logger logger;
    protected final JwtUtilService jwtService;
    protected BaseController(Logger logger, JwtUtilService jwtUtilService){
        this.logger = logger;
        this.jwtService = jwtUtilService;
    }
    protected String validateAndFetchToken(String token){
        if(!token.startsWith("Bearer") || !token.substring(6).startsWith(" ")){
            logger.warn("Token is invalid");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        token = token.split(" ")[1];
        if(StringUtils.isBlank(token)) {
            logger.warn("Token is empty");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        try {
            if(!jwtService.isValid(token)) {
                logger.warn("Couldn't fetch user from token, unauthorized");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
            return token;
        }
        catch (IllegalArgumentException | JwtException e){
            logger.warn("Unauthorized: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
