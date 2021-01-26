/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artemispocket.services;

import com.artemispocket.entities.Enviosuva;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.artemispocket.entities.Lenguajes;
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
public class EnviosuvaJpaController implements Serializable {

    public EnviosuvaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Enviosuva enviosuva) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Lenguajes lenguajeid = enviosuva.getLenguajeid();
            if (lenguajeid != null) {
                lenguajeid = em.getReference(lenguajeid.getClass(), lenguajeid.getId());
                enviosuva.setLenguajeid(lenguajeid);
            }
            Usuarios userid = enviosuva.getUserid();
            if (userid != null) {
                userid = em.getReference(userid.getClass(), userid.getId());
                enviosuva.setUserid(userid);
            }
            em.persist(enviosuva);
            if (lenguajeid != null) {
                lenguajeid.getEnviosuvaList().add(enviosuva);
                lenguajeid = em.merge(lenguajeid);
            }
            if (userid != null) {
                userid.getEnviosuvaList().add(enviosuva);
                userid = em.merge(userid);
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

    public void edit(Enviosuva enviosuva) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Enviosuva persistentEnviosuva = em.find(Enviosuva.class, enviosuva.getId());
            Lenguajes lenguajeidOld = persistentEnviosuva.getLenguajeid();
            Lenguajes lenguajeidNew = enviosuva.getLenguajeid();
            Usuarios useridOld = persistentEnviosuva.getUserid();
            Usuarios useridNew = enviosuva.getUserid();
            if (lenguajeidNew != null) {
                lenguajeidNew = em.getReference(lenguajeidNew.getClass(), lenguajeidNew.getId());
                enviosuva.setLenguajeid(lenguajeidNew);
            }
            if (useridNew != null) {
                useridNew = em.getReference(useridNew.getClass(), useridNew.getId());
                enviosuva.setUserid(useridNew);
            }
            enviosuva = em.merge(enviosuva);
            if (lenguajeidOld != null && !lenguajeidOld.equals(lenguajeidNew)) {
                lenguajeidOld.getEnviosuvaList().remove(enviosuva);
                lenguajeidOld = em.merge(lenguajeidOld);
            }
            if (lenguajeidNew != null && !lenguajeidNew.equals(lenguajeidOld)) {
                lenguajeidNew.getEnviosuvaList().add(enviosuva);
                lenguajeidNew = em.merge(lenguajeidNew);
            }
            if (useridOld != null && !useridOld.equals(useridNew)) {
                useridOld.getEnviosuvaList().remove(enviosuva);
                useridOld = em.merge(useridOld);
            }
            if (useridNew != null && !useridNew.equals(useridOld)) {
                useridNew.getEnviosuvaList().add(enviosuva);
                useridNew = em.merge(useridNew);
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
                Integer id = enviosuva.getId();
                if (findEnviosuva(id) == null) {
                    throw new NonexistentEntityException("The enviosuva with id " + id + " no longer exists.");
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
            Enviosuva enviosuva;
            try {
                enviosuva = em.getReference(Enviosuva.class, id);
                enviosuva.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The enviosuva with id " + id + " no longer exists.", enfe);
            }
            Lenguajes lenguajeid = enviosuva.getLenguajeid();
            if (lenguajeid != null) {
                lenguajeid.getEnviosuvaList().remove(enviosuva);
                lenguajeid = em.merge(lenguajeid);
            }
            Usuarios userid = enviosuva.getUserid();
            if (userid != null) {
                userid.getEnviosuvaList().remove(enviosuva);
                userid = em.merge(userid);
            }
            em.remove(enviosuva);
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

    public List<Enviosuva> findEnviosuvaEntities() {
        return findEnviosuvaEntities(true, -1, -1);
    }

    public List<Enviosuva> findEnviosuvaEntities(int maxResults, int firstResult) {
        return findEnviosuvaEntities(false, maxResults, firstResult);
    }

    private List<Enviosuva> findEnviosuvaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Enviosuva.class));
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

    public Enviosuva findEnviosuva(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Enviosuva.class, id);
        } finally {
            em.close();
        }
    }

    public int getEnviosuvaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Enviosuva> rt = cq.from(Enviosuva.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
