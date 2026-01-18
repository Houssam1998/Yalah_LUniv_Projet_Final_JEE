<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="../common/header.jsp">
            <jsp:param name="title" value="Contact" />
            <jsp:param name="page" value="contact" />
        </jsp:include>

        <main class="main-content">
            <div class="container">
                <div class="card">
                    <div class="card-body" style="max-width: 600px; margin: 0 auto;">
                        <h1 style="text-align: center; margin-bottom: 2rem;">📬 Contactez-nous</h1>

                        <p class="text-center text-muted mb-xl">
                            Une question ? Une suggestion ? N'hésitez pas à nous contacter.
                        </p>

                        <form action="#" method="POST">
                            <div class="form-group">
                                <label class="form-label">Nom complet</label>
                                <input type="text" name="name" class="form-input" required
                                    value="${sessionScope.user != null ? sessionScope.user.fullName : ''}">
                            </div>

                            <div class="form-group">
                                <label class="form-label">Email</label>
                                <input type="email" name="email" class="form-input" required
                                    value="${sessionScope.user != null ? sessionScope.user.email : ''}">
                            </div>

                            <div class="form-group">
                                <label class="form-label">Sujet</label>
                                <select name="subject" class="form-select" required>
                                    <option value="">Sélectionner un sujet</option>
                                    <option value="general">Question générale</option>
                                    <option value="technical">Problème technique</option>
                                    <option value="suggestion">Suggestion</option>
                                    <option value="report">Signalement</option>
                                    <option value="other">Autre</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Message</label>
                                <textarea name="message" class="form-textarea" rows="5" required
                                    placeholder="Décrivez votre demande..."></textarea>
                            </div>

                            <button type="submit" class="btn btn-primary" style="width: 100%;">
                                Envoyer le message
                            </button>
                        </form>

                        <div style="margin-top: 2rem; padding-top: 2rem; border-top: 1px solid var(--border);">
                            <h3>Autres moyens de contact</h3>
                            <div class="flex flex-wrap gap-md mt-md">
                                <div
                                    style="flex: 1; min-width: 200px; padding: 1rem; background: var(--background); border-radius: var(--radius-md);">
                                    <strong>📧 Email</strong>
                                    <p class="text-muted">yalah@uemf.edu.ma</p>
                                </div>
                                <div
                                    style="flex: 1; min-width: 200px; padding: 1rem; background: var(--background); border-radius: var(--radius-md);">
                                    <strong>🏫 Adresse</strong>
                                    <p class="text-muted">Campus UEMF, Route de Meknès, Fès</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>

        <jsp:include page="../common/footer.jsp" />