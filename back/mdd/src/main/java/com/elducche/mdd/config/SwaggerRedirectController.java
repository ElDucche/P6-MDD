package com.elducche.mdd.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerRedirectController {

    @GetMapping({"/", "/swagger.html"})
    public String redirectToSwaggerUi() {
        return "redirect:/swagger-ui/index.html";
    }
}
