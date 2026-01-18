<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="../common/header.jsp">
            <jsp:param name="title" value="Donner un avis" />
            <jsp:param name="page" value="reviews" />
        </jsp:include>

        <main class="main-content">
            <div class="container" style="max-width: 600px;">

                <div class="flex items-center gap-md mb-lg">
                    <a href="${pageContext.request.contextPath}/bookings/my-bookings" class="btn btn-ghost">
                        ← Retour
                    </a>
                    <h1 style="margin: 0;">Donner un avis</h1>
                </div>

                <!-- Error Alert -->
                <c:if test="${param.error == 'invalid_rating'}">
                    <div class="alert alert-error mb-lg">
                        La note doit être entre 1 et 5 étoiles.
                    </div>
                </c:if>

                <!-- User to Review -->
                <div class="card mb-lg">
                    <div class="card-body">
                        <div class="flex items-center gap-lg">
                            <div class="avatar avatar-lg" style="width: 80px; height: 80px; font-size: 2rem;">
                                ${toReview.firstName.substring(0,1)}${toReview.lastName.substring(0,1)}
                            </div>
                            <div>
                                <h2 style="margin-bottom: 0.25rem;">${toReview.firstName} ${toReview.lastName}</h2>
                                <p class="text-muted">
                                    Trajet: ${booking.ride.departureLocation} → ${booking.ride.arrivalLocation}
                                </p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Review Form -->
                <div class="card">
                    <div class="card-header">
                        <h3 style="margin: 0;">⭐ Votre évaluation</h3>
                    </div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/reviews/create" method="POST">
                            <input type="hidden" name="bookingId" value="${booking.id}">

                            <!-- Star Rating -->
                            <div class="form-group">
                                <label class="form-label">Note *</label>
                                <div class="star-rating" id="starRating"
                                    style="display: flex; gap: 0.5rem; font-size: 2.5rem; cursor: pointer;">
                                    <span data-rating="1">☆</span>
                                    <span data-rating="2">☆</span>
                                    <span data-rating="3">☆</span>
                                    <span data-rating="4">☆</span>
                                    <span data-rating="5">☆</span>
                                </div>
                                <input type="hidden" name="rating" id="ratingInput" required>
                                <p class="form-hint" id="ratingHint">Cliquez sur les étoiles pour noter</p>
                            </div>

                            <!-- Comment -->
                            <div class="form-group">
                                <label class="form-label">Commentaire (optionnel)</label>
                                <textarea name="comment" class="form-textarea" rows="4"
                                    placeholder="Partagez votre expérience..."></textarea>
                            </div>

                            <!-- Submit -->
                            <div class="flex justify-between mt-lg">
                                <a href="${pageContext.request.contextPath}/bookings/my-bookings" class="btn btn-ghost">
                                    Annuler
                                </a>
                                <button type="submit" class="btn btn-primary btn-lg" id="submitBtn" disabled>
                                    ⭐ Publier l'avis
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>

        <script>
            const starRating = document.getElementById('starRating');
            const ratingInput = document.getElementById('ratingInput');
            const ratingHint = document.getElementById('ratingHint');
            const submitBtn = document.getElementById('submitBtn');
            const stars = starRating.querySelectorAll('span');

            const hints = {
                1: '😟 Très mauvais',
                2: '😕 Mauvais',
                3: '😐 Moyen',
                4: '😊 Bien',
                5: '🤩 Excellent !'
            };

            let selectedRating = 0;

            stars.forEach(star => {
                star.addEventListener('click', () => {
                    selectedRating = parseInt(star.dataset.rating);
                    ratingInput.value = selectedRating;
                    updateStars();
                    ratingHint.textContent = hints[selectedRating];
                    submitBtn.disabled = false;
                });

                star.addEventListener('mouseenter', () => {
                    const hoverRating = parseInt(star.dataset.rating);
                    highlightStars(hoverRating);
                });
            });

            starRating.addEventListener('mouseleave', () => {
                updateStars();
            });

            function updateStars() {
                stars.forEach(star => {
                    const starRating = parseInt(star.dataset.rating);
                    star.textContent = starRating <= selectedRating ? '★' : '☆';
                    star.style.color = starRating <= selectedRating ? '#ffc107' : '#ccc';
                });
            }

            function highlightStars(rating) {
                stars.forEach(star => {
                    const starRating = parseInt(star.dataset.rating);
                    star.textContent = starRating <= rating ? '★' : '☆';
                    star.style.color = starRating <= rating ? '#ffc107' : '#ccc';
                });
            }
        </script>

        <jsp:include page="../common/footer.jsp" />