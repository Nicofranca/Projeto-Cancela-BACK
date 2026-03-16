package com.weg.projeto_cancela.infra.web.controller;

import com.weg.projeto_cancela.application.service.RelatorioService;
import com.weg.projeto_cancela.domain.model.RegistroCancela;
import com.weg.projeto_cancela.application.service.EntradasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entradas")
@CrossOrigin(origins = "*")
@Tag(name = "Entradas", description = "Endpoints para vizualização de entradas e baixar csv")
public class EntradasController {

    private final EntradasService service;
    private final RelatorioService relatorioService;

    public EntradasController(EntradasService service, RelatorioService relatorioService){
        this.service = service;
        this.relatorioService = relatorioService;
    }

    @Operation(summary = "Buscar todas as entradas", description = "Retorna o total de entradas ja registradas no banco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Entradas não encontrado")
    })
    @GetMapping
    public List<RegistroCancela> getEntradas(){
        return service.buscarEntradas();
    }

    @Operation(summary = "Buscar todas as entradas feitas por botão", description = "Retorna o total de entradas feitas por botão ja registradas no banco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Entradas feitas por botão não encontrado")
    })
    @GetMapping("/botao")
    public List<RegistroCancela> getAberturasBotao(){
        return service.buscarAberturasPorBotao();
    }

    @Operation(summary = "Calcula o total de vagas", description = "Retorna o total de vagas disponiveis no estacionamento, realizando um calculo sobre o total de entradas e o total de saidas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Total de vagas não encontrado")
    })
    @GetMapping("/vagas")
    public int getVagas(){
        return service.calcularVagasDisponiveis();
    }

    @Operation(summary = "Busca todas as entradas de hoje", description = "Retorna o total de entradas no período de hoje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Total de entadas hoje não encontrado")
    })
    @GetMapping("/hoje")
    public List<RegistroCancela> getResumoDia(){
        return service.ListarEntradasHoje();
    }

    @Operation(summary = "Busca todas as entradas de ontem", description = "Retorna o total de entradas no período de ontem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Total de entadas ontem não encontrado")
    })
    @GetMapping("/ontem")
    public List<RegistroCancela> getEntradasOntem(){
        return service.listarEntradasOntem();
    }

    @Operation(summary = "Busca todas as entradas de semana", description = "Retorna o total de entradas da semana")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Total de entadas na semana não encontrado")
    })
    @GetMapping("/semana")
    public List<RegistroCancela> getEntradasSemana(){
        return service.listarEntradasSemana();
    }

    @Operation(summary = "Busca todas as entradas de semana passada", description = "Retorna o total de entradas da semana passada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Total de entadas na semana passada não encontrado")
    })
    @GetMapping("/semanapassada")
    public List<RegistroCancela> getEntradasPassada(){
        return service.listarEntradasPassada();
    }

    @Operation(summary = "Gera relatorio csv", description = "Retorna um arquivo csv de todos os dados visiveis, com base no funcionamento do String Buider")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Busca não encontrada")
    })
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
