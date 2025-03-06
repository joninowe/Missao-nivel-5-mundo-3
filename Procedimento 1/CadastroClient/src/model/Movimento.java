/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@Table(name = "movimento")
@NamedQueries({
    @NamedQuery(name = "Movimento.findAll", query = "SELECT m FROM Movimento m"),
    @NamedQuery(name = "Movimento.findByIdmovimento", query = "SELECT m FROM Movimento m WHERE m.idmovimento = :idmovimento"),
    @NamedQuery(name = "Movimento.findByQuantidade", query = "SELECT m FROM Movimento m WHERE m.quantidade = :quantidade"),
    @NamedQuery(name = "Movimento.findByTipo", query = "SELECT m FROM Movimento m WHERE m.tipo = :tipo"),
    @NamedQuery(name = "Movimento.findByValorUnitario", query = "SELECT m FROM Movimento m WHERE m.valorUnitario = :valorUnitario")})
public class Movimento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idmovimento")
    private Integer idmovimento;
    @Basic(optional = false)
    @Column(name = "quantidade")
    private int quantidade;
    @Basic(optional = false)
    @Column(name = "tipo")
    private Character tipo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "valorUnitario")
    private BigDecimal valorUnitario;
    @JoinColumn(name = "pessoa_idpessoa", referencedColumnName = "idpessoa")
    @ManyToOne(optional = false)
    private Pessoa pessoaIdpessoa;
    @JoinColumn(name = "produto_idproduto", referencedColumnName = "idproduto")
    @ManyToOne(optional = false)
    private Produto produtoIdproduto;
    @JoinColumn(name = "Usuario_idUsuario", referencedColumnName = "idusuario")
    @ManyToOne(optional = false)
    private Usuario usuarioidUsuario;

    public Movimento() {
    }

    public Movimento(Integer idmovimento) {
        this.idmovimento = idmovimento;
    }

    public Movimento(Integer idmovimento, int quantidade, Character tipo, BigDecimal valorUnitario) {
        this.idmovimento = idmovimento;
        this.quantidade = quantidade;
        this.tipo = tipo;
        this.valorUnitario = valorUnitario;
    }

    public Integer getIdmovimento() {
        return idmovimento;
    }

    public void setIdmovimento(Integer idmovimento) {
        this.idmovimento = idmovimento;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Character getTipo() {
        return tipo;
    }

    public void setTipo(Character tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Pessoa getPessoaIdpessoa() {
        return pessoaIdpessoa;
    }

    public void setPessoaIdpessoa(Pessoa pessoaIdpessoa) {
        this.pessoaIdpessoa = pessoaIdpessoa;
    }

    public Produto getProdutoIdproduto() {
        return produtoIdproduto;
    }

    public void setProdutoIdproduto(Produto produtoIdproduto) {
        this.produtoIdproduto = produtoIdproduto;
    }

    public Usuario getUsuarioidUsuario() {
        return usuarioidUsuario;
    }

    public void setUsuarioidUsuario(Usuario usuarioidUsuario) {
        this.usuarioidUsuario = usuarioidUsuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idmovimento != null ? idmovimento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Movimento)) {
            return false;
        }
        Movimento other = (Movimento) object;
        if ((this.idmovimento == null && other.idmovimento != null) || (this.idmovimento != null && !this.idmovimento.equals(other.idmovimento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Movimento[ idmovimento=" + idmovimento + " ]";
    }
    
}
