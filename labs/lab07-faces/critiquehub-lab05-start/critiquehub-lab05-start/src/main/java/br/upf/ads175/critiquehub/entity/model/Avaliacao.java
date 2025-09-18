package br.upf.ads175.critiquehub.entity.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="avaliacoes")
public class Avaliacao extends BaseEntity {

    @ManyToOne()
    @JoinColumn(name="usuario_id")
    private Usuario usuario;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        if (this.usuario != null)
        {
            this.usuario.removerAvaliacao(this);
        }

        this.usuario = usuario;

        if (this.usuario != null)
        {
            this.usuario.adicionarAvaliacao(this);
        }
    }



}
