package com.weg.projeto_cancela.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "historico")
public class RegistroCancela {

    @Id
    private String id;
    private String evento;
    private String data;

    @Field("data_formatada")
    private String dataFormatada;
    private Integer hora;

    public RegistroCancela() {
    }

    public RegistroCancela(String id, String evento, String data, String dataFormatada, Integer hora) {
        this.id = id;
        this.evento = evento;
        this.data = data;
        this.dataFormatada = dataFormatada;
        this.hora = hora;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDataFormatada() {
        return dataFormatada;
    }

    public void setDataFormatada(String dataFormatada) {
        this.dataFormatada = dataFormatada;
    }

    public Integer getHora() {
        return hora;
    }

    public void setHora(Integer hora) {
        this.hora = hora;
    }
}