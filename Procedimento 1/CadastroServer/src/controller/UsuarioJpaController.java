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
import javax.persistence.NoResultException;
import model.Usuario;
import org.eclipse.persistence.tools.PackageRenamer;


public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, Exception {
        if (usuario.getMovimentoList() == null) {
            usuario.setMovimentoList(new ArrayList<Movimento>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Movimento> attachedMovimentoList = new ArrayList<Movimento>();
            for (Movimento movimentoListMovimentoToAttach : usuario.getMovimentoList()) {
                movimentoListMovimentoToAttach = em.getReference(movimentoListMovimentoToAttach.getClass(), movimentoListMovimentoToAttach.getIdmovimento());
                attachedMovimentoList.add(movimentoListMovimentoToAttach);
            }
            usuario.setMovimentoList(attachedMovimentoList);
            em.persist(usuario);
            for (Movimento movimentoListMovimento : usuario.getMovimentoList()) {
                Usuario oldUsuarioidUsuarioOfMovimentoListMovimento = movimentoListMovimento.getUsuarioidUsuario();
                movimentoListMovimento.setUsuarioidUsuario(usuario);
                movimentoListMovimento = em.merge(movimentoListMovimento);
                if (oldUsuarioidUsuarioOfMovimentoListMovimento != null) {
                    oldUsuarioidUsuarioOfMovimentoListMovimento.getMovimentoList().remove(movimentoListMovimento);
                    oldUsuarioidUsuarioOfMovimentoListMovimento = em.merge(oldUsuarioidUsuarioOfMovimentoListMovimento);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuario( usuario.getLogin(), usuario.getSenha()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getIdusuario());
            List<Movimento> movimentoListOld = persistentUsuario.getMovimentoList();
            List<Movimento> movimentoListNew = usuario.getMovimentoList();
            List<String> illegalOrphanMessages = null;
            for (Movimento movimentoListOldMovimento : movimentoListOld) {
                if (!movimentoListNew.contains(movimentoListOldMovimento)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Movimento " + movimentoListOldMovimento + " since its usuarioidUsuario field is not nullable.");
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
            usuario.setMovimentoList(movimentoListNew);
            usuario = em.merge(usuario);
            for (Movimento movimentoListNewMovimento : movimentoListNew) {
                if (!movimentoListOld.contains(movimentoListNewMovimento)) {
                    Usuario oldUsuarioidUsuarioOfMovimentoListNewMovimento = movimentoListNewMovimento.getUsuarioidUsuario();
                    movimentoListNewMovimento.setUsuarioidUsuario(usuario);
                    movimentoListNewMovimento = em.merge(movimentoListNewMovimento);
                    if (oldUsuarioidUsuarioOfMovimentoListNewMovimento != null && !oldUsuarioidUsuarioOfMovimentoListNewMovimento.equals(usuario)) {
                        oldUsuarioidUsuarioOfMovimentoListNewMovimento.getMovimentoList().remove(movimentoListNewMovimento);
                        oldUsuarioidUsuarioOfMovimentoListNewMovimento = em.merge(oldUsuarioidUsuarioOfMovimentoListNewMovimento);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuario.getIdusuario();
                if (findUsuario(usuario.getLogin(), usuario.getSenha()) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getIdusuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Movimento> movimentoListOrphanCheck = usuario.getMovimentoList();
            for (Movimento movimentoListOrphanCheckMovimento : movimentoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Movimento " + movimentoListOrphanCheckMovimento + " in its movimentoList field has a non-nullable usuarioidUsuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(String login, String senha) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.login = :login AND u.senha = :senha", Usuario.class)
                .setParameter("login", login)
                .setParameter("senha", senha)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
