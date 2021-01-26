/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artemispocket.services;

import com.artemispocket.entities.Rangos;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.artemispocket.entities.Usuarios;
import com.artemispocket.services.exceptions.IllegalOrphanException;
import com.artemispocket.services.exceptions.NonexistentEntityException;
import com.artemispocket.services.exceptions.RollbackFailureException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author roca12
 */
public class RangosJpaController implements Serializable {

    public RangosJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Rangos rangos) throws RollbackFailureException, Exception {
        if (rangos.getUsuariosList() == null) {
            rangos.setUsuariosList(new ArrayList<Usuarios>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Usuarios> attachedUsuariosList = new ArrayList<Usuarios>();
            for (Usuarios usuariosListUsuariosToAttach : rangos.getUsuariosList()) {
                usuariosListUsuariosToAttach = em.getReference(usuariosListUsuariosToAttach.getClass(), usuariosListUsuariosToAttach.getId());
                attachedUsuariosList.add(usuariosListUsuariosToAttach);
            }
            rangos.setUsuariosList(attachedUsuariosList);
            em.persist(rangos);
            for (Usuarios usuariosListUsuarios : rangos.getUsuariosList()) {
                Rangos oldRangoOfUsuariosListUsuarios = usuariosListUsuarios.getRango();
                usuariosListUsuarios.setRango(rangos);
                usuariosListUsuarios = em.merge(usuariosListUsuarios);
                if (oldRangoOfUsuariosListUsuarios != null) {
                    oldRangoOfUsuariosListUsuarios.getUsuariosList().remove(usuariosListUsuarios);
                    oldRangoOfUsuariosListUsuarios = em.merge(oldRangoOfUsuariosListUsuarios);
                }
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

    public void edit(Rangos rangos) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Rangos persistentRangos = em.find(Rangos.class, rangos.getId());
            List<Usuarios> usuariosListOld = persistentRangos.getUsuariosList();
            List<Usuarios> usuariosListNew = rangos.getUsuariosList();
            List<String> illegalOrphanMessages = null;
            for (Usuarios usuariosListOldUsuarios : usuariosListOld) {
                if (!usuariosListNew.contains(usuariosListOldUsuarios)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Usuarios " + usuariosListOldUsuarios + " since its rango field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Usuarios> attachedUsuariosListNew = new ArrayList<Usuarios>();
            for (Usuarios usuariosListNewUsuariosToAttach : usuariosListNew) {
                usuariosListNewUsuariosToAttach = em.getReference(usuariosListNewUsuariosToAttach.getClass(), usuariosListNewUsuariosToAttach.getId());
                attachedUsuariosListNew.add(usuariosListNewUsuariosToAttach);
            }
            usuariosListNew = attachedUsuariosListNew;
            rangos.setUsuariosList(usuariosListNew);
            rangos = em.merge(rangos);
            for (Usuarios usuariosListNewUsuarios : usuariosListNew) {
                if (!usuariosListOld.contains(usuariosListNewUsuarios)) {
                    Rangos oldRangoOfUsuariosListNewUsuarios = usuariosListNewUsuarios.getRango();
                    usuariosListNewUsuarios.setRango(rangos);
                    usuariosListNewUsuarios = em.merge(usuariosListNewUsuarios);
                    if (oldRangoOfUsuariosListNewUsuarios != null && !oldRangoOfUsuariosListNewUsuarios.equals(rangos)) {
                        oldRangoOfUsuariosListNewUsuarios.getUsuariosList().remove(usuariosListNewUsuarios);
                        oldRangoOfUsuariosListNewUsuarios = em.merge(oldRangoOfUsuariosListNewUsuarios);
                    }
                }
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
                Integer id = rangos.getId();
                if (findRangos(id) == null) {
                    throw new NonexistentEntityException("The rangos with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Rangos rangos;
            try {
                rangos = em.getReference(Rangos.class, id);
                rangos.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rangos with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Usuarios> usuariosListOrphanCheck = rangos.getUsuariosList();
            for (Usuarios usuariosListOrphanCheckUsuarios : usuariosListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Rangos (" + rangos + ") cannot be destroyed since the Usuarios " + usuariosListOrphanCheckUsuarios + " in its usuariosList field has a non-nullable rango field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(rangos);
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

    public List<Rangos> findRangosEntities() {
        return findRangosEntities(true, -1, -1);
    }

    public List<Rangos> findRangosEntities(int maxResults, int firstResult) {
        return findRangosEntities(false, maxResults, firstResult);
    }

    private List<Rangos> findRangosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Rangos.class));
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

    public Rangos findRangos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Rangos.class, id);
        } finally {
            em.close();
        }
    }

    public int getRangosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Rangos> rt = cq.from(Rangos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
