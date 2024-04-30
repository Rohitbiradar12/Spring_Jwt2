package com.thbs.applicationgateway.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@CrossOrigin(origins={"*"})
public class simplecontroller {
    
    @GetMapping("/hello")
    public String getMethodName() {
        return new String("hi from torry harris");
    }
    
}
