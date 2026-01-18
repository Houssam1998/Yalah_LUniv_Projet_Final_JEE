<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <jsp:include page="../common/header.jsp">
                <jsp:param name="title" value="Rechercher un trajet" />
                <jsp:param name="page" value="search" />
            </jsp:include>

            <main class="main-content">
                <div class="container">
                    <h1 class="mb-lg">Rechercher un trajet</h1>

                    <!-- Search Form -->
                    <div class="card mb-xl">
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/rides/search" method="POST"
                                id="searchForm">
                                <div class="grid grid-4 gap-md" style="align-items: end;">
                                    <div class="form-group">
                                        <label class="form-label">Départ</label>
                                        <input type="text" name="departure" class="form-input"
                                            placeholder="Ex: Fès Centre" value="${departure}">
                                        <input type="hidden" name="departureLat" value="${departureLat}">
                                        <input type="hidden" name="departureLng" value="${departureLng}">
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label">Destination</label>
                                        <input type="text" name="arrival" class="form-input"
                                            placeholder="Ex: Campus UEMF" value="${arrival}">
                                        <input type="hidden" name="arrivalLat" value="${arrivalLat}">
                                        <input type="hidden" name="arrivalLng" value="${arrivalLng}">
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label">Date</label>
                                        <input type="text" name="date" class="form-input datepicker"
                                            placeholder="Sélectionner" value="${date}">
                                    </div>
                                    <div class="form-group">
                                        <button type="submit" class="btn btn-primary" style="width: 100%;">
                                            Rechercher
                                        </button>
                                    </div>
                                </div>

                                <!-- Advanced Filters (collapsible) -->
                                <details class="mt-md">
                                    <summary class="text-sm text-primary" style="cursor: pointer;">
                                        + Filtres avancés
                                    </summary>
                                    <div class="grid grid-3 gap-md mt-md">
                                        <div class="form-group">
                                            <label class="form-label">Nombre de places</label>
                                            <select name="seats" class="form-select">
                                                <option value="1" ${seats==1 ? 'selected' : '' }>1 place</option>
                                                <option value="2" ${seats==2 ? 'selected' : '' }>2 places</option>
                                                <option value="3" ${seats==3 ? 'selected' : '' }>3 places</option>
                                                <option value="4" ${seats==4 ? 'selected' : '' }>4+ places</option>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label">Prix maximum (MAD)</label>
                                            <input type="number" name="maxPrice" class="form-input" placeholder="Ex: 50"
                                                value="${maxPrice}" min="0" step="5">
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label">Options</label>
                                            <div class="flex gap-md">
                                                <label class="form-check">
                                                    <input type="checkbox" name="allowsBaggage">
                                                    <span class="text-sm">Bagages</span>
                                                </label>
                                                <label class="form-check">
                                                    <input type="checkbox" name="instantBooking">
                                                    <span class="text-sm">Résa. instantanée</span>
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                </details>
                            </form>
                        </div>
                    </div>

                    <!-- Results -->
                    <c:if test="${searchPerformed}">
                        <div class="flex justify-between items-center mb-lg">
                            <h2>
                                <c:choose>
                                    <c:when test="${not empty searchResults}">
                                        ${searchResults.size()} trajet(s) trouvé(s)
                                        <c:if test="${hasProximity}">
                                            <span class="text-sm text-muted" style="font-weight: normal;"> - triés par
                                                pertinence</span>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        Aucun trajet trouvé
                                    </c:otherwise>
                                </c:choose>
                            </h2>
                            <div class="flex gap-sm">
                                <button class="btn btn-ghost btn-sm" onclick="sortBy('price')">Prix ↕</button>
                                <button class="btn btn-ghost btn-sm" onclick="sortBy('time')">Heure ↕</button>
                            </div>
                        </div>
                    </c:if>

                    <c:choose>
                        <c:when test="${not empty searchResults}">
                            <div class="grid gap-md" id="ridesGrid">
                                <c:forEach items="${searchResults}" var="result">
                                    <c:set var="ride" value="${result.ride}" />
                                    <div class="card ride-card" data-price="${ride.pricePerSeat}"
                                        data-time="${ride.departureTime}">
                                        <div class="card-body">
                                            <!-- Proximity badges (BlaBlaCar style) -->
                                            <c:if
                                                test="${result.departureProximityKm != null || result.arrivalProximityKm != null}">
                                                <div class="flex gap-sm mb-md" style="flex-wrap: wrap;">
                                                    <c:if test="${result.departureProximityKm != null}">
                                                        <c:choose>
                                                            <c:when test="${result.departureProximityLevel == 'exact'}">
                                                                <span class="badge"
                                                                    style="background: #10b981; color: white;">
                                                                    📍 Départ à ${result.departureProximityText}
                                                                </span>
                                                            </c:when>
                                                            <c:when test="${result.departureProximityLevel == 'close'}">
                                                                <span class="badge"
                                                                    style="background: #3b82f6; color: white;">
                                                                    📍 Départ à ${result.departureProximityText}
                                                                </span>
                                                            </c:when>
                                                            <c:when
                                                                test="${result.departureProximityLevel == 'nearby'}">
                                                                <span class="badge"
                                                                    style="background: #f59e0b; color: white;">
                                                                    📍 Départ à ${result.departureProximityText}
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge"
                                                                    style="background: var(--surface-hover);">
                                                                    📍 Départ à ${result.departureProximityText}
                                                                </span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                    <c:if test="${result.arrivalProximityKm != null}">
                                                        <c:choose>
                                                            <c:when test="${result.arrivalProximityLevel == 'exact'}">
                                                                <span class="badge"
                                                                    style="background: #10b981; color: white;">
                                                                    🎯 Arrivée à ${result.arrivalProximityText}
                                                                </span>
                                                            </c:when>
                                                            <c:when test="${result.arrivalProximityLevel == 'close'}">
                                                                <span class="badge"
                                                                    style="background: #3b82f6; color: white;">
                                                                    🎯 Arrivée à ${result.arrivalProximityText}
                                                                </span>
                                                            </c:when>
                                                            <c:when test="${result.arrivalProximityLevel == 'nearby'}">
                                                                <span class="badge"
                                                                    style="background: #f59e0b; color: white;">
                                                                    🎯 Arrivée à ${result.arrivalProximityText}
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge"
                                                                    style="background: var(--surface-hover);">
                                                                    🎯 Arrivée à ${result.arrivalProximityText}
                                                                </span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                </div>
                                            </c:if>

                                            <div class="flex gap-lg" style="flex-wrap: wrap;">
                                                <!-- Route Info -->
                                                <div class="flex-1" style="min-width: 300px;">
                                                    <div class="flex items-center gap-lg">
                                                        <div class="ride-card-locations">
                                                            <div class="ride-card-location">
                                                                <span class="badge badge-secondary"
                                                                    style="width: 28px; height: 28px; border-radius: 50%;">A</span>
                                                                <div>
                                                                    <strong
                                                                        style="font-size: 1.1rem;">${ride.departureLocation}</strong>
                                                                    <div class="text-sm text-muted">
                                                                        <fmt:parseDate value="${ride.departureTime}"
                                                                            pattern="yyyy-MM-dd'T'HH:mm"
                                                                            var="parsedDate" type="both" />
                                                                        <fmt:formatDate value="${parsedDate}"
                                                                            pattern="HH:mm" />
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="ride-card-arrow" style="margin-left: 13px;">
                                                            </div>
                                                            <div class="ride-card-location">
                                                                <span class="badge badge-primary"
                                                                    style="width: 28px; height: 28px; border-radius: 50%;">B</span>
                                                                <div>
                                                                    <strong
                                                                        style="font-size: 1.1rem;">${ride.arrivalLocation}</strong>
                                                                    <c:if test="${ride.estimatedArrivalTime != null}">
                                                                        <div class="text-sm text-muted">
                                                                            <fmt:parseDate
                                                                                value="${ride.estimatedArrivalTime}"
                                                                                pattern="yyyy-MM-dd'T'HH:mm"
                                                                                var="arrivalDate" type="both" />
                                                                            ~
                                                                            <fmt:formatDate value="${arrivalDate}"
                                                                                pattern="HH:mm" />
                                                                        </div>
                                                                    </c:if>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <div class="flex gap-lg mt-md text-sm text-muted">
                                                        <span>📅
                                                            <fmt:formatDate value="${parsedDate}"
                                                                pattern="EEEE dd MMMM" />
                                                        </span>
                                                        <c:if test="${ride.distanceKm != null}">
                                                            <span>📍 ${ride.distanceKm} km</span>
                                                        </c:if>
                                                        <c:if test="${ride.durationMinutes != null}">
                                                            <span>⏱ ${ride.durationMinutes} min</span>
                                                        </c:if>
                                                    </div>
                                                </div>

                                                <!-- Driver Info -->
                                                <div class="flex items-center gap-md">
                                                    <div class="avatar">
                                                        ${ride.driver.firstName.substring(0,1)}${ride.driver.lastName.substring(0,1)}
                                                    </div>
                                                    <div>
                                                        <strong>${ride.driver.firstName}</strong>
                                                        <c:if test="${ride.driver.verified}">
                                                            <span class="badge badge-verified"
                                                                style="font-size: 0.6rem; padding: 2px 6px;">✓
                                                                UEMF</span>
                                                        </c:if>
                                                        <div class="rating">
                                                            <c:forEach begin="1" end="5" var="star">
                                                                <span
                                                                    class="rating-star ${star <= ride.driver.rating ? 'filled' : ''}">★</span>
                                                            </c:forEach>
                                                            <span class="rating-value">${ride.driver.rating}</span>
                                                        </div>
                                                    </div>
                                                </div>

                                                <!-- Price & Action -->
                                                <div class="text-right">
                                                    <div class="ride-card-price" style="font-size: 1.75rem;">
                                                        ${ride.pricePerSeat} <span>MAD</span>
                                                    </div>
                                                    <div class="text-sm text-muted mb-sm">${ride.availableSeats}
                                                        place(s) dispo.</div>
                                                    <a href="${pageContext.request.contextPath}/rides/${ride.id}"
                                                        class="btn btn-primary">
                                                        Voir détails
                                                    </a>
                                                </div>
                                            </div>

                                            <!-- Features badges -->
                                            <div class="flex gap-sm mt-md">
                                                <c:if test="${ride.instantBooking}">
                                                    <span class="badge badge-success">⚡ Résa. instantanée</span>
                                                </c:if>
                                                <c:if test="${ride.allowsBaggage}">
                                                    <span class="badge" style="background: var(--surface-hover);">🧳
                                                        Bagages OK</span>
                                                </c:if>
                                                <c:if test="${ride.allowsPets}">
                                                    <span class="badge" style="background: var(--surface-hover);">🐾
                                                        Animaux OK</span>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:when test="${searchPerformed}">
                            <div class="card">
                                <div class="card-body text-center" style="padding: 3rem;">
                                    <svg width="80" height="80" fill="none" stroke="var(--text-muted)"
                                        stroke-width="1.5" style="margin: 0 auto 1.5rem;">
                                        <circle cx="40" cy="40" r="35" />
                                        <path d="M30 45l10 10 20-20" stroke="var(--text-muted)" stroke-width="3" />
                                    </svg>
                                    <h3>Aucun trajet ne correspond à votre recherche</h3>
                                    <p class="text-muted">Essayez de modifier vos critères ou créez une alerte.</p>
                                    <div class="flex gap-md justify-center mt-lg">
                                        <button onclick="document.getElementById('searchForm').reset()"
                                            class="btn btn-ghost">
                                            Réinitialiser
                                        </button>
                                        <a href="${pageContext.request.contextPath}/rides/create"
                                            class="btn btn-primary">
                                            Proposer ce trajet
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <!-- Show popular rides if no search performed -->
                            <c:if test="${not empty campusRides}">
                                <h2 class="mb-lg">🎓 Trajets populaires vers le campus</h2>
                                <div class="grid grid-2 gap-lg">
                                    <c:forEach items="${campusRides}" var="ride">
                                        <div class="card ride-card">
                                            <div class="card-body">
                                                <div class="flex justify-between items-center">
                                                    <div>
                                                        <strong>${ride.departureLocation}</strong>
                                                        <span class="text-muted">→</span>
                                                        <strong>${ride.arrivalLocation}</strong>
                                                    </div>
                                                    <div class="ride-card-price">${ride.pricePerSeat} <span>MAD</span>
                                                    </div>
                                                </div>
                                                <div class="flex justify-between items-center mt-md">
                                                    <span class="text-sm text-muted">
                                                        <fmt:parseDate value="${ride.departureTime}"
                                                            pattern="yyyy-MM-dd'T'HH:mm" var="dt" type="both" />
                                                        <fmt:formatDate value="${dt}" pattern="dd/MM à HH:mm" /> •
                                                        ${ride.availableSeats} places
                                                    </span>
                                                    <a href="${pageContext.request.contextPath}/rides/${ride.id}"
                                                        class="btn btn-outline btn-sm">Détails</a>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>
            </main>

            <script>
                document.addEventListener('DOMContentLoaded', function () {
                    flatpickr('.datepicker', {
                        locale: 'fr',
                        dateFormat: 'Y-m-d',
                        minDate: 'today'
                    });
                });

                function sortBy(criteria) {
                    const grid = document.getElementById('ridesGrid');
                    const cards = Array.from(grid.querySelectorAll('.ride-card'));

                    cards.sort((a, b) => {
                        if (criteria === 'price') {
                            return parseFloat(a.dataset.price) - parseFloat(b.dataset.price);
                        } else {
                            return a.dataset.time.localeCompare(b.dataset.time);
                        }
                    });

                    cards.forEach(card => grid.appendChild(card));
                }
            </script>

            <jsp:include page="../common/footer.jsp" />