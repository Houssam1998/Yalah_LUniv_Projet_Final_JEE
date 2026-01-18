<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <jsp:include page="../common/header.jsp">
                <jsp:param name="title" value="Détails du trajet" />
                <jsp:param name="page" value="rides" />
            </jsp:include>

            <main class="main-content">
                <div class="container container-lg">
                    <!-- Breadcrumb -->
                    <nav class="mb-lg text-sm">
                        <a href="${pageContext.request.contextPath}/rides/search" class="text-muted">← Retour à la
                            recherche</a>
                    </nav>

                    <c:if test="${not empty ride}">
                        <div class="grid gap-lg" style="grid-template-columns: 2fr 1fr;">
                            <!-- Main Content -->
                            <div>
                                <!-- Route Card -->
                                <div class="card mb-lg">
                                    <div class="card-body">
                                        <div class="flex items-center gap-xl">
                                            <div class="ride-card-locations" style="flex: 1;">
                                                <div class="ride-card-location">
                                                    <span class="badge badge-secondary"
                                                        style="width: 36px; height: 36px; border-radius: 50%; font-size: 1.1rem;">A</span>
                                                    <div>
                                                        <strong
                                                            style="font-size: 1.25rem;">${ride.departureLocation}</strong>
                                                        <div class="text-muted">
                                                            <fmt:parseDate value="${ride.departureTime}"
                                                                pattern="yyyy-MM-dd'T'HH:mm" var="depTime"
                                                                type="both" />
                                                            <fmt:formatDate value="${depTime}"
                                                                pattern="EEEE dd MMMM yyyy 'à' HH:mm" />
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="ride-card-arrow" style="height: 40px; margin-left: 17px;">
                                                </div>
                                                <div class="ride-card-location">
                                                    <span class="badge badge-primary"
                                                        style="width: 36px; height: 36px; border-radius: 50%; font-size: 1.1rem;">B</span>
                                                    <div>
                                                        <strong
                                                            style="font-size: 1.25rem;">${ride.arrivalLocation}</strong>
                                                        <c:if test="${ride.estimatedArrivalTime != null}">
                                                            <div class="text-muted">
                                                                <fmt:parseDate value="${ride.estimatedArrivalTime}"
                                                                    pattern="yyyy-MM-dd'T'HH:mm" var="arrTime"
                                                                    type="both" />
                                                                Arrivée estimée:
                                                                <fmt:formatDate value="${arrTime}" pattern="HH:mm" />
                                                            </div>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="text-right">
                                                <div class="ride-card-price" style="font-size: 2.5rem;">
                                                    ${ride.pricePerSeat} <span style="font-size: 1rem;">MAD</span>
                                                </div>
                                                <div class="text-muted">par place</div>
                                            </div>
                                        </div>

                                        <!-- Stats -->
                                        <div class="flex gap-xl mt-lg"
                                            style="border-top: 1px solid var(--border); padding-top: 1rem;">
                                            <div class="text-center">
                                                <div
                                                    style="font-size: 1.5rem; font-weight: 700; color: var(--primary);">
                                                    ${ride.availableSeats}
                                                </div>
                                                <div class="text-sm text-muted">place(s) dispo.</div>
                                            </div>
                                            <c:if test="${ride.distanceKm != null}">
                                                <div class="text-center">
                                                    <div
                                                        style="font-size: 1.5rem; font-weight: 700; color: var(--secondary);">
                                                        ${ride.distanceKm}
                                                    </div>
                                                    <div class="text-sm text-muted">kilomètres</div>
                                                </div>
                                            </c:if>
                                            <c:if test="${ride.durationMinutes != null}">
                                                <div class="text-center">
                                                    <div
                                                        style="font-size: 1.5rem; font-weight: 700; color: var(--accent);">
                                                        ${ride.durationMinutes}
                                                    </div>
                                                    <div class="text-sm text-muted">minutes</div>
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>

                                <!-- Map -->
                                <div class="card mb-lg">
                                    <div class="card-header">
                                        <h3 style="margin: 0;">🗺️ Itinéraire</h3>
                                    </div>
                                    <div class="card-body" style="padding: 0;">
                                        <div id="map" class="map-container"></div>
                                    </div>
                                </div>

                                <!-- Options -->
                                <div class="card mb-lg">
                                    <div class="card-header">
                                        <h3 style="margin: 0;">ℹ️ Informations du trajet</h3>
                                    </div>
                                    <div class="card-body">
                                        <div class="flex flex-wrap gap-md mb-lg">
                                            <c:if test="${ride.instantBooking}">
                                                <span class="badge badge-success">⚡ Réservation instantanée</span>
                                            </c:if>
                                            <c:if test="${ride.allowsBaggage}">
                                                <span class="badge" style="background: var(--surface-hover);">🧳 Bagages
                                                    autorisés</span>
                                            </c:if>
                                            <c:if test="${ride.allowsPets}">
                                                <span class="badge" style="background: var(--surface-hover);">🐾 Animaux
                                                    autorisés</span>
                                            </c:if>
                                            <c:if test="${ride.allowsDetours}">
                                                <span class="badge" style="background: var(--surface-hover);">↪️ Détours
                                                    possibles</span>
                                            </c:if>
                                        </div>

                                        <c:if test="${not empty ride.description}">
                                            <h4>Description</h4>
                                            <p>${ride.description}</p>
                                        </c:if>

                                        <!-- Vehicle -->
                                        <h4>Véhicule</h4>
                                        <div class="flex items-center gap-md">
                                            <div
                                                style="width: 60px; height: 60px; background: var(--surface-hover); border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center;">
                                                🚗
                                            </div>
                                            <div>
                                                <strong>${ride.vehicle.brand} ${ride.vehicle.model}</strong>
                                                <div class="text-sm text-muted">${ride.vehicle.color} •
                                                    ${ride.vehicle.seats} places</div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Passengers (for driver) -->
                                <c:if test="${sessionScope.user.id == ride.driver.id && not empty bookings}">
                                    <div class="card">
                                        <div class="card-header flex justify-between items-center">
                                            <h3 style="margin: 0;">👥 Passagers</h3>
                                            <span class="badge badge-primary">${bookings.size()} réservation(s)</span>
                                        </div>
                                        <div class="card-body">
                                            <c:forEach items="${bookings}" var="booking">
                                                <div class="flex items-center gap-md mb-md"
                                                    style="padding: 1rem; background: var(--background); border-radius: var(--radius-md);">
                                                    <a
                                                        href="${pageContext.request.contextPath}/profile/user/${booking.passenger.id}">
                                                        <div class="avatar" style="cursor: pointer;">
                                                            ${booking.passenger.firstName.substring(0,1)}${booking.passenger.lastName.substring(0,1)}
                                                        </div>
                                                    </a>
                                                    <div style="flex: 1;">
                                                        <a href="${pageContext.request.contextPath}/profile/user/${booking.passenger.id}"
                                                            style="text-decoration: none; color: inherit;">
                                                            <strong>${booking.passenger.fullName}</strong>
                                                        </a>
                                                        <div class="text-sm text-muted">${booking.seatsBooked} place(s)
                                                        </div>
                                                    </div>
                                                    <span
                                                        class="badge badge-${booking.status == 'CONFIRMED' ? 'success' : booking.status == 'PENDING' ? 'warning' : 'error'}">
                                                        ${booking.status.displayName}
                                                    </span>
                                                    <div class="flex gap-sm">
                                                        <a href="${pageContext.request.contextPath}/messages/${booking.passenger.id}"
                                                            class="btn btn-sm btn-outline" title="Contacter">💬</a>
                                                        <a href="${pageContext.request.contextPath}/profile/user/${booking.passenger.id}"
                                                            class="btn btn-sm btn-ghost" title="Profil">👤</a>
                                                    </div>
                                                    <c:if test="${booking.status == 'PENDING'}">
                                                        <form
                                                            action="${pageContext.request.contextPath}/bookings/confirm"
                                                            method="POST" style="display: inline;">
                                                            <input type="hidden" name="bookingId" value="${booking.id}">
                                                            <button type="submit"
                                                                class="btn btn-sm btn-secondary">Accepter</button>
                                                        </form>
                                                        <form
                                                            action="${pageContext.request.contextPath}/bookings/reject"
                                                            method="POST" style="display: inline;">
                                                            <input type="hidden" name="bookingId" value="${booking.id}">
                                                            <button type="submit"
                                                                class="btn btn-sm btn-ghost">Refuser</button>
                                                        </form>
                                                    </c:if>
                                                    <c:if test="${booking.status == 'COMPLETED'}">
                                                        <a href="${pageContext.request.contextPath}/reviews/create/${booking.id}"
                                                            class="btn btn-sm btn-primary">⭐ Noter</a>
                                                    </c:if>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </c:if>
                            </div>

                            <!-- Sidebar -->
                            <div>
                                <!-- Driver Card -->
                                <div class="card mb-lg" style="position: sticky; top: 100px;">
                                    <div class="card-body text-center">
                                        <div class="avatar avatar-xl" style="margin: 0 auto 1rem;">
                                            ${ride.driver.firstName.substring(0,1)}${ride.driver.lastName.substring(0,1)}
                                        </div>
                                        <h3>${ride.driver.firstName}</h3>
                                        <c:if test="${ride.driver.verified}">
                                            <span class="badge badge-verified">✓ Vérifié UEMF</span>
                                        </c:if>

                                        <div class="rating justify-center mt-md">
                                            <c:forEach begin="1" end="5" var="star">
                                                <span
                                                    class="rating-star ${star <= ride.driver.rating ? 'filled' : ''}">★</span>
                                            </c:forEach>
                                            <span class="rating-value">${ride.driver.rating}</span>
                                            <span class="text-sm text-muted">(${ride.driver.ratingCount} avis)</span>
                                        </div>

                                        <p class="text-muted mt-md">${ride.driver.role.displayName}</p>

                                        <c:if test="${not empty ride.driver.bio}">
                                            <p class="text-sm mt-md"
                                                style="border-top: 1px solid var(--border); padding-top: 1rem;">
                                                ${ride.driver.bio}
                                            </p>
                                        </c:if>
                                    </div>

                                    <div class="card-footer">
                                        <c:choose>
                                            <c:when test="${sessionScope.user.id == ride.driver.id}">
                                                <a href="${pageContext.request.contextPath}/rides/edit/${ride.id}"
                                                    class="btn btn-outline" style="width: 100%;">
                                                    Modifier le trajet
                                                </a>
                                            </c:when>
                                            <c:when test="${empty sessionScope.user}">
                                                <a href="${pageContext.request.contextPath}/auth/login"
                                                    class="btn btn-primary" style="width: 100%;">
                                                    Connectez-vous pour réserver
                                                </a>
                                            </c:when>
                                            <c:when test="${ride.availableSeats > 0}">
                                                <form action="${pageContext.request.contextPath}/bookings/create"
                                                    method="POST">
                                                    <input type="hidden" name="rideId" value="${ride.id}">
                                                    <div class="form-group">
                                                        <label class="form-label">Nombre de places</label>
                                                        <select name="seats" class="form-select">
                                                            <c:forEach begin="1" end="${ride.availableSeats}" var="i">
                                                                <option value="${i}">${i} place(s)</option>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                    <div class="form-group">
                                                        <label class="form-label">Message (optionnel)</label>
                                                        <textarea name="message" class="form-textarea" rows="2"
                                                            placeholder="Présentez-vous..."></textarea>
                                                    </div>
                                                    <button type="submit" class="btn btn-primary btn-lg"
                                                        style="width: 100%;">
                                                        ${ride.instantBooking ? 'Réserver maintenant' : 'Demander une
                                                        réservation'}
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <button class="btn btn-ghost" style="width: 100%;" disabled>
                                                    Complet
                                                </button>
                                            </c:otherwise>
                                        </c:choose>

                                        <a href="${pageContext.request.contextPath}/messages/new?to=${ride.driver.id}&ride=${ride.id}"
                                            class="btn btn-ghost mt-md" style="width: 100%;">
                                            💬 Contacter ${ride.driver.firstName}
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </div>
            </main>

            <script>
                document.addEventListener('DOMContentLoaded', function () {
                    // Initialize map
                    const map = L.map('map').setView([34.0331, -5.0003], 11);

                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '© OpenStreetMap'
                    }).addTo(map);

                    <c:if test="${ride.departureLat != null && ride.arrivalLat != null}">
                        const depLat = ${ride.departureLat};
                        const depLng = ${ride.departureLng};
                        const arrLat = ${ride.arrivalLat};
                        const arrLng = ${ride.arrivalLng};

                        // Add markers
                        const depMarker = L.marker([depLat, depLng]).addTo(map)
                        .bindPopup('<strong>Départ:</strong> ${ride.departureLocation}');
                        const arrMarker = L.marker([arrLat, arrLng]).addTo(map)
                        .bindPopup('<strong>Arrivée:</strong> ${ride.arrivalLocation}');

                        // Fetch real route from OSRM API
                        fetch(`https://router.project-osrm.org/route/v1/driving/` + depLng + `,` + depLat + `;` + arrLng + `,` + arrLat + `?overview=full&geometries=geojson`)
                            .then(response => response.json())
                            .then(data => {
                                if (data.code === 'Ok' && data.routes && data.routes.length > 0) {
                                    const route = data.routes[0];
                                    const coordinates = route.geometry.coordinates.map(coord => [coord[1], coord[0]]);

                        // Draw the real route
                        L.polyline(coordinates, {
                            color: '#003B73',
                        weight: 5,
                        opacity: 0.8
                                    }).addTo(map);

                        // Update distance and duration if available
                        const distanceKm = (route.distance / 1000).toFixed(1);
                        const durationMin = Math.round(route.duration / 60);

                        console.log('Route: ' + distanceKm + ' km, ' + durationMin + ' min');
                                } else {
                            // Fallback to straight line if OSRM fails
                            L.polyline([[depLat, depLng], [arrLat, arrLng]], {
                                color: '#003B73',
                                weight: 4,
                                dashArray: '10, 10'
                            }).addTo(map);
                                }
                            })
                            .catch(error => {
                            console.error('OSRM error:', error);
                        // Fallback to straight line
                        L.polyline([[depLat, depLng], [arrLat, arrLng]], {
                            color: '#003B73',
                        weight: 4,
                        dashArray: '10, 10'
                                }).addTo(map);
                            });

                        // Fit bounds
                        map.fitBounds([
                        [depLat, depLng],
                        [arrLat, arrLng]
                        ], {padding: [50, 50]});
                    </c:if>
                });
            </script>

            <jsp:include page="../common/footer.jsp" />