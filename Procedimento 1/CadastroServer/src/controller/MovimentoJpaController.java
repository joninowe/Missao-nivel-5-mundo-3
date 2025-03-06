/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import cadastroserver.exceptions.NonexistentEntityException;
import cadastroserver.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Movimento;
import model.Pessoa;
import model.Produto;
import model.Usuario;


public class MovimentoJpaController implements Serializable {

    public MovimentoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Movimento movimento) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa pessoaIdpessoa = movimento.getPessoaIdpessoa();
            if (pessoaIdpessoa != null) {
                pessoaIdpessoa = em.getReference(pessoaIdpessoa.getClass(), pessoaIdpessoa.getIdpessoa());
                movimento.setPessoaIdpessoa(pessoaIdpessoa);
            }
            Produto produtoIdproduto = movimento.getProdutoIdproduto();
            if (produtoIdproduto != null) {
                produtoIdproduto = em.getReference(produtoIdproduto.getClass(), produtoIdproduto.getIdproduto());
                movimento.setProdutoIdproduto(produtoIdproduto);
            }
            Usuario usuarioidUsuario = movimento.getUsuarioidUsuario();
            if (usuarioidUsuario != null) {
                usuarioidUsuario = em.getReference(usuarioidUsuario.getClass(), usuarioidUsuario.getIdusuario());
                movimento.setUsuarioidUsuario(usuarioidUsuario);
            }
            em.persist(movimento);
            if (pessoaIdpessoa != null) {
                pessoaIdpessoa.getMovimentoList().add(movimento);
                pessoaIdpessoa = em.merge(pessoaIdpessoa);
            }
            if (produtoIdproduto != null) {
                produtoIdproduto.getMovimentoList().add(movimento);
                produtoIdproduto = em.merge(produtoIdproduto);
            }
            if (usuarioidUsuario != null) {
                usuarioidUsuario.getMovimentoList().add(movimento);
                usuarioidUsuario = em.merge(usuarioidUsuario);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMovimento(movimento.getIdmovimento()) != null) {
                throw new PreexistingEntityException("Movimento " + movimento + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Movimento movimento) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movimento persistentMovimento = em.find(Movimento.class, movimento.getIdmovimento());
            Pessoa pessoaIdpessoaOld = persistentMovimento.getPessoaIdpessoa();
            Pessoa pessoaIdpessoaNew = movimento.getPessoaIdpessoa();
            Produto produtoIdprodutoOld = persistentMovimento.getProdutoIdproduto();
            Produto produtoIdprodutoNew = movimento.getProdutoIdproduto();
            Usuario usuarioidUsuarioOld = persistentMovimento.getUsuarioidUsuario();
            Usuario usuarioidUsuarioNew = movimento.getUsuarioidUsuario();
            if (pessoaIdpessoaNew != null) {
                pessoaIdpessoaNew = em.getReference(pessoaIdpessoaNew.getClass(), pessoaIdpessoaNew.getIdpessoa());
                movimento.setPessoaIdpessoa(pessoaIdpessoaNew);
            }
            if (produtoIdprodutoNew != null) {
                produtoIdprodutoNew = em.getReference(produtoIdprodutoNew.getClass(), produtoIdprodutoNew.getIdproduto());
                movimento.setProdutoIdproduto(produtoIdprodutoNew);
            }
            if (usuarioidUsuarioNew != null) {
                usuarioidUsuarioNew = em.getReference(usuarioidUsuarioNew.getClass(), usuarioidUsuarioNew.getIdusuario());
                movimento.setUsuarioidUsuario(usuarioidUsuarioNew);
            }
            movimento = em.merge(movimento);
            if (pessoaIdpessoaOld != null && !pessoaIdpessoaOld.equals(pessoaIdpessoaNew)) {
                pessoaIdpessoaOld.getMovimentoList().remove(movimento);
                pessoaIdpessoaOld = em.merge(pessoaIdpessoaOld);
            }
            if (pessoaIdpessoaNew != null && !pessoaIdpessoaNew.equals(pessoaIdpessoaOld)) {
                pessoaIdpessoaNew.getMovimentoList().add(movimento);
                pessoaIdpessoaNew = em.merge(pessoaIdpessoaNew);
            }
            if (produtoIdprodutoOld != null && !produtoIdprodutoOld.equals(produtoIdprodutoNew)) {
                produtoIdprodutoOld.getMovimentoList().remove(movimento);
                produtoIdprodutoOld = em.merge(produtoIdprodutoOld);
            }
            if (produtoIdprodutoNew != null && !produtoIdprodutoNew.equals(produtoIdprodutoOld)) {
                produtoIdprodutoNew.getMovimentoList().add(movimento);
                produtoIdprodutoNew = em.merge(produtoIdprodutoNew);
            }
            if (usuarioidUsuarioOld != null && !usuarioidUsuarioOld.equals(usuarioidUsuarioNew)) {
                usuarioidUsuarioOld.getMovimentoList().remove(movimento);
                usuarioidUsuarioOld = em.merge(usuarioidUsuarioOld);
            }
            if (usuarioidUsuarioNew != null && !usuarioidUsuarioNew.equals(usuarioidUsuarioOld)) {
                usuarioidUsuarioNew.getMovimentoList().add(movimento);
                usuarioidUsuarioNew = em.merge(usuarioidUsuarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = movimento.getIdmovimento();
                if (findMovimento(id) == null) {
                    throw new NonexistentEntityException("The movimento with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movimento movimento;
            try {
                movimento = em.getReference(Movimento.class, id);
                movimento.getIdmovimento();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The movimento with id " + id + " no longer exists.", enfe);
            }
            Pessoa pessoaIdpessoa = movimento.getPessoaIdpessoa();
            if (pessoaIdpessoa != null) {
                pessoaIdpessoa.getMovimentoList().remove(movimento);
                pessoaIdpessoa = em.merge(pessoaIdpessoa);
            }
            Produto produtoIdproduto = movimento.getProdutoIdproduto();
            if (produtoIdproduto != null) {
                produtoIdproduto.getMovimentoList().remove(movimento);
                produtoIdproduto = em.merge(produtoIdproduto);
            }
            Usuario usuarioidUsuario = movimento.getUsuarioidUsuario();
            if (usuarioidUsuario != null) {
                usuarioidUsuario.getMovimentoList().remove(movimento);
                usuarioidUsuario = em.merge(usuarioidUsuario);
            }
            em.remove(movimento);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Movimento> findMovimentoEntities() {
        return findMovimentoEntities(true, -1, -1);
    }

    public List<Movimento> findMovimentoEntities(int maxResults, int firstResult) {
        return findMovimentoEntities(false, maxResults, firstResult);
    }

    private List<Movimento> findMovimentoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Movimento.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Movimento findMovimento(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Movimento.class, id);
        } finally {
            em.close();
        }
    }

    public int getMovimentoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Movimento> rt = cq.from(Movimento.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
