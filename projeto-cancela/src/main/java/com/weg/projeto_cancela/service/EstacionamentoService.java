package com.weg.projeto_cancela.service;

import com.weg.projeto_cancela.model.RegistroCancela;
import com.weg.projeto_cancela.repository.RegistroCancelaRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EstacionamentoService {

    private RegistroCancelaRepository repository;

    public EstacionamentoService(RegistroCancelaRepository repository){
        this.repository = repository;
    }

    private final int CAPACIDADE_TOTAL = 1381;

    public List<RegistroCancela> buscarEntradas(){
        return repository.findByEvento("Carro Entrando");
    }

    public List<RegistroCancela> buscarSaidas(){
        return repository.findByEvento("Carro Saindo");
    }

    public List<RegistroCancela> buscarAberturasPorBotao(){
        return repository.findByEventoContainingIgnoreCase("Botao Fisico");
    }

    public int calcularVagasDisponiveis(){
        long totalEntradas = repository.countByEvento("Carro Entrando");
        long totalSaidas = repository.countByEvento("Carro Saindo");

        long carrosDentro = totalEntradas - totalSaidas;

        if (carrosDentro < 0){
            carrosDentro = 0;
        }

        return (int) (CAPACIDADE_TOTAL - carrosDentro);
    }

    public long entradasPorDia(){

        String data = LocalDate.now(ZoneId.of("America/Sao_Paulo")).toString();

        return repository.countByEventoAndDataStartingWith("Carro Entrando", data);

    }

    public List<RegistroCancela> listarEntradasOntem(){
        String ontem = LocalDate.now(ZoneId.of("America/Sao_Paulo"))
                .minusDays(1)
                .toString();

        return repository.findByEventoAndDataStartingWith("Carro Entrando", ontem);
    }

    public List<RegistroCancela> listarSaidasOntem(){
        String ontem = LocalDate.now(ZoneId.of("America/Sao_Paulo"))
                .minusDays(1)
                .toString();

        return repository.findByEventoAndDataStartingWith("Carro Saindo", ontem);
    }

    public List<RegistroCancela> listarEntradasSemana() {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

        LocalDate segundaAtual = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDate domingoAtual = hoje.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        String dataInicio = segundaAtual.toString();
        String dataFim = domingoAtual.toString() + "T23:59:59";

        return repository.findByEventoAndDataBetween("Carro Entrando", dataInicio, dataFim);
    }

    public List<RegistroCancela> listarSaidasSemana() {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

        LocalDate segundaAtual = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDate domingoAtual = hoje.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        String dataInicio = segundaAtual.toString();
        String dataFim = domingoAtual.toString() + "T23:59:59";

        return repository.findByEventoAndDataBetween("Carro Saindo", dataInicio, dataFim);
    }

    public List<RegistroCancela> listarEntradasPassada(){
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

        LocalDate segundaAtual = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDate segundaPassada = segundaAtual.minusWeeks(1);
        LocalDate domingoPassado = segundaPassada.plusDays(6);

        String dataInicio = segundaPassada.toString();
        String dataFim = domingoPassado.toString() + "T23:59:59";

        return repository.findByEventoAndDataBetween("Carro Entrando", dataInicio, dataFim);
    }

    public List<RegistroCancela> listarSaidasPassada(){
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

        LocalDate segundaAtual = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDate segundaPassada = segundaAtual.minusWeeks(1);
        LocalDate domingoPassado = segundaPassada.plusDays(6);

        String dataInicio = segundaPassada.toString();
        String dataFim = domingoPassado.toString() + "T23:59:59";

        return repository.findByEventoAndDataBetween("Carro Saindo", dataInicio, dataFim);
    }



    public String gerarRelatorio(){

        List<RegistroCancela> todosRegistros = repository.findAll();

        StringBuilder exelData = new StringBuilder();

        exelData.append("ID;Evento;Data;Local\n");

        for (RegistroCancela registroCancela : todosRegistros){
            exelData.append(registroCancela.getId()).append(";")
                    .append(registroCancela.getEvento()).append(";")
                    .append(registroCancela.getData()).append("\n");
        }

        return exelData.toString();
    }
}
