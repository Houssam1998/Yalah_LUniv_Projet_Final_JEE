<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="../common/header.jsp">
            <jsp:param name="title" value="À propos" />
            <jsp:param name="page" value="about" />
        </jsp:include>

        <main class="main-content">
            <div class="container">
                <div class="card">
                    <div class="card-body" style="max-width: 800px; margin: 0 auto;">
                        <h1 style="text-align: center; margin-bottom: 2rem;">🚗 À propos de Yalah L'Univ</h1>

                        <p class="text-lg" style="text-align: center; color: var(--text-muted); margin-bottom: 2rem;">
                            La plateforme de covoiturage dédiée à la communauté UEMF
                        </p>

                        <div class="grid gap-lg"
                            style="grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); margin: 2rem 0;">
                            <div class="text-center"
                                style="padding: 1.5rem; background: var(--background); border-radius: var(--radius-lg);">
                                <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">🌍</div>
                                <h3>Écologique</h3>
                                <p class="text-muted">Réduisez votre empreinte carbone en partageant vos trajets</p>
                            </div>
                            <div class="text-center"
                                style="padding: 1.5rem; background: var(--background); border-radius: var(--radius-lg);">
                                <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">💰</div>
                                <h3>Économique</h3>
                                <p class="text-muted">Partagez les frais de transport avec d'autres étudiants</p>
                            </div>
                            <div class="text-center"
                                style="padding: 1.5rem; background: var(--background); border-radius: var(--radius-lg);">
                                <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">🤝</div>
                                <h3>Communautaire</h3>
                                <p class="text-muted">Rencontrez d'autres membres de la communauté UEMF</p>
                            </div>
                        </div>

                        <h2 style="margin-top: 2rem;">Notre Mission</h2>
                        <p>
                            Yalah L'Univ est née de la volonté de faciliter les déplacements quotidiens des étudiants,
                            professeurs et personnel de l'Université Euro-Méditerranéenne de Fès. Notre plateforme
                            permet de mettre en relation conducteurs et passagers pour des trajets partagés vers le
                            campus.
                        </p>

                        <h2 style="margin-top: 2rem;">Comment ça marche ?</h2>
                        <ol style="line-height: 2;">
                            <li><strong>Inscrivez-vous</strong> avec votre email universitaire</li>
                            <li><strong>Proposez un trajet</strong> si vous êtes conducteur ou
                                <strong>recherchez</strong> un trajet existant</li>
                            <li><strong>Réservez</strong> une place et contactez le conducteur</li>
                            <li><strong>Voyagez ensemble</strong> et partagez les frais</li>
                        </ol>

                        <div style="text-align: center; margin-top: 2rem;">
                            <a href="${pageContext.request.contextPath}/auth/register" class="btn btn-primary btn-lg">
                                Rejoindre la communauté
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </main>

        <jsp:include page="../common/footer.jsp" />