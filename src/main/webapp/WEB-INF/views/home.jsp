<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <fmt:setLocale value="fr_FR" />

            <jsp:include page="common/header.jsp">
                <jsp:param name="title" value="Accueil" />
                <jsp:param name="page" value="home" />
            </jsp:include>

            <!-- Hero Section -->
            <section class="hero">
                <div class="container">
                    <div class="hero-content">
                        <h1>Partagez vos trajets avec la communauté UEMF</h1>
                        <p>Économisez sur vos trajets quotidiens, rencontrez de nouvelles personnes et réduisez votre
                            empreinte carbone. Rejoignez le réseau de covoiturage de l'Université Euro-Méditerranéenne
                            de Fès.</p>
                    </div>

                    <!-- Search Box -->
                    <div class="search-box mt-xl animate-slide-up">
                        <form action="${pageContext.request.contextPath}/rides/search" method="POST" class="flex gap-md"
                            style="width: 100%; flex-wrap: wrap;">
                            <div class="form-group" style="flex: 2; min-width: 200px;">
                                <label class="form-label">Départ</label>
                                <input type="text" name="departure" class="form-input"
                                    placeholder="Ex: Fès Ville Nouvelle">
                                <input type="hidden" name="departureLat">
                                <input type="hidden" name="departureLng">
                            </div>
                            <div class="form-group" style="flex: 2; min-width: 200px;">
                                <label class="form-label">Destination</label>
                                <input type="text" name="arrival" class="form-input" placeholder="Ex: Campus UEMF">
                                <input type="hidden" name="arrivalLat">
                                <input type="hidden" name="arrivalLng">
                            </div>
                            <div class="form-group" style="flex: 1; min-width: 150px;">
                                <label class="form-label">Date</label>
                                <input type="text" name="date" class="form-input datepicker"
                                    placeholder="Choisir une date">
                            </div>
                            <div class="form-group" style="flex: 0 0 auto; align-self: flex-end;">
                                <button type="submit" class="btn btn-secondary btn-lg">
                                    <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                                        <circle cx="11" cy="11" r="8" />
                                        <path d="M21 21l-4.35-4.35" />
                                    </svg>
                                    Rechercher
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </section>

            <main class="main-content">
                <div class="container">
                    <!-- Welcome message for new users -->
                    <c:if test="${param.registered == 'true'}">
                        <div class="alert alert-success animate-slide-up">
                            <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd"
                                    d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" />
                            </svg>
                            <span>Bienvenue sur Yalah L'Univ! Votre compte a été créé avec succès.</span>
                        </div>
                    </c:if>

                    <!-- Dashboard for logged in users -->
                    <c:if test="${not empty sessionScope.user}">
                        <section class="mb-xl">
                            <div class="flex justify-between items-center mb-lg">
                                <h2>Bonjour, ${sessionScope.user.firstName} 👋</h2>
                                <a href="${pageContext.request.contextPath}/rides/create" class="btn btn-primary">
                                    + Proposer un trajet
                                </a>
                            </div>

                            <div class="grid grid-3 gap-lg">
                                <!-- Stats Card 1 -->
                                <div class="card">
                                    <div class="card-body text-center">
                                        <div style="font-size: 2.5rem; font-weight: 700; color: var(--primary);">
                                            ${upcomingRides != null ? upcomingRides.size() : 0}
                                        </div>
                                        <p class="text-muted mb-0">Trajets à venir</p>
                                    </div>
                                </div>

                                <!-- Stats Card 2 -->
                                <div class="card">
                                    <div class="card-body text-center">
                                        <div style="font-size: 2.5rem; font-weight: 700; color: var(--secondary);">
                                            ${confirmedBookings != null ? confirmedBookings.size() : 0}
                                        </div>
                                        <p class="text-muted mb-0">Réservations confirmées</p>
                                    </div>
                                </div>

                                <!-- Stats Card 3 -->
                                <div class="card">
                                    <div class="card-body text-center">
                                        <div style="font-size: 2.5rem; font-weight: 700; color: var(--warning);">
                                            ${pendingBookingsCount != null ? pendingBookingsCount : 0}
                                        </div>
                                        <p class="text-muted mb-0">Demandes en attente</p>
                                    </div>
                                </div>
                            </div>
                        </section>
                    </c:if>

                    <!-- Campus Rides Section -->
                    <c:if test="${not empty campusRides}">
                        <section class="mb-xl">
                            <div class="flex justify-between items-center mb-lg">
                                <h2>🎓 Trajets vers le Campus</h2>
                                <a href="${pageContext.request.contextPath}/rides/search?arrival=UEMF"
                                    class="btn btn-ghost">
                                    Voir tout →
                                </a>
                            </div>

                            <div class="grid grid-2 gap-lg">
                                <c:forEach items="${campusRides}" var="ride" end="3">
                                    <div class="card ride-card">
                                        <div class="ride-card-route">
                                            <div class="ride-card-locations">
                                                <div class="ride-card-location">
                                                    <span class="badge badge-secondary"
                                                        style="width: 24px; height: 24px; border-radius: 50%; display: flex; align-items: center; justify-content: center;">A</span>
                                                    <strong>${ride.departureLocation}</strong>
                                                </div>
                                                <div class="ride-card-arrow"></div>
                                                <div class="ride-card-location">
                                                    <span class="badge badge-primary"
                                                        style="width: 24px; height: 24px; border-radius: 50%; display: flex; align-items: center; justify-content: center;">B</span>
                                                    <strong>${ride.arrivalLocation}</strong>
                                                </div>
                                            </div>
                                            <div class="text-right">
                                                <div class="ride-card-price">${ride.pricePerSeat} <span>MAD</span></div>
                                                <a href="${pageContext.request.contextPath}/rides/${ride.id}"
                                                    class="btn btn-primary btn-sm">Réserver</a>
                                            </div>
                                        </div>
                                        <div class="ride-card-details">
                                            <div class="ride-card-detail">
                                                <svg width="16" height="16" fill="none" stroke="currentColor"
                                                    stroke-width="2">
                                                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                                                    <line x1="16" y1="2" x2="16" y2="6" />
                                                    <line x1="8" y1="2" x2="8" y2="6" />
                                                    <line x1="3" y1="10" x2="21" y2="10" />
                                                </svg>
                                                <fmt:parseDate value="${ride.departureTime}"
                                                    pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                                <fmt:formatDate value="${parsedDate}" pattern="dd MMM" />
                                            </div>
                                            <div class="ride-card-detail">
                                                <svg width="16" height="16" fill="none" stroke="currentColor"
                                                    stroke-width="2">
                                                    <circle cx="12" cy="12" r="10" />
                                                    <polyline points="12,6 12,12 16,14" />
                                                </svg>
                                                <fmt:formatDate value="${parsedDate}" pattern="HH:mm" />
                                            </div>
                                            <div class="ride-card-detail">
                                                <svg width="16" height="16" fill="none" stroke="currentColor"
                                                    stroke-width="2">
                                                    <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" />
                                                    <circle cx="9" cy="7" r="4" />
                                                </svg>
                                                ${ride.availableSeats} place(s)
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </section>
                    </c:if>

                    <!-- Recent Rides Section -->
                    <section>
                        <div class="flex justify-between items-center mb-lg">
                            <h2>🚗 Trajets récents</h2>
                            <a href="${pageContext.request.contextPath}/rides/search" class="btn btn-ghost">
                                Voir tout →
                            </a>
                        </div>

                        <c:choose>
                            <c:when test="${not empty recentRides}">
                                <div class="grid grid-3 gap-lg">
                                    <c:forEach items="${recentRides}" var="ride">
                                        <div class="card ride-card">
                                            <div class="ride-card-route"
                                                style="flex-direction: column; align-items: stretch;">
                                                <div class="ride-card-locations">
                                                    <div class="ride-card-location">
                                                        <span class="badge badge-secondary"
                                                            style="width: 20px; height: 20px; border-radius: 50%; font-size: 0.7rem;">A</span>
                                                        <span>${ride.departureLocation}</span>
                                                    </div>
                                                    <div class="ride-card-arrow" style="height: 20px;"></div>
                                                    <div class="ride-card-location">
                                                        <span class="badge badge-primary"
                                                            style="width: 20px; height: 20px; border-radius: 50%; font-size: 0.7rem;">B</span>
                                                        <span>${ride.arrivalLocation}</span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="ride-card-details">
                                                <div class="ride-card-detail">
                                                    <fmt:parseDate value="${ride.departureTime}"
                                                        pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                                    <fmt:formatDate value="${parsedDate}" pattern="dd/MM HH:mm" />
                                                </div>
                                                <div class="ride-card-detail">
                                                    ${ride.availableSeats} place(s)
                                                </div>
                                                <div class="ride-card-price">${ride.pricePerSeat} <span>MAD</span></div>
                                            </div>
                                            <div class="card-footer">
                                                <a href="${pageContext.request.contextPath}/rides/${ride.id}"
                                                    class="btn btn-outline btn-sm" style="width: 100%;">
                                                    Voir détails
                                                </a>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="card">
                                    <div class="card-body text-center" style="padding: 3rem;">
                                        <svg width="64" height="64" fill="none" stroke="var(--text-muted)"
                                            stroke-width="1.5" style="margin: 0 auto 1rem;">
                                            <circle cx="32" cy="32" r="28" />
                                            <path d="M20 32h24M32 20v24" />
                                        </svg>
                                        <h3>Aucun trajet disponible</h3>
                                        <p class="text-muted">Soyez le premier à proposer un trajet!</p>
                                        <a href="${pageContext.request.contextPath}/rides/create"
                                            class="btn btn-primary">
                                            Proposer un trajet
                                        </a>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </section>

                    <!-- Features Section -->
                    <section class="mt-3xl">
                        <h2 class="text-center mb-xl">Pourquoi Yalah L'Univ?</h2>
                        <div class="grid grid-3 gap-xl">
                            <div class="text-center">
                                <div
                                    style="width: 80px; height: 80px; border-radius: 50%; background: var(--success-light); display: flex; align-items: center; justify-content: center; margin: 0 auto 1rem;">
                                    <svg width="40" height="40" fill="var(--success)" viewBox="0 0 24 24">
                                        <path
                                            d="M12 22c5.523 0 10-4.477 10-10S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10z" />
                                        <path fill="#fff" d="M8 12l2 2 4-4" />
                                    </svg>
                                </div>
                                <h4>Communauté vérifiée</h4>
                                <p class="text-muted">Tous les membres sont vérifiés via leur email UEMF pour une
                                    expérience sécurisée.</p>
                            </div>
                            <div class="text-center">
                                <div
                                    style="width: 80px; height: 80px; border-radius: 50%; background: var(--info-light); display: flex; align-items: center; justify-content: center; margin: 0 auto 1rem;">
                                    <svg width="40" height="40" fill="var(--info)" viewBox="0 0 24 24">
                                        <circle cx="12" cy="12" r="10" />
                                        <path fill="#fff" d="M12 6v6l4 2" />
                                    </svg>
                                </div>
                                <h4>Économisez du temps</h4>
                                <p class="text-muted">Trouvez des trajets correspondant à vos horaires de cours et
                                    évitez les transports bondés.</p>
                            </div>
                            <div class="text-center">
                                <div
                                    style="width: 80px; height: 80px; border-radius: 50%; background: var(--secondary-light); display: flex; align-items: center; justify-content: center; margin: 0 auto 1rem; opacity: 0.3;">
                                    <svg width="40" height="40" fill="var(--secondary)" viewBox="0 0 24 24">
                                        <path
                                            d="M12 22c5.523 0 10-4.477 10-10S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10z" />
                                        <path fill="#fff" d="M7 12l3 3 7-7" />
                                    </svg>
                                </div>
                                <h4>Éco-responsable</h4>
                                <p class="text-muted">Réduisez votre empreinte carbone en partageant vos trajets avec
                                    d'autres étudiants.</p>
                            </div>
                        </div>
                    </section>
                </div>
            </main>

            <script>
                document.addEventListener('DOMContentLoaded', function () {
                    // Initialize date picker
                    if (typeof flatpickr !== 'undefined') {
                        flatpickr('.datepicker', {
                            locale: 'fr',
                            dateFormat: 'Y-m-d',
                            minDate: 'today'
                        });
                    }
                });
            </script>

            <jsp:include page="common/footer.jsp" />