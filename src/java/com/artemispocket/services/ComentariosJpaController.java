/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artemispocket.services;

import com.artemispocket.entities.Comentarios;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.artemispocket.entities.Usuarios;
import com.artemispocket.services.exceptions.NonexistentEntityException;
import com.artemispocket.services.exceptions.RollbackFailureException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author roca12
 */
public class ComentariosJpaController implements Serializable {

    public ComentariosJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Comentarios comentarios) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuarios usuario = comentarios.getUsuario();
            if (usuario != null) {
                usuario = em.getReference(usuario.getClass(), usuario.getId());
                comentarios.setUsuario(usuario);
            }
            em.persist(comentarios);
            if (usuario != null) {
                usuario.getComentariosList().add(comentarios);
                usuario = em.merge(usuario);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Comentarios comentarios) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Comentarios persistentComentarios = em.find(Comentarios.class, comentarios.getId());
            Usuarios usuarioOld = persistentComentarios.getUsuario();
            Usuarios usuarioNew = comentarios.getUsuario();
            if (usuarioNew != null) {
                usuarioNew = em.getReference(usuarioNew.getClass(), usuarioNew.getId());
                comentarios.setUsuario(usuarioNew);
            }
            comentarios = em.merge(comentarios);
            if (usuarioOld != null && !usuarioOld.equals(usuarioNew)) {
                usuarioOld.getComentariosList().remove(comentarios);
                usuarioOld = em.merge(usuarioOld);
            }
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                usuarioNew.getComentariosList().add(comentarios);
                usuarioNew = em.merge(usuarioNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = comentarios.getId();
                if (findComentarios(id) == null) {
                    throw new NonexistentEntityException("The comentarios with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Comentarios comentarios;
            try {
                comentarios = em.getReference(Comentarios.class, id);
                comentarios.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The comentarios with id " + id + " no longer exists.", enfe);
            }
            Usuarios usuario = comentarios.getUsuario();
            if (usuario != null) {
                usuario.getComentariosList().remove(comentarios);
                usuario = em.merge(usuario);
            }
            em.remove(comentarios);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Comentarios> findComentariosEntities() {
        return findComentariosEntities(true, -1, -1);
    }

    public List<Comentarios> findComentariosEntities(int maxResults, int firstResult) {
        return findComentariosEntities(false, maxResults, firstResult);
    }

    private List<Comentarios> findComentariosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Comentarios.class));
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

    public Comentarios findComentarios(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Comentarios.class, id);
        } finally {
            em.close();
        }
    }

    public int getComentariosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Comentarios> rt = cq.from(Comentarios.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
