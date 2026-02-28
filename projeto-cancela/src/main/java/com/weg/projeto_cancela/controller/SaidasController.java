package com.weg.projeto_cancela.controller;

import com.weg.projeto_cancela.model.RegistroCancela;
import com.weg.projeto_cancela.service.EstacionamentoService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/saidas")
@CrossOrigin(origins = "*")
public class SaidasController {
    private EstacionamentoService service;

    public SaidasController(EstacionamentoService service){
        this.service = service;
    }

    @GetMapping
    public List<RegistroCancela> getSaidas(){
        return service.buscarSaidas();
    }

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