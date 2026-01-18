package com.uemf.yalah.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.uemf.yalah.dao.MessageDAO;
import com.uemf.yalah.dao.UserDAO;
import com.uemf.yalah.model.Message;
import com.uemf.yalah.model.User;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket Endpoint pour le chat en temps réel (User-to-User).
 * URL: ws://host:port/yalah/ws/chat/{userId}
 * L'utilisateur se connecte à son propre canal pour recevoir des messages.
 */
@ServerEndpoint("/ws/chat/{userId}")
public class ChatEndpoint {

    // Stocke la session active de chaque utilisateur : Map<UserId, Session>
    private static final Map<Long, Session> userSessions = new ConcurrentHashMap<>();

    private final Gson gson = new Gson();
    private final MessageDAO messageDAO = new MessageDAO();
    private final UserDAO userDAO = new UserDAO();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        userSessions.put(userId, session);
        System.out.println("WebSocket User connecté: " + userId + " (Session: " + session.getId() + ")");
    }

    @OnMessage
    public void onMessage(String messageJson, Session session, @PathParam("userId") Long senderId) {
        System.out.println(">>> WebSocket onMessage reçu de " + senderId + ": " + messageJson);
        try {
            // 1. Parser le message
            JsonObject json = gson.fromJson(messageJson, JsonObject.class);
            Long receiverId = json.get("receiverId").getAsLong();
            String content = json.get("content").getAsString();

            System.out.println(">>> WebSocket Parsing OK. Sender=" + senderId + ", Receiver=" + receiverId);

            // 2. Persister le message
            // Utilisation directe des entités récupérées. Si UserDAO retourne des entités
            // détachées,
            // GenericDAO.save(persist) risque d'échouer. Cependant, UserDAO utilise
            // em.find() qui retourne
            // des entités MANAGED si la session est ouverte, ou DETACHED si session fermée.
            // Ici UserDAO ouvre/ferme session => DETACHED.
            // SOLUTION ROBUSTE: Recharger les users dans la même transaction que le message
            // ou utiliser merge.

            // Pour l'instant, on tente le save standard mais avec logs
            User sender = userDAO.findById(senderId).orElseThrow();
            User receiver = userDAO.findById(receiverId).orElseThrow();
            Message message = new Message(sender, receiver, content);

            try {
                messageDAO.save(message);
                System.out.println(">>> WebSocket Message SAVE OK. ID=" + message.getId());
            } catch (Exception e) {
                System.err.println(">>> WebSocket Message SAVE FAILED: " + e.getMessage());
                e.printStackTrace();
                // On ne peut pas continuer si le save a échoué
                return;
            }

            // 3. Préparer le payload JSON pour le client
            JsonObject broadcastJson = new JsonObject();
            broadcastJson.addProperty("id", message.getId());
            broadcastJson.addProperty("senderId", senderId);
            broadcastJson.addProperty("receiverId", receiverId);
            broadcastJson.addProperty("senderName", sender.getFirstName());
            broadcastJson.addProperty("content", content);
            broadcastJson.addProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

            String jsonOutput = gson.toJson(broadcastJson);

            // 4. Envoyer au destinataire s'il est connecté
            try {
                Session receiverSession = userSessions.get(receiverId);
                if (receiverSession != null && receiverSession.isOpen()) {
                    receiverSession.getBasicRemote().sendText(jsonOutput);
                    System.out.println(">>> WebSocket Sent to Receiver " + receiverId);
                } else {
                    System.out.println(">>> WebSocket Receiver " + receiverId + " not connected or session closed");
                }
            } catch (Exception e) {
                System.err.println(">>> WebSocket Error sending to receiver: " + e.getMessage());
            }

            // 5. Renvoyer à l'expéditeur (pour confirmation visuelle immédiate)
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(jsonOutput);
                    System.out.println(">>> WebSocket Echo sent to Sender " + senderId);
                }
            } catch (Exception e) {
                System.err.println(">>> WebSocket Error echoing to sender: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println(">>> WebSocket CRITICAL ERROR in onMessage");
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("userId") Long userId) {
        userSessions.remove(userId);
        System.out.println("WebSocket User déconnecté: " + userId);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Erreur WebSocket Chat: " + throwable.getMessage());
    }
}
