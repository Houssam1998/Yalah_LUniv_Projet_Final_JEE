<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="../common/header.jsp">
            <jsp:param name="title" value="Proposer un trajet" />
            <jsp:param name="page" value="create" />
        </jsp:include>

        <main class="main-content">
            <div class="container container-lg">
                <h1 class="mb-lg">Proposer un trajet</h1>

                <!-- Error Alert -->
                <c:if test="${not empty error}">
                    <div class="alert alert-error mb-lg">
                        <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
                            <path fill-rule="evenodd"
                                d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" />
                        </svg>
                        <span>${error}</span>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/rides/create" method="POST" id="createRideForm">
                    <div class="grid gap-lg" style="grid-template-columns: 1fr 1fr;">
                        <!-- Left Column - Form -->
                        <div>
                            <div class="card mb-lg">
                                <div class="card-header">
                                    <h3 style="margin-bottom: 0;">📍 Itinéraire</h3>
                                </div>
                                <div class="card-body">
                                    <div class="form-group">
                                        <label class="form-label">Lieu de départ *</label>
                                        <input type="text" name="departureLocation" id="departureLocation"
                                            class="form-input" placeholder="Ex: Fès Centre, Av. Mohammed V" required>
                                        <input type="hidden" name="departureLat" id="departureLat">
                                        <input type="hidden" name="departureLng" id="departureLng">
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label">Lieu d'arrivée *</label>
                                        <input type="text" name="arrivalLocation" id="arrivalLocation"
                                            class="form-input" placeholder="Ex: Campus UEMF, Route de Meknès" required>
                                        <input type="hidden" name="arrivalLat" id="arrivalLat">
                                        <input type="hidden" name="arrivalLng" id="arrivalLng">
                                    </div>

                                    <div class="alert alert-info">
                                        <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
                                            <path fill-rule="evenodd"
                                                d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" />
                                        </svg>
                                        <span class="text-sm">Tip: Soyez précis pour que les passagers vous trouvent
                                            facilement!</span>
                                    </div>
                                </div>
                            </div>

                            <div class="card mb-lg">
                                <div class="card-header">
                                    <h3 style="margin-bottom: 0;">📅 Date et heure</h3>
                                </div>
                                <div class="card-body">
                                    <div class="grid grid-2 gap-md">
                                        <div class="form-group">
                                            <label class="form-label">Date de départ *</label>
                                            <input type="text" name="departureDate" id="departureDate"
                                                class="form-input datepicker" placeholder="Sélectionner" required>
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label">Heure de départ *</label>
                                            <input type="text" name="departureTime" id="departureTime"
                                                class="form-input timepicker" placeholder="HH:MM" required>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="card mb-lg">
                                <div class="card-header">
                                    <h3 style="margin-bottom: 0;">🚗 Véhicule et places</h3>
                                </div>
                                <div class="card-body">
                                    <c:choose>
                                        <c:when test="${not empty vehicles}">
                                            <div class="form-group">
                                                <label class="form-label">Véhicule *</label>
                                                <select name="vehicleId" class="form-select" required>
                                                    <c:forEach items="${vehicles}" var="vehicle">
                                                        <option value="${vehicle.id}">
                                                            ${vehicle.brand} ${vehicle.model}
                                                            (${vehicle.color}) - ${vehicle.licensePlate}
                                                            (${vehicle.seats} places)
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="alert alert-warning">
                                                <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
                                                    <path fill-rule="evenodd"
                                                        d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" />
                                                </svg>
                                                <div>
                                                    <strong>Aucun véhicule enregistré</strong>
                                                    <p class="text-sm mb-0">Ajoutez d'abord un véhicule dans votre
                                                        profil.</p>
                                                    <a href="${pageContext.request.contextPath}/profile/vehicles"
                                                        class="btn btn-sm btn-outline mt-sm">
                                                        Ajouter un véhicule
                                                    </a>
                                                </div>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>

                                    <div class="grid grid-2 gap-md">
                                        <div class="form-group">
                                            <label class="form-label">Places disponibles *</label>
                                            <select name="availableSeats" class="form-select" required>
                                                <option value="1">1 place</option>
                                                <option value="2">2 places</option>
                                                <option value="3" selected>3 places</option>
                                                <option value="4">4 places</option>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label">Prix par place (MAD) *</label>
                                            <input type="number" name="pricePerSeat" class="form-input"
                                                placeholder="Ex: 20" min="0" max="200" step="5" required>
                                            <p class="form-hint">Prix suggéré: 15-30 MAD pour Fès ↔ UEMF</p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="card mb-lg">
                                <div class="card-header">
                                    <h3 style="margin-bottom: 0;">⚙️ Options</h3>
                                </div>
                                <div class="card-body">
                                    <div class="grid grid-2 gap-md">
                                        <label class="form-check">
                                            <input type="checkbox" name="instantBooking" checked>
                                            <span>Réservation instantanée</span>
                                        </label>
                                        <label class="form-check">
                                            <input type="checkbox" name="allowsBaggage" checked>
                                            <span>Bagages autorisés</span>
                                        </label>
                                        <label class="form-check">
                                            <input type="checkbox" name="allowsPets">
                                            <span>Animaux autorisés</span>
                                        </label>
                                        <label class="form-check">
                                            <input type="checkbox" name="allowsDetours">
                                            <span>Détours possibles</span>
                                        </label>
                                    </div>

                                    <div class="form-group mt-lg">
                                        <label class="form-label">Description (optionnel)</label>
                                        <textarea name="description" class="form-textarea" rows="3"
                                            placeholder="Informations supplémentaires sur le trajet..."></textarea>
                                    </div>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-primary btn-lg" style="width: 100%;" ${empty vehicles
                                ? 'disabled' : '' }>
                                Publier le trajet
                            </button>
                        </div>

                        <!-- Right Column - Map -->
                        <div>
                            <div class="card" style="position: sticky; top: 100px;">
                                <div class="card-header">
                                    <h3 style="margin-bottom: 0;">🗺️ Aperçu de l'itinéraire</h3>
                                </div>
                                <div class="card-body" style="padding: 0;">
                                    <div id="map" class="map-container-lg"></div>
                                </div>
                                <div class="card-footer">
                                    <div class="flex justify-between text-sm">
                                        <span id="routeDistance">Distance: --</span>
                                        <span id="routeDuration">Durée: --</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </main>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Date picker
                flatpickr('#departureDate', {
                    locale: 'fr',
                    dateFormat: 'Y-m-d',
                    minDate: 'today'
                });

                // Time picker
                flatpickr('#departureTime', {
                    enableTime: true,
                    noCalendar: true,
                    dateFormat: 'H:i',
                    time_24hr: true,
                    minTime: '05:00',
                    maxTime: '23:00'
                });

                // Initialize map centered on Fès
                const map = L.map('map').setView([34.0331, -5.0003], 12);

                // Add JawgMaps tiles (or OpenStreetMap as fallback)
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '© OpenStreetMap contributors'
                }).addTo(map);

                let departureMarker = null;
                let arrivalMarker = null;
                let routeLine = null;

                // Custom icons
                const departureIcon = L.divIcon({
                    html: '<div style="background: var(--secondary); color: white; width: 30px; height: 30px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: bold; border: 2px solid white; box-shadow: 0 2px 5px rgba(0,0,0,0.3);">A</div>',
                    iconSize: [30, 30],
                    iconAnchor: [15, 15]
                });

                const arrivalIcon = L.divIcon({
                    html: '<div style="background: var(--primary); color: white; width: 30px; height: 30px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: bold; border: 2px solid white; box-shadow: 0 2px 5px rgba(0,0,0,0.3);">B</div>',
                    iconSize: [30, 30],
                    iconAnchor: [15, 15]
                });

                // UEMF Campus marker
                const uemfMarker = L.marker([33.9633, -4.9972], {
                    icon: L.divIcon({
                        html: '<div style="background: var(--accent); color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; white-space: nowrap;">🎓 UEMF</div>',
                        iconSize: [80, 25],
                        iconAnchor: [40, 12]
                    })
                }).addTo(map);

                // Click on map to set markers
                let settingDeparture = true;

                map.on('click', function (e) {
                    if (settingDeparture) {
                        if (departureMarker) map.removeLayer(departureMarker);
                        departureMarker = L.marker(e.latlng, { icon: departureIcon }).addTo(map);
                        document.getElementById('departureLat').value = e.latlng.lat;
                        document.getElementById('departureLng').value = e.latlng.lng;
                        settingDeparture = false;
                    } else {
                        if (arrivalMarker) map.removeLayer(arrivalMarker);
                        arrivalMarker = L.marker(e.latlng, { icon: arrivalIcon }).addTo(map);
                        document.getElementById('arrivalLat').value = e.latlng.lat;
                        document.getElementById('arrivalLng').value = e.latlng.lng;
                        settingDeparture = true;

                        // Draw route line
                        if (departureMarker && arrivalMarker) {
                            if (routeLine) map.removeLayer(routeLine);
                            routeLine = L.polyline([
                                departureMarker.getLatLng(),
                                arrivalMarker.getLatLng()
                            ], { color: 'var(--primary)', weight: 4, dashArray: '10, 10' }).addTo(map);

                            // Estimate distance
                            const distance = departureMarker.getLatLng().distanceTo(arrivalMarker.getLatLng()) / 1000;
                            const duration = Math.round(distance * 2.5); // Approx 2.5 min per km

                            document.getElementById('routeDistance').textContent = 'Distance: ~' + distance.toFixed(1) + ' km';
                            document.getElementById('routeDuration').textContent = 'Durée: ~' + duration + ' min';
                        }
                    }
                });
            });
        </script>

        <jsp:include page="../common/footer.jsp" />