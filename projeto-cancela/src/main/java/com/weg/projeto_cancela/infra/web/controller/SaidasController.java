package com.weg.projeto_cancela.infra.web.controller;

import com.weg.projeto_cancela.application.service.SaidasService;
import com.weg.projeto_cancela.domain.model.RegistroCancela;
import com.weg.projeto_cancela.application.service.EntradasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/saidas")
@CrossOrigin(origins = "*")
@Tag(name = "Saidas")
public class SaidasController {
    private SaidasService service;

    public SaidasController(SaidasService service){
        this.service = service;
    }

    @Operation(summary = "Busca todas as saidas", description = "Retorna o total de saidas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Total de saidas não encontrado")
    })
    @GetMapping
    public List<RegistroCancela> getSaidas(){
        return service.buscarSaidas();
    }

    @Operation(summary = "Busca todas as saidas de hoje", description = "Retorna o total de saidas no período de hoje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Total de saidas hoje não encontrado")
    })
    @GetMapping("/hoje")
    public List<RegistroCancela> getSaidasHoje(){ return service.ListarSaidasHoje(); }

    @Operation(summary = "Busca todas as saidas de ontem", description = "Retorna o total de saidas no período de ontem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Total de saidas ontem não encontrado")
    })
    @GetMapping("/ontem")
    public List<RegistroCancela> getSaidasOntem(){
        return service.listarSaidasOntem();
    }

    @Operation(summary = "Busca todas as saidas na Semana", description = "Retorna o total de saidas na Semana")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Total de saidas na Semana não encontrado")
    })
    @GetMapping("/semana")
    public List<RegistroCancela> getSaidasSemana(){
        return service.listarSaidasSemana();
    }

    @Operation(summary = "Busca todas as saidas na semana passada", description = "Retorna o total de saidas na semana passada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Total de saidas na semana passada não encontrado")
    })
    @GetMapping("/passada")
    public List<RegistroCancela> getSaidasPassada(){
        return service.listarSaidasPassada();
    }
}