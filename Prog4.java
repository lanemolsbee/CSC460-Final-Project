import java.sql.*;
import java.time.*;
import java.util.*;

public class Prog4 {
    // INTERFACE (Annabelle)
    public static void main(String[] args) {
        // Call connector to connect to DB
        if (args.length < 2) {
            System.out.println("Usage: java Prog4 <username> <password>");
            System.exit(-1);
        }
        connector(args[0], args[1]);
    }

    // Connects to the Database and calls loopMechanism
    // JDBC LOGIN HANDLING (Jordan, thank you Jordan!)
    private static void connector(String username, String password) {
        // INITIALIZE (including all DB stuff)
        System.out.println("");
        System.out.println("PLACEHOLDER WELCOME MESSAGE: DATABASE LOADING...");

        // Magic lectura -> aloe access spell
        // change this depending on team verdict of hardcoding this
        final String oracleURL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";

        // load the (Oracle) JDBC driver by initializing its base
        // class, 'oracle.jdbc.OracleDriver'.
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("*** ClassNotFoundException:  "
                    + "Error loading Oracle JDBC driver.  \n"
                    + "\tPerhaps the driver is not on the Classpath?");
            System.exit(-1);
        }

        // make and return a database connection to the user's Oracle database
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(oracleURL, username, password);
        }

        // Oops - password may be incorrect.
        catch (SQLException e) {
            System.err.println("*** SQLException:  "
                    + "Could not open JDBC connection. Check username/password.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }

        // Run loop
        loopMechanism(conn);

        // Upon loop close, close DB
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("*** SQLException:  "
                    + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }

        // Print good bye message
        System.out.println("CLOSED SUCCESSFULLY. GOODBYE.");
        System.out.println("");
    }

    // Method that loops for user query accepting
    // Calls printOptions and queryToAction
    private static void loopMechanism(Connection conn) {
        // Boolean for loop control
        Boolean loopRun = true;
        // Scanner for user input
        Scanner input = new Scanner(System.in);
        // String for current user query
        String query = "";

        // SHOW OPTIONS
        System.out.println("");
        System.out.println("Enter 'exit' to exit the program, 'functionalities' for functionality options, and 'queries' for query options.");
        System.out.println("");

        while (loopRun == true) {
            System.out.print("--> ENTER QUERY: ");
            query = input.nextLine();
            System.out.print("");

            if (query.contentEquals("exit"))
                loopRun = false;
            else if (query.contentEquals("functionalities 1"))
                printFunctionalities(1);
            else if (query.contentEquals("functionalities 2"))
                printFunctionalities(2);
            else if (query.contentEquals("queries"))
                printQueries();
            else
                queryToAction(query, conn);
            System.out.println("");
        }
        System.out.println("");
        input.close();
        System.out.println("EXIT RECEIEVED; CLOSING CONNECTION...");
    }

    // Prints all the user functionality options
    private static void printFunctionalities(int page) {
        if (page == 1) {
            System.out.println("");
            System.out.println("~ ~ ~");
            System.out.println("FUNCTIONALITIES 1:");
            System.out.println("   USER...");
            System.out.println("      create user: 'user.create <string NAME> <string EMAIL> <string LANGUAGE> <int TIER>'");
            System.out.println("      change user information: 'user.change <int USERID> <'name'/'email'/'language'/'tierId'>");
            System.out.println("         <string NEW VALUE> <int NEW TIER>'")
            System.out.println("      delete user: 'user.delete <int USERID>'");
            System.out.println("   CONVERSATION...");
            System.out.println("      create convo: 'convo.create <int USERID> <int PERSONAID> <int WORKSPACEID> <string TITLE>'");
            System.out.println("      add msg to convo: 'convo.add <int CONVOID> <string MESSAGE>'");
            System.out.println("      add feedback to msg: 'convo.feedback <int MESSAGEID> <int RATING> <string FEEDBACK>'");
            System.out.println("   WORKSPACE...");
            System.out.println("      create workspace: 'workspace.create <int USERID> <string NAME>'");
            System.out.println("      create membership: 'workspace.member <int USERID> <int WORKSPACEID>'");
            System.out.println("      change workspace name: 'workspace.change <int WORKSPACEID> <string NEW NAME>'");
            System.out.println("      add convo to workspace: 'workspace.add <int USERID> <int PERSONAID> <int WORKSPACEID> <string TITLE>'");
            System.out.println("   PERSONA...");
            System.out.println("      create persona: 'persona.create <string NAME> <string DIRECTIVE>'");
            System.out.println("      delete persona: 'persona.delete <int PERSONAID>'");
            System.out.println("~ ~ ~");
            System.out.println("");
        } else if (page == 2) {
            System.out.println("");
            System.out.println("~ ~ ~");
            System.out.println("FUNCTIONALITIES 2:");
            System.out.println("   TEMPLATE...");
            System.out.println("      create template: 'template.create <string TITLE> <string CONTENT> <int USERID> <int WORKSPACEID>'");
            System.out.println("      change template: 'template.change <int TEMPLATEID> <'title'/'content'> <string NEW VALUE>'");
            System.out.println("   SUBSCRIPTION...");
            System.out.println("      change subscription: 'subscription.change <int USERID> <int NEW TIER>'");
            System.out.println("   INVOICE...");
            System.out.println("      create invoice: 'invoice.create <int USERID> <double AMOUNT>'");
            System.out.println("      pay invoice: 'invoice.pay <int INVOICEID>'");
            System.out.println("   TICKET...");
            System.out.println("      open ticket: 'ticket.open <int USERID> <int AGENTID> <string TOPIC>'");
            System.out.println("      close ticket: 'ticket.close <int TICKETID> <int NUMBER OF MINUTES TAKEN>'");
            System.out.println("~ ~ ~");
            System.out.println("");
        }
        System.out.println("EXAMPLE: 'user.change 2 language mandarin 5' (Note: as language is chosen, 5 is ignored)");
        System.out.println("");
    }


    private static void printQueries() {
        System.out.println("");
        System.out.println("~ ~ ~");
        System.out.println("QUERIES:");
        System.out.println("   QUERY 1...");
        System.out.println("      List all bookmarked messages' conversation titles and timestamps from a given User:");
        System.out.println("      'query1 <int USERID>'");
        System.out.println("   QUERY 2...");
        System.out.println("      List email, amount owed, last conversation for all Users with unpaid invoices:");
        System.out.println("      'query2'");
        System.out.println("   QUERY 3...");
        System.out.println("      List most helpful Personas (most thumbs ups across all conversations):");
        System.out.println("      'query3'");
        System.out.println("   QUERY 4...");
        System.out.println("      List top 5 conversations' title and average rating from a given User:");
        System.out.println("      'query4 <int USERID>'");
        System.out.println("~ ~ ~");
        System.out.println("");
    }


    // Processes query sent by user and sends it on its way to the right method
    // Currently partial duplicate of code from my Prog3; not functional!
    private static void queryToAction(String query, Connection dbconn) {
        // Split for processing the query and its parameters
        String[] split = query.split(" ");

        // !!!!! IF DELETEUSER, CALL DELETEUSERMESSAGES!
        //       IF ADDMESSSAGE CALL WITHINLIMIT

        // Check to see which query/functionality to run
        if ((query.charAt(0) == '1') && (split.length == 1)) {
            
            return;
        } 
        // If it matches none of them, print an error and move on
        System.out.println("ERROR: INCORRECT SYNTAX OR QUERY.");
    }

    /*---------------------------------------------------------------
    |   Method query1(int userId, Connection con)
    |
    |   Purpose: This method takes in a given userID and returns all of the bookmarked messages across all of their conversations.
    |            Information to be returned will include the messageID, the conversationID, and the timestamp of the message. 
    |
    |   Pre-Condition: There is a Users table with value userId, 
    |                  a conversation table with values conversationId, userId, and title,
    |                  a message table with values messageId and conversationId.
    |                  The userId value is used to identifythe user.
    |
    |   Post-Condition: None
    |
    |   Parameters: 
    |       con -- The Connection to JDBC to connect to SQL.
    |       userId -- the integer representing the user in the DBMS
    |
    |   Returns: void
    |   
    |   Author: Jordan Orvik
    |   Extra background: I have typically used a PreparedStatement in previous code I've written,
                          particularly for CSC 436, so I defaulted to using that here. 
    *--------------------------------------------------------------*/
    public static void query1(int userId, Connection conn) {
        try {
            // Long query here - this will join these three components like messageID,
            // conversationID, and timeStamp
            // And then select only bookmarked messages.
            String queryString = "SELECT messageId, conversationId, timestamp FROM orvik.message JOIN orvik.conversation"
                    +
                    "ON orvik.message.conversationId = orvik.conversation.conversationId" +
                    " WHERE messageId IN (SELECT messageId FROM orvik.bookmark WHERE userId = ?);";

            // Using a PreparedStatement here to prevent SQL injection.
            PreparedStatement stmt = conn.prepareStatement(queryString);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Bookmarks for User " + userId + " (Use the message ID to view the message):");
            while (rs.next()) {
                System.out.println(rs.getInt("messageId") + " " + rs.getInt("conversationId") + " "
                        + rs.getTimestamp("timestamp"));
            }
        } catch (SQLException e) {
            System.out.println("Could not execute Query #1: " + e.getMessage());
        }
    }


    public static void query2(Connection conn) {
        try {
            // Long query that joins user, conversation and invoice and returns the
            // email, amount owed and most recent conversation date for all outstanding
            // invoices.
            String queryString = "SELECT email, amount, orvik.conversation.creationDate "+
            "FROM orvik.users JOIN orvik.invoice USING (userId) JOIN orvik.conversation "+
            "USING (userId) WHERE status='UNPAID' AND orvik.conversation.creationDate IN "+
            "(SELECT max(orvik.conversation.creationDate) as maxDate FROM orvik.users "+
            "JOIN orvik.conversation USING (userId) GROUP BY userId);";

            // No need for PreparedStatement due to no User input.
            ResultSet rs = stmt.executeQuery();
            System.out.println("All outstanding invoices are:");
            while (rs.next()) {
                System.out.println(rs.getString("email") + " " + rs.getInt("amount") + " "
                        + rs.getTimestamp("creationDate"));
            }
        } catch (SQLException e) {
            System.out.println("Could not execute Query #1: " + e.getMessage());
        }
    }


    /*---------------------------------------------------------------
    |   Method query3(Connection conn)
    |
    |   Purpose: This method carries out the third required SQL query.
    |            It is supposed to answer: Identify the “Most Helpful” 
    |            Persona: List the persona name that has received the 
    |            highest percentage of “Thumbs Up” feedback across all 
    |            conversations linked to it. The result will be neatly
    |            printed.
    |
    |   Pre-Condition: The following tables exist: persona, conversation
    |                  message, and feedback. And, they all have keys that
    |                  "connect" to each other by being foreign keys.
    |
    |   Post-Condition: None of the tables have changed. Data has been
    |                   gathered from them and printed out.
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |
    |   Returns: N/A
    *--------------------------------------------------------------*/
    public static void query3(Connection conn) {
        try {
            String sqlStatement = "SELECT p.name as personaName, " +
            // f.rating is either 1 or 0?? 
            // so we can take the average and multiply by 100 to get %
                "ROUND(AVG(f.rating) * 100, 2) as thumbsUpPercent " + 
            // we want the persona name
            "FROM orvik.persona p " + 
                // but we have to get to feedback, so first we connect to conversation
                "JOIN orvik.conversation c ON p.personaId = c.personaId " + 
                // then we connect to message
                "JOIN orvik.message m ON c.conversationId = m.conversationId " + 
                // and finally we connect to feedback
                "JOIN orvik.feedback f ON m.messageId = f.messageId " +
            // sort by the personaId and name (since we print name)
            "GROUP BY p.personaId, p.name " + 
            // descending order of average ratings
            "ORDER BY AVG(f.rating) DESC " +
            // we only want the first one!
            "FETCH FIRST 1 ROWS ONLY";

            PreparedStatement stmt = conn.prepareStatement(sqlStatement);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // get persona name
                String name = rs.getString("personaName");
                // get percentage
                double percentage = rs.getDouble("thumbsUpPercent");
                // print out the name
                System.out.print("Persona Name: " + name + "\n");
                // print out their percentage
                System.out.println("Thumb's Up Percentage: " + percentage + "%");
            }
        } catch (SQLException e) {
            // catch sql exceptions
            System.out.println("Could not execute Query #3: " + e.getMessage());
        }
    }

    /*---------------------------------------------------------------
    |   Method query4(int userId, Connection con)
    |
    |   Purpose: This method returns the top 5 conversations with the highest average
    |            rating for a specific user (identified by userId). The title and average rating
    |            for each of the top 5 conversations is printed. If there are sql issues, they
    |            are caught and an error message is displayed.
    |
    |   Pre-Condition: There is a Users table with value userId, 
    |                  a conversation table with values conversationId, userId, and title,
    |                  a message table with values messageId and conversationId, and a feedback
    |                  table with values messageId and rating. The userId value is used to identify
    |                  the user for which we want to find the top 5 conversations. The conversationId
    |                  value is used to connect the conversation table to the message table, and the
    |                  messageId value is used to connect the message table to the feedback table.
    |
    |   Post-Condition: None
    |
    |   Parameters: 
    |       con -- The Connection to JDBC to connect to SQL.
    |       userId -- the integer representing the user in the DBMS
    
    |   Returns: void
    |   
    |   Author: Lane Molsbee
    |   Extra background: I have typically used a PreparedStatement in previous code I've written,
                          particularly for CSC 436, so I defaulted to using that here. 
    *--------------------------------------------------------------*/
    public static void query4(int userId, Connection con) {
        try {
            String query = " SELECT c.title, AVG(f.rating) AS avg_rating " +
                    "FROM orvik.conversation c " +
                    "JOIN orvik.message m ON c.conversationId = m.conversationId " +
                    "JOIN orvik.feedback f ON m.messageId = f.messageId " +
                    "WHERE c.userId = :? " +
                    "GROUP BY c.conversationId, c.itle " +
                    "ORDER BY avg_rating DESC " +
                    "FETCH FIRST 5 ROWS ONLY";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                double avgRating = rs.getDouble("avg_rating");
                System.out.print("Conversation Title: " + title + " ");
                System.out.println("Average rating: " + avgRating);
            }
        } catch (Exception e) {
            System.err.println("Error executing query: " + e.getMessage());
        }
    }

    // FUNCTIONALITY #1 (Pearl)

    /*---------------------------------------------------------------
    |   Method addUser(Connection conn, String name, String email, 
    |                            String language, int tierID)
    |
    |   Purpose: This method creates a new user and inserts it into 
    |            the Users table. In the process, a unique identifying
    |            value is generated (userId), added to the table, and
    |            returned. The parameters are also included in the 
    |            new row of the table. If there are any sql issues,
    |            -1 is returned.
    |
    |   Pre-Condition: There is a Users table with 6 values: userId
    |                  (generated in this method), name, email,
    |                   creationDate (current date/time), language,
    |                   and tierId.
    |
    |   Post-Condition: A new row has been added to the Users table
    |                   with a unique userId and the parameter values 
    |                   from this method along with the current
    |                   date/time. If there are sql issues, they
    |                   are caught and -1 is returned.
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       name -- A string representing the name for the new user.
    |       email -- A string representing the new user's email.
    |       language -- A string representing the new user's language.
    |       tierID -- An int of the tierId (representing membership 
    |                 tier) to add to the new user.
    |
    |   Returns: An integer representing the newly created user
    |            is returned (userId). Also, this new user is 
    |            inserted into the Users table.
    *--------------------------------------------------------------*/
    public int addUser(Connection conn, String name, String email, String language, int tierID) {
        String sqlStatement = "INSERT INTO orvik.Users (userId, name, email, creationDate, language, tierId) VALUES (Users_seq.nextval, ?, ?, ?, ?, ?)";
        try {
            String[] generatedCols = { "userId" };
            int userId = -1;
            PreparedStatement stmt = conn.prepareStatement(sqlStatement, generatedCols);
            // name parameter added
            stmt.setString(1, name);
            // email parameter added
            stmt.setString(2, email);
            // current date/time added as date
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            // language parameter added
            stmt.setString(4, language);
            // membership tierID parameter added
            stmt.setInt(5, tierID);
            stmt.executeUpdate();
            stmt.close();

            // userId generated
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                userId = rs.getInt(1);
            }
            // new userId returned
            return userId;
        } catch (SQLException e) {
            // catch sql exceptions
            System.err.println("Could not create a new user! : " + e.getMessage());
            return -1;
        }
    }

    /*---------------------------------------------------------------
    |   Method updateUser(Connection conn, int userID, String toUpdate, 
    |                     String changeStr, int newTier)
    |
    |   Purpose: This method changes a value in a specific row (based on
    |            userId) of the Users table. The value to change could be
    |            a user's name, email, language, or membership tier level
    |            (tierId). The parameter toUpdate will specify this. The 
    |            last two parameters will specify what the toUpdate value
    |            should be changed to. If this is done successfully, true
    |            is returned. Otherwise, false is returned, which is also
    |            what happens if toUpdate is not valid.
    |
    |   Pre-Condition: There exists a Users table with rows containing 6
    |                  values each. These include name, email, language, 
    |                  and tierId (membership tier level), etc. These four
    |                  value can be updated in this method.
    |
    |   Post-Condition: A row of the Users table has been updated. The 
    |                   value for name, email, language, or tierId has 
    |                   been changed for a specific userId. If that 
    |                   row does not exist or there is a sql problem,
    |                   a change does not occur.
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       userID -- The integer representing a user in the Users
    |                 table that we want to change a value in. 
    |       toUpdate -- This represents what value should be changed.
    |                   It should be either, "name", "email",
    |                   "language", or "tierId". If the value is 
    |                   something else, false is returned (invalid).
    |       changeStr -- This represents either the new name, email,
    |                    or language of the specified user (by userId)
    |                    but only if toUpdate was equal to "name",
    |                    "email", or "language".
    |       newTier -- This represents the new tier for the specified
    |                  user (by userId), but only if toUpdate was 
    |                  equal to "tierId"
    |
    |   Returns: A boolean is returned of whether a value was 
    |            successfully changed within a certain user's (userId)
    |            row of the Users table. If the toUpdate value is invalid
    |            or the value cannot be changed, false is returned.
    *--------------------------------------------------------------*/
    public boolean updateUser(Connection conn, int userID, String toUpdate, String changeStr, int newTier) {
        // if toUpdate is "name"
        if (toUpdate.equals("name")) {
            // sql statement to change the name with the specific userId
            String sqlStatement = "UPDATE orvik.Users SET name = ? WHERE userId = ?";
            try {
                PreparedStatement stmt = conn.prepareStatement(sqlStatement);
                // new name
                stmt.setString(1, changeStr);
                // specific userId
                stmt.setInt(2, userID);
                stmt.executeUpdate();
                stmt.close();
                // return true if name changed correctly
                return true;
            } catch (SQLException e) {
                // catch sql exceptions
                System.err.println("Could not update name! : " + e.getMessage());
                return false;
            }
        }
        // if toUpdate is "email"
        else if (toUpdate.equals("email")) {
            // sql statement to change the email with the specific userId
            String sqlStatement = "UPDATE orvik.Users SET email = ? WHERE userId = ?";
            try {
                PreparedStatement stmt = conn.prepareStatement(sqlStatement);
                // add new email
                stmt.setString(1, changeStr);
                // specific userId
                stmt.setInt(2, userID);
                stmt.executeUpdate();
                stmt.close();
                // return true if email changed correctly
                return true;
            } catch (SQLException e) {
                // catch sql exceptions
                System.err.println("Could not update email! : " + e.getMessage());
                return false;
            }
        }
        // if toUpdate is language
        else if (toUpdate.equals("language")) {
            // sql statement to change the language with the specific userId
            String sqlStatement = "UPDATE orvik.Users SET language = ? WHERE userId = ?";
            try {
                PreparedStatement stmt = conn.prepareStatement(sqlStatement);
                // add new language
                stmt.setString(1, changeStr);
                // specific userId
                stmt.setInt(2, userID);
                stmt.executeUpdate();
                stmt.close();
                // return true if language is changed correctly
                return true;
            } catch (SQLException e) {
                System.err.println("Could not update language! : " + e.getMessage());
                return false;
            }
        }
        // if toUpdate is tierId
        else if (toUpdate.equals("tierId")) {
            // jordan already wrote a method for this!
            return updateSubscription(conn, userID, newTier);
        }
        // if input of toUpdate was invalid, return false
        return false;
    }

    /*---------------------------------------------------------------
    |   Method deleteUser(Connection conn, int userID)
    |
    |   Purpose: This method has the purpose of deleting a specific
    |            user (identified by userId parameter) from the Users
    |            table. But first, two conditions must be checked. If
    |            any unpaid invoices exist for the user, or if there
    |            are any open support tickets, the user cannot be
    |            deleted. If a user is successfully deleted, true is 
    |            returned. Otherwise, false is returned. 
    |
    |   Pre-Condition: There exists a Users table where each row
    |                  is identified (PK) by a unique userId value. This
    |                  value is used in this method to identify users
    |                  for deletion. There also exists an invoice table
    |                  and a supportTicket table that include userId
    |                  as a field in each. 
    |
    |   Post-Condition: The user (identified by userId) has been removed 
    |                   from the Users table but only if the two 
    |                   conditions (listed in purpose) have been met. If 
    |                   the user does not meet the conditions, or does not
    |                   exist, the table remains unchanged.
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       userID -- The integer representing a user in the Users
    |                 table that we want to delete.
    |
    |   Returns: A boolean is returned representing whether a user was
    |            removed from the Users table. If a user meets both 
    |            conditions and has been removed, true is returned. 
    |            Otherwise, false is returned.
    *--------------------------------------------------------------*/
    public boolean deleteUser(Connection conn, int userID) {

        // First condition! User cannot have any unpaid invoices
        // create sql statement to get number of unpaid invoices for the user
        String sqlInvoice = "SELECT COUNT(*) FROM orvik.invoice WHERE userId = ? AND status = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sqlInvoice);
            // specific user
            stmt.setInt(1, userID);
            // but only unpaid invoices
            stmt.setString(2, "UNPAID");
            ResultSet rs = stmt.executeQuery();
            // if the value is more than 0, we can't delete the user
            if (rs.next() && rs.getInt(1) > 0) {
                // has unpaid invoices!
                System.err.format("Cannot delete user %d, it has unpaid invoices.\n", userID);
                stmt.close();
                rs.close();
                // return false because there were unpaid invoices
                return false;
            }
        } catch (SQLException e) {
            // catch sql exceptions
            System.err.println("Could not read from the table: " + e.getMessage());
            return false;
        }

        // Second condition! User cannot have any open support tickets
        // create sql statement to get number of open support tickets for the user
        String sqlTicket = "SELECT COUNT(*) FROM orvik.supportTicket WHERE userId = ? AND outcome = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sqlTicket);
            // specific user
            stmt.setInt(1, userID);
            // but only open support tickets
            stmt.setString(2, "OPEN");
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // has open support tickets!
                System.err.format("Cannot delete user %d, it has open support tickets.\n",
                        userID);
                stmt.close();
                rs.close();
                // return false because there were open support tickets
                return false;
            }
        } catch (SQLException e) {
            // catch sql exceptions
            System.err.println("Could not read from the table: " + e.getMessage());
            return false;
        }

        // now we've passed both conditions
        // sql statement to delete the specific user by userId
        String sqlStatement = "DELETE FROM orvik.Users WHERE userId = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sqlStatement);
            // specific user
            stmt.setInt(1, userID);
            stmt.executeUpdate();
            stmt.close();
            // we've deleted the user, so return true
            return true;
        } catch (SQLException e) {
            // catch sql exceptions
            System.err.println("Could not write to the table: " + e.getMessage());
            return false;
        }
    }

    // FUNCTIONALITY #2 (Jordan)

   /*---------------------------------------------------------------
    |   Method nwUser(Connection conn, int userID)
    |
    |   Purpose: Given a userId, this method has the purpose of creating a new 
    |            conversation for the user based on the given persona, workspace, and title of the converation.
    |
    |   Pre-Condition: There exists a Users table where each row
    |                  is identified (PK) by a unique userId value. This
    |                  value is used in this method to identify users
    |                  for deletion. There also exists a workspace table and a
    |                  persona table that include userId as a field in each.
    |                  These values are linked to the conversation table
    |                  through a foreign key.
    |
    |   Post-Condition: The conversation with all of its links has been created.
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       userID -- The integer representing a user in the Users
    |                 table that we are linking to a conversation.
    |       personaID -- The integer representing a persona in the persona
    |                 table that we are linking to a conversation.
    |       workspaceID -- The integer representing a workspace in the workspace
    |                 table that we are linking to a conversation.
    |       title -- The string representing the title of the conversation.
    |
    |
    |   Returns: The ID of the new conversation such that it can be referenced later.
    *--------------------------------------------------------------*/
    public int newConvo(Connection conn, int userID, int personaID, int workspaceID, String title) {
        // Note the prepared statement syntax as well as the use of a sequence to minimize Java PRNG collsions.
        String sqlStatement = "INSERT INTO orvik.conversation (conversationId, userId, title, creationDate, personaId, workspaceId) VALUES (convo_seq.nextval, ?, ?, ?, ?, ?)";
        try {
            // We are using a sequence here, so we need to pass in the generated columns to our prepared statement.
            String[] generatedCols = { "conversationId" };
            PreparedStatement stmt = conn.prepareStatement(sqlStatement, generatedCols);

            // Pass in the parameters to the table.
            stmt.setInt(1, userID);
            stmt.setString(2, title);
            stmt.setTimestamp(3, new Timestamp(LocalTime.now().toNanoOfDay()));
            stmt.setInt(4, personaID);
            stmt.setInt(5, workspaceID);
            stmt.executeUpdate();
            stmt.close();

            // We are using SQL sequence to get the ID of the new conversation, so we need
            // to get it.
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            // not found!
            return -1;

        } 
        // whoops, something went wrong! 
        catch (SQLException e) {
            System.err.println("Could not write to the table: " + e.getMessage());
            return -1;
        }

    }

    /*---------------------------------------------------------------
    |   Method addMessageToConvo(Connection conn, int conversationID, String message)
    |
    |   Purpose: This method will add a message to a specific conversation.
    |
    |
    |   Pre-Condition: The message and conversation tables must exist alreadt.
    |                  convoId must point to a valid conversation in the table.
    |
    |   Post-Condition: The message and its contents have been added to the message table.
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       convoID -- The integer representing the current conversation to be added to.
    |       message -- The string representing the message to be added to the conversation.
    |
    |   Returns: The ID of the new message such that it can be referenced later.
    *--------------------------------------------------------------*/
    public int addMessageToConvo(Connection conn, int convoID, String message) {

        // See the comments in newConvo, these are barely any different.
        String sqlStatement = "INSERT INTO orvik.message (messageId, conversationId, role, content, timestamp) VALUES (message_seq.nextval, ?, ?, ?, ?)";
        try {
            // Not found starter.
            int messageID = -1;
            // Sequence, so generated columns are needed.
            String[] generatedCols = { "messageId" };
            PreparedStatement stmt = conn.prepareStatement(sqlStatement, generatedCols);

            // Set params
            stmt.setInt(1, convoID);
            stmt.setString(2, "USER");
            stmt.setString(3, message);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
            stmt.close();

            // Get the ID of the new message.
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

    /*---------------------------------------------------------------
    |   Method aupdateMessageFeedback(Connection conn, int messageID, int rating, String feedback)
    |
    |   Purpose: This method will add feedback to a particular message and store it in the table.
    |
    |
    |
    |   Pre-Condition: messageID must be pointing to a valid message in the table.
    |                  Rating is a number between one and five.
    |
    |   Post-Condition: The message and its contents have been added to the message table.
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       messageID -- The integer representing the current message to add feedback to.
    |       rating -- The integer representing the rating of the message.
    |       feedback -- The string representing the feedback of the message.
    |
    |   Returns: The ID of the new message such that it can be referenced later.
    *--------------------------------------------------------------*/
    public boolean updateMsgFeedback(Connection conn, int messageID, int rating, String feedback) {

        // see above inline comments.
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

    /*---------------------------------------------------------------
    |   Method deleteUserMessages(Connection conn, int userID)
    |
    |   Purpose: This method will delete all of a user's messages from our server.
    |            This is likely used when a user is deleted from our database.
    |
    |
    |
    |   Pre-Condition: userID must have had messages in the table.
    |
    |   Post-Condition: All of the user's messages will have been deleted from our table.
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       userID -- The integer representing the current user to delete messages from.
    |
    |   Returns:  A boolean representing whether or not the deletion was successful.
    *--------------------------------------------------------------*/
    public boolean deleteUserMessages(Connection conn, int userID) {

        // Delete the entries where the userID matches.
        String sqlStatement = "DELETE FROM orvik.message WHERE conversationId IN (SELECT conversationId FROM orvik.conversation WHERE userId = ?)";
        try {
            // There are no sequences used here. 
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

    // FUNCTIONALITY #3 (Pearl)

    /*---------------------------------------------------------------
    |   Method createWorkspace(Connection conn, int userID, String name)
    |
    |   Purpose: This method creates a new workspace. A workspace only 
    |            contains a generated workspaceId and the name of the 
    |            workspace. Within this method is a call to a method,
    |            createMembership(), which creates a connection between
    |            the workspace and a specific user (parameter userID).
    |            This method returns the unique identifier for the new
    |            workspace (workspaceId). Or, -1 is returned if there
    |            are any sql issues.
    |
    |   Pre-Condition: There exists a workspace table each with a unique,
    |                  generated workspaceId (identifier) and a name. There
    |                  also exists a workspaceMembership table which
    |                  connects workspaces to users.
    |
    |   Post-Condition: A new workspace has been created and connected to a
    |                   user using workspaceMembership. In this process
    |                   a new unique workspaceId has been created. 
    |                   If there were sql issues, this was not done.
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       userID -- Represents a user to connect the workspace to
    |                 using a workspaceMembership connection.
    |       name -- A string representing the name for the workspace.
    |
    |   Returns: An integer representing the newly created workspace
    |            is returned (workspaceId). Also, this workspace is 
    |            inserted into the workspace table.
    *--------------------------------------------------------------*/
    public int createWorkspace(Connection conn, int userID, String name) {
        // sql statement for creating a new workspace
        String sqlStatement = "INSERT INTO orvik.workspace (workspaceId, name) VALUES (workspace_seq.nextval, ?)";
        try {
            String[] generatedCols = { "workspaceId" };
            // create workspaceId
            int workspaceId = -1;
            PreparedStatement stmt = conn.prepareStatement(sqlStatement, generatedCols);
            // add in the name
            stmt.setString(1, name);
            stmt.executeUpdate();
            stmt.close();
            // generate the workspaceId value
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                workspaceId = rs.getInt(1);
            }
            // call the method to create the connection to user
            createMembership(conn, userID, workspaceId);
            // return the uniquely generated workspaceId
            return workspaceId;
        } catch (SQLException e) {
            // catch sql exceptions
            System.err.println("Could not create a new workspace! : " + e.getMessage());
            return -1;
        }
    }

    /*---------------------------------------------------------------
    |   Method createMembership(Connection conn, int userID, 
    |                          int workspaceID)
    |
    |   Purpose: This method connects a workspace to a user. This is done
    |            by adding a row to the workspaceMembership table which
    |            has the sole purpose of connecting workspaces and users.
    |            This method does not return anything. 
    |
    |   Pre-Condition: There exists a workspace table each with a unique,
    |                  generated workspaceId (identifier) and a name. There
    |                  also exists a workspaceMembership table which
    |                  connects workspaces to users.
    |
    |   Post-Condition: There is now a connection between the given user
    |                   and workspace (parameters). If there were sql
    |                   issues, this connection was not made.
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       userID -- Represents a user to connect the workspace to
    |                 using a workspaceMembership connection.
    |       workspaceID -- Represents a unique workspace to connect
    |                      to a user.
    |
    |   Returns: None.
    *--------------------------------------------------------------*/
    public void createMembership(Connection conn, int userID, int workspaceID) {
        // sql statement to create a new workspace membership
        // this is a connection between user and workspace
        String sqlStatement = "INSERT INTO orvik.workspaceMembership (userId, workspaceId) VALUES (?, ?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sqlStatement);
            // add in the user
            stmt.setInt(1, userID);
            // add in the worspace
            stmt.setInt(2, workspaceID);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            // catch sql exceptions
            System.err.println("Could not create a new workspace membership! : " + e.getMessage());
            return;
        }
    }

    /*---------------------------------------------------------------
    |   Method changeWorkspaceName(Connection conn, int workspaceID, 
    |                              String newName)
    |
    |   Purpose: This method allows for the changing of the name of a
    |            specific workspace identified by its workspaceId. The 
    |            name is changed into another paramter, newName. If this
    |            is done successfully, the method returns true. Otherwise,
    |            if there are sql issues, false is returned.
    |
    |   Pre-Condition: There exists a workspace table each with a unique,
    |                  generated workspaceId (identifier) and a name. There
    |                  also exists a workspaceMembership table which
    |                  connects workspaces to users.
    |
    |   Post-Condition: The name of a specific workspace (identified
    |                   by workspaceId) has been changed to a new value
    |                   (given by paramter newName). If there were sql
    |                   issues, this was not done.
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       workspaceID -- Represents a unique workspace to have its name
    |                      changed to the newName value.
    |       newName -- A string representing the new name for the workspace.
    |
    |   Returns: This method returns true if the name of the workspace
    |            was successfully changed to the parameter newName. If there
    |            were any issues in sql, false is returned.
    *--------------------------------------------------------------*/
    public boolean changeWorkspaceName(Connection conn, int workspaceID, String newName) {
        // sql statement to change workspace name
        String sqlStatement = "UPDATE orvik.workspace SET name = ? WHERE workspaceId = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sqlStatement);
            // add new (parameter) name
            stmt.setString(1, newName);
            // only for specific workspaceId
            stmt.setInt(2, workspaceID);
            stmt.executeUpdate();
            stmt.close();
            // name has been changed, return true
            return true;
        } catch (SQLException e) {
            // catch sql exceptions
            System.err.println("Could not update name! : " + e.getMessage());
            return false;
        }
    }

    /*---------------------------------------------------------------
    |   Method addWorkspaceConvo(Connection conn, int userID, 
    |                            int personaID, int workspaceID, 
    |                            String title)
    |
    |   Purpose: This method adds a conversation to a workspace. In
    |            order for this to happen, a condition must first be met.
    |            The given user (for the conversation) must "own" the
    |            workspace. This can be checked in the workspaceMembership
    |            table. After this, the newConvo() method can be called
    |            to create a conversation and add it to the conversation
    |            table. This method also generates a unique conversationId
    |            which is returned by this method. If the condition is not
    |            met, or there is a sql issue, -1 is returned.
    |
    |   Pre-Condition: There exists workspace, workspaceMembership, 
    |                  conversation, and Users tables. All 4 of these
    |                  tables will be used within this method. 
    |
    |   Post-Condition: A new conversation has been created and added
    |                   to the conversation table. The new row has been
    |                   given a generated conversationId and that has been
    |                   returned by this method.
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       userID -- The user to connect to the workspace in the conversation
    |                 and the user to check for with workspace ownership.
    |       personaID -- The persona inside of the conversation - used by 
    |                    the newConvo() method.
    |       workspaceID -- Represents a unique workspace to add a conversation
    |                      to, and to check its ownership by userid.
    |       String title -- The title of the new conversation.
    |
    |   Returns: An integer representing the newly created workspace 
    |            conversation is returned (conversationId). Also, this 
    |            conversation is inserted into the convo table.
    *--------------------------------------------------------------*/
    public int addWorkspaceConvo(Connection conn, int userID, int personaID, int workspaceID, String title) {
        // sql statement to get items in workspaceMembership with specific userId and
        // workspaceId
        String belongs = "SELECT COUNT(*) FROM orvik.workspaceMembership WHERE workspaceId = ? AND userId = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(belongs);
            // add specific workspaceId
            stmt.setInt(1, workspaceID);
            // add specific userId
            stmt.setInt(2, userID);
            ResultSet rs = stmt.executeQuery();
            // need to check if it exists in the table
            // need to know if the user owns that workspace
            if (rs.next() && rs.getInt(1) > 0) {
                // create new conversation
                // using jordan's method
                int convoID = newConvo(conn, userID, personaID, workspaceID, title);
                stmt.close();
                rs.close();
                // return the new conversation id
                return convoID;
            }
            // if it doesn't exist, return -1
            return -1;
        } catch (SQLException e) {
            // catch sql exceptions
            System.err.println("Could not read from the table: " + e.getMessage());
            return -1;
        }
    }

    // FUNCTIONALITY #4 (Jordan)
    /*---------------------------------------------------------------
    |   Method createPersona(Connection conn, String name, String directive)
    |
    |   Purpose: This method will create a new persona(lity) in our LLM system.
    |            You can give it a name as well as its instructions.
    |            After this is done, the persona will be added to the
    |            persona table.
    |
    |
    |   Pre-Condition: None (apart from the connection being solid.)
    |
    |   Post-Condition: The new persona with its name and instructions will be created.
    |
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       name -- The name of the new persona.
    |       directive -- The instructions of the new persona. The model will read this when generating messages.
    |
    |   Returns: The ID of the new persona such that it can be referenced later.
    *--------------------------------------------------------------*/
    public int createPersona(Connection conn, String name, String directive) {

        // see newConvo's comments, they're almost identical to this.
        String sqlStatement = "INSERT INTO orvik.persona (personaId, name, instructions) VALUES (persona_seq.nextval, ?, ?)";
        try {
            String[] generatedCols = { "personaId" };
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

    /*---------------------------------------------------------------
    |   Method delete(Connection conn, int personaID)
    |
    |   Purpose: This method will delete a persona from our LLM system.
    |
    |
    |   Pre-Condition: personaID must point to a valid persona in the table.
    |
    |   Post-Condition: The given persona will be deleted from the persona table.
    |
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       personaID -- The integer representing the current persona to be deleted.
    |
    |   Returns: A boolean representing whether or not the deletion was successful.
    *--------------------------------------------------------------*/
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

    // FUNCTIONALITY #5 (Pearl)

    /*---------------------------------------------------------------
    |   Method addPromptTemplate(Connection conn, String title, 
    |                            String content, int userID,
    |                            int workspaceID)
    |
    |   Purpose: This method creates a new promptTemplate and adds it
    |            as a new row to the promptTemplate table. In the process
    |            a unique value is generated to represent the
    |            promptTemplate (templateId) and this is returned.
    |
    |   Pre-Condition: There exists a promptTemplate table where each
    |                  row has 6 values: templateId (PK), title, content
    |                  userId, workspaceId.
    |
    |   Post-Condition: A new row has been added to the promptTemplate
    |                   table, with a newly generated identifier 
    |                   (templateId). The parameter values have been
    |                   inserted along with the generated id and the
    |                   current date/time. If there were sql issues,
    |                   -1 has been returned.
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       title -- A string representing the title of the newly
    |                created prompt template.
    |       content -- A string representing the contents of the newly
    |                  created prompt template.
    |       userID -- An int of the userId to assign this prompt
    |                 template to.
    |       workspaceID -- An int of the workspaceID to assign this
    |                      prompt template to.
    |
    |   Returns: An integer representing the newly created prompt template
    |            is returned (templateId). Also, this new template is 
    |            inserted into the template table.
    *--------------------------------------------------------------*/
    public int addPromptTemplate(Connection conn, String title, String content, int userID, int workspaceID) {
        String sqlStatement = "INSERT INTO orvik.promptTemplate (templateId, title, content, userId, workspaceId) VALUES (promptTemplate_seq.nextval, ?, ?, ?, ?)";
        try {
            String[] generatedCols = { "templateId" };
            PreparedStatement stmt = conn.prepareStatement(sqlStatement, generatedCols);
            int templateId = -1;
            // add title parameter
            stmt.setString(1, title);
            // add content parameter
            stmt.setString(2, content);
            // add userID parameter
            stmt.setInt(3, userID);
            // add workspaceID parameter
            stmt.setInt(4, workspaceID);
            stmt.executeUpdate();
            stmt.close();

            // generate templateId
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                templateId = rs.getInt(1);
            }
            // return the generated templateId
            return templateId;
        } catch (SQLException e) {
            // catch sql exception
            System.err.println("Could not create prompt template! : " + e.getMessage());
            return -1;
        }
    }

    /*---------------------------------------------------------------
    |   Method updatePromptTemplate(Connection conn, int templateID,
    |                              String toUpdate, String changeStr)
    |
    |   Purpose: This method updates a value in the promptTemplate 
    |            table if its templateId matches that of the parameter.
    |            From there, the toUpdate parameter decides what value
    |            is being updated: the title or the content of the 
    |            prompt template. If it's neither of these, the input is
    |            invalid and false is returned. The last parameter 
    |            is what the new value should be. If all of this is done
    |            successfully, then true is returned. Otherwise, false
    |            is returned.
    |
    |   Pre-Condition: A promptTemplate table exists, and for each row
    |                  there are 5 values. Two of these are the title
    |                  and content of each promptTemplate.
    |
    |   Post-Condition: A row of the promptTemplate table has been
    |                   adjusted based on the parameters. Either the
    |                   content or title has been changed. If the specific
    |                   row does not exist, false has been returned.
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       templateID -- The identifier (PK) for which template
    |                     that we want to update in some way.
    |       toUpdate -- This is a string that will either be "title",
    |                   "content", or something invalid. This identifies
    |                   which value we want to change.
    |       changeStr -- This string identifies what we want to change the
    |                    above identified value to. So, what the new title
    |                    or content will be.
    |
    |   Returns: This returns true if the table was successfully updated
    |            and false otherwise. If the toUpdate string is not
    |            "title" or "content", then false is automatically returned.
    *--------------------------------------------------------------*/
    public boolean updatePromptTemplate(Connection conn, int templateID, String toUpdate, String changeStr) {
        // if we want to update the prompt template title
        if (toUpdate.equals("title")) {
            // sql statement for changing the title
            String sqlStatement = "UPDATE orvik.promptTemplate SET title = ? WHERE templateId = ?";
            try {
                PreparedStatement stmt = conn.prepareStatement(sqlStatement);
                // what we are changing it to
                stmt.setString(1, changeStr);
                // primary key
                stmt.setInt(2, templateID);
                stmt.executeUpdate();
                stmt.close();
                // return true, title successfully changed
                return true;
            } catch (SQLException e) {
                // catch sql exceptions
                System.err.println("Could not update prompt template title! : " + e.getMessage());
                return false;
            }
        }
        // if we want to update the prompt template content
        else if (toUpdate.equals("content")) {
            // sql statement for changing the content
            String sqlStatement = "UPDATE orvik.promptTemplate SET content = ? WHERE templateId = ?";
            try {
                PreparedStatement stmt = conn.prepareStatement(sqlStatement);
                // what we're changing it to
                stmt.setString(1, changeStr);
                // primary key
                stmt.setInt(2, templateID);
                stmt.executeUpdate();
                stmt.close();
                // return true, content successfully changed
                return true;
            } catch (SQLException e) {
                // catch sql exceptions
                System.err.println("Could not update prompt template content! : " + e.getMessage());
                return false;
            }
        }
        // return false if invalid toUpdate value
        return false;
    }

    // FUNCTIONALITY #6 (Jordan)
    /*---------------------------------------------------------------
    |   Method updateSubscription(Connection conn, int userID, int tierID)
    |
    |   Purpose: Updates the subscription tier of a user in our LLM system.
    |            This will happen whenever a user subscribes to a higher tier and gets promoted
    |            or unsubscribes from a higher tier and gets demoted.
    |
    |
    |
    |   Pre-Condition: userID must be a valid user in our system.
    |                  tierID must be a valid tier in our system (corresponding to a row in the tier table).
    |
    |   Post-Condition: The subscription status of our user will be updated.
    |
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       userID -- The identifier (PK) for which user that we want to update in some way.
    |       tierID -- The tier that we will update the user to.
    |
    |   Returns: The ID of the new persona such that it can be referenced later.
    *--------------------------------------------------------------*/
    public boolean updateSubscription(Connection conn, int userID, int tierID) {

        // Uses an UPDATE statement instead of an INSERT.
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


    /*---------------------------------------------------------------
    |   Method withinLimit(Connection conn, int userID)
    |
    |   Purpose: This method will load a particular user's information,
    |            specifically the number of messages they have sent (tokens)
    |            and their message limit. Once it has those, it will check to see
    |            if they are within their message limit (based on their membership tier).
    |            This will be tied into sending messages and return an error message
    |            if they are over their limit.
    |
    |
    |   Pre-Condition: userID must be a valid user in our system.
    |
    |   Post-Condition: The limit of the user will be checked.
    |
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       userID -- The user that we are checking their limit against.
    |
    |   Returns: A boolean representing whether or not the user is within their limit or not.
    *--------------------------------------------------------------*/
    public boolean withinLimit(Connection conn, int userID) {
        // Takes the number of messages a user has sent and their message limit
        // and joins it with the user table
        // and the membership tier table
        String sql = "SELECT COUNT(m.messageId) AS totalMsgs, MAX(mt.messageLimit) AS msgLimit " +
                "FROM Users u " +
                "JOIN membershipTier mt ON u.tierId = mt.tierId " +
                "LEFT JOIN conversation c ON c.userId = u.userId " +
                "LEFT JOIN message m ON m.conversationId = c.conversationId " +
                "WHERE u.userId = ?";

        // Try/with syntax to handle sql exceptions
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                // Are we within the message limit?
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

    // FUNCTIONALITY #7 (Pearl)

    /*---------------------------------------------------------------
    |   Method newInvoice(Connection conn, int userID, int amount)
    |
    |   Purpose: This method creates a new invoice and adds it to the
    |            invoice table (if possible). In this process, an
    |            invoiceId is generated, and this is returned if the 
    |            process is successful. 
    |
    |   Pre-Condition: There exists an invoice table where each row has
    |                  6 values. They are: invoiceId (generated in this
    |                  method), userId, amount, date, and status.  
    |
    |   Post-Condition: If all works with SQL, a new invoice has been 
    |                   added to the invoice table with a newly generated, 
    |                   unique invoiceId. 
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       userID -- The identifier of the user for which a new
    |                 invoice needs to be created.
    |       amount -- The amount for the invoice to be added to the
    |                 new row in the invoice table.
    |
    |   Returns: An integer representing the newly created invoice is
    |            returned (invoiceId). Also, this new invoice is 
    |            inserted into the invoice table. 
    *--------------------------------------------------------------*/
    public int newInvoice(Connection conn, int userID, double amount) {
        // sql statement to add new invoice to table
        String sqlStatement = "INSERT INTO orvik.invoice (invoiceId, userId, amount, invoiceDate, status) VALUES (invoice_seq.nextval, ?, ?, ?, ?)";
        try {
            String[] generatedCols = { "invoiceId" };
            PreparedStatement stmt = conn.prepareStatement(sqlStatement, generatedCols);
            int invoiceId = -1;
            // add parameter userID
            stmt.setInt(1, userID);
            // add parameter amount
            stmt.setDouble(2, amount);
            // set date to current timestamp
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            // set status to unpaid
            stmt.setString(4, "UNPAID");
            stmt.executeUpdate();
            stmt.close();

            // generate invoiceId
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                invoiceId = rs.getInt(1);
            }
            // return newly generated invoiceId
            return invoiceId;
        } catch (SQLException e) {
            // catch sql exception
            System.err.println("Could not create invoice! : " + e.getMessage());
            return -1;
        }
    }

    /*---------------------------------------------------------------
    |   Method paidBill(Connection conn, int invoiceID)
    |
    |   Purpose: This method takes an invoiceID as a parameter and 
    |            makes the status of this invoice within the table
    |            "invoice" set to "PAID". If this happens, true is
    |            returned, otherwise, false is returned.
    |
    |   Pre-Condition: There exists an invoice table where each row has
    |                  6 values, and one of them is status, which either
    |                  has a value of "PAID" or "UNPAID".
    |
    |   Post-Condition: If it exists, the row with the invoiceId
    |                   identified as a parameter, has its status set
    |                   to "PAID" within the invoice table.
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       invoiceID -- The identifier of the invoice at which to 
    |                    change the status to "PAID"
    |
    |   Returns: A boolean which identifies whether the invoice with
    |            the identifier invoiceID had its status changed to
    |            "PAID". If this is not able to happen, false is 
    |            returned by the method.
    *--------------------------------------------------------------*/
    public boolean paidBill(Connection conn, int invoiceID) {
        // statement to update invoice status to "PAID"
        String sqlStatement = "UPDATE orvik.invoice SET status = ? WHERE invoiceId = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sqlStatement);
            // change to "PAID"
            stmt.setString(1, "PAID");
            stmt.setInt(2, invoiceID);
            stmt.executeUpdate();
            stmt.close();
            return true;
        } catch (SQLException e) {
            // catches sql exceptions
            System.err.println("Could not update invoice! : " + e.getMessage());
            return false;
        }
    }

    // FUNCTIONALITY #8 (Jordan)
    /*---------------------------------------------------------------
    |   Method openTicket(Connection conn, int userID, int agentID, String topic)
    |
    |   Purpose: This method will open a support ticket assigned to a user and an agent.
    |            This will be useful if we have any problems with our system, people can add to them
    |
    |
    |   Pre-Condition: userID and agentID must point to a valid user and agent in the table.
    |
    |   Post-Condition: The new support ticket will be added to the support ticket table with OPEN status.
    |
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       userID -- The integer representing the current user to be added to the support ticket.
    |       agentID -- The integer representing the current agent to be added to the support ticket.
    |       topic -- The string representing the topic of the support ticket
    |
    |   Returns: The ID of the ticket such that it can be referenced later.
    *--------------------------------------------------------------*/
    public int openTicket(Connection conn, int userID, int agentID, String topic) {
        String sqlStatement = "INSERT INTO orvik.supportTicket (ticketId, userId, agentId, topic, duration, outcome) VALUES (ticket_seq.nextval, ?, ?, ?, ?, ?)";
        try {
            String[] generatedCols = { "ticketId" };
            PreparedStatement stmt = conn.prepareStatement(sqlStatement, generatedCols);
            int ticketID = -1;
            stmt.setInt(1, userID);
            stmt.setInt(2, agentID);
            stmt.setString(3, topic);

            // the ticket has not been worked on now. set duration to 0.
            stmt.setInt(4, 0);

            // new ticket, so it is open.
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

    /*---------------------------------------------------------------
    |   MethoddeleteTicket(Connection conn, int ticketID)
    |
    |   Purpose: This method will close out a support ticket by setting the status to CLOSED.
    |
    |
    |   Pre-Condition: ticketID must point to a valid ticket in the table.
    |
    |   Post-Condition: The support ticket will be updated with CLOSED status.
    |
    |
    |   Parameters: 
    |       conn -- The Connection to JDBC to connect to SQL.
    |       ticketID -- The integer representing the current ticket to be added to the support ticket.
    |       duration -- The integer representing the duration of the ticket in minutes.
    |
    |
    |   Returns: A boolean which identifies whether the ticket was successfully closed.
    *--------------------------------------------------------------*/
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