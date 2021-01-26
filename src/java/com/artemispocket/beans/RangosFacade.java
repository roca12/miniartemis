/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artemispocket.beans;

import com.artemispocket.entities.Rangos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author roca12
 */
@Stateless
public class RangosFacade extends AbstractFacade<Rangos> {

    @PersistenceContext(unitName = "templatePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RangosFacade() {
        super(Rangos.class);
    }
    
}
