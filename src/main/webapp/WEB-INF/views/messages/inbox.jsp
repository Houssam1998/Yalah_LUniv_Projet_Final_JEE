<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="../common/header.jsp">
            <jsp:param name="title" value="Messages" />
            <jsp:param name="page" value="messages" />
        </jsp:include>

        <main class="main-content">
            <div class="container" style="max-width: 900px;">

                <div class="flex justify-between items-center mb-lg">
                    <h1>💬 Messages</h1>
                    <c:if test="${unreadCount > 0}">
                        <span class="badge badge-primary">${unreadCount} non lus</span>
                    </c:if>
                </div>

                <!-- Conversations List -->
                <div class="card">
                    <div class="card-body" style="padding: 0;">
                        <c:choose>
                            <c:when test="${not empty conversations}">
                                <c:forEach items="${conversations}" var="msg">
                                    <c:set var="otherUser"
                                        value="${msg.sender.id == currentUserId ? msg.receiver : msg.sender}" />
                                    <c:set var="isUnread" value="${!msg.isRead && msg.receiver.id == currentUserId}" />

                                    <a href="${pageContext.request.contextPath}/messages/${otherUser.id}"
                                        class="conversation-item" style="display: flex; gap: 1rem; padding: 1rem 1.5rem; text-decoration: none; color: inherit; 
                                      border-bottom: 1px solid var(--gray-200); transition: background 0.2s;
                                      ${isUnread ? 'background: rgba(79, 70, 229, 0.05);' : ''}">

                                        <!-- Avatar -->
                                        <div class="avatar" style="width: 50px; height: 50px; font-size: 1.25rem;">
                                            ${otherUser.firstName.substring(0,1)}${otherUser.lastName.substring(0,1)}
                                        </div>

                                        <!-- Content -->
                                        <div style="flex: 1; min-width: 0;">
                                            <div class="flex justify-between items-center">
                                                <div style="font-weight: ${isUnread ? '600' : '400'};">
                                                    ${otherUser.firstName} ${otherUser.lastName}
                                                </div>
                                                <div class="text-muted text-xs">
                                                    ${msg.sentAt.dayOfMonth}/${msg.sentAt.monthValue}
                                                </div>
                                            </div>
                                            <div class="text-muted text-sm"
                                                style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                                                <c:if test="${msg.sender.id == currentUserId}">
                                                    <span style="color: var(--gray-500);">Vous: </span>
                                                </c:if>
                                                ${msg.content.length() > 50 ? msg.content.substring(0, 50).concat('...')
                                                : msg.content}
                                            </div>
                                        </div>

                                        <!-- Unread indicator -->
                                        <c:if test="${isUnread}">
                                            <div
                                                style="width: 10px; height: 10px; background: var(--primary); border-radius: 50%; margin-top: 0.5rem;">
                                            </div>
                                        </c:if>
                                    </a>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center" style="padding: 3rem;">
                                    <div style="font-size: 4rem; margin-bottom: 1rem;">💬</div>
                                    <h3>Aucune conversation</h3>
                                    <p class="text-muted">Vos conversations avec d'autres utilisateurs apparaîtront ici.
                                    </p>
                                    <a href="${pageContext.request.contextPath}/rides/search"
                                        class="btn btn-primary mt-md">
                                        Rechercher des trajets
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </main>

        <style>
            .conversation-item:hover {
                background: var(--gray-100) !important;
            }
        </style>

        <jsp:include page="../common/footer.jsp" />