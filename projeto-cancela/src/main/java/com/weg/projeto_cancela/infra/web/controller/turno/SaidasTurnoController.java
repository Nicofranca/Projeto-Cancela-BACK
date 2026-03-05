package com.weg.projeto_cancela.infra.web.controller.turno;

import com.weg.projeto_cancela.application.service.SaidasService;
import com.weg.projeto_cancela.domain.model.RegistroCancela;
import com.weg.projeto_cancela.application.service.EntradasService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/turno/saidas")
@CrossOrigin(origins = "*")
public class SaidasTurnoController {
    private SaidasService service;

    public SaidasTurnoController(SaidasService service){
        this.service = service;
    }

    @GetMapping("/primeiro")
    public List<RegistroCancela> getEntradaPrimeiroTurno(){
        return service.buscarSaidasTurno(1);
    }

    @GetMapping("/segundo")
    public List<RegistroCancela> getEntradaSegundoTurno(){
        return service.buscarSaidasTurno(2);
    }

    @GetMapping("/terceiro")
    public List<RegistroCancela> getEntradaTerceiroTurno(){
        return service.buscarSaidasTurno(3);
    }

}