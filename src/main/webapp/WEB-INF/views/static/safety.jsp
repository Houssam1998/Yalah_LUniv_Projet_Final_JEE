<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="../common/header.jsp">
            <jsp:param name="title" value="Sécurité" />
            <jsp:param name="page" value="safety" />
        </jsp:include>

        <main class="main-content">
            <div class="container">
                <div class="card">
                    <div class="card-body" style="max-width: 800px; margin: 0 auto;">
                        <h1 style="text-align: center; margin-bottom: 2rem;">🛡️ Règles de Sécurité</h1>

                        <p class="text-center text-muted mb-xl">
                            Votre sécurité est notre priorité. Suivez ces conseils pour voyager sereinement.
                        </p>

                        <h2>👤 Pour les passagers</h2>
                        <ul style="line-height: 2;">
                            <li>Vérifiez le <strong>profil du conducteur</strong> et ses évaluations avant de réserver
                            </li>
                            <li>Préférez les conducteurs <strong>vérifiés UEMF</strong> (badge bleu)</li>
                            <li>Partagez votre <strong>trajet avec un proche</strong> (envoyez-lui les détails)</li>
                            <li>Restez sur la plateforme pour <strong>communiquer avec le conducteur</strong></li>
                            <li>Signez votre participation au trajet au départ et à l'arrivée</li>
                        </ul>

                        <h2 style="margin-top: 2rem;">🚗 Pour les conducteurs</h2>
                        <ul style="line-height: 2;">
                            <li>Maintenez votre <strong>véhicule en bon état</strong></li>
                            <li>Respectez le <strong>code de la route</strong> et les limitations de vitesse</li>
                            <li>N'acceptez que des passagers <strong>inscrits sur la plateforme</strong></li>
                            <li>Vérifiez l'identité de vos passagers au départ</li>
                            <li>Ne conduisez pas fatigué ou sous l'influence de substances</li>
                        </ul>

                        <h2 style="margin-top: 2rem;">⚠️ En cas de problème</h2>
                        <div
                            style="background: var(--error-light, #fff5f5); padding: 1.5rem; border-radius: var(--radius-md); border-left: 4px solid var(--error);">
                            <p><strong>En cas d'urgence :</strong> Appelez le <strong>190</strong> (Police) ou le
                                <strong>150</strong> (Protection Civile)</p>
                            <p class="mt-md">Pour signaler un comportement inapproprié sur la plateforme, utilisez le
                                bouton
                                <strong>"Signaler"</strong> sur le profil concerné ou contactez-nous à
                                <strong>yalah@uemf.edu.ma</strong>
                            </p>
                        </div>

                        <div style="text-align: center; margin-top: 2rem;">
                            <a href="${pageContext.request.contextPath}/contact" class="btn btn-outline">
                                Signaler un problème
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </main>

        <jsp:include page="../common/footer.jsp" />