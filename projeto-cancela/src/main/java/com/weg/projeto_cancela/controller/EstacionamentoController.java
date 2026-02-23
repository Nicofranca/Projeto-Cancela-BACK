package com.weg.projeto_cancela.controller;

import com.weg.projeto_cancela.model.RegistroCancela;
import com.weg.projeto_cancela.service.EstacionamentoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estacionamento")
@CrossOrigin(origins = "*")
public class EstacionamentoController {

    private EstacionamentoService service;

    public EstacionamentoController(EstacionamentoService service){
        this.service = service;
    }

    @GetMapping("/entradas")
    public List<RegistroCancela> getEntradas(){
        return service.buscarEntradas();
    }

    @GetMapping("/saidas")
    public List<RegistroCancela> getSaidas(){
        return service.buscarSaidas();
    }

    @GetMapping("/botao")
    public List<RegistroCancela> getAberturasBotao(){
        return service.buscarAberturasPorBotao();
    }

    @GetMapping("/vagas")
    public int getVagas(){
        return service.calcularVagasDisponiveis();
    }

    @GetMapping("/resumo/entradas/hoje")
    public Map<String, Long> getResumoDia(){
        return service.entradasPorDia();
    }


}
