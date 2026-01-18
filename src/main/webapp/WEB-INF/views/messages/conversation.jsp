<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="../common/header.jsp">
            <jsp:param name="title" value="Conversation" />
            <jsp:param name="page" value="messages" />
        </jsp:include>

        <main class="main-content">
            <div class="container" style="max-width: 700px;">

                <!-- Header -->
                <div class="card mb-md">
                    <div class="card-body">
                        <div class="flex items-center gap-md">
                            <a href="${pageContext.request.contextPath}/messages" class="btn btn-ghost">
                                ←
                            </a>
                            <div class="avatar" style="width: 45px; height: 45px;">
                                ${otherUser.firstName.substring(0,1)}${otherUser.lastName.substring(0,1)}
                            </div>
                            <div style="flex: 1;">
                                <div style="font-weight: 600;">
                                    ${otherUser.firstName} ${otherUser.lastName}
                                </div>
                                <a href="${pageContext.request.contextPath}/profile/user/${otherUser.id}"
                                    class="text-sm text-muted" style="text-decoration: none;">
                                    Voir le profil →
                                </a>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Messages -->
                <div class="card" style="margin-bottom: 100px;">
                    <div class="card-body messages-container" id="messagesContainer"
                        style="height: 400px; overflow-y: auto; display: flex; flex-direction: column; gap: 0.75rem;">
                        <c:choose>
                            <c:when test="${not empty messages}">
                                <c:forEach items="${messages}" var="msg">
                                    <c:set var="isOwn" value="${msg.sender.id == currentUserId}" />
                                    <div class="message ${isOwn ? 'own' : 'other'}" style="max-width: 75%; padding: 0.75rem 1rem; border-radius: 12px;
                                        ${isOwn ? 'align-self: flex-end; background: var(--primary); color: white;' : 
                                                  'align-self: flex-start; background: var(--gray-100);'}">
                                        <div>${msg.content}</div>
                                        <div
                                            style="font-size: 0.7rem; margin-top: 0.25rem; opacity: 0.7; text-align: right;">
                                            ${msg.sentAt.hour}h${msg.sentAt.minute < 10 ? '0' : '' }${msg.sentAt.minute}
                                                </div>
                                        </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center text-muted" style="padding: 2rem;">
                                    Aucun message. Commencez la conversation!
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Send Form -->
                <div
                    style="position: fixed; bottom: 0; left: 0; right: 0; background: white; border-top: 1px solid var(--gray-200); padding: 1rem;">
                    <div class="container" style="max-width: 700px;">
                        <form action="${pageContext.request.contextPath}/messages/send" method="POST"
                            class="flex gap-md">
                            <input type="hidden" name="receiverId" value="${otherUser.id}">
                            <input type="text" name="content" class="form-input" placeholder="Tapez votre message..."
                                required autofocus autocomplete="off" style="flex: 1;">
                            <button type="submit" class="btn btn-primary">
                                Envoyer
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </main>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const messagesContainer = document.getElementById('messagesContainer');
                const messageForm = document.querySelector('form');
                const inputField = messageForm.querySelector('input[name="content"]');
                const currentUserId = ${ currentUserId };
                const otherUserId = ${ otherUser.id };

                // Scroll to bottom
                messagesContainer.scrollTop = messagesContainer.scrollHeight;

                // WebSocket Connection
                const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
                const socketUrl = protocol + '//' + window.location.host + '${pageContext.request.contextPath}/ws/chat/' + currentUserId;
                const socket = new WebSocket(socketUrl);

                socket.onopen = function () {
                    console.log('Chat WebSocket connecté');
                };

                socket.onmessage = function (event) {
                    console.log('WS Reçu Raw:', event.data);
                    const msg = JSON.parse(event.data);
                    console.log('WS Parsé:', msg);
                    console.log('Comparaison: Sender=' + msg.senderId + ' vs Current=' + currentUserId + ' | Receiver=' + msg.receiverId + ' vs Other=' + otherUserId);

                    // Filter: Only show messages for THIS conversation (between me and otherUser)
                    if ((msg.senderId == otherUserId && msg.receiverId == currentUserId) ||
                        (msg.senderId == currentUserId && msg.receiverId == otherUserId)) {

                        console.log('Filtre OK -> Affichage');
                        appendMessage(msg.content, msg.timestamp, msg.senderId == currentUserId);
                    } else {
                        console.log('Filtre KO -> Ignoré');
                    }
                };

                socket.onerror = function (error) {
                    console.error('WebSocket Error:', error);
                };

                // Send Message via WebSocket
                messageForm.addEventListener('submit', function (e) {
                    e.preventDefault();
                    const content = inputField.value.trim();
                    if (!content) return;

                    if (socket.readyState === WebSocket.OPEN) {
                        const payload = {
                            receiverId: otherUserId,
                            content: content
                        };
                        socket.send(JSON.stringify(payload));
                        inputField.value = '';
                    } else {
                        // Fallback: Submit form normally if WS is down
                        messageForm.submit();
                    }
                });

                function appendMessage(content, time, isOwn) {
                    console.log('Appel appendMessage. Content=' + content + ', isOwn=' + isOwn);
                    const div = document.createElement('div');
                    div.className = 'message ' + (isOwn ? 'own' : 'other');

                    div.style.cssText = 'max-width: 75%; padding: 0.75rem 1rem; border-radius: 12px; ' +
                        (isOwn ? 'align-self: flex-end; background: var(--primary); color: white;' :
                            'align-self: flex-start; background: var(--gray-100);');

                    div.innerHTML =
                        '<div>' + content + '</div>' +
                        '<div style="font-size: 0.7rem; margin-top: 0.25rem; opacity: 0.7; text-align: right;">' + time + '</div>';

                    messagesContainer.appendChild(div);
                    messagesContainer.scrollTop = messagesContainer.scrollHeight;
                }
            });
        </script>

        <jsp:include page="../common/footer.jsp" />