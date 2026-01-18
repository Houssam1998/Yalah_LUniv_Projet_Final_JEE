<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <jsp:include page="../common/header.jsp">
                <jsp:param name="pageTitle" value="Détails réservation" />
            </jsp:include>

            <main class="container" style="padding: 2rem 1rem;">
                <c:if test="${not empty booking}">
                    <div style="margin-bottom: 1rem;">
                        <a href="${pageContext.request.contextPath}/bookings/my-bookings"
                            style="color: var(--color-primary);">
                            ← Retour à mes réservations
                        </a>
                    </div>

                    <div class="card" style="max-width: 700px; margin: 0 auto;">
                        <div class="card-body">
                            <div
                                style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
                                <h2 style="margin: 0;">Réservation #${booking.id}</h2>
                                <span
                                    class="badge ${booking.status == 'CONFIRMED' ? 'badge-success' : booking.status == 'PENDING' ? 'badge-warning' : 'badge-secondary'}">
                                    ${booking.status.displayName}
                                </span>
                            </div>

                            <!-- Trajet -->
                            <div
                                style="background: var(--bg-secondary); padding: 1rem; border-radius: 8px; margin-bottom: 1.5rem;">
                                <h4 style="margin-bottom: 0.5rem;">📍 Trajet</h4>
                                <p style="margin: 0;"><strong>${booking.ride.departureLocation}</strong></p>
                                <p style="margin: 0; color: var(--text-secondary);">↓</p>
                                <p style="margin: 0;"><strong>${booking.ride.arrivalLocation}</strong></p>

                                <div style="display: flex; gap: 2rem; margin-top: 1rem; color: var(--text-secondary);">
                                    <fmt:parseDate value="${booking.ride.departureTime}" pattern="yyyy-MM-dd'T'HH:mm"
                                        var="departureDate" type="both" />
                                    <span>📅
                                        <fmt:formatDate value="${departureDate}" pattern="dd MMMM yyyy" />
                                    </span>
                                    <span>🕐
                                        <fmt:formatDate value="${departureDate}" pattern="HH:mm" />
                                    </span>
                                </div>
                            </div>

                            <!-- Détails réservation -->
                            <div
                                style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1.5rem;">
                                <div>
                                    <p style="color: var(--text-secondary); margin-bottom: 0.25rem;">Places réservées
                                    </p>
                                    <p style="font-size: 1.25rem; font-weight: 600;">${booking.seatsBooked} place(s)</p>
                                </div>
                                <div>
                                    <p style="color: var(--text-secondary); margin-bottom: 0.25rem;">Prix total</p>
                                    <p style="font-size: 1.25rem; font-weight: 600; color: var(--color-primary);">
                                        ${booking.seatsBooked * booking.ride.pricePerSeat} MAD
                                    </p>
                                </div>
                                <div>
                                    <p style="color: var(--text-secondary); margin-bottom: 0.25rem;">Réservé le</p>
                                    <fmt:parseDate value="${booking.bookedAt}" pattern="yyyy-MM-dd'T'HH:mm"
                                        var="bookedDate" type="both" />
                                    <p>
                                        <fmt:formatDate value="${bookedDate}" pattern="dd/MM/yyyy à HH:mm" />
                                    </p>
                                </div>
                                <c:if test="${not empty booking.confirmedAt}">
                                    <div>
                                        <p style="color: var(--text-secondary); margin-bottom: 0.25rem;">Confirmé le</p>
                                        <fmt:parseDate value="${booking.confirmedAt}" pattern="yyyy-MM-dd'T'HH:mm"
                                            var="confirmedDate" type="both" />
                                        <p>
                                            <fmt:formatDate value="${confirmedDate}" pattern="dd/MM/yyyy à HH:mm" />
                                        </p>
                                    </div>
                                </c:if>
                            </div>

                            <!-- Conducteur -->
                            <div style="border-top: 1px solid var(--border-color); padding-top: 1.5rem;">
                                <h4 style="margin-bottom: 1rem;">🚗 Conducteur</h4>
                                <div style="display: flex; align-items: center; gap: 1rem;">
                                    <div
                                        style="width: 50px; height: 50px; background: var(--color-primary); border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; font-weight: 600;">
                                        ${booking.ride.driver.firstName.charAt(0)}${booking.ride.driver.lastName.charAt(0)}
                                    </div>
                                    <div>
                                        <p style="font-weight: 600; margin: 0;">${booking.ride.driver.fullName}</p>
                                        <p style="color: var(--text-secondary); margin: 0;">
                                            ⭐ ${booking.ride.driver.rating}/5 (${booking.ride.driver.ratingCount} avis)
                                        </p>
                                    </div>
                                </div>
                            </div>

                            <!-- Message -->
                            <c:if test="${not empty booking.message}">
                                <div
                                    style="border-top: 1px solid var(--border-color); padding-top: 1.5rem; margin-top: 1.5rem;">
                                    <h4 style="margin-bottom: 0.5rem;">💬 Votre message</h4>
                                    <p style="background: var(--bg-secondary); padding: 1rem; border-radius: 8px;">
                                        ${booking.message}</p>
                                </div>
                            </c:if>

                            <!-- Actions -->
                            <c:if test="${booking.status == 'PENDING' || booking.status == 'CONFIRMED'}">
                                <div
                                    style="border-top: 1px solid var(--border-color); padding-top: 1.5rem; margin-top: 1.5rem;">
                                    <form action="${pageContext.request.contextPath}/bookings/cancel" method="post"
                                        onsubmit="return confirm('Êtes-vous sûr de vouloir annuler cette réservation?')">
                                        <input type="hidden" name="bookingId" value="${booking.id}">
                                        <button type="submit" class="btn btn-danger" style="width: 100%;">
                                            Annuler la réservation
                                        </button>
                                    </form>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </c:if>
            </main>

            <jsp:include page="../common/footer.jsp" />