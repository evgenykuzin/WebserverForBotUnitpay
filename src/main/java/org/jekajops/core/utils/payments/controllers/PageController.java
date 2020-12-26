package org.jekajops.core.utils.payments.controllers;

import org.jekajops.core.utils.files.FileManager;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;

@Controller
@RequestMapping("")
public class PageController {
    @RequestMapping(value = { "/" }, method = RequestMethod.GET)
    public String main() {
        return "/index";
    }

    @GetMapping("/wakemydyno.txt")
    @ResponseBody
    public FileSystemResource ping() {
        return new FileSystemResource(FileManager.getFileFromResources("wakemydyno.txt"));
    }

}
