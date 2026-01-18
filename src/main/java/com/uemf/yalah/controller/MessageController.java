package com.uemf.yalah.controller;

import com.uemf.yalah.dao.UserDAO;
import com.uemf.yalah.model.Message;
import com.uemf.yalah.model.User;
import com.uemf.yalah.service.MessageService;
import com.uemf.yalah.service.NotificationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des messages
 */
@WebServlet("/messages/*")
public class MessageController extends HttpServlet {

    private final MessageService messageService = new MessageService();
    private final NotificationService notificationService = new NotificationService();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
            pathInfo = "/";

        System.out.println("[MessageController] GET pathInfo: " + pathInfo);

        User currentUser = (User) request.getSession().getAttribute("user");

        switch (pathInfo) {
            case "/":
                showInbox(request, response, currentUser);
                break;
            default:
                // Conversation with user: /messages/123
                try {
                    Long otherUserId = Long.parseLong(pathInfo.substring(1));
                    showConversation(request, response, currentUser, otherUserId);
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/messages");
                }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
            pathInfo = "/";

        System.out.println("[MessageController] POST pathInfo: " + pathInfo);

        User currentUser = (User) request.getSession().getAttribute("user");

        if (pathInfo.equals("/send")) {
            sendMessage(request, response, currentUser);
        } else {
            response.sendRedirect(request.getContextPath() + "/messages");
        }
    }

    /**
     * Affiche la boîte de réception (liste des conversations)
     */
    private void showInbox(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {

        List<Message> conversations = messageService.getConversations(currentUser.getId());
        long unreadCount = messageService.countUnread(currentUser.getId());

        request.setAttribute("conversations", conversations);
        request.setAttribute("unreadCount", unreadCount);
        request.setAttribute("currentUserId", currentUser.getId());

        request.getRequestDispatcher("/WEB-INF/views/messages/inbox.jsp").forward(request, response);
    }

    /**
     * Affiche une conversation avec un utilisateur
     */
    private void showConversation(HttpServletRequest request, HttpServletResponse response,
            User currentUser, Long otherUserId) throws ServletException, IOException {

        Optional<User> otherUserOpt = userDAO.findById(otherUserId);
        if (otherUserOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utilisateur non trouvé");
            return;
        }

        User otherUser = otherUserOpt.get();
        List<Message> messages = messageService.getMessagesBetween(currentUser.getId(), otherUserId);

        // Marquer les messages reçus comme lus
        messageService.markAsRead(currentUser.getId(), otherUserId);

        request.setAttribute("otherUser", otherUser);
        request.setAttribute("messages", messages);
        request.setAttribute("currentUserId", currentUser.getId());

        request.getRequestDispatcher("/WEB-INF/views/messages/conversation.jsp").forward(request, response);
    }

    /**
     * Envoie un message
     */
    private void sendMessage(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws IOException {

        String receiverIdStr = request.getParameter("receiverId");
        String content = request.getParameter("content");

        if (receiverIdStr == null || content == null || content.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/messages?error=invalid_params");
            return;
        }

        try {
            Long receiverId = Long.parseLong(receiverIdStr);
            Optional<User> receiverOpt = userDAO.findById(receiverId);

            if (receiverOpt.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/messages?error=user_not_found");
                return;
            }

            User receiver = receiverOpt.get();
            messageService.sendMessage(currentUser, receiver, content.trim());

            // Envoyer une notification au destinataire (lien vers conversation avec
            // l'expéditeur)
            notificationService.notifyNewMessage(receiverId, currentUser.getFullName(), currentUser.getId());

            response.sendRedirect(request.getContextPath() + "/messages/" + receiverId);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/messages?error=send_failed");
        }
    }
}
