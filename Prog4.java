import java.sql.*;
import java.time.*;
import java.util.*;

public class Prog4
{
    // CONN + INTERFACE (Annabelle <== comments are for TAs so I'm using this name)
    public static void main(String[] args) {
        // Call connector to connect to DB
        connector();
    }


    // Connects to the Database and calls loopMechanism
    private void connector() {
        // INITIALIZE (including all DB stuff)
        System.out.println("");
        System.out.println("PLACEHOLDER WELCOME MESSAGE: DATABASE LOADING...");

        // Magic lectura -> aloe access spell
        // change this depending on team verdict of hardcoding this
        final String oracleURL =
            "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
        // Oracle DBMS username, password
        String username = "ajonatan", password = "FALSE PASSWORD";

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
            conn = DriverManager.getConnection(oracleURL,username,password);
        } catch (SQLException e) {
            System.err.println("*** SQLException:  "
                + "Could not open JDBC connection.");
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
    private void loopMechanism(Connection conn) {
        // Boolean for loop control
        Boolean loopRun = true;
        // Scanner for user input
        Scanner input = new Scanner(System.in);
        // String for current user query
        String query = "";

        // SHOW OPTIONS
        printOptions();
        System.out.println("");

        while (loopRun == true)
        {
            System.out.print("--> ENTER QUERY: ");
            query = input.nextLine();
            System.out.print("");

            if (query.contentEquals("exit"))
                loopRun = false;
            else if (query.contentEquals("help"))
                printOptions();
            else
                queryToAction(query, conn);
            System.out.println("");
        }
        System.out.println("");
        System.out.println("EXIT RECEIEVED; CLOSING CONNECTION...");
    }


    // Prints all the user functionality options
    private void printOptions() {
        System.out.println("");
        System.out.println("|-----------------------------------------------------------------|");
        System.out.println("FUNCTIONALITIES:");
        System.out.println("   USER...");
        System.out.println("      add user: 'user.add <string NAME> <string EMAIL> <string LANGUAGE> <int TIER>'");
        System.out.println("      update user information: 'user.update <int USERID> <string name/email/language" +
                                    " OR int tier> <string NEW NAME/ETC.> <int NEW TIER>'");
        System.out.println("      delete user: 'user.delete <int USERID>'");
        System.out.println("   CONVERSATION...");
        System.out.println("      ");
        System.out.println("   WORKSPACE...");
        System.out.println("      ");
        System.out.println("   PERSONA...");
        System.out.println("      ");
        System.out.println("   TEMPLATE...");
        System.out.println("      ");
        System.out.println("   SUBSCRIPTION...");
        System.out.println("      ");
        System.out.println("   INVOICE...");
        System.out.println("      ");
        System.out.println("   TICKET...");
        System.out.println("      ");
        System.out.println("|-----------------------------------------------------------------|");
        System.out.println("");
    }


    // Processes query sent by user and sends it on its way to the right method
    // Currently partial duplicate of code from my Prog3; not functional! 
    private void queryToAction(String query, Connection dbconn)
    {
        // Split for processing the query and its parameters
        String[] split = query.split(" ");

        // Check if the query matches the format requirements for each of the 4
        if ((query.charAt(0) == '1') && (split.length == 1))
        {
            // If so, convert to the SQL queries and load to the needed method
            queryOne(new String[]{"SELECT count(*) as numOfIncidents FROM "
            + "ajonatan1980", "SELECT count(*) as numOfIncidents FROM "
            + "ajonatan1995", "SELECT count(*) as numOfIncidents FROM "
            + "ajonatan2010", "SELECT count(*) as numOfIncidents FROM "
            + "ajonatan2025"}, dbconn);
            return;
        }
        else if ((query.charAt(0) == '2') && (split.length == 2))
        {
            if (isViable(split[1]))
            {
                queryTwo("SELECT * FROM ( SELECT statename, COUNT(*) as" +
                    " numOfIncidents FROM ajonatan" + Integer.parseInt(split[1])
                    + " GROUP BY statename ORDER BY numOfIncidents DESC ) " +
                    "WHERE ROWNUM <= 10", dbconn);
                return;
            }
        }
        // If it matches none of them, print an error and move on
        System.out.println("ERROR: INCORRECT SYNTAX OR QUERY.");
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
            String[] generatedCols = {"userId"};
            int userId = -1;
            PreparedStatement stmt = conn.prepareStatement(sqlStatement, generatedCols);
            // name parameter added
            stmt.setString(1, name);
            // email parameter added
            stmt.setString(2, email);
            // current date/time added as date
            stmt.setTimestamp(3, new Timestamp(LocalTime.now().toNanoOfDay()));
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
                stmt.setString(2, userID);
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
                stmt.setString(2, userID);
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
                stmt.setString(2, userID);
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
            String[] generatedCols = {"workspaceId"};
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
        //return false;
    }


    // FUNCTIONALITY #4 (Jordan)
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
            String[] generatedCols = {"templateId"};
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
    public int newInvoice(Connection conn, int userID, int amount) {
        // sql statement to add new invoice to table
        String sqlStatement = "INSERT INTO orvik.invoice (invoiceId, userId, amount, date, status) VALUES (invoice_seq.nextval, ?, ?, ?, ?)";
        try {
            String[] generatedCols = {"invoiceId"};
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