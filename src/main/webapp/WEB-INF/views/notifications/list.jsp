<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <jsp:include page="../common/header.jsp">
                <jsp:param name="title" value="Notifications" />
                <jsp:param name="page" value="notifications" />
            </jsp:include>

            <main class="main-content">
                <div class="container" style="max-width: 800px;">

                    <div class="flex justify-between items-center mb-lg">
                        <h1>🔔 Notifications</h1>
                        <c:if test="${unreadCount > 0}">
                            <form action="${pageContext.request.contextPath}/notifications/read-all" method="POST">
                                <button type="submit" class="btn btn-outline">
                                    Tout marquer comme lu
                                </button>
                            </form>
                        </c:if>
                    </div>

                    <!-- Success Message -->
                    <c:if test="${param.success == 'all_read'}">
                        <div class="alert alert-success mb-lg">
                            <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd"
                                    d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" />
                            </svg>
                            <span>Toutes les notifications ont été marquées comme lues</span>
                        </div>
                    </c:if>

                    <!-- Stats -->
                    <div class="card mb-lg">
                        <div class="card-body">
                            <div class="flex justify-around text-center">
                                <div>
                                    <div style="font-size: 2rem; font-weight: 700; color: var(--primary);">
                                        ${notifications.size()}
                                    </div>
                                    <div class="text-muted">Total</div>
                                </div>
                                <div>
                                    <div style="font-size: 2rem; font-weight: 700; color: var(--warning);">
                                        ${unreadCount}
                                    </div>
                                    <div class="text-muted">Non lues</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Notifications List -->
                    <div class="card">
                        <div class="card-body" style="padding: 0;">
                            <c:choose>
                                <c:when test="${not empty notifications}">
                                    <c:forEach items="${notifications}" var="notif" varStatus="status">
                                        <a href="${pageContext.request.contextPath}/notifications/read/${notif.id}?redirect=${notif.relatedUrl}"
                                            class="notification-item ${!notif.isRead ? 'unread' : ''}"
                                            style="display: flex; gap: 1rem; padding: 1rem 1.5rem; text-decoration: none; color: inherit; border-bottom: 1px solid var(--gray-200); transition: background 0.2s; ${!notif.isRead ? 'background: rgba(79, 70, 229, 0.05);' : ''}">

                                            <!-- Icon based on type -->
                                            <div style="font-size: 1.5rem; width: 40px; text-align: center;">
                                                <c:choose>
                                                    <c:when test="${notif.type == 'BOOKING_REQUEST'}">📨</c:when>
                                                    <c:when test="${notif.type == 'BOOKING_CONFIRMED'}">✅</c:when>
                                                    <c:when test="${notif.type == 'BOOKING_REJECTED'}">❌</c:when>
                                                    <c:when test="${notif.type == 'BOOKING_CANCELLED'}">🚫</c:when>
                                                    <c:when test="${notif.type == 'NEW_MESSAGE'}">💬</c:when>
                                                    <c:when test="${notif.type == 'RIDE_REMINDER'}">⏰</c:when>
                                                    <c:when test="${notif.type == 'REVIEW_RECEIVED'}">⭐</c:when>
                                                    <c:otherwise>🔔</c:otherwise>
                                                </c:choose>
                                            </div>

                                            <!-- Content -->
                                            <div style="flex: 1;">
                                                <div
                                                    style="font-weight: ${!notif.isRead ? '600' : '400'}; margin-bottom: 0.25rem;">
                                                    ${notif.title}
                                                </div>
                                                <div class="text-muted text-sm">
                                                    ${notif.message}
                                                </div>
                                                <div class="text-muted text-xs" style="margin-top: 0.5rem;">
                                                    <c:set var="now" value="<%= java.time.LocalDateTime.now() %>" />
                                                    <c:set var="createdAt" value="${notif.createdAt}" />
                                                    <!-- Simple date display -->
                                                    ${notif.createdAt.dayOfMonth}/${notif.createdAt.monthValue}/${notif.createdAt.year}
                                                    à ${notif.createdAt.hour}h${notif.createdAt.minute < 10 ? '0' : ''
                                                        }${notif.createdAt.minute} </div>
                                                </div>

                                                <!-- Unread indicator -->
                                                <c:if test="${!notif.isRead}">
                                                    <div
                                                        style="width: 10px; height: 10px; background: var(--primary); border-radius: 50%; margin-top: 0.5rem;">
                                                    </div>
                                                </c:if>
                                        </a>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-center" style="padding: 3rem;">
                                        <div style="font-size: 4rem; margin-bottom: 1rem;">🔔</div>
                                        <h3>Aucune notification</h3>
                                        <p class="text-muted">Vous recevrez des notifications ici lorsque quelque chose
                                            d'important se passe.</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </main>

            <style>
                .notification-item:hover {
                    background: var(--gray-100) !important;
                }
            </style>

            <jsp:include page="../common/footer.jsp" />