<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="../common/header.jsp">
            <jsp:param name="title" value="Mes véhicules" />
            <jsp:param name="page" value="profile" />
        </jsp:include>

        <main class="main-content">
            <div class="container" style="max-width: 900px;">

                <div class="flex items-center gap-md mb-lg">
                    <a href="${pageContext.request.contextPath}/profile" class="btn btn-ghost">
                        ← Retour au profil
                    </a>
                    <h1 style="margin: 0;">Mes véhicules</h1>
                </div>

                <!-- Success/Error Messages -->
                <c:if test="${param.success == 'vehicle_added'}">
                    <div class="alert alert-success mb-lg">
                        <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
                            <path fill-rule="evenodd"
                                d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" />
                        </svg>
                        <span>Véhicule ajouté avec succès!</span>
                    </div>
                </c:if>
                <c:if test="${param.success == 'vehicle_deleted'}">
                    <div class="alert alert-success mb-lg">
                        <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
                            <path fill-rule="evenodd"
                                d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" />
                        </svg>
                        <span>Véhicule supprimé!</span>
                    </div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-error mb-lg">
                        <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
                            <path fill-rule="evenodd"
                                d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" />
                        </svg>
                        <span>${error}</span>
                    </div>
                </c:if>

                <div class="grid gap-lg" style="grid-template-columns: 1fr 1fr;">

                    <!-- Vehicle List -->
                    <div>
                        <div class="card">
                            <div class="card-header">
                                <h3 style="margin: 0;">🚗 Mes véhicules (${vehicles.size()})</h3>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${not empty vehicles}">
                                        <c:forEach items="${vehicles}" var="vehicle">
                                            <div class="vehicle-item"
                                                style="display: flex; justify-content: space-between; align-items: center; padding: 1rem; background: var(--gray-100); border-radius: 8px; margin-bottom: 0.75rem;">
                                                <div>
                                                    <div style="font-weight: 600; font-size: 1.1rem;">
                                                        ${vehicle.brand} ${vehicle.model}
                                                    </div>
                                                    <div class="text-sm text-muted">
                                                        🎨 ${vehicle.color} •
                                                        💺 ${vehicle.seats} places •
                                                        🔢 ${vehicle.licensePlate}
                                                    </div>
                                                </div>
                                                <form
                                                    action="${pageContext.request.contextPath}/profile/vehicles/delete"
                                                    method="POST"
                                                    onsubmit="return confirm('Êtes-vous sûr de vouloir supprimer ce véhicule?');">
                                                    <input type="hidden" name="vehicleId" value="${vehicle.id}">
                                                    <button type="submit" class="btn btn-ghost btn-sm"
                                                        style="color: var(--danger);">
                                                        🗑️
                                                    </button>
                                                </form>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center" style="padding: 2rem;">
                                            <div style="font-size: 3rem; margin-bottom: 1rem;">🚗</div>
                                            <p class="text-muted">Vous n'avez pas encore ajouté de véhicule</p>
                                            <p class="text-sm text-muted">Ajoutez un véhicule pour proposer des trajets
                                            </p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>

                    <!-- Add Vehicle Form -->
                    <div>
                        <div class="card">
                            <div class="card-header">
                                <h3 style="margin: 0;">➕ Ajouter un véhicule</h3>
                            </div>
                            <div class="card-body">
                                <form action="${pageContext.request.contextPath}/profile/vehicles/add" method="POST">

                                    <div class="form-group">
                                        <label class="form-label">Marque *</label>
                                        <input type="text" name="brand" class="form-input"
                                            placeholder="Ex: Dacia, Renault, Toyota..." required>
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label">Modèle *</label>
                                        <input type="text" name="model" class="form-input"
                                            placeholder="Ex: Logan, Clio, Yaris..." required>
                                    </div>

                                    <div class="grid grid-2 gap-md">
                                        <div class="form-group">
                                            <label class="form-label">Couleur *</label>
                                            <input type="text" name="color" class="form-input"
                                                placeholder="Ex: Blanc, Noir..." required>
                                        </div>

                                        <div class="form-group">
                                            <label class="form-label">Places *</label>
                                            <select name="seats" class="form-select" required>
                                                <option value="2">2 places</option>
                                                <option value="3">3 places</option>
                                                <option value="4" selected>4 places</option>
                                                <option value="5">5 places</option>
                                                <option value="6">6 places</option>
                                                <option value="7">7 places</option>
                                            </select>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label">Plaque d'immatriculation *</label>
                                        <input type="text" name="licensePlate" class="form-input"
                                            placeholder="Ex: 12345-A-10" required>
                                    </div>

                                    <button type="submit" class="btn btn-primary" style="width: 100%;">
                                        ➕ Ajouter ce véhicule
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>

        <jsp:include page="../common/footer.jsp" />