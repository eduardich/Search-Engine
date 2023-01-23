package com.example.search_engine.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

    @RequestMapping("${user-config.web-interface-path}")
    public String index() {

        return "index";
    }

}
