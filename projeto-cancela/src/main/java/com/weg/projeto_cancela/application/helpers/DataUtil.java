package com.weg.projeto_cancela.application.helpers;

import com.weg.projeto_cancela.domain.model.RegistroCancela;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataUtil {

    public List<RegistroCancela> filtrarPorHorario(List<RegistroCancela> registroCancelas, LocalTime inicio, LocalTime fim) {
        List<RegistroCancela> filtro = new ArrayList<>();

        for (RegistroCancela r : registroCancelas) {
            try {
                //Pega a UTC do banco e converte para o Brasil
                ZonedDateTime dataUtc = ZonedDateTime.parse(r.getData());
                LocalTime horaRegistro = dataUtc.withZoneSameInstant(ZoneId.of("America/Sao_Paulo")).toLocalTime();

                if (!horaRegistro.isBefore(inicio) && !horaRegistro.isAfter(fim)) {
                    filtro.add(r);
                }
            } catch (Exception e) {
                System.err.println("Erro ao converter horário: " + e.getMessage());
            }
        }
        return filtro;
    }

    public String[] obterLimitesDiaUTC(LocalDate dataBrasil) {
        ZonedDateTime inicioBR = dataBrasil.atStartOfDay(ZoneId.of("America/Sao_Paulo"));
        ZonedDateTime fimBR = dataBrasil.atTime(23, 59, 59, 999999999).atZone(ZoneId.of("America/Sao_Paulo"));

        return new String[]{
                inicioBR.toInstant().toString(),
                fimBR.toInstant().toString()
        };
    }

    public String[] obterLimitesPeriodoUTC(LocalDate dataInicioBR, LocalDate dataFimBR) {
        ZonedDateTime inicioBR = dataInicioBR.atStartOfDay(ZoneId.of("America/Sao_Paulo"));
        ZonedDateTime fimBR = dataFimBR.atTime(23, 59, 59, 999999999).atZone(ZoneId.of("America/Sao_Paulo"));

        return new String[]{
                inicioBR.toInstant().toString(),
                fimBR.toInstant().toString()
        };
    }
}
