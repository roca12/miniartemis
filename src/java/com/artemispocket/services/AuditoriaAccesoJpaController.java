/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artemispocket.services;

import com.artemispocket.entities.AuditoriaAcceso;
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
public class AuditoriaAccesoJpaController implements Serializable {

    public AuditoriaAccesoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(AuditoriaAcceso auditoriaAcceso) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuarios usuario = auditoriaAcceso.getUsuario();
            if (usuario != null) {
                usuario = em.getReference(usuario.getClass(), usuario.getId());
                auditoriaAcceso.setUsuario(usuario);
            }
            em.persist(auditoriaAcceso);
            if (usuario != null) {
                usuario.getAuditoriaAccesoList().add(auditoriaAcceso);
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

    public void edit(AuditoriaAcceso auditoriaAcceso) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            AuditoriaAcceso persistentAuditoriaAcceso = em.find(AuditoriaAcceso.class, auditoriaAcceso.getId());
            Usuarios usuarioOld = persistentAuditoriaAcceso.getUsuario();
            Usuarios usuarioNew = auditoriaAcceso.getUsuario();
            if (usuarioNew != null) {
                usuarioNew = em.getReference(usuarioNew.getClass(), usuarioNew.getId());
                auditoriaAcceso.setUsuario(usuarioNew);
            }
            auditoriaAcceso = em.merge(auditoriaAcceso);
            if (usuarioOld != null && !usuarioOld.equals(usuarioNew)) {
                usuarioOld.getAuditoriaAccesoList().remove(auditoriaAcceso);
                usuarioOld = em.merge(usuarioOld);
            }
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                usuarioNew.getAuditoriaAccesoList().add(auditoriaAcceso);
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
                Integer id = auditoriaAcceso.getId();
                if (findAuditoriaAcceso(id) == null) {
                    throw new NonexistentEntityException("The auditoriaAcceso with id " + id + " no longer exists.");
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
            AuditoriaAcceso auditoriaAcceso;
            try {
                auditoriaAcceso = em.getReference(AuditoriaAcceso.class, id);
                auditoriaAcceso.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The auditoriaAcceso with id " + id + " no longer exists.", enfe);
            }
            Usuarios usuario = auditoriaAcceso.getUsuario();
            if (usuario != null) {
                usuario.getAuditoriaAccesoList().remove(auditoriaAcceso);
                usuario = em.merge(usuario);
            }
            em.remove(auditoriaAcceso);
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

    public List<AuditoriaAcceso> findAuditoriaAccesoEntities() {
        return findAuditoriaAccesoEntities(true, -1, -1);
    }

    public List<AuditoriaAcceso> findAuditoriaAccesoEntities(int maxResults, int firstResult) {
        return findAuditoriaAccesoEntities(false, maxResults, firstResult);
    }

    private List<AuditoriaAcceso> findAuditoriaAccesoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(AuditoriaAcceso.class));
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

    public AuditoriaAcceso findAuditoriaAcceso(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(AuditoriaAcceso.class, id);
        } finally {
            em.close();
        }
    }

    public int getAuditoriaAccesoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<AuditoriaAcceso> rt = cq.from(AuditoriaAcceso.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
