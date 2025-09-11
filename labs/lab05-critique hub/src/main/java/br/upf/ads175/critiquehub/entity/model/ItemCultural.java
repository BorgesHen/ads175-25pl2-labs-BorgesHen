package br.upf.ads175.critiquehub.entity.model;

import jakarta.persistence.*;
import org.locationtech.jts.geom.LineSegment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "itens_culturais")
public class ItemCultural extends BaseEntity {
    @Column(nullable = false, length = 255)
    private String titulo;

    @ManyToMany
    @JoinTable(
        name = "item_cultural_genero",           // Nome da tabela de junção
        joinColumns = @JoinColumn(name = "item_cultural_id"),  // FK para ItemCultural
        inverseJoinColumns = @JoinColumn(name = "genero_id")   // FK para Genero
    )
    protected List<Genero> generos = new ArrayList<>();
}
