<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="../common/header.jsp">
            <jsp:param name="title" value="Inscription" />
            <jsp:param name="page" value="register" />
        </jsp:include>

        <main class="main-content flex items-center justify-center">
            <div class="container container-sm">
                <div class="card animate-slide-up">
                    <div class="card-body" style="padding: 2.5rem;">
                        <!-- Logo -->
                        <div class="text-center mb-xl">
                            <img src="${pageContext.request.contextPath}/assets/images/uemf-logo.webp" alt="UEMF Logo"
                                style="height: 60px; margin-bottom: 1rem;">
                            <h2>Créer un compte</h2>
                            <p class="text-muted">Rejoignez la communauté Yalah L'Univ</p>
                        </div>

                        <!-- Error Alert -->
                        <c:if test="${not empty error}">
                            <div class="alert alert-error">
                                <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
                                    <path fill-rule="evenodd"
                                        d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" />
                                </svg>
                                <span>${error}</span>
                            </div>
                        </c:if>

                        <!-- Form -->
                        <form action="${pageContext.request.contextPath}/auth/register" method="POST">
                            <div class="grid grid-2 gap-md">
                                <div class="form-group">
                                    <label class="form-label" for="firstName">Prénom</label>
                                    <input type="text" id="firstName" name="firstName" class="form-input"
                                        placeholder="Ahmed" value="${firstName}" required>
                                </div>

                                <div class="form-group">
                                    <label class="form-label" for="lastName">Nom</label>
                                    <input type="text" id="lastName" name="lastName" class="form-input"
                                        placeholder="Benani" value="${lastName}" required>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="email">Adresse email</label>
                                <input type="email" id="email" name="email" class="form-input"
                                    placeholder="ahmed.benani@ueuromed.org" value="${email}" required>
                                <p class="form-hint">
                                    <span class="badge badge-verified" style="font-size: 0.7rem;">✓ Vérifié</span>
                                    Les emails @ueuromed.org sont automatiquement vérifiés
                                </p>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="role">Je suis</label>
                                <select id="role" name="role" class="form-select" required>
                                    <c:forEach items="${roles}" var="roleOption">
                                        <c:if test="${roleOption != 'ADMIN'}">
                                            <option value="${roleOption}">${roleOption.displayName}</option>
                                        </c:if>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="password">Mot de passe</label>
                                <input type="password" id="password" name="password" class="form-input"
                                    placeholder="••••••••" required minlength="8">
                                <p class="form-hint">Minimum 8 caractères avec majuscule, minuscule et chiffre</p>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="confirmPassword">Confirmer le mot de passe</label>
                                <input type="password" id="confirmPassword" name="confirmPassword" class="form-input"
                                    placeholder="••••••••" required>
                            </div>

                            <div class="form-group">
                                <label class="form-check">
                                    <input type="checkbox" name="terms" required>
                                    <span class="text-sm">
                                        J'accepte les <a href="${pageContext.request.contextPath}/terms">conditions
                                            d'utilisation</a>
                                        et la <a href="${pageContext.request.contextPath}/privacy">politique de
                                            confidentialité</a>
                                    </span>
                                </label>
                            </div>

                            <button type="submit" class="btn btn-secondary btn-lg" style="width: 100%;">
                                Créer mon compte
                            </button>
                        </form>

                        <div class="text-center mt-lg">
                            <p class="text-muted">
                                Déjà inscrit?
                                <a href="${pageContext.request.contextPath}/auth/login">Connectez-vous</a>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </main>

        <jsp:include page="../common/footer.jsp" />