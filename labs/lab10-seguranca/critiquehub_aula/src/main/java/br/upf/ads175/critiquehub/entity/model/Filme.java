package br.upf.ads175.critiquehub.entity.model;


import dev.morphia.annotations.Entity;
import jakarta.persistence.Column;

import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@NamedQueries({
    @NamedQuery(
        name = "Filme.listarTodos",
        query = "select f from Filme f"
    )
})
@Table(name="filmes")
public class Filme extends BaseEntity {
 
    //@NotBlank(message = "O título é obrigatório")
    //@Size(max = 150, message = "O título não pode ter mais de 150 caracteres")
    @Column(nullable = false, length = 150)
    private String titulo;

    public Filme() {

    }

    public Filme(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String toString() {
        return titulo;
    }
}
