/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import cadastroserver.exceptions.IllegalOrphanException;
import cadastroserver.exceptions.NonexistentEntityException;
import cadastroserver.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Movimento;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Produto;


public class ProdutoJpaController implements Serializable {

    public ProdutoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Produto produto) throws PreexistingEntityException, Exception {
        if (produto.getMovimentoList() == null) {
            produto.setMovimentoList(new ArrayList<Movimento>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Movimento> attachedMovimentoList = new ArrayList<Movimento>();
            for (Movimento movimentoListMovimentoToAttach : produto.getMovimentoList()) {
                movimentoListMovimentoToAttach = em.getReference(movimentoListMovimentoToAttach.getClass(), movimentoListMovimentoToAttach.getIdmovimento());
                attachedMovimentoList.add(movimentoListMovimentoToAttach);
            }
            produto.setMovimentoList(attachedMovimentoList);
            em.persist(produto);
            for (Movimento movimentoListMovimento : produto.getMovimentoList()) {
                Produto oldProdutoIdprodutoOfMovimentoListMovimento = movimentoListMovimento.getProdutoIdproduto();
                movimentoListMovimento.setProdutoIdproduto(produto);
                movimentoListMovimento = em.merge(movimentoListMovimento);
                if (oldProdutoIdprodutoOfMovimentoListMovimento != null) {
                    oldProdutoIdprodutoOfMovimentoListMovimento.getMovimentoList().remove(movimentoListMovimento);
                    oldProdutoIdprodutoOfMovimentoListMovimento = em.merge(oldProdutoIdprodutoOfMovimentoListMovimento);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProduto(produto.getIdproduto()) != null) {
                throw new PreexistingEntityException("Produto " + produto + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Produto produto) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produto persistentProduto = em.find(Produto.class, produto.getIdproduto());
            List<Movimento> movimentoListOld = persistentProduto.getMovimentoList();
            List<Movimento> movimentoListNew = produto.getMovimentoList();
            List<String> illegalOrphanMessages = null;
            for (Movimento movimentoListOldMovimento : movimentoListOld) {
                if (!movimentoListNew.contains(movimentoListOldMovimento)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Movimento " + movimentoListOldMovimento + " since its produtoIdproduto field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Movimento> attachedMovimentoListNew = new ArrayList<Movimento>();
            for (Movimento movimentoListNewMovimentoToAttach : movimentoListNew) {
                movimentoListNewMovimentoToAttach = em.getReference(movimentoListNewMovimentoToAttach.getClass(), movimentoListNewMovimentoToAttach.getIdmovimento());
                attachedMovimentoListNew.add(movimentoListNewMovimentoToAttach);
            }
            movimentoListNew = attachedMovimentoListNew;
            produto.setMovimentoList(movimentoListNew);
            produto = em.merge(produto);
            for (Movimento movimentoListNewMovimento : movimentoListNew) {
                if (!movimentoListOld.contains(movimentoListNewMovimento)) {
                    Produto oldProdutoIdprodutoOfMovimentoListNewMovimento = movimentoListNewMovimento.getProdutoIdproduto();
                    movimentoListNewMovimento.setProdutoIdproduto(produto);
                    movimentoListNewMovimento = em.merge(movimentoListNewMovimento);
                    if (oldProdutoIdprodutoOfMovimentoListNewMovimento != null && !oldProdutoIdprodutoOfMovimentoListNewMovimento.equals(produto)) {
                        oldProdutoIdprodutoOfMovimentoListNewMovimento.getMovimentoList().remove(movimentoListNewMovimento);
                        oldProdutoIdprodutoOfMovimentoListNewMovimento = em.merge(oldProdutoIdprodutoOfMovimentoListNewMovimento);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = produto.getIdproduto();
                if (findProduto(id) == null) {
                    throw new NonexistentEntityException("The produto with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produto produto;
            try {
                produto = em.getReference(Produto.class, id);
                produto.getIdproduto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The produto with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Movimento> movimentoListOrphanCheck = produto.getMovimentoList();
            for (Movimento movimentoListOrphanCheckMovimento : movimentoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Produto (" + produto + ") cannot be destroyed since the Movimento " + movimentoListOrphanCheckMovimento + " in its movimentoList field has a non-nullable produtoIdproduto field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(produto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Produto> findProdutoEntities() {
        return findProdutoEntities(true, -1, -1);
    }

    public List<Produto> findProdutoEntities(int maxResults, int firstResult) {
        return findProdutoEntities(false, maxResults, firstResult);
    }

    private List<Produto> findProdutoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Produto.class));
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

    public Produto findProduto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Produto.class, id);
        } finally {
            em.close();
        }
    }

    public int getProdutoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produto> rt = cq.from(Produto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
