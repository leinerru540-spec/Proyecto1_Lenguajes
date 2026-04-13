package com.spring.app.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index"; 
    }

    @GetMapping("/vista/clientes")
    public String clientes() {
        return "clientes";
    }

    @GetMapping("/vista/consultorias")
    public String consultorias() {
        return "consultorias";
    }

    @GetMapping("/vista/solicitudes")
    public String solicitudes() {
        return "solicitudes";
    }
}
