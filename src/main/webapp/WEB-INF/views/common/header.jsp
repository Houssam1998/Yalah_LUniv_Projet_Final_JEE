<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <meta name="description"
                content="Yalah L'Univ - Application de covoiturage pour l'Université Euro-Méditerranéenne de Fès">
            <title>${param.title != null ? param.title : 'Yalah L\'Univ'} - Covoiturage UEMF</title>

            <!-- Google Fonts -->
            <link rel="preconnect" href="https://fonts.googleapis.com">
            <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
            <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap"
                rel="stylesheet">

            <!-- Leaflet CSS -->
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />

            <!-- Flatpickr CSS -->
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">

            <!-- Theme CSS -->
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/theme.css">

            <!-- Favicon -->
            <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/assets/images/logo.png">
        </head>

        <body>
            <div class="page-wrapper">
                <!-- Navbar -->
                <nav class="navbar">
                    <div class="navbar-container">
                        <a href="${pageContext.request.contextPath}/" class="navbar-brand">
                            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="UEMF Logo">
                            <span class="navbar-brand-text">Yalah L'Univ</span>
                        </a>

                        <ul class="navbar-nav">
                            <li><a href="${pageContext.request.contextPath}/rides/search"
                                    class="navbar-link ${param.page == 'search' ? 'active' : ''}">Rechercher</a></li>
                            <li><a href="${pageContext.request.contextPath}/rides/create"
                                    class="navbar-link ${param.page == 'create' ? 'active' : ''}">Proposer un trajet</a>
                            </li>
                            <c:if test="${not empty sessionScope.user}">
                                <li><a href="${pageContext.request.contextPath}/rides/my-rides"
                                        class="navbar-link ${param.page == 'my-rides' ? 'active' : ''}">Mes trajets</a>
                                </li>
                                <li><a href="${pageContext.request.contextPath}/bookings/my-bookings"
                                        class="navbar-link ${param.page == 'bookings' ? 'active' : ''}">Mes
                                        réservations</a></li>
                            </c:if>
                        </ul>

                        <div class="navbar-actions">
                            <c:choose>
                                <c:when test="${not empty sessionScope.user}">
                                    <!-- Dashboard -->
                                    <a href="${pageContext.request.contextPath}/dashboard"
                                        class="btn btn-ghost btn-icon" title="Dashboard" style="position: relative;">
                                        📊
                                    </a>

                                    <!-- Notifications -->
                                    <a href="${pageContext.request.contextPath}/notifications"
                                        class="btn btn-ghost btn-icon" title="Notifications"
                                        style="position: relative;">
                                        🔔
                                        <c:if
                                            test="${sessionScope.unreadNotifications != null && sessionScope.unreadNotifications > 0}">
                                            <span
                                                style="position: absolute; top: -2px; right: -2px; background: #ef4444; color: white; 
                                                         font-size: 0.65rem; padding: 2px 5px; border-radius: 10px; font-weight: 600;">
                                                ${sessionScope.unreadNotifications > 9 ? '9+' :
                                                sessionScope.unreadNotifications}
                                            </span>
                                        </c:if>
                                    </a>

                                    <!-- Messages -->
                                    <a href="${pageContext.request.contextPath}/messages" class="btn btn-ghost btn-icon"
                                        title="Messages" style="position: relative;">
                                        💬
                                        <c:if
                                            test="${sessionScope.unreadMessages != null && sessionScope.unreadMessages > 0}">
                                            <span
                                                style="position: absolute; top: -2px; right: -2px; background: #ef4444; color: white; 
                                                         font-size: 0.65rem; padding: 2px 5px; border-radius: 10px; font-weight: 600;">
                                                ${sessionScope.unreadMessages > 9 ? '9+' : sessionScope.unreadMessages}
                                            </span>
                                        </c:if>
                                    </a>

                                    <!-- Profile -->
                                    <a href="${pageContext.request.contextPath}/profile"
                                        class="flex items-center gap-sm">
                                        <div class="avatar avatar-sm">
                                            ${sessionScope.user.firstName.substring(0,1)}${sessionScope.user.lastName.substring(0,1)}
                                        </div>
                                        <span class="text-sm">${sessionScope.user.firstName}</span>
                                    </a>
                                    <a href="${pageContext.request.contextPath}/auth/logout"
                                        class="btn btn-ghost btn-sm">Déconnexion</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/auth/login"
                                        class="btn btn-ghost btn-sm">Connexion</a>
                                    <a href="${pageContext.request.contextPath}/auth/register"
                                        class="btn btn-primary btn-sm">S'inscrire</a>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <button class="btn btn-ghost btn-icon navbar-toggle" aria-label="Menu">
                            <svg width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M3 12h18M3 6h18M3 18h18" />
                            </svg>
                        </button>
                    </div>
                </nav>