import java.sql.*;
//import java.io.*;
//import java.util.*;


import java.time.*;

public class Prog4 {
    public static void main(String[] args) {
        // put JDBC reading stuff here
    }

    // functionality #1 - Pearl
    public boolean manageAccount() {
        return true;
    }

    // functionality #2 - Jordan
    public int newConvo(Connection conn, int userID, int personaID, int workspaceID, String title) {
        String sqlStatement = "INSERT INTO orvik.conversation (conversationId, userId, title, creationDate, personaId, workspaceId) VALUES (convo_seq.nextval, ?, ?, ?, ?, ?)";
        try {
            String[] generatedCols = {"conversationId"};
            PreparedStatement stmt = conn.prepareStatement(sqlStatement, generatedCols);
            stmt.setInt(1, userID);
            stmt.setString(2, title);
            stmt.setTimestamp(3, new Timestamp(LocalTime.now().toNanoOfDay()));
            stmt.setInt(4, personaID);
            stmt.setInt(5, workspaceID);
            stmt.executeUpdate();
            stmt.close();
            
            // We are using SQL sequence to get the ID of the new conversation, so we need to get it.
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("Could not write to the table: " + e.getMessage());
            return -1;
        }

    }

    public int addMessageToConvo(Connection conn, int convoID, String message) {
        String sqlStatement = "INSERT INTO orvik.message (messageId, conversationId, role, content, timestamp) VALUES (message_seq.nextval, ?, ?, ?, ?)";
        try {
            int messageID = -1;
            String[] generatedCols = {"messageId"};
            PreparedStatement stmt = conn.prepareStatement(sqlStatement, generatedCols);
            stmt.setInt(1, convoID);
            stmt.setString(2, "USER");
            stmt.setString(3, message);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
            stmt.close();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                messageID = rs.getInt(1);
            }
            return messageID;
        } catch (SQLException e) {
            System.err.println("Could not write to the table: " + e.getMessage());
            return -1;
        }
    }

    public boolean updateMsgFeedback(Connection conn, int messageID, int rating, String feedback) {
        String sqlStatement = "INSERT INTO orvik.feedback (messageId, rating, feedback) VALUES (?, ?, ?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sqlStatement);
            stmt.setInt(1, messageID);
            stmt.setInt(2, rating);
            stmt.setString(3, feedback);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Could not write to the table: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean removeuserIDFromMessages(Connection conn, int userID) {
        String sqlStatement = "DELETE FROM orvik.message WHERE conversationId IN (SELECT conversationId FROM orvik.conversation WHERE userId = ?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sqlStatement);
            stmt.setInt(1, userID);
            stmt.executeUpdate();
            stmt.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Could not write to the table: " + e.getMessage());
            return false;
        }
    }

    // functionality #3 - Pearl
    public boolean manageWorkspace() {
        return true;
    }

    // functionality #4 - Jordan

    public int createPersona(Connection conn, String name, String directive) {
        String sqlStatement = "INSERT INTO orvik.persona (personaId, name, instructions) VALUES (persona_seq.nextval, ?, ?)";
        try {
            String[] generatedCols = {"personaId"};
            PreparedStatement stmt = conn.prepareStatement(sqlStatement, generatedCols);
            int personaID = -1;
            stmt.setString(1, name);
            stmt.setString(2, directive);
            stmt.executeUpdate();
            stmt.close();
    
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                personaID = rs.getInt(1);
            }
            return personaID;
        } catch (SQLException e) {
            System.err.println("Could not write to the table: " + e.getMessage());
            return -1;
        }

    }

    public boolean deletePersona(Connection conn, int personaID) {

        // If we have a persona with more than 5 conversations, we can't delete it.
        String countStatement = "SELECT COUNT(*) FROM orvik.conversation WHERE personaId = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(countStatement);
            stmt.setInt(1, personaID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 5) {
                System.err.format("Cannot delete persona %d, it is in use by at least five conversations.\n",
                        personaID);
                stmt.close();
                rs.close();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Could not read from the table: " + e.getMessage());
            return false;
        }

        // Otherwise, we can delete it from the table.
        String sqlStatement = "DELETE FROM orvik.persona WHERE personaId = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sqlStatement);
            stmt.setInt(1, personaID);
            stmt.executeUpdate();
            stmt.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Could not write to the table: " + e.getMessage());
            return false;
        }
    }

    // functionality #5 - Pearl
    public boolean managePrompt() {
        return true;
    }

    // functionality #6 - Jordan

    public boolean updateSubscription(Connection conn, int userID, int tierID) {
        String sqlStatement = "UPDATE orvik.Users SET tierId = ? WHERE userId = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sqlStatement);
            stmt.setInt(1, tierID);
            stmt.setInt(2, userID);
            stmt.executeUpdate();
            stmt.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Could not write to the table: " + e.getMessage());
            return false;
        }

    }

   public boolean withinLimit(Connection conn, int userID) {
    // probably one of the longest queries ngl
    String sql = "SELECT COUNT(m.messageId) AS totalMsgs, MAX(mt.messageLimit) AS msgLimit " +
                 "FROM Users u " +
                 "JOIN membershipTier mt ON u.tierId = mt.tierId " +
                 "LEFT JOIN conversation c ON c.userId = u.userId " +
                 "LEFT JOIN message m ON m.conversationId = c.conversationId " +
                 "WHERE u.userId = ?";
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, userID);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int totalMsgs = rs.getInt("totalMsgs");
                int limit = rs.getInt("msgLimit");
                return totalMsgs < limit;
            }
        }
    } catch (SQLException e) {
        System.err.println("ERROR: Cannot verify message limit: " + e.getMessage());
        return false;
    }
    return true; 
}

    // functionality #7 - Pearl
    public boolean billingOperations() {
        return true;
    }

    // functionality #8 - Jordan

    public int openTicket(Connection conn, int userID, int agentID, String topic) {
        String sqlStatement = "INSERT INTO orvik.supportTicket (ticketId, userId, agentId, topic, duration, outcome) VALUES (ticket_seq.nextval, ?, ?, ?, ?, ?)";
        try {
            String[] generatedCols = {"ticketId"};
            PreparedStatement stmt = conn.prepareStatement(sqlStatement, generatedCols);
            int ticketID = -1;
            stmt.setInt(1, userID);
            stmt.setInt(2, agentID);
            stmt.setString(3, topic);
            stmt.setInt(4, 0);
            stmt.setString(5, "OPEN");
            stmt.executeUpdate();
            stmt.close();
    
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                ticketID = rs.getInt(1);
            }
            return ticketID;
        } catch (SQLException e) {
            System.err.println("Could not create ticket! : " + e.getMessage());
            return -1;
        }
    }

    public boolean closeTicket(Connection conn, int ticketID, int duration) {
        String sqlStatement = "UPDATE orvik.ticket SET outcome = ?, duration = ? WHERE ticketId = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sqlStatement);
            stmt.setString(1, "CLOSED");
            stmt.setInt(2, duration);
            stmt.setInt(3, ticketID);
            stmt.executeUpdate();
            stmt.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Could not close ticket! : " + e.getMessage());
            return false;
        }
    }
}