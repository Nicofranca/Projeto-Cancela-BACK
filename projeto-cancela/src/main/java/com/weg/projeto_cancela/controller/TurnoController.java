package com.weg.projeto_cancela.controller;

import com.weg.projeto_cancela.model.RegistroCancela;
import com.weg.projeto_cancela.service.EstacionamentoService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/turno")
@CrossOrigin(origins = "*")
public class TurnoController {
    private EstacionamentoService service;

    public TurnoController(EstacionamentoService service){
        this.service = service;
    }

    @GetMapping("/primeiro")
    public List<RegistroCancela> getPrimeiroTurno(){
        return service.buscarEntradasTurno(1);
    }

    @GetMapping("/segundo")
    public List<RegistroCancela> getSegundoTurno(){
        return service.buscarEntradasTurno(2);
    }

    @GetMapping("/terceiro")
    public List<RegistroCancela> getTerceiroTurno(){
        return service.buscarEntradasTurno(3);
    }
}