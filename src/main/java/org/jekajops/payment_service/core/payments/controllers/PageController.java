package org.jekajops.payment_service.core.payments.controllers;

import org.jekajops.payment_service.core.utils.files.FileManager;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("")
public class PageController {
    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String main() {
        return "/index";
    }

    @RequestMapping(value = "/{file_name}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getFile(@PathVariable("file_name") String fileName) {
        return new FileSystemResource(FileManager.getFileFromResources("/files/"+fileName));
    }

}
