package com.weg.projeto_cancela.controller;

import com.weg.projeto_cancela.model.RegistroCancela;
import com.weg.projeto_cancela.service.EstacionamentoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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

    @GetMapping(value = "/relatorio/excel", produces = "text/csv")
    public ResponseEntity<String> baixarExel(){
        String conteudo = service.gerarRelatorio();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_cancela.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

        return ResponseEntity.ok()
                .headers(headers)
                .body(conteudo);
    }

    @GetMapping("/entradas/ontem")
    public List<RegistroCancela> getEntradasOntem(){
        return service.listarEntradasOntem();
    }

    @GetMapping("/saidas/ontem")
    public List<RegistroCancela> getSaidasOntem(){
        return service.listarSaidasOntem();
    }

    @GetMapping("/entradas/semana")
    public List<RegistroCancela> getEntradasSemana(){
        return service.listarEntradasSemana();
    }

    @GetMapping("/saidas/semana")
    public List<RegistroCancela> getSaidasSemana(){
        return service.listarSaidasSemana();
    }

    @GetMapping("/semanapassada")
    public List<RegistroCancela> getEntradasPassada(){
        return List.of();
    }

    @GetMapping("/turno/primeiro")
    public List<RegistroCancela> getPrimeiroTurno(){
        return List.of();
    }

    @GetMapping("/turno/segundo")
    public List<RegistroCancela> getSegundoTurno(){
        return List.of();
    }

    @GetMapping("/turno/terceiro")
    public List<RegistroCancela> getTerceiroTurno(){
        return List.of();
    }

}
