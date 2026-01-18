<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <jsp:include page="../common/header.jsp">
                <jsp:param name="pageTitle" value="Mes trajets" />
            </jsp:include>

            <main class="container" style="padding: 2rem 1rem;">
                <%-- Messages de succès/erreur --%>
                    <c:if test="${param.error != null}">
                        <div class="alert alert-danger" style="margin-bottom: 1rem;">
                            ⚠️ Erreur: ${param.error}
                        </div>
                    </c:if>
                    <c:if test="${param.completed == 'true'}">
                        <div class="alert alert-success" style="margin-bottom: 1rem;">
                            ✅ Trajet marqué comme terminé avec succès!
                        </div>
                    </c:if>
                    <c:if test="${param.cancelled == 'true'}">
                        <div class="alert alert-success" style="margin-bottom: 1rem;">
                            ✅ Trajet annulé avec succès!
                        </div>
                    </c:if>

                    <div
                        style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
                        <h1 style="margin: 0;">🚗 Mes trajets</h1>
                        <a href="${pageContext.request.contextPath}/rides/create" class="btn btn-primary">
                            <span>➕</span> Proposer un trajet
                        </a>
                    </div>

                    <!-- Réservations en attente -->
                    <c:if test="${not empty pendingBookings}">
                        <div class="card" style="margin-bottom: 2rem; border-left: 4px solid var(--color-warning);">
                            <div class="card-body">
                                <h3 style="color: var(--color-warning);">⏳ Réservations en attente
                                    (${pendingBookings.size()})</h3>
                                <div class="rides-grid">
                                    <c:forEach items="${pendingBookings}" var="booking">
                                        <div class="card" style="padding: 1rem;">
                                            <div
                                                style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.75rem;">
                                                <a
                                                    href="${pageContext.request.contextPath}/profile/user/${booking.passenger.id}">
                                                    <div class="avatar avatar-sm" style="cursor: pointer;">
                                                        ${booking.passenger.firstName.substring(0,1)}${booking.passenger.lastName.substring(0,1)}
                                                    </div>
                                                </a>
                                                <div>
                                                    <a href="${pageContext.request.contextPath}/profile/user/${booking.passenger.id}"
                                                        style="text-decoration: none; color: inherit;">
                                                        <strong>${booking.passenger.fullName}</strong>
                                                    </a>
                                                    <span class="text-muted"> souhaite réserver ${booking.seatsBooked}
                                                        place(s)</span>
                                                </div>
                                            </div>
                                            <p style="color: var(--text-secondary); font-size: 0.9rem;">
                                                📍 ${booking.ride.departureLocation} → ${booking.ride.arrivalLocation}
                                            </p>
                                            <div style="display: flex; gap: 0.5rem; margin-top: 1rem; flex-wrap: wrap;">
                                                <form action="${pageContext.request.contextPath}/bookings/confirm"
                                                    method="post" style="display: inline;">
                                                    <input type="hidden" name="bookingId" value="${booking.id}">
                                                    <button type="submit" class="btn btn-success btn-sm">✓
                                                        Confirmer</button>
                                                </form>
                                                <form action="${pageContext.request.contextPath}/bookings/reject"
                                                    method="post" style="display: inline;">
                                                    <input type="hidden" name="bookingId" value="${booking.id}">
                                                    <button type="submit" class="btn btn-danger btn-sm">✗
                                                        Refuser</button>
                                                </form>
                                                <a href="${pageContext.request.contextPath}/messages/${booking.passenger.id}"
                                                    class="btn btn-outline btn-sm">💬 Contacter</a>
                                                <a href="${pageContext.request.contextPath}/profile/user/${booking.passenger.id}"
                                                    class="btn btn-ghost btn-sm">👤 Profil</a>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <!-- Liste des trajets -->
                    <c:choose>
                        <c:when test="${empty rides}">
                            <div class="empty-state" style="text-align: center; padding: 4rem 2rem;">
                                <div style="font-size: 4rem; margin-bottom: 1rem;">🚗</div>
                                <h3>Aucun trajet programmé</h3>
                                <p style="color: var(--text-secondary);">Proposez votre premier trajet et commencez à
                                    partager vos trajets!</p>
                                <a href="${pageContext.request.contextPath}/rides/create" class="btn btn-primary"
                                    style="margin-top: 1rem;">
                                    Proposer un trajet
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="rides-grid"
                                style="display: grid; grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); gap: 1.5rem;">
                                <c:forEach items="${rides}" var="ride">
                                    <div class="card ride-card">
                                        <div class="card-body">
                                            <div
                                                style="display: flex; justify-content: space-between; align-items: start;">
                                                <div>
                                                    <span
                                                        class="badge ${ride.status == 'SCHEDULED' ? 'badge-success' : ride.status == 'COMPLETED' ? 'badge-secondary' : 'badge-warning'}">
                                                        ${ride.status.displayName}
                                                    </span>
                                                </div>
                                                <span
                                                    style="font-weight: 700; color: var(--color-primary);">${ride.pricePerSeat}
                                                    MAD</span>
                                            </div>

                                            <div style="margin: 1rem 0;">
                                                <div style="display: flex; align-items: center; gap: 0.5rem;">
                                                    <span style="color: var(--color-secondary);">📍</span>
                                                    <strong>${ride.departureLocation}</strong>
                                                </div>
                                                <div
                                                    style="border-left: 2px dashed var(--color-secondary); height: 20px; margin-left: 0.6rem;">
                                                </div>
                                                <div style="display: flex; align-items: center; gap: 0.5rem;">
                                                    <span style="color: var(--color-secondary);">🎯</span>
                                                    <strong>${ride.arrivalLocation}</strong>
                                                </div>
                                            </div>

                                            <div
                                                style="display: flex; gap: 1rem; color: var(--text-secondary); font-size: 0.9rem;">
                                                <span>📅
                                                    <fmt:parseDate value="${ride.departureTime}"
                                                        pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                                    <fmt:formatDate value="${parsedDate}" pattern="dd MMM" />
                                                </span>
                                                <span>🕐
                                                    <fmt:formatDate value="${parsedDate}" pattern="HH:mm" />
                                                </span>
                                                <span>👥 ${ride.availableSeats}/${ride.totalSeats} places</span>
                                            </div>

                                            <div style="display: flex; gap: 0.5rem; margin-top: 1rem; flex-wrap: wrap;">
                                                <a href="${pageContext.request.contextPath}/rides/${ride.id}"
                                                    class="btn btn-outline btn-sm" style="flex: 1;">
                                                    Voir détails
                                                </a>
                                                <%-- Pour les trajets SCHEDULED: Valider (si passé) et Annuler --%>
                                                    <c:if test="${ride.status == 'SCHEDULED'}">
                                                        <%-- Bouton Valider seulement si l'heure de départ est passée
                                                            --%>
                                                            <c:if test="${ride.departureTime.isBefore(currentTime)}">
                                                                <form
                                                                    action="${pageContext.request.contextPath}/rides/complete"
                                                                    method="post" style="flex: 1;">
                                                                    <input type="hidden" name="rideId"
                                                                        value="${ride.id}">
                                                                    <button type="submit" class="btn btn-success btn-sm"
                                                                        style="width: 100%;"
                                                                        onclick="return confirm('Confirmer que ce trajet est terminé?')">
                                                                        ✅ Valider trajet
                                                                    </button>
                                                                </form>
                                                            </c:if>
                                                            <form
                                                                action="${pageContext.request.contextPath}/rides/cancel"
                                                                method="post" style="flex: 1;">
                                                                <input type="hidden" name="rideId" value="${ride.id}">
                                                                <button type="submit" class="btn btn-danger btn-sm"
                                                                    style="width: 100%;"
                                                                    onclick="return confirm('Annuler ce trajet?')">
                                                                    ❌ Annuler
                                                                </button>
                                                            </form>
                                                    </c:if>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
            </main>

            <jsp:include page="../common/footer.jsp" />