package com.weg.projeto_cancela.repository;

import com.weg.projeto_cancela.model.RegistroCancela;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroCancelaRepository extends MongoRepository<RegistroCancela, String> {

    List<RegistroCancela> findByEvento(String evento);
    long countByEvento(String evento);
    List<RegistroCancela> findByEventoContainingIgnoreCase(String texto);
    List<RegistroCancela> findByEventoAndDataStartingWith(String evento, String data);
    List<RegistroCancela> findByEventoAndDataBetween(String evento, String dataInicio, String dataFim);
}
