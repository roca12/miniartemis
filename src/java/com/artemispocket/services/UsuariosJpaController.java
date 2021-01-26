/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artemispocket.services;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.artemispocket.entities.Ciudades;
import com.artemispocket.entities.Paises;
import com.artemispocket.entities.Rangos;
import com.artemispocket.entities.Universidades;
import com.artemispocket.entities.AuditoriaAcceso;
import java.util.ArrayList;
import java.util.List;
import com.artemispocket.entities.Feed;
import com.artemispocket.entities.Comentarios;
import com.artemispocket.entities.Enviosuva;
import com.artemispocket.entities.Usuarios;
import com.artemispocket.services.exceptions.NonexistentEntityException;
import com.artemispocket.services.exceptions.RollbackFailureException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author roca12
 */
public class UsuariosJpaController implements Serializable {

    public UsuariosJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuarios usuarios) throws RollbackFailureException, Exception {
        if (usuarios.getAuditoriaAccesoList() == null) {
            usuarios.setAuditoriaAccesoList(new ArrayList<AuditoriaAcceso>());
        }
        if (usuarios.getFeedList() == null) {
            usuarios.setFeedList(new ArrayList<Feed>());
        }
        if (usuarios.getComentariosList() == null) {
            usuarios.setComentariosList(new ArrayList<Comentarios>());
        }
        if (usuarios.getEnviosuvaList() == null) {
            usuarios.setEnviosuvaList(new ArrayList<Enviosuva>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ciudades ciudad = usuarios.getCiudad();
            if (ciudad != null) {
                ciudad = em.getReference(ciudad.getClass(), ciudad.getIdCiudades());
                usuarios.setCiudad(ciudad);
            }
            Paises pais = usuarios.getPais();
            if (pais != null) {
                pais = em.getReference(pais.getClass(), pais.getCodigo());
                usuarios.setPais(pais);
            }
            Rangos rango = usuarios.getRango();
            if (rango != null) {
                rango = em.getReference(rango.getClass(), rango.getId());
                usuarios.setRango(rango);
            }
            Universidades universidad = usuarios.getUniversidad();
            if (universidad != null) {
                universidad = em.getReference(universidad.getClass(), universidad.getId());
                usuarios.setUniversidad(universidad);
            }
            List<AuditoriaAcceso> attachedAuditoriaAccesoList = new ArrayList<AuditoriaAcceso>();
            for (AuditoriaAcceso auditoriaAccesoListAuditoriaAccesoToAttach : usuarios.getAuditoriaAccesoList()) {
                auditoriaAccesoListAuditoriaAccesoToAttach = em.getReference(auditoriaAccesoListAuditoriaAccesoToAttach.getClass(), auditoriaAccesoListAuditoriaAccesoToAttach.getId());
                attachedAuditoriaAccesoList.add(auditoriaAccesoListAuditoriaAccesoToAttach);
            }
            usuarios.setAuditoriaAccesoList(attachedAuditoriaAccesoList);
            List<Feed> attachedFeedList = new ArrayList<Feed>();
            for (Feed feedListFeedToAttach : usuarios.getFeedList()) {
                feedListFeedToAttach = em.getReference(feedListFeedToAttach.getClass(), feedListFeedToAttach.getId());
                attachedFeedList.add(feedListFeedToAttach);
            }
            usuarios.setFeedList(attachedFeedList);
            List<Comentarios> attachedComentariosList = new ArrayList<Comentarios>();
            for (Comentarios comentariosListComentariosToAttach : usuarios.getComentariosList()) {
                comentariosListComentariosToAttach = em.getReference(comentariosListComentariosToAttach.getClass(), comentariosListComentariosToAttach.getId());
                attachedComentariosList.add(comentariosListComentariosToAttach);
            }
            usuarios.setComentariosList(attachedComentariosList);
            List<Enviosuva> attachedEnviosuvaList = new ArrayList<Enviosuva>();
            for (Enviosuva enviosuvaListEnviosuvaToAttach : usuarios.getEnviosuvaList()) {
                enviosuvaListEnviosuvaToAttach = em.getReference(enviosuvaListEnviosuvaToAttach.getClass(), enviosuvaListEnviosuvaToAttach.getId());
                attachedEnviosuvaList.add(enviosuvaListEnviosuvaToAttach);
            }
            usuarios.setEnviosuvaList(attachedEnviosuvaList);
            em.persist(usuarios);
            if (ciudad != null) {
                ciudad.getUsuariosList().add(usuarios);
                ciudad = em.merge(ciudad);
            }
            if (pais != null) {
                pais.getUsuariosList().add(usuarios);
                pais = em.merge(pais);
            }
            if (rango != null) {
                rango.getUsuariosList().add(usuarios);
                rango = em.merge(rango);
            }
            if (universidad != null) {
                universidad.getUsuariosList().add(usuarios);
                universidad = em.merge(universidad);
            }
            for (AuditoriaAcceso auditoriaAccesoListAuditoriaAcceso : usuarios.getAuditoriaAccesoList()) {
                Usuarios oldUsuarioOfAuditoriaAccesoListAuditoriaAcceso = auditoriaAccesoListAuditoriaAcceso.getUsuario();
                auditoriaAccesoListAuditoriaAcceso.setUsuario(usuarios);
                auditoriaAccesoListAuditoriaAcceso = em.merge(auditoriaAccesoListAuditoriaAcceso);
                if (oldUsuarioOfAuditoriaAccesoListAuditoriaAcceso != null) {
                    oldUsuarioOfAuditoriaAccesoListAuditoriaAcceso.getAuditoriaAccesoList().remove(auditoriaAccesoListAuditoriaAcceso);
                    oldUsuarioOfAuditoriaAccesoListAuditoriaAcceso = em.merge(oldUsuarioOfAuditoriaAccesoListAuditoriaAcceso);
                }
            }
            for (Feed feedListFeed : usuarios.getFeedList()) {
                Usuarios oldUsuariocreadorOfFeedListFeed = feedListFeed.getUsuariocreador();
                feedListFeed.setUsuariocreador(usuarios);
                feedListFeed = em.merge(feedListFeed);
                if (oldUsuariocreadorOfFeedListFeed != null) {
                    oldUsuariocreadorOfFeedListFeed.getFeedList().remove(feedListFeed);
                    oldUsuariocreadorOfFeedListFeed = em.merge(oldUsuariocreadorOfFeedListFeed);
                }
            }
            for (Comentarios comentariosListComentarios : usuarios.getComentariosList()) {
                Usuarios oldUsuarioOfComentariosListComentarios = comentariosListComentarios.getUsuario();
                comentariosListComentarios.setUsuario(usuarios);
                comentariosListComentarios = em.merge(comentariosListComentarios);
                if (oldUsuarioOfComentariosListComentarios != null) {
                    oldUsuarioOfComentariosListComentarios.getComentariosList().remove(comentariosListComentarios);
                    oldUsuarioOfComentariosListComentarios = em.merge(oldUsuarioOfComentariosListComentarios);
                }
            }
            for (Enviosuva enviosuvaListEnviosuva : usuarios.getEnviosuvaList()) {
                Usuarios oldUseridOfEnviosuvaListEnviosuva = enviosuvaListEnviosuva.getUserid();
                enviosuvaListEnviosuva.setUserid(usuarios);
                enviosuvaListEnviosuva = em.merge(enviosuvaListEnviosuva);
                if (oldUseridOfEnviosuvaListEnviosuva != null) {
                    oldUseridOfEnviosuvaListEnviosuva.getEnviosuvaList().remove(enviosuvaListEnviosuva);
                    oldUseridOfEnviosuvaListEnviosuva = em.merge(oldUseridOfEnviosuvaListEnviosuva);
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

    public void edit(Usuarios usuarios) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuarios persistentUsuarios = em.find(Usuarios.class, usuarios.getId());
            Ciudades ciudadOld = persistentUsuarios.getCiudad();
            Ciudades ciudadNew = usuarios.getCiudad();
            Paises paisOld = persistentUsuarios.getPais();
            Paises paisNew = usuarios.getPais();
            Rangos rangoOld = persistentUsuarios.getRango();
            Rangos rangoNew = usuarios.getRango();
            Universidades universidadOld = persistentUsuarios.getUniversidad();
            Universidades universidadNew = usuarios.getUniversidad();
            List<AuditoriaAcceso> auditoriaAccesoListOld = persistentUsuarios.getAuditoriaAccesoList();
            List<AuditoriaAcceso> auditoriaAccesoListNew = usuarios.getAuditoriaAccesoList();
            List<Feed> feedListOld = persistentUsuarios.getFeedList();
            List<Feed> feedListNew = usuarios.getFeedList();
            List<Comentarios> comentariosListOld = persistentUsuarios.getComentariosList();
            List<Comentarios> comentariosListNew = usuarios.getComentariosList();
            List<Enviosuva> enviosuvaListOld = persistentUsuarios.getEnviosuvaList();
            List<Enviosuva> enviosuvaListNew = usuarios.getEnviosuvaList();
            if (ciudadNew != null) {
                ciudadNew = em.getReference(ciudadNew.getClass(), ciudadNew.getIdCiudades());
                usuarios.setCiudad(ciudadNew);
            }
            if (paisNew != null) {
                paisNew = em.getReference(paisNew.getClass(), paisNew.getCodigo());
                usuarios.setPais(paisNew);
            }
            if (rangoNew != null) {
                rangoNew = em.getReference(rangoNew.getClass(), rangoNew.getId());
                usuarios.setRango(rangoNew);
            }
            if (universidadNew != null) {
                universidadNew = em.getReference(universidadNew.getClass(), universidadNew.getId());
                usuarios.setUniversidad(universidadNew);
            }
            List<AuditoriaAcceso> attachedAuditoriaAccesoListNew = new ArrayList<AuditoriaAcceso>();
            for (AuditoriaAcceso auditoriaAccesoListNewAuditoriaAccesoToAttach : auditoriaAccesoListNew) {
                auditoriaAccesoListNewAuditoriaAccesoToAttach = em.getReference(auditoriaAccesoListNewAuditoriaAccesoToAttach.getClass(), auditoriaAccesoListNewAuditoriaAccesoToAttach.getId());
                attachedAuditoriaAccesoListNew.add(auditoriaAccesoListNewAuditoriaAccesoToAttach);
            }
            auditoriaAccesoListNew = attachedAuditoriaAccesoListNew;
            usuarios.setAuditoriaAccesoList(auditoriaAccesoListNew);
            List<Feed> attachedFeedListNew = new ArrayList<Feed>();
            for (Feed feedListNewFeedToAttach : feedListNew) {
                feedListNewFeedToAttach = em.getReference(feedListNewFeedToAttach.getClass(), feedListNewFeedToAttach.getId());
                attachedFeedListNew.add(feedListNewFeedToAttach);
            }
            feedListNew = attachedFeedListNew;
            usuarios.setFeedList(feedListNew);
            List<Comentarios> attachedComentariosListNew = new ArrayList<Comentarios>();
            for (Comentarios comentariosListNewComentariosToAttach : comentariosListNew) {
                comentariosListNewComentariosToAttach = em.getReference(comentariosListNewComentariosToAttach.getClass(), comentariosListNewComentariosToAttach.getId());
                attachedComentariosListNew.add(comentariosListNewComentariosToAttach);
            }
            comentariosListNew = attachedComentariosListNew;
            usuarios.setComentariosList(comentariosListNew);
            List<Enviosuva> attachedEnviosuvaListNew = new ArrayList<Enviosuva>();
            for (Enviosuva enviosuvaListNewEnviosuvaToAttach : enviosuvaListNew) {
                enviosuvaListNewEnviosuvaToAttach = em.getReference(enviosuvaListNewEnviosuvaToAttach.getClass(), enviosuvaListNewEnviosuvaToAttach.getId());
                attachedEnviosuvaListNew.add(enviosuvaListNewEnviosuvaToAttach);
            }
            enviosuvaListNew = attachedEnviosuvaListNew;
            usuarios.setEnviosuvaList(enviosuvaListNew);
            usuarios = em.merge(usuarios);
            if (ciudadOld != null && !ciudadOld.equals(ciudadNew)) {
                ciudadOld.getUsuariosList().remove(usuarios);
                ciudadOld = em.merge(ciudadOld);
            }
            if (ciudadNew != null && !ciudadNew.equals(ciudadOld)) {
                ciudadNew.getUsuariosList().add(usuarios);
                ciudadNew = em.merge(ciudadNew);
            }
            if (paisOld != null && !paisOld.equals(paisNew)) {
                paisOld.getUsuariosList().remove(usuarios);
                paisOld = em.merge(paisOld);
            }
            if (paisNew != null && !paisNew.equals(paisOld)) {
                paisNew.getUsuariosList().add(usuarios);
                paisNew = em.merge(paisNew);
            }
            if (rangoOld != null && !rangoOld.equals(rangoNew)) {
                rangoOld.getUsuariosList().remove(usuarios);
                rangoOld = em.merge(rangoOld);
            }
            if (rangoNew != null && !rangoNew.equals(rangoOld)) {
                rangoNew.getUsuariosList().add(usuarios);
                rangoNew = em.merge(rangoNew);
            }
            if (universidadOld != null && !universidadOld.equals(universidadNew)) {
                universidadOld.getUsuariosList().remove(usuarios);
                universidadOld = em.merge(universidadOld);
            }
            if (universidadNew != null && !universidadNew.equals(universidadOld)) {
                universidadNew.getUsuariosList().add(usuarios);
                universidadNew = em.merge(universidadNew);
            }
            for (AuditoriaAcceso auditoriaAccesoListOldAuditoriaAcceso : auditoriaAccesoListOld) {
                if (!auditoriaAccesoListNew.contains(auditoriaAccesoListOldAuditoriaAcceso)) {
                    auditoriaAccesoListOldAuditoriaAcceso.setUsuario(null);
                    auditoriaAccesoListOldAuditoriaAcceso = em.merge(auditoriaAccesoListOldAuditoriaAcceso);
                }
            }
            for (AuditoriaAcceso auditoriaAccesoListNewAuditoriaAcceso : auditoriaAccesoListNew) {
                if (!auditoriaAccesoListOld.contains(auditoriaAccesoListNewAuditoriaAcceso)) {
                    Usuarios oldUsuarioOfAuditoriaAccesoListNewAuditoriaAcceso = auditoriaAccesoListNewAuditoriaAcceso.getUsuario();
                    auditoriaAccesoListNewAuditoriaAcceso.setUsuario(usuarios);
                    auditoriaAccesoListNewAuditoriaAcceso = em.merge(auditoriaAccesoListNewAuditoriaAcceso);
                    if (oldUsuarioOfAuditoriaAccesoListNewAuditoriaAcceso != null && !oldUsuarioOfAuditoriaAccesoListNewAuditoriaAcceso.equals(usuarios)) {
                        oldUsuarioOfAuditoriaAccesoListNewAuditoriaAcceso.getAuditoriaAccesoList().remove(auditoriaAccesoListNewAuditoriaAcceso);
                        oldUsuarioOfAuditoriaAccesoListNewAuditoriaAcceso = em.merge(oldUsuarioOfAuditoriaAccesoListNewAuditoriaAcceso);
                    }
                }
            }
            for (Feed feedListOldFeed : feedListOld) {
                if (!feedListNew.contains(feedListOldFeed)) {
                    feedListOldFeed.setUsuariocreador(null);
                    feedListOldFeed = em.merge(feedListOldFeed);
                }
            }
            for (Feed feedListNewFeed : feedListNew) {
                if (!feedListOld.contains(feedListNewFeed)) {
                    Usuarios oldUsuariocreadorOfFeedListNewFeed = feedListNewFeed.getUsuariocreador();
                    feedListNewFeed.setUsuariocreador(usuarios);
                    feedListNewFeed = em.merge(feedListNewFeed);
                    if (oldUsuariocreadorOfFeedListNewFeed != null && !oldUsuariocreadorOfFeedListNewFeed.equals(usuarios)) {
                        oldUsuariocreadorOfFeedListNewFeed.getFeedList().remove(feedListNewFeed);
                        oldUsuariocreadorOfFeedListNewFeed = em.merge(oldUsuariocreadorOfFeedListNewFeed);
                    }
                }
            }
            for (Comentarios comentariosListOldComentarios : comentariosListOld) {
                if (!comentariosListNew.contains(comentariosListOldComentarios)) {
                    comentariosListOldComentarios.setUsuario(null);
                    comentariosListOldComentarios = em.merge(comentariosListOldComentarios);
                }
            }
            for (Comentarios comentariosListNewComentarios : comentariosListNew) {
                if (!comentariosListOld.contains(comentariosListNewComentarios)) {
                    Usuarios oldUsuarioOfComentariosListNewComentarios = comentariosListNewComentarios.getUsuario();
                    comentariosListNewComentarios.setUsuario(usuarios);
                    comentariosListNewComentarios = em.merge(comentariosListNewComentarios);
                    if (oldUsuarioOfComentariosListNewComentarios != null && !oldUsuarioOfComentariosListNewComentarios.equals(usuarios)) {
                        oldUsuarioOfComentariosListNewComentarios.getComentariosList().remove(comentariosListNewComentarios);
                        oldUsuarioOfComentariosListNewComentarios = em.merge(oldUsuarioOfComentariosListNewComentarios);
                    }
                }
            }
            for (Enviosuva enviosuvaListOldEnviosuva : enviosuvaListOld) {
                if (!enviosuvaListNew.contains(enviosuvaListOldEnviosuva)) {
                    enviosuvaListOldEnviosuva.setUserid(null);
                    enviosuvaListOldEnviosuva = em.merge(enviosuvaListOldEnviosuva);
                }
            }
            for (Enviosuva enviosuvaListNewEnviosuva : enviosuvaListNew) {
                if (!enviosuvaListOld.contains(enviosuvaListNewEnviosuva)) {
                    Usuarios oldUseridOfEnviosuvaListNewEnviosuva = enviosuvaListNewEnviosuva.getUserid();
                    enviosuvaListNewEnviosuva.setUserid(usuarios);
                    enviosuvaListNewEnviosuva = em.merge(enviosuvaListNewEnviosuva);
                    if (oldUseridOfEnviosuvaListNewEnviosuva != null && !oldUseridOfEnviosuvaListNewEnviosuva.equals(usuarios)) {
                        oldUseridOfEnviosuvaListNewEnviosuva.getEnviosuvaList().remove(enviosuvaListNewEnviosuva);
                        oldUseridOfEnviosuvaListNewEnviosuva = em.merge(oldUseridOfEnviosuvaListNewEnviosuva);
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
                Integer id = usuarios.getId();
                if (findUsuarios(id) == null) {
                    throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.");
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
            Usuarios usuarios;
            try {
                usuarios = em.getReference(Usuarios.class, id);
                usuarios.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.", enfe);
            }
            Ciudades ciudad = usuarios.getCiudad();
            if (ciudad != null) {
                ciudad.getUsuariosList().remove(usuarios);
                ciudad = em.merge(ciudad);
            }
            Paises pais = usuarios.getPais();
            if (pais != null) {
                pais.getUsuariosList().remove(usuarios);
                pais = em.merge(pais);
            }
            Rangos rango = usuarios.getRango();
            if (rango != null) {
                rango.getUsuariosList().remove(usuarios);
                rango = em.merge(rango);
            }
            Universidades universidad = usuarios.getUniversidad();
            if (universidad != null) {
                universidad.getUsuariosList().remove(usuarios);
                universidad = em.merge(universidad);
            }
            List<AuditoriaAcceso> auditoriaAccesoList = usuarios.getAuditoriaAccesoList();
            for (AuditoriaAcceso auditoriaAccesoListAuditoriaAcceso : auditoriaAccesoList) {
                auditoriaAccesoListAuditoriaAcceso.setUsuario(null);
                auditoriaAccesoListAuditoriaAcceso = em.merge(auditoriaAccesoListAuditoriaAcceso);
            }
            List<Feed> feedList = usuarios.getFeedList();
            for (Feed feedListFeed : feedList) {
                feedListFeed.setUsuariocreador(null);
                feedListFeed = em.merge(feedListFeed);
            }
            List<Comentarios> comentariosList = usuarios.getComentariosList();
            for (Comentarios comentariosListComentarios : comentariosList) {
                comentariosListComentarios.setUsuario(null);
                comentariosListComentarios = em.merge(comentariosListComentarios);
            }
            List<Enviosuva> enviosuvaList = usuarios.getEnviosuvaList();
            for (Enviosuva enviosuvaListEnviosuva : enviosuvaList) {
                enviosuvaListEnviosuva.setUserid(null);
                enviosuvaListEnviosuva = em.merge(enviosuvaListEnviosuva);
            }
            em.remove(usuarios);
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

    public List<Usuarios> findUsuariosEntities() {
        return findUsuariosEntities(true, -1, -1);
    }

    public List<Usuarios> findUsuariosEntities(int maxResults, int firstResult) {
        return findUsuariosEntities(false, maxResults, firstResult);
    }

    private List<Usuarios> findUsuariosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuarios.class));
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

    public Usuarios findUsuarios(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuarios.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuariosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuarios> rt = cq.from(Usuarios.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
