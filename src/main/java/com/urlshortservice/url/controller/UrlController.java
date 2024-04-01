package com.urlshortservice.url.controller;

import com.urlshortservice.url.model.Url;
import com.urlshortservice.url.model.UrlDto;
import com.urlshortservice.url.model.UrlErrorResponseDto;
import com.urlshortservice.url.model.UrlResponseDto;
import com.urlshortservice.url.service.UrlService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RestController
public class UrlController {


        @Autowired UrlService urlService;
        private  String baseUrl="mkmkmmckmk/";
        @PostMapping("/generate")
        public ResponseEntity<?> generateShortLink(@RequestBody UrlDto urlDto)
        {
            Url urlToRet = urlService.generateShortLink(urlDto);

            if(urlToRet != null)
            {
                UrlResponseDto urlResponseDto = new UrlResponseDto();
                urlResponseDto.setOriginalUrl(urlToRet.getOriginalUrl());
                urlResponseDto.setExpirationDate(urlToRet.getExpirationDate());
                urlResponseDto.setShortLink(urlToRet.getShortLink());
                return new ResponseEntity<UrlResponseDto>(urlResponseDto, HttpStatus.OK);
            }

            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setStatus("404");
            urlErrorResponseDto.setError("There was an error processing your request. please try again.");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);

        }

        @GetMapping("/{shortLink}")
        public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortLink, HttpServletResponse response) throws IOException {

            if(StringUtils.isEmpty(shortLink))
            {
                UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
                urlErrorResponseDto.setError("Invalid Url");
                urlErrorResponseDto.setStatus("400");
                return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);
            }
            Url urlToRet = urlService.getEncodedUrl(shortLink);

            if(urlToRet == null)
            {
                UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
                urlErrorResponseDto.setError("Url does not exist or it might have expired!");
                urlErrorResponseDto.setStatus("400");
                return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);
            }

            if(urlToRet.getExpirationDate().isBefore(LocalDateTime.now()))
            {
                urlService.deleteShortLink(urlToRet);
                UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
                urlErrorResponseDto.setError("Url Expired. Please try generating a fresh one.");
                urlErrorResponseDto.setStatus("200");
                return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);
            }

            response.sendRedirect(urlToRet.getOriginalUrl());
            return null;
        }

}
