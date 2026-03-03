package com.weg.projeto_cancela.controller.turno;

import com.weg.projeto_cancela.model.RegistroCancela;
import com.weg.projeto_cancela.service.EstacionamentoService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/turno/saidas")
@CrossOrigin(origins = "*")
public class SaidasTurnoController {
    private EstacionamentoService service;

    public SaidasTurnoController(EstacionamentoService service){
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