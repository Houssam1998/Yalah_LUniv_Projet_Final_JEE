<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <div class="footer-grid">
                <div>
                    <div class="footer-brand">
                        <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="UEMF Logo"
                            style="height: 40px;">
                        <span style="font-size: 1.25rem; font-weight: 700; color: white;">Yalah L'Univ</span>
                    </div>
                    <p>Application de covoiturage pour la communauté universitaire de l'UEMF. Partagez vos trajets,
                        économisez et protégez l'environnement.</p>
                </div>

                <div>
                    <h4>Navigation</h4>
                    <ul class="footer-links">
                        <li><a href="${pageContext.request.contextPath}/rides/search">Rechercher un trajet</a></li>
                        <li><a href="${pageContext.request.contextPath}/rides/create">Proposer un trajet</a></li>
                        <li><a href="${pageContext.request.contextPath}/about">À propos</a></li>
                        <li><a href="${pageContext.request.contextPath}/contact">Contact</a></li>
                    </ul>
                </div>

                <div>
                    <h4>Assistance</h4>
                    <ul class="footer-links">
                        <li><a href="${pageContext.request.contextPath}/faq">FAQ</a></li>
                        <li><a href="${pageContext.request.contextPath}/safety">Sécurité</a></li>
                        <li><a href="${pageContext.request.contextPath}/terms">Conditions d'utilisation</a></li>
                        <li><a href="${pageContext.request.contextPath}/privacy">Politique de confidentialité</a></li>
                    </ul>
                </div>

                <div>
                    <h4>UEMF</h4>
                    <ul class="footer-links">
                        <li><a href="https://ueuromed.org/fr" target="_blank">Site officiel UEMF</a></li>
                        <li><a href="#">Campus de Fès</a></li>
                        <li><a href="#">Vie étudiante</a></li>
                    </ul>
                </div>
            </div>

            <div class="footer-bottom">
                <p>&copy; 2026 Yalah L'Univ - Université Euro-Méditerranéenne de Fès. Tous droits réservés.</p>
            </div>
        </div>
    </footer>
    </div>

    <!-- JavaScript Libraries -->
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
    <script src="https://cdn.jsdelivr.net/npm/flatpickr/dist/l10n/fr.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <!-- App JavaScript -->
    <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
    </body>

    </html>