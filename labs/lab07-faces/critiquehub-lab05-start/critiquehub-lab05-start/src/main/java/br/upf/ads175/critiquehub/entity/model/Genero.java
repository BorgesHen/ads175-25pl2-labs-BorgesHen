package br.upf.ads175.critiquehub.entity.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "generos")
public class Genero extends BaseEntity {
    @Column(unique = true, nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @ManyToMany(mappedBy = "generos")
    protected List<ItemCultural> items = new ArrayList<>();
}
