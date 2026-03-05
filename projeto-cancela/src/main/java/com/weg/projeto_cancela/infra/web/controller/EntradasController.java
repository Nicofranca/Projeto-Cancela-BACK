package com.weg.projeto_cancela.infra.web.controller;

import com.weg.projeto_cancela.application.service.RelatorioService;
import com.weg.projeto_cancela.domain.model.RegistroCancela;
import com.weg.projeto_cancela.application.service.EntradasService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entradas")
@CrossOrigin(origins = "*")
public class EntradasController {

    private final EntradasService service;
    private final RelatorioService relatorioService;

    public EntradasController(EntradasService service, RelatorioService relatorioService){
        this.service = service;
        this.relatorioService = relatorioService;
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
    public List<RegistroCancela> getResumoDia(){
        return service.ListarEntradasHoje();
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
        return service.listarEntradasPassada();
    }

    @GetMapping(value = "/relatorio/excel", produces = "text/csv")
    public ResponseEntity<String> baixarExel(){
        String conteudo = relatorioService.gerarRelatorio();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_cancela.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

        return ResponseEntity.ok()
                .headers(headers)
                .body(conteudo);
    }

}
