<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="../common/header.jsp">
            <jsp:param name="title" value="Dashboard" />
            <jsp:param name="page" value="dashboard" />
        </jsp:include>

        <main class="main-content">
            <div class="container">

                <h1 class="mb-lg">📊 Tableau de bord</h1>

                <!-- MongoDB Status -->
                <c:if test="${!mongoConnected}">
                    <div class="alert alert-warning mb-lg">
                        <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20" style="margin-right: 8px;">
                            <path fill-rule="evenodd"
                                d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" />
                        </svg>
                        <span>MongoDB non connecté - Certaines statistiques sont indisponibles</span>
                    </div>
                </c:if>

                <!-- Stats Cards -->
                <div class="grid grid-4 gap-lg mb-xl">
                    <div class="card"
                        style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
                        <div class="card-body text-center">
                            <div style="font-size: 3rem; font-weight: 700;">${totalRides}</div>
                            <div style="opacity: 0.9;">🚗 Trajets proposés</div>
                        </div>
                    </div>

                    <div class="card"
                        style="background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); color: white;">
                        <div class="card-body text-center">
                            <div style="font-size: 3rem; font-weight: 700;">${activeRides}</div>
                            <div style="opacity: 0.9;">🟢 Trajets actifs</div>
                        </div>
                    </div>

                    <div class="card"
                        style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white;">
                        <div class="card-body text-center">
                            <div style="font-size: 3rem; font-weight: 700;">${totalBookings}</div>
                            <div style="opacity: 0.9;">📦 Réservations</div>
                        </div>
                    </div>

                    <div class="card"
                        style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white;">
                        <div class="card-body text-center">
                            <div style="font-size: 3rem; font-weight: 700;">${searchesToday}</div>
                            <div style="opacity: 0.9;">🔍 Recherches (aujourd'hui)</div>
                        </div>
                    </div>
                </div>

                <div class="grid gap-lg" style="grid-template-columns: 2fr 1fr;">
                    <!-- Quick Actions -->
                    <div class="card">
                        <div class="card-header">
                            <h3 style="margin: 0;">⚡ Actions rapides</h3>
                        </div>
                        <div class="card-body">
                            <div class="grid grid-2 gap-md">
                                <a href="${pageContext.request.contextPath}/rides/create" class="btn btn-primary btn-lg"
                                    style="text-align: center;">
                                    ➕ Proposer un trajet
                                </a>
                                <a href="${pageContext.request.contextPath}/rides/search" class="btn btn-outline btn-lg"
                                    style="text-align: center;">
                                    🔍 Chercher un trajet
                                </a>
                                <a href="${pageContext.request.contextPath}/bookings/my-bookings"
                                    class="btn btn-outline btn-lg" style="text-align: center;">
                                    📋 Mes réservations
                                </a>
                                <a href="${pageContext.request.contextPath}/rides/my-rides"
                                    class="btn btn-outline btn-lg" style="text-align: center;">
                                    🚗 Mes trajets
                                </a>
                            </div>
                        </div>
                    </div>

                    <!-- Profile Summary -->
                    <div class="card">
                        <div class="card-header">
                            <h3 style="margin: 0;">👤 Mon profil</h3>
                        </div>
                        <div class="card-body text-center">
                            <div class="avatar avatar-lg"
                                style="width: 80px; height: 80px; font-size: 2rem; margin: 0 auto 1rem;">
                                ${sessionScope.user.firstName.substring(0,1)}${sessionScope.user.lastName.substring(0,1)}
                            </div>
                            <h4>${sessionScope.user.firstName} ${sessionScope.user.lastName}</h4>
                            <c:if test="${sessionScope.user.rating > 0}">
                                <div style="color: #ffc107; font-size: 1.25rem;">
                                    ★ ${sessionScope.user.rating}
                                </div>
                            </c:if>
                            <a href="${pageContext.request.contextPath}/profile" class="btn btn-sm btn-outline mt-md">
                                Voir mon profil
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Recent Activity (MongoDB) -->
                <c:if test="${mongoConnected && not empty recentActivity}">
                    <div class="card mt-lg">
                        <div class="card-header flex justify-between items-center">
                            <h3 style="margin: 0;">📋 Activité récente (MongoDB)</h3>
                            <span class="badge badge-success">Live</span>
                        </div>
                        <div class="card-body" style="padding: 0;">
                            <c:forEach items="${recentActivity}" var="log" varStatus="status">
                                <c:if test="${status.index < 5}">
                                    <div style="padding: 1rem 1.5rem; border-bottom: 1px solid var(--gray-200);">
                                        <div class="flex justify-between">
                                            <div>
                                                <span class="badge badge-primary"
                                                    style="font-size: 0.7rem;">${log.event_type}</span>
                                                <span style="margin-left: 0.5rem;">${log.description}</span>
                                            </div>
                                            <span class="text-muted text-sm">${log.timestamp}</span>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
            </div>
        </main>

        <jsp:include page="../common/footer.jsp" />