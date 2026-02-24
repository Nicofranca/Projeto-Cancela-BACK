package com.weg.projeto_cancela.service;

import com.weg.projeto_cancela.model.RegistroCancela;
import com.weg.projeto_cancela.repository.RegistroCancelaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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

    public Map<String, Long> entradasPorDia(){

        String data = LocalDate.now(ZoneId.of("America/Sao_Paulo")).toString();

        List<RegistroCancela> entradasDia = repository.findByEventoAndDataStartingWith("Carro Entrando", data);

        long turno1 = 0;
        long turno2 = 0;
        long turno3 = 0;

        for (RegistroCancela registro : entradasDia){
            try {
                String horaString = registro.getData().substring(11, 13);

                int hora = Integer.parseInt(horaString);

                if (hora >= 5 && hora < 14){
                    turno1++;
                } else if (hora >= 14 && hora < 23) {
                    turno2++;
                } else {
                    turno3++;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Map<String, Long> resumo = new HashMap<>();
        resumo.put("total_dia", (long) entradasDia.size());
        resumo.put("turno_manha", turno1);
        resumo.put("turno_tarde", turno2);
        resumo.put("turno_noite", turno3);

        return resumo;

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
