package com.uemf.yalah.dao;

import com.uemf.yalah.model.Vehicle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO pour les opérations sur les véhicules
 */
public class VehicleDAO extends GenericDAO<Vehicle, Long> {

    public VehicleDAO() {
        super(Vehicle.class);
    }

    /**
     * Trouve tous les véhicules d'un propriétaire
     */
    public List<Vehicle> findByOwner(Long ownerId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Vehicle> query = em.createQuery(
                    "SELECT v FROM Vehicle v WHERE v.owner.id = :ownerId AND v.active = true",
                    Vehicle.class);
            query.setParameter("ownerId", ownerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve un véhicule par ID avec son propriétaire chargé
     */
    public java.util.Optional<Vehicle> findByIdWithOwner(Long vehicleId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Vehicle> query = em.createQuery(
                    "SELECT v FROM Vehicle v JOIN FETCH v.owner WHERE v.id = :vehicleId",
                    Vehicle.class);
            query.setParameter("vehicleId", vehicleId);
            return query.getResultList().stream().findFirst();
        } finally {
            em.close();
        }
    }
}
