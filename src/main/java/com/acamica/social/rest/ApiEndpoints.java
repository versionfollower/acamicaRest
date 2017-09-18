package com.acamica.social.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(path = ApiEndpoints.V1)
public class ApiEndpoints {
    public static final String V1 = "/v1";

    @RequestMapping(method = GET)
    public ResponseEntity<ApiResource> entrtypoint() {
       return ResponseEntity.ok(new ApiResource());
    }
}
