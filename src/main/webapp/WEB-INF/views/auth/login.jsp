<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="../common/header.jsp">
            <jsp:param name="title" value="Connexion" />
            <jsp:param name="page" value="login" />
        </jsp:include>

        <main class="main-content flex items-center justify-center">
            <div class="container container-sm">
                <div class="card animate-slide-up">
                    <div class="card-body" style="padding: 2.5rem;">
                        <!-- Logo -->
                        <div class="text-center mb-xl">
                            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="UEMF Logo"
                                style="height: 60px; margin-bottom: 1rem;">
                            <h2>Connexion</h2>
                            <p class="text-muted">Accédez à votre compte Yalah L'Univ</p>
                        </div>

                        <!-- Alerts -->
                        <c:if test="${param.logout == 'true'}">
                            <div class="alert alert-success">
                                <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
                                    <path fill-rule="evenodd"
                                        d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" />
                                </svg>
                                <span>Vous avez été déconnecté avec succès.</span>
                            </div>
                        </c:if>

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
                        <form action="${pageContext.request.contextPath}/auth/login" method="POST">
                            <div class="form-group">
                                <label class="form-label" for="email">Adresse email</label>
                                <div class="form-input-icon">
                                    <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M16 4H4a2 2 0 00-2 2v8a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2z" />
                                        <path d="M2 6l8 5 8-5" />
                                    </svg>
                                    <input type="email" id="email" name="email" class="form-input"
                                        placeholder="votre.nom@ueuromed.org" value="${email}" required autofocus>
                                </div>
                                <p class="form-hint">Utilisez votre email UEMF pour un accès vérifié</p>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="password">Mot de passe</label>
                                <div class="form-input-icon">
                                    <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                                        <rect x="3" y="8" width="14" height="10" rx="2" />
                                        <path d="M7 8V5a3 3 0 016 0v3" />
                                    </svg>
                                    <input type="password" id="password" name="password" class="form-input"
                                        placeholder="••••••••" required>
                                </div>
                            </div>

                            <div class="flex justify-between items-center mb-lg">
                                <label class="form-check">
                                    <input type="checkbox" name="remember">
                                    <span class="text-sm">Se souvenir de moi</span>
                                </label>
                                <a href="${pageContext.request.contextPath}/auth/forgot-password" class="text-sm">
                                    Mot de passe oublié?
                                </a>
                            </div>

                            <button type="submit" class="btn btn-primary btn-lg" style="width: 100%;">
                                Se connecter
                            </button>
                        </form>

                        <div class="text-center mt-lg">
                            <p class="text-muted">
                                Pas encore de compte?
                                <a href="${pageContext.request.contextPath}/auth/register">Inscrivez-vous</a>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </main>

        <jsp:include page="../common/footer.jsp" />