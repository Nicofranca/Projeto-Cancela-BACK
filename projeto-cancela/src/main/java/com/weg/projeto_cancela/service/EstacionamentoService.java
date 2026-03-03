package com.weg.projeto_cancela.service;

import com.weg.projeto_cancela.model.RegistroCancela;
import com.weg.projeto_cancela.repository.RegistroCancelaRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
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
        List<RegistroCancela> listaCompleta = new ArrayList<>();

        listaCompleta.addAll(repository.findByEvento("Aberta por: Botao Fisico"));

        listaCompleta.addAll(repository.findByEvento("Carro Entrando"));

        return listaCompleta;
    }

    public List<RegistroCancela> buscarSaidas(){
        return repository.findByEvento("Carro Saindo");
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

    public List<RegistroCancela> ListarEntradasHoje(){

        String data = LocalDate.now(ZoneId.of("America/Sao_Paulo")).toString();

        List<RegistroCancela> listaCompleta = new ArrayList<>();

        listaCompleta.addAll(repository.findByEventoAndDataStartingWith("Carro Entrando", data));
        listaCompleta.addAll(repository.findByEventoAndDataStartingWith("Aberta por: Botao Fisico", data));

        return listaCompleta;
    }

    public List<RegistroCancela> listarEntradasOntem(){
        String ontem = LocalDate.now(ZoneId.of("America/Sao_Paulo"))
                .minusDays(1)
                .toString();

        List<RegistroCancela> listaCompleta = new ArrayList<>();

        listaCompleta.addAll(repository.findByEventoAndDataStartingWith("Carro Entrando", ontem));
        listaCompleta.addAll(repository.findByEventoAndDataStartingWith("Aberta por: Botao Fisico", ontem));

        return listaCompleta;
    }

    public List<RegistroCancela> ListarSaidasHoje(){

        String data = LocalDate.now(ZoneId.of("America/Sao_Paulo")).toString();

        List<RegistroCancela> listaCompleta = new ArrayList<>();

        listaCompleta.addAll(repository.findByEventoAndDataStartingWith("Carro Saindo", data));

        return listaCompleta;

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

        List<RegistroCancela> listaCompleta = new ArrayList<>();

        listaCompleta.addAll(repository.findByEventoAndDataBetween("Carro Entrando", dataInicio, dataFim));
        listaCompleta.addAll(repository.findByEventoAndDataBetween("Aberta por: Botao Fisico", dataInicio, dataFim));

        return listaCompleta;
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

        List<RegistroCancela> listaCompleta = new ArrayList<>();

        listaCompleta.addAll(repository.findByEventoAndDataBetween("Carro Entrando", dataInicio, dataFim));
        listaCompleta.addAll(repository.findByEventoAndDataBetween("Aberta por: Botao Fisico", dataInicio, dataFim));

        return listaCompleta;
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

    public List<RegistroCancela> filtrarPorHorario(List<RegistroCancela> registroCancelas, LocalTime inicio, LocalTime fim){
        List<RegistroCancela> filtro = new ArrayList<>();

        for (RegistroCancela r: registroCancelas){
            try {
                LocalTime horaRegistro = LocalTime.parse(r.getData().substring(11, 16));

                if(!horaRegistro.isBefore(inicio) && !horaRegistro.isAfter(fim)){
                    filtro.add(r);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return filtro;
    }

    public List<RegistroCancela> buscarEntradasTurno(int turno){
        LocalDate dataAtual = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        String dataHoje = dataAtual.toString();

        List<RegistroCancela> entradasHoje;
        List<RegistroCancela> entradasBotaoHoje;
        List<RegistroCancela> listaAll = new ArrayList<>();

        switch (turno){
            case 1:
                listaAll.addAll(repository.findByEventoAndDataStartingWith("Carro Entrando", dataHoje));
                listaAll.addAll(repository.findByEventoAndDataStartingWith("Aberta por: Botao Fisico", dataHoje));

                return filtrarPorHorario(listaAll, LocalTime.of(5, 0), LocalTime.of(14, 18));

            case 2:
                listaAll.addAll(repository.findByEventoAndDataStartingWith("Carro Entrando", dataHoje));
                listaAll.addAll(repository.findByEventoAndDataStartingWith("Aberta por: Botao Fisico", dataHoje));
                return  filtrarPorHorario(listaAll, LocalTime.of(14, 24), LocalTime.of(23, 18));
            case 3:
                String dataOntem = dataAtual.minusDays(1).toString();

                List<RegistroCancela> entradasOntem = repository.findByEventoAndDataStartingWith("Carro Entrando", dataOntem);
                entradasHoje = repository.findByEventoAndDataStartingWith("Carro Entrando", dataHoje);

                List<RegistroCancela> entradaBotaoOntem = repository.findByEventoAndDataStartingWith("Aberta por: Botao Fisico", dataOntem);
                entradasBotaoHoje =repository.findByEventoAndDataStartingWith("Aberta por: Botao Fisico", dataHoje);

                List<RegistroCancela> turno3 = new ArrayList<>();

                turno3.addAll(filtrarPorHorario(entradasOntem, LocalTime.of(23, 24), LocalTime.of(23, 59)));
                turno3.addAll(filtrarPorHorario(entradasHoje, LocalTime.of(0, 0), LocalTime.of(5, 0)));

                turno3.addAll(filtrarPorHorario(entradaBotaoOntem, LocalTime.of(23, 24), LocalTime.of(23, 59)));
                turno3.addAll(filtrarPorHorario(entradasBotaoHoje, LocalTime.of(0, 0), LocalTime.of(5, 0)));

                return turno3;
        }
        return new ArrayList<>();
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
