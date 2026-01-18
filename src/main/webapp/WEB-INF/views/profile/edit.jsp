<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="../common/header.jsp">
            <jsp:param name="title" value="Modifier mon profil" />
            <jsp:param name="page" value="profile" />
        </jsp:include>

        <main class="main-content">
            <div class="container" style="max-width: 700px;">

                <div class="flex items-center gap-md mb-lg">
                    <a href="${pageContext.request.contextPath}/profile" class="btn btn-ghost">
                        ← Retour
                    </a>
                    <h1 style="margin: 0;">Modifier mon profil</h1>
                </div>

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

                <form action="${pageContext.request.contextPath}/profile/edit" method="POST">

                    <!-- Personal Info -->
                    <div class="card mb-lg">
                        <div class="card-header">
                            <h3 style="margin: 0;">👤 Informations personnelles</h3>
                        </div>
                        <div class="card-body">
                            <div class="grid grid-2 gap-md">
                                <div class="form-group">
                                    <label class="form-label">Prénom *</label>
                                    <input type="text" name="firstName" class="form-input" value="${user.firstName}"
                                        required>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Nom *</label>
                                    <input type="text" name="lastName" class="form-input" value="${user.lastName}"
                                        required>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Email</label>
                                <input type="email" class="form-input" value="${user.email}" disabled>
                                <p class="form-hint">L'email ne peut pas être modifié</p>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Téléphone</label>
                                <input type="tel" name="phone" class="form-input" value="${user.phone}"
                                    placeholder="+212 6XX XXX XXX">
                            </div>

                            <div class="form-group">
                                <label class="form-label">Bio</label>
                                <textarea name="bio" class="form-textarea" rows="4"
                                    placeholder="Présentez-vous en quelques mots...">${user.bio}</textarea>
                                <p class="form-hint">Cette description sera visible par les autres utilisateurs</p>
                            </div>
                        </div>
                    </div>

                    <!-- Preferences -->
                    <div class="card mb-lg">
                        <div class="card-header">
                            <h3 style="margin: 0;">⚙️ Préférences de trajet</h3>
                        </div>
                        <div class="card-body">
                            <p class="text-muted mb-md">Ces préférences aident les passagers à mieux vous connaître</p>

                            <div class="grid grid-2 gap-md">
                                <label class="form-check">
                                    <input type="checkbox" name="prefMusic" ${user.prefMusic ? 'checked' : '' }>
                                    <span>🎵 J'aime écouter de la musique</span>
                                </label>

                                <label class="form-check">
                                    <input type="checkbox" name="prefTalking" ${user.prefTalking ? 'checked' : '' }>
                                    <span>💬 J'aime discuter pendant le trajet</span>
                                </label>

                                <label class="form-check">
                                    <input type="checkbox" name="prefSmoking" ${user.prefSmoking ? 'checked' : '' }>
                                    <span>🚬 Fumeur</span>
                                </label>


                            </div>
                        </div>
                    </div>

                    <!-- Submit -->
                    <div class="flex justify-between">
                        <a href="${pageContext.request.contextPath}/profile" class="btn btn-ghost">
                            Annuler
                        </a>
                        <button type="submit" class="btn btn-primary btn-lg">
                            💾 Enregistrer les modifications
                        </button>
                    </div>
                </form>
            </div>
        </main>

        <jsp:include page="../common/footer.jsp" />