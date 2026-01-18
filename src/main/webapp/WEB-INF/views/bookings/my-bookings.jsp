<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <jsp:include page="../common/header.jsp">
                <jsp:param name="title" value="Mes réservations" />
                <jsp:param name="page" value="bookings" />
            </jsp:include>

            <main class="main-content">
                <div class="container">
                    <h1 class="mb-lg">Mes réservations</h1>

                    <!-- Tabs -->
                    <div class="flex gap-sm mb-xl" style="border-bottom: 2px solid var(--border);">
                        <button class="btn btn-ghost active" onclick="showTab('upcoming')">
                            À venir
                        </button>
                        <button class="btn btn-ghost" onclick="showTab('past')">
                            Passées
                        </button>
                        <button class="btn btn-ghost" onclick="showTab('cancelled')">
                            Annulées
                        </button>
                    </div>

                    <c:choose>
                        <c:when test="${not empty bookings}">
                            <div class="grid gap-md" id="bookingsGrid">
                                <c:forEach items="${bookings}" var="booking">
                                    <div class="card" data-status="${booking.status}">
                                        <div class="card-body">
                                            <div class="flex gap-lg" style="flex-wrap: wrap;">
                                                <!-- Route -->
                                                <div style="flex: 1; min-width: 250px;">
                                                    <div class="flex items-center gap-sm mb-md">
                                                        <span
                                                            class="badge badge-${booking.status == 'CONFIRMED' ? 'success' : booking.status == 'PENDING' ? 'warning' : 'error'}">
                                                            ${booking.status.displayName}
                                                        </span>
                                                        <span class="text-sm text-muted">
                                                            Réservé le
                                                            <fmt:parseDate value="${booking.bookedAt}"
                                                                pattern="yyyy-MM-dd'T'HH:mm" var="bookedAt"
                                                                type="both" />
                                                            <fmt:formatDate value="${bookedAt}" pattern="dd/MM/yyyy" />
                                                        </span>
                                                    </div>

                                                    <div class="ride-card-locations">
                                                        <div class="ride-card-location">
                                                            <span class="badge badge-secondary"
                                                                style="width: 24px; height: 24px; border-radius: 50%;">A</span>
                                                            <strong>${booking.ride.departureLocation}</strong>
                                                        </div>
                                                        <div class="ride-card-arrow"
                                                            style="height: 25px; margin-left: 11px;"></div>
                                                        <div class="ride-card-location">
                                                            <span class="badge badge-primary"
                                                                style="width: 24px; height: 24px; border-radius: 50%;">B</span>
                                                            <strong>${booking.ride.arrivalLocation}</strong>
                                                        </div>
                                                    </div>

                                                    <div class="flex gap-md mt-md text-sm text-muted">
                                                        <fmt:parseDate value="${booking.ride.departureTime}"
                                                            pattern="yyyy-MM-dd'T'HH:mm" var="depTime" type="both" />
                                                        <span>📅
                                                            <fmt:formatDate value="${depTime}" pattern="EEEE dd MMMM" />
                                                        </span>
                                                        <span>🕐
                                                            <fmt:formatDate value="${depTime}" pattern="HH:mm" />
                                                        </span>
                                                        <span>👤 ${booking.seatsBooked} place(s)</span>
                                                    </div>
                                                </div>

                                                <!-- Driver -->
                                                <div class="flex items-center gap-md">
                                                    <a
                                                        href="${pageContext.request.contextPath}/profile/user/${booking.ride.driver.id}">
                                                        <div class="avatar" style="cursor: pointer;">
                                                            ${booking.ride.driver.firstName.substring(0,1)}${booking.ride.driver.lastName.substring(0,1)}
                                                        </div>
                                                    </a>
                                                    <div>
                                                        <a href="${pageContext.request.contextPath}/profile/user/${booking.ride.driver.id}"
                                                            style="text-decoration: none; color: inherit;">
                                                            <strong>${booking.ride.driver.firstName}
                                                                ${booking.ride.driver.lastName}</strong>
                                                        </a>
                                                        <div class="rating">
                                                            <c:forEach begin="1" end="5" var="star">
                                                                <span
                                                                    class="rating-star ${star <= booking.ride.driver.rating ? 'filled' : ''}"
                                                                    style="font-size: 0.9rem;">★</span>
                                                            </c:forEach>
                                                        </div>
                                                        <div class="flex gap-sm mt-sm">
                                                            <a href="${pageContext.request.contextPath}/messages/${booking.ride.driver.id}"
                                                                class="btn btn-sm btn-outline" title="Contacter">
                                                                💬 Contacter
                                                            </a>
                                                            <a href="${pageContext.request.contextPath}/profile/user/${booking.ride.driver.id}"
                                                                class="btn btn-sm btn-ghost" title="Voir profil">
                                                                👤 Profil
                                                            </a>
                                                        </div>
                                                    </div>
                                                </div>

                                                <!-- Actions -->
                                                <div class="text-right">
                                                    <div class="ride-card-price" style="font-size: 1.5rem;">
                                                        ${booking.ride.pricePerSeat * booking.seatsBooked}
                                                        <span>MAD</span>
                                                    </div>
                                                    <div class="flex gap-sm mt-md">
                                                        <a href="${pageContext.request.contextPath}/rides/${booking.ride.id}"
                                                            class="btn btn-outline btn-sm">
                                                            Détails
                                                        </a>
                                                        <c:if test="${booking.status == 'COMPLETED'}">
                                                            <a href="${pageContext.request.contextPath}/reviews/create/${booking.id}"
                                                                class="btn btn-primary btn-sm">
                                                                ⭐ Noter
                                                            </a>
                                                        </c:if>
                                                        <%-- Bouton Valider pour réservations confirmées avec trajet
                                                            passé --%>
                                                            <c:if test="${booking.status == 'CONFIRMED'}">
                                                                <c:if
                                                                    test="${booking.ride.departureTime.isBefore(currentTime)}">
                                                                    <form
                                                                        action="${pageContext.request.contextPath}/bookings/complete"
                                                                        method="post" style="display: inline;">
                                                                        <input type="hidden" name="bookingId"
                                                                            value="${booking.id}">
                                                                        <button type="submit"
                                                                            class="btn btn-success btn-sm"
                                                                            onclick="return confirm('Confirmer que ce trajet est terminé?')">
                                                                            ✅ Valider trajet
                                                                        </button>
                                                                    </form>
                                                                </c:if>
                                                            </c:if>
                                                            <c:if
                                                                test="${booking.status == 'CONFIRMED' || booking.status == 'PENDING'}">
                                                                <button class="btn btn-ghost btn-sm"
                                                                    onclick="cancelBooking(${booking.id})">
                                                                    Annuler
                                                                </button>
                                                            </c:if>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="card">
                                <div class="card-body text-center" style="padding: 3rem;">
                                    <svg width="80" height="80" fill="none" stroke="var(--text-muted)"
                                        stroke-width="1.5" style="margin: 0 auto 1.5rem;">
                                        <circle cx="40" cy="40" r="35" />
                                        <path d="M25 40h30M40 25v30" />
                                    </svg>
                                    <h3>Aucune réservation</h3>
                                    <p class="text-muted">Vous n'avez pas encore de réservation.</p>
                                    <a href="${pageContext.request.contextPath}/rides/search"
                                        class="btn btn-primary mt-md">
                                        Trouver un trajet
                                    </a>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </main>

            <!-- Cancel Modal -->
            <div id="cancelModal" class="modal-backdrop hidden">
                <div class="card" style="max-width: 400px; margin: 20px;">
                    <div class="card-body">
                        <h3>Annuler la réservation</h3>
                        <form action="${pageContext.request.contextPath}/bookings/cancel" method="POST">
                            <input type="hidden" name="bookingId" id="cancelBookingId">
                            <div class="form-group">
                                <label class="form-label">Raison (optionnel)</label>
                                <textarea name="reason" class="form-textarea" rows="3"
                                    placeholder="Pourquoi annulez-vous?"></textarea>
                            </div>
                            <div class="flex gap-md">
                                <button type="button" class="btn btn-ghost" onclick="closeCancelModal()">Retour</button>
                                <button type="submit" class="btn btn-primary">Confirmer l'annulation</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <script>
                function showTab(tab) {
                    // Update tab buttons
                    document.querySelectorAll('.flex.gap-sm button').forEach(btn => {
                        btn.classList.remove('active');
                    });
                    event.target.classList.add('active');

                    // Filter bookings
                    document.querySelectorAll('#bookingsGrid .card').forEach(card => {
                        const status = card.dataset.status;
                        if (tab === 'upcoming' && (status === 'PENDING' || status === 'CONFIRMED')) {
                            card.style.display = '';
                        } else if (tab === 'past' && status === 'COMPLETED') {
                            card.style.display = '';
                        } else if (tab === 'cancelled' && (status === 'CANCELLED' || status === 'REJECTED')) {
                            card.style.display = '';
                        } else {
                            card.style.display = 'none';
                        }
                    });
                }

                function cancelBooking(bookingId) {
                    document.getElementById('cancelBookingId').value = bookingId;
                    document.getElementById('cancelModal').classList.remove('hidden');
                }

                function closeCancelModal() {
                    document.getElementById('cancelModal').classList.add('hidden');
                }

                // Initial filter
                document.addEventListener('DOMContentLoaded', () => showTab('upcoming'));
            </script>

            <style>
                .modal-backdrop {
                    position: fixed;
                    inset: 0;
                    background: rgba(0, 0, 0, 0.5);
                    backdrop-filter: blur(4px);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    z-index: 300;
                }

                .modal-backdrop.hidden {
                    display: none;
                }
            </style>

            <jsp:include page="../common/footer.jsp" />