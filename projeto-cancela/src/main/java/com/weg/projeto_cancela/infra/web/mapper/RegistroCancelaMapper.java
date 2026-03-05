package com.weg.projeto_cancela.infra.web.mapper;

import com.weg.projeto_cancela.domain.model.RegistroCancela;
import com.weg.projeto_cancela.infra.web.dto.RegistroCancelaDTO;
import org.springframework.stereotype.Component;

@Component
public class RegistroCancelaMapper {
    public RegistroCancelaDTO toEntity(RegistroCancela registroCancela){
        return new RegistroCancelaDTO(
                registroCancela.getId(),
                registroCancela.getEvento(),
                registroCancela.getData()
        );
    }
}
