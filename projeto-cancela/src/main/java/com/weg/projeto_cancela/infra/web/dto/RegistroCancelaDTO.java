package com.weg.projeto_cancela.infra.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objeto de transferência de dados representando um Registro na Cancela")
public record RegistroCancelaDTO(
        @Schema(description = "ID único gerado pelo banco")
        String id,

        @Schema(description = "Tipo de evento que ocorreu")
        String evento,

        @Schema(description = "LocalDate representando o horario em que foi feito o Registro")
        String data
) {
}
