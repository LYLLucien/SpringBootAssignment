package com.lucien.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Lucien on 2017/6/8.
 */
@Controller
public class AppController {

    @RequestMapping("/")
    String home(ModelMap modal) {
        return "index";
    }
}
