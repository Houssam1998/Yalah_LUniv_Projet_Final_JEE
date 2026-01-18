<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="../common/header.jsp">
            <jsp:param name="title" value="Mon Profil" />
            <jsp:param name="page" value="profile" />
        </jsp:include>

        <main class="main-content">
            <div class="container container-lg">

                <!-- Success/Error Messages -->
                <c:if test="${param.success == 'profile_updated'}">
                    <div class="alert alert-success mb-lg">
                        <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
                            <path fill-rule="evenodd"
                                d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" />
                        </svg>
                        <span>Profil mis à jour avec succès!</span>
                    </div>
                </c:if>

                <div class="grid gap-lg" style="grid-template-columns: 300px 1fr;">

                    <!-- Profile Card -->
                    <div>
                        <div class="card">
                            <div class="card-body" style="text-align: center;">
                                <!-- Avatar -->
                                <div class="avatar avatar-xl"
                                    style="margin: 0 auto 1rem; width: 120px; height: 120px; font-size: 2.5rem;">
                                    ${user.firstName.substring(0,1)}${user.lastName.substring(0,1)}
                                </div>

                                <h2 style="margin-bottom: 0.5rem;">${user.firstName} ${user.lastName}</h2>

                                <c:if test="${user.verified}">
                                    <span class="badge badge-success mb-sm">
                                        <svg width="14" height="14" fill="currentColor" viewBox="0 0 20 20"
                                            style="margin-right: 4px;">
                                            <path fill-rule="evenodd"
                                                d="M6.267 3.455a3.066 3.066 0 001.745-.723 3.066 3.066 0 013.976 0 3.066 3.066 0 001.745.723 3.066 3.066 0 012.812 2.812c.051.643.304 1.254.723 1.745a3.066 3.066 0 010 3.976 3.066 3.066 0 00-.723 1.745 3.066 3.066 0 01-2.812 2.812 3.066 3.066 0 00-1.745.723 3.066 3.066 0 01-3.976 0 3.066 3.066 0 00-1.745-.723 3.066 3.066 0 01-2.812-2.812 3.066 3.066 0 00-.723-1.745 3.066 3.066 0 010-3.976 3.066 3.066 0 00.723-1.745 3.066 3.066 0 012.812-2.812zm7.44 5.252a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" />
                                        </svg>
                                        Vérifié UEMF
                                    </span>
                                </c:if>

                                <!-- Rating -->
                                <div style="margin: 1rem 0;">
                                    <c:choose>
                                        <c:when test="${user.rating != null && user.rating > 0}">
                                            <div
                                                style="display: flex; align-items: center; justify-content: center; gap: 0.5rem;">
                                                <span style="font-size: 1.5rem; color: #ffc107;">★</span>
                                                <span
                                                    style="font-size: 1.25rem; font-weight: 600;">${user.rating}</span>
                                                <span class="text-muted">(${user.ratingCount} avis)</span>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">Pas encore d'avis</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <!-- Contact Info -->
                                <div
                                    style="text-align: left; margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid var(--gray-200);">
                                    <p class="text-sm mb-sm">
                                        <strong>📧 Email:</strong><br />
                                        ${user.email}
                                    </p>
                                    <c:if test="${not empty user.phone}">
                                        <p class="text-sm mb-sm">
                                            <strong>📱 Téléphone:</strong><br />
                                            ${user.phone}
                                        </p>
                                    </c:if>
                                    <p class="text-sm">
                                        <strong>📅 Membre depuis:</strong><br />
                                        <c:if test="${user.createdAt != null}">
                                            ${user.createdAt.month.toString().substring(0,3)} ${user.createdAt.year}
                                        </c:if>
                                    </p>
                                </div>

                                <!-- Edit Button -->
                                <c:if test="${isOwnProfile}">
                                    <a href="${pageContext.request.contextPath}/profile/edit" class="btn btn-primary"
                                        style="width: 100%; margin-top: 1rem;">
                                        ✏️ Modifier mon profil
                                    </a>
                                </c:if>
                            </div>
                        </div>

                        <!-- Preferences Card -->
                        <div class="card mt-lg">
                            <div class="card-header">
                                <h3 style="margin: 0;">⚙️ Préférences</h3>
                            </div>
                            <div class="card-body">
                                <div class="flex items-center gap-sm mb-sm">
                                    <span style="font-size: 1.25rem;">${user.prefMusic ? '🎵' : '🔇'}</span>
                                    <span>${user.prefMusic ? 'Musique autorisée' : 'Préfère le silence'}</span>
                                </div>
                                <div class="flex items-center gap-sm mb-sm">
                                    <span style="font-size: 1.25rem;">${user.prefTalking ? '💬' : '🤫'}</span>
                                    <span>${user.prefTalking ? 'Aime discuter' : 'Préfère le calme'}</span>
                                </div>
                                <div class="flex items-center gap-sm">
                                    <span style="font-size: 1.25rem;">${user.prefSmoking ? '🚬' : '🚭'}</span>
                                    <span>${user.prefSmoking ? 'Fumeur' : 'Non-fumeur'}</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Main Content -->
                    <div>
                        <!-- Bio -->
                        <div class="card mb-lg">
                            <div class="card-header">
                                <h3 style="margin: 0;">👋 À propos</h3>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${not empty user.bio}">
                                        <p>${user.bio}</p>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted">
                                            <c:choose>
                                                <c:when test="${isOwnProfile}">
                                                    Vous n'avez pas encore ajouté de bio.
                                                    <a href="${pageContext.request.contextPath}/profile/edit">Ajouter
                                                        une bio</a>
                                                </c:when>
                                                <c:otherwise>
                                                    Cet utilisateur n'a pas encore ajouté de bio.
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <!-- Vehicles -->
                        <div class="card mb-lg">
                            <div class="card-header flex justify-between items-center">
                                <h3 style="margin: 0;">🚗 Véhicules (${vehicles.size()})</h3>
                                <c:if test="${isOwnProfile}">
                                    <a href="${pageContext.request.contextPath}/profile/vehicles"
                                        class="btn btn-sm btn-outline">
                                        Gérer
                                    </a>
                                </c:if>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${not empty vehicles}">
                                        <div class="grid gap-md"
                                            style="grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));">
                                            <c:forEach items="${vehicles}" var="vehicle">
                                                <div
                                                    style="padding: 1rem; background: var(--gray-100); border-radius: 8px;">
                                                    <div style="font-weight: 600; margin-bottom: 0.25rem;">
                                                        ${vehicle.brand} ${vehicle.model}
                                                    </div>
                                                    <div class="text-sm text-muted">
                                                        ${vehicle.color} • ${vehicle.seats} places
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted">
                                            <c:choose>
                                                <c:when test="${isOwnProfile}">
                                                    Vous n'avez pas encore ajouté de véhicule.
                                                    <a href="${pageContext.request.contextPath}/profile/vehicles">Ajouter
                                                        un véhicule</a>
                                                </c:when>
                                                <c:otherwise>
                                                    Aucun véhicule enregistré.
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <!-- Stats -->
                        <div class="card mb-lg">
                            <div class="card-header">
                                <h3 style="margin: 0;">📊 Statistiques</h3>
                            </div>
                            <div class="card-body">
                                <div class="grid grid-3 gap-lg text-center">
                                    <div>
                                        <div style="font-size: 2rem; font-weight: 700; color: var(--primary);">
                                            ${vehicles.size()}
                                        </div>
                                        <div class="text-muted">Véhicules</div>
                                    </div>
                                    <div>
                                        <div style="font-size: 2rem; font-weight: 700; color: var(--secondary);">
                                            ${user.ratingCount != null ? user.ratingCount : 0}
                                        </div>
                                        <div class="text-muted">Avis reçus</div>
                                    </div>
                                    <div>
                                        <div style="font-size: 2rem; font-weight: 700; color: var(--success);">
                                            ${user.verified ? 'Oui' : 'Non'}
                                        </div>
                                        <div class="text-muted">Vérifié</div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Reviews 
                <div class="card">
                    <div class="card-header">
                        <h3 style="margin: 0;">⭐ Avis reçus</h3>
                    </div>
                    <div class="card-body">
                        <p class="text-muted">Les avis seront affichés ici.</p>
                    </div>
                </div>
                -->
                    </div>
                </div>
            </div>
        </main>

        <jsp:include page="../common/footer.jsp" />