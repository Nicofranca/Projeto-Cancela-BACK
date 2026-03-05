package com.weg.projeto_cancela.application.service;

import com.weg.projeto_cancela.application.helpers.DataUtil;
import com.weg.projeto_cancela.domain.model.RegistroCancela;
import com.weg.projeto_cancela.domain.repository.RegistroCancelaRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaidasService {

    private final RegistroCancelaRepository repository;
    private final DataUtil dataUtil;


    public SaidasService(RegistroCancelaRepository repository, DataUtil dataUtil){
        this.repository = repository;
        this.dataUtil = dataUtil;
    }

    public List<RegistroCancela> buscarSaidas(){
        return repository.findByEvento("Carro Saindo");
    }

    public List<RegistroCancela> ListarSaidasHoje() {
        String[] limites = dataUtil.obterLimitesDiaUTC(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
        return repository.findByEventoAndDataBetween("Carro Saindo", limites[0], limites[1]);
    }

    public List<RegistroCancela> listarSaidasOntem() {
        String[] limites = dataUtil.obterLimitesDiaUTC(LocalDate.now(ZoneId.of("America/Sao_Paulo")).minusDays(1));
        return repository.findByEventoAndDataBetween("Carro Saindo", limites[0], limites[1]);
    }

    public List<RegistroCancela> listarSaidasSemana() {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate segundaAtual = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingoAtual = hoje.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        String[] limites = dataUtil.obterLimitesPeriodoUTC(segundaAtual, domingoAtual);
        return repository.findByEventoAndDataBetween("Carro Saindo", limites[0], limites[1]);
    }

    public List<RegistroCancela> listarSaidasPassada() {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate segundaPassada = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
        LocalDate domingoPassado = segundaPassada.plusDays(6);

        String[] limites = dataUtil.obterLimitesPeriodoUTC(segundaPassada, domingoPassado);
        return repository.findByEventoAndDataBetween("Carro Saindo", limites[0], limites[1]);
    }

    public List<RegistroCancela> buscarSaidasTurno(int turno) {
        List<RegistroCancela> saidasHoje = ListarSaidasHoje();

        switch (turno) {
            case 1:
                return dataUtil.filtrarPorHorario(saidasHoje, LocalTime.of(5, 0), LocalTime.of(14, 18));

            case 2:
                return dataUtil.filtrarPorHorario(saidasHoje, LocalTime.of(14, 24), LocalTime.of(23, 18));

            case 3:
                List<RegistroCancela> saidasOntem = listarSaidasOntem();
                List<RegistroCancela> turno3 = new ArrayList<>();

                turno3.addAll(dataUtil.filtrarPorHorario(saidasOntem, LocalTime.of(23, 24), LocalTime.of(23, 59)));
                turno3.addAll(dataUtil.filtrarPorHorario(saidasHoje, LocalTime.of(0, 0), LocalTime.of(5, 0)));

                return turno3;
        }
        return new ArrayList<>();
    }

}
