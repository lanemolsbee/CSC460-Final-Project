import java.sql.*;
//import java.io.*;
//import java.util.*;


import java.time.*;

public class Prog4 {
    public static void main(String[] args) {
        // put JDBC reading stuff here
    }

    // functionality #1 - Pearl

    // add account
    public int addUser(Connection conn, String name, String email, String language, int tierID) {
        String sqlStatement = "INSERT INTO orvik.Users (userId, name, email, creationDate, language, tierId) VALUES (users_seq.nextval, ?, ?, ?, ?, ?)";
        try {
            String[] generatedCols = {"userId"};
            int userId = -1;
            PreparedStatement stmt = conn.prepareStatement(sqlStatement, generatedCols);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setTimestamp(3, new Timestamp(LocalTime.now().toNanoOfDay()));
            stmt.setString(4, language);
            stmt.setInt(5, tierID);
            stmt.executeUpdate();
            stmt.close();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                userId = rs.getInt(1);
            }
            return userId;
        } catch (SQLException e) {
            System.err.println("Could not create a new user! : " + e.getMessage());
            return -1;
        }
    }

    // use last two for extra values to change
    // make changeStr NULL if not necessary
    // make newtier NULL if not necessary
    public boolean updateUser(Connection conn, int userID, String toUpdate, String changeStr, int newTier) {
        // update name
        if (toUpdate.equals("name")) {
            String sqlStatement = "UPDATE orvik.invoice SET name = ? WHERE userId = ?";
            try {
                PreparedStatement stmt = conn.prepareStatement(sqlStatement);
                stmt.setString(1, changeStr);
                stmt.executeUpdate();
                stmt.close();
                return true;
            } catch (SQLException e) {
                System.err.println("Could not update name! : " + e.getMessage());
                return false;
            }
        }
        // update email
        else if (toUpdate.equals("email")) {
            String sqlStatement = "UPDATE orvik.invoice SET email = ? WHERE userId = ?";
            try {
                PreparedStatement stmt = conn.prepareStatement(sqlStatement);
                stmt.setString(1, changeStr);
                stmt.executeUpdate();
                stmt.close();
                return true;
            } catch (SQLException e) {
                System.err.println("Could not update email! : " + e.getMessage());
                return false;
            }
        }
        // update language
        else if (toUpdate.equals("language")) {
            String sqlStatement = "UPDATE orvik.invoice SET language = ? WHERE userId = ?";
            try {
                PreparedStatement stmt = conn.prepareStatement(sqlStatement);
                stmt.setString(1, changeStr);
                stmt.executeUpdate();
                stmt.close();
                return true;
            } catch (SQLException e) {
                System.err.println("Could not update language! : " + e.getMessage());
                return false;
            }
        }
        // update membership tier
        else if (toUpdate.equals("tierId")) {
            return updateSubscription(conn, userID, newTier);
        }
        return false;
    }

    // return false if unable to delete
    public boolean deleteUser(Connection conn, int userID) {

        // must check invoice table
        // if any unpaid --> return false
        String sqlInvoice = "SELECT COUNT(*) FROM orvik.invoice WHERE userId = ? AND status = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sqlInvoice);
            stmt.setInt(1, userID);;
            stmt.setString(2, "UNPAID");
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.err.format("Cannot delete user %d, it has unpaid invoices.\n",
                        userID);
                stmt.close();
                rs.close();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Could not read from the table: " + e.getMessage());
            return false;
        }

        // must check support ticket table
        // if any open with user id --> return false
        String sqlTicket = "SELECT COUNT(*) FROM orvik.supportTicket WHERE userId = ? AND outcome = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sqlTicket);
            stmt.setInt(1, userID);
            stmt.setString(2, "OPEN");
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.err.format("Cannot delete user %d, it has open support tickets.\n",
                        userID);
                stmt.close();
                rs.close();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Could not read from the table: " + e.getMessage());
            return false;
        }

        String sqlStatement = "DELETE FROM orvik.Users WHERE userId = ?";
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
    // the system must verify that a user belongs to a workspace before they can move a conversation into it

    // create workspace
    public int createWorkspace(Connection conn, int userID, String name) {
        String sqlStatement = "INSERT INTO orvik.workspace (workspaceId, name) VALUES (workspace_seq.nextval, ?)";
        try {
            String[] generatedCols = {"workspaceId"};
            int workspaceId = -1;
            PreparedStatement stmt = conn.prepareStatement(sqlStatement, generatedCols);
            stmt.setString(1, name);
            stmt.executeUpdate();
            stmt.close();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                workspaceId = rs.getInt(1);
            }

            createMembership(conn, userID, workspaceId);
            return workspaceId;
        } catch (SQLException e) {
            System.err.println("Could not create a new workspace! : " + e.getMessage());
            return -1;
        }
    }

    public void createMembership(Connection conn, int userID, int workspaceID) {
        String sqlStatement = "INSERT INTO orvik.workspaceMembership (userId, workspaceId) VALUES (?, ?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sqlStatement);
            stmt.setInt(0, userID);
            stmt.setInt(1, workspaceID);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Could not create a new workspace membership! : " + e.getMessage());
            return;
        }
    }

    // modify workspace
    public boolean modifyWorkspace(Connection conn, int workspaceID, String newName) {
        String sqlStatement = "UPDATE orvik.workspace SET name = ? WHERE workspaceId = ?";
        try {
                PreparedStatement stmt = conn.prepareStatement(sqlStatement);
                stmt.setString(1, newName);
                stmt.setInt(2, workspaceID);
                stmt.executeUpdate();
                stmt.close();
                return true;
            } catch (SQLException e) {
                System.err.println("Could not update name! : " + e.getMessage());
                return false;
            }
        return false;
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

    // add prompt template
    public int addPromptTemplate(Connection conn, String title, String content, int userID, int workspaceID) {
        String sqlStatement = "INSERT INTO orvik.promptTemplace (templateId, title, content, userId, workspaceId) VALUES (promptTemplace_seq.nextval, ?, ?, ?, ?)";
        try {
            String[] generatedCols = {"templateId"};
            PreparedStatement stmt = conn.prepareStatement(sqlStatement, generatedCols);
            int templateId = -1;
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setInt(3, userID);
            stmt.setInt(4, workspaceID);
            stmt.executeUpdate();
            stmt.close();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                templateId = rs.getInt(1);
            }
            return templateId;
        } catch (SQLException e) {
            System.err.println("Could not create prompt template! : " + e.getMessage());
            return -1;
        }
    }

    // update prompt template
    // last value new string value
    public boolean updatePromptTemplate(Connection conn, int userID, int workplaceID, int templateID, String toUpdate, String changeStr) {
        // need to check what should be updated
        if (toUpdate.equals("title")) {
            String sqlStatement = "UPDATE orvik.promptTemplate SET title = ? WHERE templateId = ?";
            try {
                PreparedStatement stmt = conn.prepareStatement(sqlStatement);
                stmt.setString(1, changeStr);
                stmt.setInt(2, templateID);
                stmt.executeUpdate();
                stmt.close();
                return true;
            } catch (SQLException e) {
                System.err.println("Could not update prompt template title! : " + e.getMessage());
                return false;
            }
        }
        else if (toUpdate.equals("content")) {
            String sqlStatement = "UPDATE orvik.promptTemplate SET content = ? WHERE templateId = ?";
            try {
                PreparedStatement stmt = conn.prepareStatement(sqlStatement);
                stmt.setString(1, changeStr);
                stmt.setInt(2, templateID);
                stmt.executeUpdate();
                stmt.close();
                return true;
            } catch (SQLException e) {
                System.err.println("Could not update prompt template content! : " + e.getMessage());
                return false;
            }
        }
        return false;
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

    // generate a new invoice for a user's monthly tier fee
    public int newInvoice(Connection conn, int userID, int amount) {
        String sqlStatement = "INSERT INTO orvik.invoice (invoiceId, userId, amount, date, status) VALUES (invoice_seq.nextval, ?, ?, ?, ?)";
        try {
            String[] generatedCols = {"invoiceId"};
            PreparedStatement stmt = conn.prepareStatement(sqlStatement, generatedCols);
            int invoiceId = -1;
            stmt.setInt(1, userID);
            stmt.setDouble(2, amount);
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stmt.setString(4, "UNPAID");
            stmt.executeUpdate();
            stmt.close();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                invoiceId = rs.getInt(1);
            }
            return invoiceId;
        } catch (SQLException e) {
            System.err.println("Could not create invoice! : " + e.getMessage());
            return -1;
        }
    }

    // mark an existing invoice as "Paid"
    public boolean paidBill(Connection conn, int invoiceID) {
        String sqlStatement = "UPDATE orvik.invoice SET status = ? WHERE invoiceId = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sqlStatement);
            stmt.setString(1, "PAID");
            stmt.executeUpdate();
            stmt.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Could not update invoice! : " + e.getMessage());
            return false;
        }
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