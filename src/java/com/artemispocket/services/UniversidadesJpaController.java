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
import com.artemispocket.entities.Usuarios;
import java.util.ArrayList;
import java.util.List;
import com.artemispocket.entities.CertInscripcion;
import com.artemispocket.entities.Universidades;
import com.artemispocket.services.exceptions.NonexistentEntityException;
import com.artemispocket.services.exceptions.RollbackFailureException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author roca12
 */
public class UniversidadesJpaController implements Serializable {

    public UniversidadesJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Universidades universidades) throws RollbackFailureException, Exception {
        if (universidades.getUsuariosList() == null) {
            universidades.setUsuariosList(new ArrayList<Usuarios>());
        }
        if (universidades.getCertInscripcionList() == null) {
            universidades.setCertInscripcionList(new ArrayList<CertInscripcion>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ciudades ciudad = universidades.getCiudad();
            if (ciudad != null) {
                ciudad = em.getReference(ciudad.getClass(), ciudad.getIdCiudades());
                universidades.setCiudad(ciudad);
            }
            Paises pais = universidades.getPais();
            if (pais != null) {
                pais = em.getReference(pais.getClass(), pais.getCodigo());
                universidades.setPais(pais);
            }
            List<Usuarios> attachedUsuariosList = new ArrayList<Usuarios>();
            for (Usuarios usuariosListUsuariosToAttach : universidades.getUsuariosList()) {
                usuariosListUsuariosToAttach = em.getReference(usuariosListUsuariosToAttach.getClass(), usuariosListUsuariosToAttach.getId());
                attachedUsuariosList.add(usuariosListUsuariosToAttach);
            }
            universidades.setUsuariosList(attachedUsuariosList);
            List<CertInscripcion> attachedCertInscripcionList = new ArrayList<CertInscripcion>();
            for (CertInscripcion certInscripcionListCertInscripcionToAttach : universidades.getCertInscripcionList()) {
                certInscripcionListCertInscripcionToAttach = em.getReference(certInscripcionListCertInscripcionToAttach.getClass(), certInscripcionListCertInscripcionToAttach.getId());
                attachedCertInscripcionList.add(certInscripcionListCertInscripcionToAttach);
            }
            universidades.setCertInscripcionList(attachedCertInscripcionList);
            em.persist(universidades);
            if (ciudad != null) {
                ciudad.getUniversidadesList().add(universidades);
                ciudad = em.merge(ciudad);
            }
            if (pais != null) {
                pais.getUniversidadesList().add(universidades);
                pais = em.merge(pais);
            }
            for (Usuarios usuariosListUsuarios : universidades.getUsuariosList()) {
                Universidades oldUniversidadOfUsuariosListUsuarios = usuariosListUsuarios.getUniversidad();
                usuariosListUsuarios.setUniversidad(universidades);
                usuariosListUsuarios = em.merge(usuariosListUsuarios);
                if (oldUniversidadOfUsuariosListUsuarios != null) {
                    oldUniversidadOfUsuariosListUsuarios.getUsuariosList().remove(usuariosListUsuarios);
                    oldUniversidadOfUsuariosListUsuarios = em.merge(oldUniversidadOfUsuariosListUsuarios);
                }
            }
            for (CertInscripcion certInscripcionListCertInscripcion : universidades.getCertInscripcionList()) {
                Universidades oldInstitucionOfCertInscripcionListCertInscripcion = certInscripcionListCertInscripcion.getInstitucion();
                certInscripcionListCertInscripcion.setInstitucion(universidades);
                certInscripcionListCertInscripcion = em.merge(certInscripcionListCertInscripcion);
                if (oldInstitucionOfCertInscripcionListCertInscripcion != null) {
                    oldInstitucionOfCertInscripcionListCertInscripcion.getCertInscripcionList().remove(certInscripcionListCertInscripcion);
                    oldInstitucionOfCertInscripcionListCertInscripcion = em.merge(oldInstitucionOfCertInscripcionListCertInscripcion);
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

    public void edit(Universidades universidades) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Universidades persistentUniversidades = em.find(Universidades.class, universidades.getId());
            Ciudades ciudadOld = persistentUniversidades.getCiudad();
            Ciudades ciudadNew = universidades.getCiudad();
            Paises paisOld = persistentUniversidades.getPais();
            Paises paisNew = universidades.getPais();
            List<Usuarios> usuariosListOld = persistentUniversidades.getUsuariosList();
            List<Usuarios> usuariosListNew = universidades.getUsuariosList();
            List<CertInscripcion> certInscripcionListOld = persistentUniversidades.getCertInscripcionList();
            List<CertInscripcion> certInscripcionListNew = universidades.getCertInscripcionList();
            if (ciudadNew != null) {
                ciudadNew = em.getReference(ciudadNew.getClass(), ciudadNew.getIdCiudades());
                universidades.setCiudad(ciudadNew);
            }
            if (paisNew != null) {
                paisNew = em.getReference(paisNew.getClass(), paisNew.getCodigo());
                universidades.setPais(paisNew);
            }
            List<Usuarios> attachedUsuariosListNew = new ArrayList<Usuarios>();
            for (Usuarios usuariosListNewUsuariosToAttach : usuariosListNew) {
                usuariosListNewUsuariosToAttach = em.getReference(usuariosListNewUsuariosToAttach.getClass(), usuariosListNewUsuariosToAttach.getId());
                attachedUsuariosListNew.add(usuariosListNewUsuariosToAttach);
            }
            usuariosListNew = attachedUsuariosListNew;
            universidades.setUsuariosList(usuariosListNew);
            List<CertInscripcion> attachedCertInscripcionListNew = new ArrayList<CertInscripcion>();
            for (CertInscripcion certInscripcionListNewCertInscripcionToAttach : certInscripcionListNew) {
                certInscripcionListNewCertInscripcionToAttach = em.getReference(certInscripcionListNewCertInscripcionToAttach.getClass(), certInscripcionListNewCertInscripcionToAttach.getId());
                attachedCertInscripcionListNew.add(certInscripcionListNewCertInscripcionToAttach);
            }
            certInscripcionListNew = attachedCertInscripcionListNew;
            universidades.setCertInscripcionList(certInscripcionListNew);
            universidades = em.merge(universidades);
            if (ciudadOld != null && !ciudadOld.equals(ciudadNew)) {
                ciudadOld.getUniversidadesList().remove(universidades);
                ciudadOld = em.merge(ciudadOld);
            }
            if (ciudadNew != null && !ciudadNew.equals(ciudadOld)) {
                ciudadNew.getUniversidadesList().add(universidades);
                ciudadNew = em.merge(ciudadNew);
            }
            if (paisOld != null && !paisOld.equals(paisNew)) {
                paisOld.getUniversidadesList().remove(universidades);
                paisOld = em.merge(paisOld);
            }
            if (paisNew != null && !paisNew.equals(paisOld)) {
                paisNew.getUniversidadesList().add(universidades);
                paisNew = em.merge(paisNew);
            }
            for (Usuarios usuariosListOldUsuarios : usuariosListOld) {
                if (!usuariosListNew.contains(usuariosListOldUsuarios)) {
                    usuariosListOldUsuarios.setUniversidad(null);
                    usuariosListOldUsuarios = em.merge(usuariosListOldUsuarios);
                }
            }
            for (Usuarios usuariosListNewUsuarios : usuariosListNew) {
                if (!usuariosListOld.contains(usuariosListNewUsuarios)) {
                    Universidades oldUniversidadOfUsuariosListNewUsuarios = usuariosListNewUsuarios.getUniversidad();
                    usuariosListNewUsuarios.setUniversidad(universidades);
                    usuariosListNewUsuarios = em.merge(usuariosListNewUsuarios);
                    if (oldUniversidadOfUsuariosListNewUsuarios != null && !oldUniversidadOfUsuariosListNewUsuarios.equals(universidades)) {
                        oldUniversidadOfUsuariosListNewUsuarios.getUsuariosList().remove(usuariosListNewUsuarios);
                        oldUniversidadOfUsuariosListNewUsuarios = em.merge(oldUniversidadOfUsuariosListNewUsuarios);
                    }
                }
            }
            for (CertInscripcion certInscripcionListOldCertInscripcion : certInscripcionListOld) {
                if (!certInscripcionListNew.contains(certInscripcionListOldCertInscripcion)) {
                    certInscripcionListOldCertInscripcion.setInstitucion(null);
                    certInscripcionListOldCertInscripcion = em.merge(certInscripcionListOldCertInscripcion);
                }
            }
            for (CertInscripcion certInscripcionListNewCertInscripcion : certInscripcionListNew) {
                if (!certInscripcionListOld.contains(certInscripcionListNewCertInscripcion)) {
                    Universidades oldInstitucionOfCertInscripcionListNewCertInscripcion = certInscripcionListNewCertInscripcion.getInstitucion();
                    certInscripcionListNewCertInscripcion.setInstitucion(universidades);
                    certInscripcionListNewCertInscripcion = em.merge(certInscripcionListNewCertInscripcion);
                    if (oldInstitucionOfCertInscripcionListNewCertInscripcion != null && !oldInstitucionOfCertInscripcionListNewCertInscripcion.equals(universidades)) {
                        oldInstitucionOfCertInscripcionListNewCertInscripcion.getCertInscripcionList().remove(certInscripcionListNewCertInscripcion);
                        oldInstitucionOfCertInscripcionListNewCertInscripcion = em.merge(oldInstitucionOfCertInscripcionListNewCertInscripcion);
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
                Integer id = universidades.getId();
                if (findUniversidades(id) == null) {
                    throw new NonexistentEntityException("The universidades with id " + id + " no longer exists.");
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
            Universidades universidades;
            try {
                universidades = em.getReference(Universidades.class, id);
                universidades.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The universidades with id " + id + " no longer exists.", enfe);
            }
            Ciudades ciudad = universidades.getCiudad();
            if (ciudad != null) {
                ciudad.getUniversidadesList().remove(universidades);
                ciudad = em.merge(ciudad);
            }
            Paises pais = universidades.getPais();
            if (pais != null) {
                pais.getUniversidadesList().remove(universidades);
                pais = em.merge(pais);
            }
            List<Usuarios> usuariosList = universidades.getUsuariosList();
            for (Usuarios usuariosListUsuarios : usuariosList) {
                usuariosListUsuarios.setUniversidad(null);
                usuariosListUsuarios = em.merge(usuariosListUsuarios);
            }
            List<CertInscripcion> certInscripcionList = universidades.getCertInscripcionList();
            for (CertInscripcion certInscripcionListCertInscripcion : certInscripcionList) {
                certInscripcionListCertInscripcion.setInstitucion(null);
                certInscripcionListCertInscripcion = em.merge(certInscripcionListCertInscripcion);
            }
            em.remove(universidades);
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

    public List<Universidades> findUniversidadesEntities() {
        return findUniversidadesEntities(true, -1, -1);
    }

    public List<Universidades> findUniversidadesEntities(int maxResults, int firstResult) {
        return findUniversidadesEntities(false, maxResults, firstResult);
    }

    private List<Universidades> findUniversidadesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Universidades.class));
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

    public Universidades findUniversidades(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Universidades.class, id);
        } finally {
            em.close();
        }
    }

    public int getUniversidadesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Universidades> rt = cq.from(Universidades.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
