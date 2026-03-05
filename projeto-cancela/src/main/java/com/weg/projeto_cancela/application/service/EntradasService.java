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
public class EntradasService {

    private final RegistroCancelaRepository repository;
    private final DataUtil dataUtil;

    public EntradasService(RegistroCancelaRepository repository, DataUtil dataUtil){
        this.repository = repository;
        this.dataUtil =dataUtil;
    }

    private final int CAPACIDADE_TOTAL = 1381;

    public List<RegistroCancela> buscarEntradas(){
        List<RegistroCancela> listaCompleta = new ArrayList<>();

        listaCompleta.addAll(repository.findByEvento("Aberta por: Botao Fisico"));

        listaCompleta.addAll(repository.findByEvento("Carro Entrando"));

        return listaCompleta;
    }

    public List<RegistroCancela> buscarAberturasPorBotao(){
        return repository.findByEventoContainingIgnoreCase("Aberta por: Botao Fisico");
    }

    public int calcularVagasDisponiveis(){
        long totalEntradas = repository.countByEvento("Carro Entrando");
        long totalEntradasBotao = repository.countByEvento("Aberta por: Botao Fisico");
        long totalSaidas = repository.countByEvento("Carro Saindo");

        long carrosDentro = (totalEntradas+totalEntradasBotao) - totalSaidas;

        if (carrosDentro < 0){
            carrosDentro = 0;
        }

        return (int) (CAPACIDADE_TOTAL - carrosDentro);
    }

    public List<RegistroCancela> ListarEntradasHoje() {
        String[] limites = dataUtil.obterLimitesDiaUTC(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
        List<RegistroCancela> listaCompleta = new ArrayList<>();

        listaCompleta.addAll(repository.findByEventoAndDataBetween("Carro Entrando", limites[0], limites[1]));
        listaCompleta.addAll(repository.findByEventoAndDataBetween("Aberta por: Botao Fisico", limites[0], limites[1]));

        return listaCompleta;
    }

    public List<RegistroCancela> listarEntradasOntem() {
        String[] limites = dataUtil.obterLimitesDiaUTC(LocalDate.now(ZoneId.of("America/Sao_Paulo")).minusDays(1));
        List<RegistroCancela> listaCompleta = new ArrayList<>();

        listaCompleta.addAll(repository.findByEventoAndDataBetween("Carro Entrando", limites[0], limites[1]));
        listaCompleta.addAll(repository.findByEventoAndDataBetween("Aberta por: Botao Fisico", limites[0], limites[1]));

        return listaCompleta;
    }

    public List<RegistroCancela> listarEntradasSemana() {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate segundaAtual = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingoAtual = hoje.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        String[] limites = dataUtil.obterLimitesPeriodoUTC(segundaAtual, domingoAtual);
        List<RegistroCancela> listaCompleta = new ArrayList<>();

        listaCompleta.addAll(repository.findByEventoAndDataBetween("Carro Entrando", limites[0], limites[1]));
        listaCompleta.addAll(repository.findByEventoAndDataBetween("Aberta por: Botao Fisico", limites[0], limites[1]));

        return listaCompleta;
    }



    public List<RegistroCancela> listarEntradasPassada() {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate segundaPassada = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
        LocalDate domingoPassado = segundaPassada.plusDays(6);

        String[] limites = dataUtil.obterLimitesPeriodoUTC(segundaPassada, domingoPassado);
        List<RegistroCancela> listaCompleta = new ArrayList<>();

        listaCompleta.addAll(repository.findByEventoAndDataBetween("Carro Entrando", limites[0], limites[1]));
        listaCompleta.addAll(repository.findByEventoAndDataBetween("Aberta por: Botao Fisico", limites[0], limites[1]));

        return listaCompleta;
    }



    public List<RegistroCancela> buscarEntradasTurno(int turno) {
        List<RegistroCancela> entradasHoje = ListarEntradasHoje();

        switch (turno) {
            case 1:
                return dataUtil.filtrarPorHorario(entradasHoje, LocalTime.of(5, 0), LocalTime.of(14, 18));

            case 2:
                return dataUtil.filtrarPorHorario(entradasHoje, LocalTime.of(14, 24), LocalTime.of(23, 18));

            case 3:
                // Busca ontem para compor o turno - "madrugada"
                List<RegistroCancela> entradasOntem = listarEntradasOntem();
                List<RegistroCancela> turno3 = new ArrayList<>();

                turno3.addAll(dataUtil.filtrarPorHorario(entradasOntem, LocalTime.of(23, 24), LocalTime.of(23, 59)));
                turno3.addAll(dataUtil.filtrarPorHorario(entradasHoje, LocalTime.of(0, 0), LocalTime.of(5, 0)));

                return turno3;
        }
        return new ArrayList<>();
    }

}
