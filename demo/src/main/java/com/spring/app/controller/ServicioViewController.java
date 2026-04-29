package com.spring.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spring.app.service.ConsultoriaService;

@Controller
@RequestMapping("/vista/servicios")
public class ServicioViewController {

    private final ConsultoriaService consultoriaService;

    public ServicioViewController(ConsultoriaService consultoriaService) {
        this.consultoriaService = consultoriaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("consultorias", consultoriaService.findAll());
        return "servicios";
    }
}
