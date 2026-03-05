package com.weg.projeto_cancela.infra.web.controller;

import com.weg.projeto_cancela.application.service.SaidasService;
import com.weg.projeto_cancela.domain.model.RegistroCancela;
import com.weg.projeto_cancela.application.service.EntradasService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/saidas")
@CrossOrigin(origins = "*")
public class SaidasController {
    private SaidasService service;

    public SaidasController(SaidasService service){
        this.service = service;
    }

    @GetMapping
    public List<RegistroCancela> getSaidas(){
        return service.buscarSaidas();
    }

    @GetMapping("/hoje")
    public List<RegistroCancela> getSaidasHoje(){ return service.ListarSaidasHoje(); }

    @GetMapping("/ontem")
    public List<RegistroCancela> getSaidasOntem(){
        return service.listarSaidasOntem();
    }

    @GetMapping("/semana")
    public List<RegistroCancela> getSaidasSemana(){
        return service.listarSaidasSemana();
    }

    @GetMapping("/passada")
    public List<RegistroCancela> getSaidasPassada(){
        return service.listarSaidasPassada();
    }
}