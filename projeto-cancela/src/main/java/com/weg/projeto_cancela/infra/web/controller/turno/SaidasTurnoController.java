package com.weg.projeto_cancela.infra.web.controller.turno;

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
@RequestMapping("/api/turno/saidas")
@CrossOrigin(origins = "*")
@Tag(name = "Saidas-Turno")
public class SaidasTurnoController {
    private SaidasService service;

    public SaidasTurnoController(SaidasService service){
        this.service = service;
    }

    @Operation(summary = "Busca todas as saidas no primeiro turno de hoje", description = "Retorna o total de saidas no primeiro turno de hoje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Total de saidas no primeiro turno de hoje não encontrado")
    })
    @GetMapping("/primeiro")
    public List<RegistroCancela> getEntradaPrimeiroTurno(){
        return service.buscarSaidasTurno(1);
    }

    @Operation(summary = "Busca todas as saidas no segundo turno de hoje", description = "Retorna o total de saidas no segundo turno de hoje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Total de saidas no segundo turno de hoje não encontrado")
    })
    @GetMapping("/segundo")
    public List<RegistroCancela> getEntradaSegundoTurno(){
        return service.buscarSaidasTurno(2);
    }

    @Operation(summary = "Busca todas as saidas no terceiro turno de hoje", description = "Retorna o total de saidas no terceiro turno de hoje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Total de saidas no terceiro turno de hoje não encontrado")
    })
    @GetMapping("/terceiro")
    public List<RegistroCancela> getEntradaTerceiroTurno(){
        return service.buscarSaidasTurno(3);
    }

}