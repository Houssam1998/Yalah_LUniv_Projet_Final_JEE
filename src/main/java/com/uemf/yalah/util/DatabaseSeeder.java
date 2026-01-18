package com.uemf.yalah.util;

import com.uemf.yalah.model.*;
import com.uemf.yalah.model.enums.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Classe utilitaire pour initialiser la base de données avec des données de
 * test.
 * Supporte l'ajout incrémental de nouvelles données (PostgreSQL).
 */
public class DatabaseSeeder {

    private static boolean seeded = false;

    /**
     * Vérifie si la base de données a déjà des utilisateurs
     */
    public static boolean isDatabaseEmpty() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
            return count == 0;
        } finally {
            em.close();
        }
    }

    /**
     * Compte le nombre de trajets existants
     */
    private static long countRides() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(r) FROM Ride r", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve un utilisateur par email
     */
    private static User findUserByEmail(EntityManager em, String email) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Trouve un véhicule d'un utilisateur
     */
    private static Vehicle findVehicleByOwner(EntityManager em, User owner) {
        try {
            return em.createQuery("SELECT v FROM Vehicle v WHERE v.owner = :owner", Vehicle.class)
                    .setParameter("owner", owner)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Initialise la base de données avec des données de test
     * Si la base existe déjà, ajoute uniquement les trajets manquants
     */
    public static void seedDatabase() {
        if (seeded) {
            return;
        }

        if (isDatabaseEmpty()) {
            seedInitialData();
        } else {
            // Base existante: ajouter les trajets supplémentaires si nécessaire
            seedAdditionalRides();
        }

        seeded = true;
    }

    /**
     * Ajoute les trajets supplémentaires si moins de 6 trajets existent
     */
    private static void seedAdditionalRides() {
        long rideCount = countRides();
        if (rideCount >= 6) {
            System.out.println("✅ Trajets déjà présents: " + rideCount);
            return;
        }

        System.out.println("📦 Ajout des trajets supplémentaires...");

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // Récupérer les utilisateurs existants
            User driver1 = findUserByEmail(em, "ahmed.benali@ueuromed.org");
            User professor = findUserByEmail(em, "prof.alami@ueuromed.org");

            if (driver1 == null || professor == null) {
                System.out.println("⚠️ Utilisateurs non trouvés, impossible d'ajouter les trajets");
                tx.rollback();
                return;
            }

            Vehicle vehicle1 = findVehicleByOwner(em, driver1);
            Vehicle vehicle2 = findVehicleByOwner(em, professor);

            if (vehicle1 == null || vehicle2 == null) {
                System.out.println("⚠️ Véhicules non trouvés, impossible d'ajouter les trajets");
                tx.rollback();
                return;
            }

            // Trajet 4: Depuis Hay Saada
            Ride ride4 = new Ride();
            ride4.setDriver(driver1);
            ride4.setVehicle(vehicle1);
            ride4.setDepartureLocation("Fès, Hay Saada");
            ride4.setDepartureLat(34.0450);
            ride4.setDepartureLng(-4.9900);
            ride4.setArrivalLocation("Campus UEMF");
            ride4.setArrivalLat(33.9633);
            ride4.setArrivalLng(-4.9972);
            ride4.setDepartureTime(LocalDateTime.now().plusDays(1).withHour(7).withMinute(45));
            ride4.setEstimatedArrivalTime(LocalDateTime.now().plusDays(1).withHour(8).withMinute(20));
            ride4.setTotalSeats(4);
            ride4.setAvailableSeats(4);
            ride4.setPricePerSeat(new BigDecimal("15.00"));
            ride4.setStatus(RideStatus.SCHEDULED);
            ride4.setInstantBooking(true);
            ride4.setAllowsBaggage(true);
            ride4.setDistanceKm(12.0);
            ride4.setDurationMinutes(35);
            ride4.setDescription("Trajet économique depuis Hay Saada");
            em.persist(ride4);

            // Trajet 5: Depuis Route Immouzer
            Ride ride5 = new Ride();
            ride5.setDriver(professor);
            ride5.setVehicle(vehicle2);
            ride5.setDepartureLocation("Fès, Route Immouzer");
            ride5.setDepartureLat(33.9950);
            ride5.setDepartureLng(-4.9500);
            ride5.setArrivalLocation("Campus UEMF, Faculté d'Ingénierie");
            ride5.setArrivalLat(33.9640);
            ride5.setArrivalLng(-4.9965);
            ride5.setDepartureTime(LocalDateTime.now().plusDays(1).withHour(8).withMinute(15));
            ride5.setEstimatedArrivalTime(LocalDateTime.now().plusDays(1).withHour(8).withMinute(45));
            ride5.setTotalSeats(4);
            ride5.setAvailableSeats(3);
            ride5.setPricePerSeat(new BigDecimal("18.00"));
            ride5.setStatus(RideStatus.SCHEDULED);
            ride5.setInstantBooking(false);
            ride5.setAllowsBaggage(true);
            ride5.setAllowsPets(false);
            ride5.setDistanceKm(10.0);
            ride5.setDurationMinutes(30);
            ride5.setDescription("Trajet calme, départ ponctuel");
            em.persist(ride5);

            // Trajet 6: Depuis Ain Chkef
            Ride ride6 = new Ride();
            ride6.setDriver(driver1);
            ride6.setVehicle(vehicle1);
            ride6.setDepartureLocation("Fès, Ain Chkef");
            ride6.setDepartureLat(33.9800);
            ride6.setDepartureLng(-5.0200);
            ride6.setArrivalLocation("Campus UEMF");
            ride6.setArrivalLat(33.9633);
            ride6.setArrivalLng(-4.9972);
            ride6.setDepartureTime(LocalDateTime.now().plusDays(3).withHour(9).withMinute(0));
            ride6.setEstimatedArrivalTime(LocalDateTime.now().plusDays(3).withHour(9).withMinute(25));
            ride6.setTotalSeats(4);
            ride6.setAvailableSeats(4);
            ride6.setPricePerSeat(new BigDecimal("12.00"));
            ride6.setStatus(RideStatus.SCHEDULED);
            ride6.setInstantBooking(true);
            ride6.setAllowsBaggage(true);
            ride6.setDistanceKm(8.0);
            ride6.setDurationMinutes(25);
            em.persist(ride6);

            // Trajet 7: Fès Ville Nouvelle
            Ride ride7 = new Ride();
            ride7.setDriver(driver1);
            ride7.setVehicle(vehicle1);
            ride7.setDepartureLocation("Fès Ville Nouvelle, Avenue Hassan II");
            ride7.setDepartureLat(34.0331);
            ride7.setDepartureLng(-5.0003);
            ride7.setArrivalLocation("Campus UEMF");
            ride7.setArrivalLat(33.9633);
            ride7.setArrivalLng(-4.9972);
            ride7.setDepartureTime(LocalDateTime.now().plusDays(2).withHour(8).withMinute(0));
            ride7.setEstimatedArrivalTime(LocalDateTime.now().plusDays(2).withHour(8).withMinute(30));
            ride7.setTotalSeats(4);
            ride7.setAvailableSeats(3);
            ride7.setPricePerSeat(new BigDecimal("20.00"));
            ride7.setStatus(RideStatus.SCHEDULED);
            ride7.setInstantBooking(true);
            ride7.setAllowsBaggage(true);
            ride7.setDistanceKm(15.0);
            ride7.setDurationMinutes(30);
            ride7.setDescription("Départ Avenue Hassan II, près du café Central");
            em.persist(ride7);

            // Trajet 8: Retour Campus -> Fès Centre
            Ride ride8 = new Ride();
            ride8.setDriver(professor);
            ride8.setVehicle(vehicle2);
            ride8.setDepartureLocation("Campus UEMF, Entrée principale");
            ride8.setDepartureLat(33.9633);
            ride8.setDepartureLng(-4.9972);
            ride8.setArrivalLocation("Fès Centre, Place Florence");
            ride8.setArrivalLat(34.0250);
            ride8.setArrivalLng(-5.0100);
            ride8.setDepartureTime(LocalDateTime.now().plusDays(1).withHour(18).withMinute(0));
            ride8.setEstimatedArrivalTime(LocalDateTime.now().plusDays(1).withHour(18).withMinute(45));
            ride8.setTotalSeats(4);
            ride8.setAvailableSeats(4);
            ride8.setPricePerSeat(new BigDecimal("20.00"));
            ride8.setStatus(RideStatus.SCHEDULED);
            ride8.setInstantBooking(false);
            ride8.setAllowsBaggage(true);
            ride8.setDistanceKm(18.0);
            ride8.setDurationMinutes(45);
            ride8.setDescription("Retour vers Fès centre après les cours");
            em.persist(ride8);

            tx.commit();
            System.out.println("✅ 5 trajets supplémentaires ajoutés avec succès!");

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.err.println("❌ Erreur lors de l'ajout des trajets: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Crée les données initiales (utilisateurs, véhicules, trajets de base)
     */
    private static void seedInitialData() {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // ========================
            // UTILISATEURS DE TEST
            // ========================

            // Admin
            User admin = new User();
            admin.setEmail("admin@ueuromed.org");
            admin.setPassword(PasswordUtil.hashPassword("Admin123!"));
            admin.setFirstName("Admin");
            admin.setLastName("Système");
            admin.setRole(UserRole.ADMIN);
            admin.setVerified(true);
            admin.setActive(true);
            admin.setPhone("+212600000000");
            admin.setPrefMusic(true);
            admin.setPrefTalking(true);
            admin.setPrefSmoking(false);
            admin.setBio("Administrateur de la plateforme Yalah L'Univ");
            em.persist(admin);

            // Étudiant conducteur
            User driver1 = new User();
            driver1.setEmail("ahmed.benali@ueuromed.org");
            driver1.setPassword(PasswordUtil.hashPassword("Test123!"));
            driver1.setFirstName("Ahmed");
            driver1.setLastName("Benali");
            driver1.setRole(UserRole.STUDENT);
            driver1.setVerified(true);
            driver1.setActive(true);
            driver1.setPhone("+212611111111");
            driver1.setRating(4.5f);
            driver1.setRatingCount(12);
            driver1.setPrefMusic(true);
            driver1.setPrefTalking(true);
            driver1.setPrefSmoking(false);
            driver1.setBio("Étudiant en 3ème année génie informatique. Je fais le trajet Fès-UEMF tous les jours!");
            em.persist(driver1);

            // Étudiant passager
            User passenger1 = new User();
            passenger1.setEmail("fatima.zahra@ueuromed.org");
            passenger1.setPassword(PasswordUtil.hashPassword("Test123!"));
            passenger1.setFirstName("Fatima");
            passenger1.setLastName("Zahra");
            passenger1.setRole(UserRole.STUDENT);
            passenger1.setVerified(true);
            passenger1.setActive(true);
            passenger1.setPhone("+212622222222");
            passenger1.setRating(4.8f);
            passenger1.setRatingCount(8);
            em.persist(passenger1);

            // Professeur
            User professor = new User();
            professor.setEmail("prof.alami@ueuromed.org");
            professor.setPassword(PasswordUtil.hashPassword("Test123!"));
            professor.setFirstName("Mohammed");
            professor.setLastName("Alami");
            professor.setRole(UserRole.PROFESSOR);
            professor.setVerified(true);
            professor.setActive(true);
            professor.setPhone("+212633333333");
            professor.setRating(5.0f);
            professor.setRatingCount(5);
            professor.setBio("Professeur de mathématiques à l'UEMF");
            em.persist(professor);

            // ========================
            // VÉHICULES
            // ========================

            Vehicle vehicle1 = new Vehicle();
            vehicle1.setOwner(driver1);
            vehicle1.setBrand("Dacia");
            vehicle1.setModel("Logan");
            vehicle1.setColor("Blanc");
            vehicle1.setLicensePlate("12345-A-1");
            vehicle1.setSeats(5);
            vehicle1.setActive(true);
            em.persist(vehicle1);

            Vehicle vehicle2 = new Vehicle();
            vehicle2.setOwner(professor);
            vehicle2.setBrand("Renault");
            vehicle2.setModel("Clio");
            vehicle2.setColor("Gris");
            vehicle2.setLicensePlate("67890-B-2");
            vehicle2.setSeats(5);
            vehicle2.setActive(true);
            em.persist(vehicle2);

            // ========================
            // TRAJETS
            // ========================

            // Trajet 1: Bab Boujloud -> Campus (celui qui existe déjà)
            Ride ride1 = new Ride();
            ride1.setDriver(professor);
            ride1.setVehicle(vehicle2);
            ride1.setDepartureLocation("Fès Médina, Bab Boujloud");
            ride1.setDepartureLat(34.0621);
            ride1.setDepartureLng(-4.9815);
            ride1.setArrivalLocation("Campus UEMF, Parking Principal");
            ride1.setArrivalLat(33.9633);
            ride1.setArrivalLng(-4.9972);
            ride1.setDepartureTime(LocalDateTime.now().plusDays(1).withHour(7).withMinute(30));
            ride1.setEstimatedArrivalTime(LocalDateTime.now().plusDays(1).withHour(8).withMinute(15));
            ride1.setTotalSeats(4);
            ride1.setAvailableSeats(4);
            ride1.setPricePerSeat(new BigDecimal("25.00"));
            ride1.setStatus(RideStatus.SCHEDULED);
            ride1.setInstantBooking(false);
            ride1.setAllowsBaggage(true);
            ride1.setAllowsPets(false);
            ride1.setDistanceKm(20.0);
            ride1.setDurationMinutes(45);
            ride1.setDescription("Trajet depuis la médina. Ambiance calme, musique classique.");
            em.persist(ride1);

            tx.commit();

            System.out.println("✅ Base de données initialisée avec les données de test!");
            System.out.println("📧 Comptes disponibles:");
            System.out.println("   - Admin: admin@ueuromed.org / Admin123!");
            System.out.println("   - Conducteur: ahmed.benali@ueuromed.org / Test123!");
            System.out.println("   - Passager: fatima.zahra@ueuromed.org / Test123!");
            System.out.println("   - Professeur: prof.alami@ueuromed.org / Test123!");

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.err.println("❌ Erreur lors de l'initialisation de la base: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
