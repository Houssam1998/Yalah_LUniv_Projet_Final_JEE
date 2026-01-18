<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="../common/header.jsp">
            <jsp:param name="title" value="FAQ" />
            <jsp:param name="page" value="faq" />
        </jsp:include>

        <main class="main-content">
            <div class="container">
                <div class="card">
                    <div class="card-body" style="max-width: 800px; margin: 0 auto;">
                        <h1 style="text-align: center; margin-bottom: 2rem;">❓ Questions Fréquentes</h1>

                        <div class="faq-list">
                            <details class="mb-md"
                                style="border: 1px solid var(--border); border-radius: var(--radius-md); padding: 1rem;">
                                <summary style="cursor: pointer; font-weight: 600;">Comment m'inscrire sur Yalah L'Univ
                                    ?</summary>
                                <p class="mt-md text-muted">
                                    Cliquez sur "S'inscrire" en haut de la page, remplissez le formulaire avec vos
                                    informations
                                    et votre email universitaire. Vous recevrez un email de confirmation pour activer
                                    votre compte.
                                </p>
                            </details>

                            <details class="mb-md"
                                style="border: 1px solid var(--border); border-radius: var(--radius-md); padding: 1rem;">
                                <summary style="cursor: pointer; font-weight: 600;">Qui peut utiliser Yalah L'Univ ?
                                </summary>
                                <p class="mt-md text-muted">
                                    La plateforme est réservée aux étudiants, professeurs et personnel de l'UEMF.
                                    Vous devez utiliser une adresse email valide pour vous inscrire.
                                </p>
                            </details>

                            <details class="mb-md"
                                style="border: 1px solid var(--border); border-radius: var(--radius-md); padding: 1rem;">
                                <summary style="cursor: pointer; font-weight: 600;">Comment proposer un trajet ?
                                </summary>
                                <p class="mt-md text-muted">
                                    Une fois connecté, cliquez sur "Proposer un trajet" dans le menu. Remplissez les
                                    informations
                                    (départ, destination, date, heure, prix) et publiez votre annonce.
                                </p>
                            </details>

                            <details class="mb-md"
                                style="border: 1px solid var(--border); border-radius: var(--radius-md); padding: 1rem;">
                                <summary style="cursor: pointer; font-weight: 600;">Comment réserver une place ?
                                </summary>
                                <p class="mt-md text-muted">
                                    Recherchez un trajet qui vous convient, consultez les détails et cliquez sur
                                    "Réserver".
                                    Le conducteur recevra votre demande et pourra l'accepter ou la refuser.
                                </p>
                            </details>

                            <details class="mb-md"
                                style="border: 1px solid var(--border); border-radius: var(--radius-md); padding: 1rem;">
                                <summary style="cursor: pointer; font-weight: 600;">Quel est le prix d'un trajet ?
                                </summary>
                                <p class="mt-md text-muted">
                                    Le conducteur fixe librement le prix par place. Nous recommandons un tarif de 10-15
                                    MAD
                                    pour les trajets vers le campus depuis Fès centre.
                                </p>
                            </details>

                            <details class="mb-md"
                                style="border: 1px solid var(--border); border-radius: var(--radius-md); padding: 1rem;">
                                <summary style="cursor: pointer; font-weight: 600;">Comment annuler une réservation ?
                                </summary>
                                <p class="mt-md text-muted">
                                    Rendez-vous dans "Mes réservations" et cliquez sur "Annuler".
                                    Veuillez prévenir le conducteur dès que possible par message.
                                </p>
                            </details>

                            <details class="mb-md"
                                style="border: 1px solid var(--border); border-radius: var(--radius-md); padding: 1rem;">
                                <summary style="cursor: pointer; font-weight: 600;">La réservation instantanée, c'est
                                    quoi ?</summary>
                                <p class="mt-md text-muted">
                                    Certains conducteurs activent la réservation instantanée. Cela signifie que votre
                                    réservation
                                    est automatiquement confirmée, sans attendre la validation du conducteur.
                                </p>
                            </details>
                        </div>

                        <div
                            style="text-align: center; margin-top: 2rem; padding-top: 2rem; border-top: 1px solid var(--border);">
                            <p class="text-muted">Vous n'avez pas trouvé la réponse à votre question ?</p>
                            <a href="${pageContext.request.contextPath}/contact" class="btn btn-outline mt-md">
                                Contactez-nous
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </main>

        <jsp:include page="../common/footer.jsp" />