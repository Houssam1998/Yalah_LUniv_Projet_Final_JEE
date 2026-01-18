<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="../common/header.jsp">
            <jsp:param name="title" value="Politique de confidentialité" />
            <jsp:param name="page" value="privacy" />
        </jsp:include>

        <main class="main-content">
            <div class="container">
                <div class="card">
                    <div class="card-body" style="max-width: 800px; margin: 0 auto;">
                        <h1 style="text-align: center; margin-bottom: 2rem;">🔒 Politique de Confidentialité</h1>

                        <p class="text-muted text-center mb-xl">Dernière mise à jour : Janvier 2026</p>

                        <h2>1. Données collectées</h2>
                        <p>Nous collectons les informations suivantes :</p>
                        <ul>
                            <li><strong>Données d'identification :</strong> nom, prénom, email, numéro de téléphone</li>
                            <li><strong>Données de profil :</strong> photo, biographie, rôle universitaire</li>
                            <li><strong>Données de véhicule :</strong> marque, modèle, couleur, immatriculation</li>
                            <li><strong>Données de trajet :</strong> itinéraires, horaires, historique</li>
                        </ul>

                        <h2 style="margin-top: 1.5rem;">2. Utilisation des données</h2>
                        <p>Vos données sont utilisées pour :</p>
                        <ul>
                            <li>Permettre le fonctionnement de la plateforme de covoiturage</li>
                            <li>Mettre en relation conducteurs et passagers</li>
                            <li>Améliorer nos services et votre expérience</li>
                            <li>Assurer la sécurité des utilisateurs</li>
                        </ul>

                        <h2 style="margin-top: 1.5rem;">3. Partage des données</h2>
                        <p>
                            Vos informations de base (prénom, photo, évaluations) sont visibles par les autres
                            utilisateurs
                            pour faciliter le covoiturage. Votre email et numéro de téléphone ne sont visibles que
                            par les personnes avec qui vous avez une réservation confirmée.
                        </p>
                        <p>
                            Nous ne vendons pas vos données à des tiers.
                        </p>

                        <h2 style="margin-top: 1.5rem;">4. Sécurité</h2>
                        <p>
                            Nous mettons en œuvre des mesures de sécurité appropriées pour protéger vos données
                            (chiffrement, accès restreint, sauvegardes régulières).
                        </p>

                        <h2 style="margin-top: 1.5rem;">5. Vos droits</h2>
                        <p>Conformément à la loi marocaine sur la protection des données personnelles, vous avez le
                            droit de :</p>
                        <ul>
                            <li>Accéder à vos données personnelles</li>
                            <li>Rectifier les informations inexactes</li>
                            <li>Supprimer votre compte et vos données</li>
                            <li>Vous opposer au traitement de vos données</li>
                        </ul>

                        <h2 style="margin-top: 1.5rem;">6. Contact</h2>
                        <p>
                            Pour exercer vos droits ou pour toute question, contactez-nous à
                            <a href="mailto:yalah@uemf.edu.ma">yalah@uemf.edu.ma</a>.
                        </p>
                    </div>
                </div>
            </div>
        </main>

        <jsp:include page="../common/footer.jsp" />