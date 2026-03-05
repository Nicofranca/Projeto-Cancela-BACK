package com.weg.projeto_cancela.application.service;

import com.weg.projeto_cancela.domain.model.RegistroCancela;
import com.weg.projeto_cancela.domain.repository.RegistroCancelaRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

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

    public List<RegistroCancela> ListarEntradasHoje() {
        String[] limites = obterLimitesDiaUTC(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
        List<RegistroCancela> listaCompleta = new ArrayList<>();

        listaCompleta.addAll(repository.findByEventoAndDataBetween("Carro Entrando", limites[0], limites[1]));
        listaCompleta.addAll(repository.findByEventoAndDataBetween("Aberta por: Botao Fisico", limites[0], limites[1]));

        return listaCompleta;
    }

    public List<RegistroCancela> listarEntradasOntem() {
        String[] limites = obterLimitesDiaUTC(LocalDate.now(ZoneId.of("America/Sao_Paulo")).minusDays(1));
        List<RegistroCancela> listaCompleta = new ArrayList<>();

        listaCompleta.addAll(repository.findByEventoAndDataBetween("Carro Entrando", limites[0], limites[1]));
        listaCompleta.addAll(repository.findByEventoAndDataBetween("Aberta por: Botao Fisico", limites[0], limites[1]));

        return listaCompleta;
    }

    public List<RegistroCancela> ListarSaidasHoje() {
        String[] limites = obterLimitesDiaUTC(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
        return repository.findByEventoAndDataBetween("Carro Saindo", limites[0], limites[1]);
    }

    public List<RegistroCancela> listarSaidasOntem() {
        String[] limites = obterLimitesDiaUTC(LocalDate.now(ZoneId.of("America/Sao_Paulo")).minusDays(1));
        return repository.findByEventoAndDataBetween("Carro Saindo", limites[0], limites[1]);
    }

    public List<RegistroCancela> listarEntradasSemana() {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate segundaAtual = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingoAtual = hoje.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        String[] limites = obterLimitesPeriodoUTC(segundaAtual, domingoAtual);
        List<RegistroCancela> listaCompleta = new ArrayList<>();

        listaCompleta.addAll(repository.findByEventoAndDataBetween("Carro Entrando", limites[0], limites[1]));
        listaCompleta.addAll(repository.findByEventoAndDataBetween("Aberta por: Botao Fisico", limites[0], limites[1]));

        return listaCompleta;
    }

    public List<RegistroCancela> listarSaidasSemana() {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate segundaAtual = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingoAtual = hoje.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        String[] limites = obterLimitesPeriodoUTC(segundaAtual, domingoAtual);
        return repository.findByEventoAndDataBetween("Carro Saindo", limites[0], limites[1]);
    }

    public List<RegistroCancela> listarEntradasPassada() {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate segundaPassada = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
        LocalDate domingoPassado = segundaPassada.plusDays(6);

        String[] limites = obterLimitesPeriodoUTC(segundaPassada, domingoPassado);
        List<RegistroCancela> listaCompleta = new ArrayList<>();

        listaCompleta.addAll(repository.findByEventoAndDataBetween("Carro Entrando", limites[0], limites[1]));
        listaCompleta.addAll(repository.findByEventoAndDataBetween("Aberta por: Botao Fisico", limites[0], limites[1]));

        return listaCompleta;
    }

    public List<RegistroCancela> listarSaidasPassada() {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate segundaPassada = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
        LocalDate domingoPassado = segundaPassada.plusDays(6);

        String[] limites = obterLimitesPeriodoUTC(segundaPassada, domingoPassado);
        return repository.findByEventoAndDataBetween("Carro Saindo", limites[0], limites[1]);
    }

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

    private String[] obterLimitesDiaUTC(LocalDate dataBrasil) {
        ZonedDateTime inicioBR = dataBrasil.atStartOfDay(ZoneId.of("America/Sao_Paulo"));
        ZonedDateTime fimBR = dataBrasil.atTime(23, 59, 59, 999999999).atZone(ZoneId.of("America/Sao_Paulo"));

        return new String[]{
                inicioBR.toInstant().toString(),
                fimBR.toInstant().toString()
        };
    }

    private String[] obterLimitesPeriodoUTC(LocalDate dataInicioBR, LocalDate dataFimBR) {
        ZonedDateTime inicioBR = dataInicioBR.atStartOfDay(ZoneId.of("America/Sao_Paulo"));
        ZonedDateTime fimBR = dataFimBR.atTime(23, 59, 59, 999999999).atZone(ZoneId.of("America/Sao_Paulo"));

        return new String[]{
                inicioBR.toInstant().toString(),
                fimBR.toInstant().toString()
        };
    }

    public List<RegistroCancela> buscarEntradasTurno(int turno) {
        List<RegistroCancela> entradasHoje = ListarEntradasHoje();

        switch (turno) {
            case 1:
                return filtrarPorHorario(entradasHoje, LocalTime.of(5, 0), LocalTime.of(14, 18));

            case 2:
                return filtrarPorHorario(entradasHoje, LocalTime.of(14, 24), LocalTime.of(23, 18));

            case 3:
                // Busca ontem para compor o turno - "madrugada"
                List<RegistroCancela> entradasOntem = listarEntradasOntem();
                List<RegistroCancela> turno3 = new ArrayList<>();

                turno3.addAll(filtrarPorHorario(entradasOntem, LocalTime.of(23, 24), LocalTime.of(23, 59)));
                turno3.addAll(filtrarPorHorario(entradasHoje, LocalTime.of(0, 0), LocalTime.of(5, 0)));

                return turno3;
        }
        return new ArrayList<>();
    }

    public List<RegistroCancela> buscarSaidasTurno(int turno) {
        List<RegistroCancela> saidasHoje = ListarSaidasHoje();

        switch (turno) {
            case 1:
                return filtrarPorHorario(saidasHoje, LocalTime.of(5, 0), LocalTime.of(14, 18));

            case 2:
                return filtrarPorHorario(saidasHoje, LocalTime.of(14, 24), LocalTime.of(23, 18));

            case 3:
                List<RegistroCancela> saidasOntem = listarSaidasOntem();
                List<RegistroCancela> turno3 = new ArrayList<>();

                turno3.addAll(filtrarPorHorario(saidasOntem, LocalTime.of(23, 24), LocalTime.of(23, 59)));
                turno3.addAll(filtrarPorHorario(saidasHoje, LocalTime.of(0, 0), LocalTime.of(5, 0)));

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
