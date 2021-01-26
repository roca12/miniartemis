/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artemispocket.services;

import com.artemispocket.entities.CertInscripcion;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.artemispocket.entities.Universidades;
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
public class CertInscripcionJpaController implements Serializable {

    public CertInscripcionJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CertInscripcion certInscripcion) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Universidades institucion = certInscripcion.getInstitucion();
            if (institucion != null) {
                institucion = em.getReference(institucion.getClass(), institucion.getId());
                certInscripcion.setInstitucion(institucion);
            }
            em.persist(certInscripcion);
            if (institucion != null) {
                institucion.getCertInscripcionList().add(certInscripcion);
                institucion = em.merge(institucion);
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

    public void edit(CertInscripcion certInscripcion) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            CertInscripcion persistentCertInscripcion = em.find(CertInscripcion.class, certInscripcion.getId());
            Universidades institucionOld = persistentCertInscripcion.getInstitucion();
            Universidades institucionNew = certInscripcion.getInstitucion();
            if (institucionNew != null) {
                institucionNew = em.getReference(institucionNew.getClass(), institucionNew.getId());
                certInscripcion.setInstitucion(institucionNew);
            }
            certInscripcion = em.merge(certInscripcion);
            if (institucionOld != null && !institucionOld.equals(institucionNew)) {
                institucionOld.getCertInscripcionList().remove(certInscripcion);
                institucionOld = em.merge(institucionOld);
            }
            if (institucionNew != null && !institucionNew.equals(institucionOld)) {
                institucionNew.getCertInscripcionList().add(certInscripcion);
                institucionNew = em.merge(institucionNew);
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
                Integer id = certInscripcion.getId();
                if (findCertInscripcion(id) == null) {
                    throw new NonexistentEntityException("The certInscripcion with id " + id + " no longer exists.");
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
            CertInscripcion certInscripcion;
            try {
                certInscripcion = em.getReference(CertInscripcion.class, id);
                certInscripcion.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The certInscripcion with id " + id + " no longer exists.", enfe);
            }
            Universidades institucion = certInscripcion.getInstitucion();
            if (institucion != null) {
                institucion.getCertInscripcionList().remove(certInscripcion);
                institucion = em.merge(institucion);
            }
            em.remove(certInscripcion);
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

    public List<CertInscripcion> findCertInscripcionEntities() {
        return findCertInscripcionEntities(true, -1, -1);
    }

    public List<CertInscripcion> findCertInscripcionEntities(int maxResults, int firstResult) {
        return findCertInscripcionEntities(false, maxResults, firstResult);
    }

    private List<CertInscripcion> findCertInscripcionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CertInscripcion.class));
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

    public CertInscripcion findCertInscripcion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CertInscripcion.class, id);
        } finally {
            em.close();
        }
    }

    public int getCertInscripcionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CertInscripcion> rt = cq.from(CertInscripcion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
