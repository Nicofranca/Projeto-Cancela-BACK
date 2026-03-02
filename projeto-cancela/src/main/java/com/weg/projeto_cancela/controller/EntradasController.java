package com.weg.projeto_cancela.controller;

import com.weg.projeto_cancela.model.RegistroCancela;
import com.weg.projeto_cancela.service.EstacionamentoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/entradas")
@CrossOrigin(origins = "*")
public class EntradasController {

    private EstacionamentoService service;

    public EntradasController(EstacionamentoService service){
        this.service = service;
    }

    @GetMapping
    public List<RegistroCancela> getEntradas(){
        return service.buscarEntradas();
    }

    @GetMapping("/botao")
    public List<RegistroCancela> getAberturasBotao(){
        return service.buscarAberturasPorBotao();
    }

    @GetMapping("/vagas")
    public int getVagas(){
        return service.calcularVagasDisponiveis();
    }

    @GetMapping("/hoje")
    public Long getResumoDia(){
        return service.entradasPorDia();
    }

    @GetMapping("/ontem")
    public List<RegistroCancela> getEntradasOntem(){
        return service.listarEntradasOntem();
    }

    @GetMapping("/semana")
    public List<RegistroCancela> getEntradasSemana(){
        return service.listarEntradasSemana();
    }

    @GetMapping("/semanapassada")
    public List<RegistroCancela> getEntradasPassada(){
        return List.of();
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

}
