package com.weg.projeto_cancela.infra.web.controller.turno;

import com.weg.projeto_cancela.domain.model.RegistroCancela;
import com.weg.projeto_cancela.application.service.EntradasService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/turno/entradas")
@CrossOrigin(origins = "*")
public class EntradasTurnoController {
    private EntradasService service;

    public EntradasTurnoController(EntradasService service){
        this.service = service;
    }

    @GetMapping("/primeiro")
    public List<RegistroCancela> getEntradaPrimeiroTurno(){
        return service.buscarEntradasTurno(1);
    }

    @GetMapping("/segundo")
    public List<RegistroCancela> getEntradaSegundoTurno(){
        return service.buscarEntradasTurno(2);
    }

    @GetMapping("/terceiro")
    public List<RegistroCancela> getEntradaTerceiroTurno(){
        return service.buscarEntradasTurno(3);
    }
}