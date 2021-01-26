/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artemispocket.services;

import com.artemispocket.entities.Feed;
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
public class FeedJpaController implements Serializable {

    public FeedJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Feed feed) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuarios usuariocreador = feed.getUsuariocreador();
            if (usuariocreador != null) {
                usuariocreador = em.getReference(usuariocreador.getClass(), usuariocreador.getId());
                feed.setUsuariocreador(usuariocreador);
            }
            em.persist(feed);
            if (usuariocreador != null) {
                usuariocreador.getFeedList().add(feed);
                usuariocreador = em.merge(usuariocreador);
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

    public void edit(Feed feed) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Feed persistentFeed = em.find(Feed.class, feed.getId());
            Usuarios usuariocreadorOld = persistentFeed.getUsuariocreador();
            Usuarios usuariocreadorNew = feed.getUsuariocreador();
            if (usuariocreadorNew != null) {
                usuariocreadorNew = em.getReference(usuariocreadorNew.getClass(), usuariocreadorNew.getId());
                feed.setUsuariocreador(usuariocreadorNew);
            }
            feed = em.merge(feed);
            if (usuariocreadorOld != null && !usuariocreadorOld.equals(usuariocreadorNew)) {
                usuariocreadorOld.getFeedList().remove(feed);
                usuariocreadorOld = em.merge(usuariocreadorOld);
            }
            if (usuariocreadorNew != null && !usuariocreadorNew.equals(usuariocreadorOld)) {
                usuariocreadorNew.getFeedList().add(feed);
                usuariocreadorNew = em.merge(usuariocreadorNew);
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
                Integer id = feed.getId();
                if (findFeed(id) == null) {
                    throw new NonexistentEntityException("The feed with id " + id + " no longer exists.");
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
            Feed feed;
            try {
                feed = em.getReference(Feed.class, id);
                feed.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The feed with id " + id + " no longer exists.", enfe);
            }
            Usuarios usuariocreador = feed.getUsuariocreador();
            if (usuariocreador != null) {
                usuariocreador.getFeedList().remove(feed);
                usuariocreador = em.merge(usuariocreador);
            }
            em.remove(feed);
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

    public List<Feed> findFeedEntities() {
        return findFeedEntities(true, -1, -1);
    }

    public List<Feed> findFeedEntities(int maxResults, int firstResult) {
        return findFeedEntities(false, maxResults, firstResult);
    }

    private List<Feed> findFeedEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Feed.class));
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

    public Feed findFeed(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Feed.class, id);
        } finally {
            em.close();
        }
    }

    public int getFeedCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Feed> rt = cq.from(Feed.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
