package com.elte.supplymanagersystem.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultPageController {

    /**
     * Returns SupplyManagerSystemApplication Is Running string for feedback
     * @return Returns a Response entity with the content: SupplyManagerSystemApplication Is Running
     */
    @RequestMapping("/")
    ResponseEntity hello() {
        return ResponseEntity.status(HttpStatus.OK).body("SupplyManagerSystemApplication Is Running");
    }

}
