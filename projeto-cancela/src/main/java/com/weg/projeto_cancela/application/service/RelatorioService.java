package com.weg.projeto_cancela.application.service;

import com.weg.projeto_cancela.domain.model.RegistroCancela;
import com.weg.projeto_cancela.domain.repository.RegistroCancelaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelatorioService {

    private RegistroCancelaRepository repository;

    public RelatorioService(RegistroCancelaRepository repository){
        this.repository = repository;
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
