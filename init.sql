-- Drop tables in reverse dependency order
-- This ensures we don't run into any foreign key constraint
-- issues when dropping tables. 
-- It is also used to reset the DBMS to a clean slate before re-creating
-- any tables and inserting new data. 
DROP TABLE supportTicket CASCADE CONSTRAINTS;
DROP TABLE invoice CASCADE CONSTRAINTS;
DROP TABLE billingProfile CASCADE CONSTRAINTS;
DROP TABLE promptTemplate CASCADE CONSTRAINTS;
DROP TABLE workspaceMembership CASCADE CONSTRAINTS;
DROP TABLE bookmark CASCADE CONSTRAINTS;
DROP TABLE feedback CASCADE CONSTRAINTS;
DROP TABLE message CASCADE CONSTRAINTS;
DROP TABLE conversation CASCADE CONSTRAINTS;
DROP TABLE workspace CASCADE CONSTRAINTS;
DROP TABLE persona CASCADE CONSTRAINTS;
DROP TABLE Users CASCADE CONSTRAINTS;
DROP TABLE membershipTier CASCADE CONSTRAINTS;
DROP TABLE agent CASCADE CONSTRAINTS;

-- Create sequences from 100 for all tables wiht auto-incrementing primary keys. 
-- This allows us to easily insert new data without worrying about PK conflicts. 
CREATE SEQUENCE convo_seq START WITH 100 INCREMENT BY 1;
CREATE SEQUENCE message_seq START WITH 100 INCREMENT BY 1;
CREATE SEQUENCE persona_seq START WITH 100 INCREMENT BY 1;
CREATE SEQUENCE ticket_seq START WITH 100 INCREMENT BY 1;

-- 1. membershipTier (no dependencies)
-- This table defines the different membership tiers available for users, along with
-- their names and the message limits associated with each tier. 
CREATE TABLE membershipTier(
    tierId INT PRIMARY KEY,
    tierName VARCHAR2(255) NOT NULL,
    messageLimit INT NOT NULL
);

-- 2. Users (depends on membershipTier)
-- This table defines a User, which represents a person using the system. 
-- It includes fields for their name and email, and also includes fields
-- for when their account was create,d their preferred language, and their
-- membership tier, as well as their user ID. 
CREATE TABLE Users(
    userId INT PRIMARY KEY,
    name VARCHAR2(255) NOT NULL,
    email VARCHAR2(255) NOT NULL UNIQUE,
    creationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    language VARCHAR2(50) NOT NULL,
    tierId INT NOT NULL,
    FOREIGN KEY (tierId) REFERENCES membershipTier(tierId)
);
-- 2.5 agent (depends on Users)
-- This table defines an agent, which is a special type of user that can be
-- assigned to support tickets. It has a one-to-=one relationship with the Users table,
-- so each agent is a user, but not all users are agents. The agent Id is the same as the user ID. 
CREATE TABLE agent(
    agentId INT PRIMARY KEY,
    FOREIGN KEY (agentId) REFERENCES Users(userId)
);

-- 3. persona (independent)
-- This table defines a persona, and exists independently of every other table.
-- It includes a name and instructions for the persona as well as a foreign key to the Users
-- table, which represents the creator of the persona. It also includes a persona ID to uniquely
-- identify each persona. 
CREATE TABLE persona(
    personaId INT PRIMARY KEY,
    name VARCHAR2(255) NOT NULL,
    instructions CLOB NOT NULL,
    userId INT,
    FOREIGN KEY (userId) REFERENCES Users(userId)
);

-- 4. workspace (independent)
-- This table defines a workspace, which also exists independently, and simply has a 
-- workspace ID for unique identification and a name for the workspace. 
CREATE TABLE workspace(
    workspaceId INT PRIMARY KEY,
    name VARCHAR2(255) NOT NULL
);

-- 5. conversation (depends on Users, persona, workspace)
-- This table defines a conversation, which represents a series of messages between
-- a user and the AI. It includes a foreign key to the Users table to represent the user.
-- It also includes an optional foreign key to the persona table which represents the persona
-- used in this conversation, and an optional foreign key to the workspace table giving
-- the workspace this conversation belongs to, if any. It includes a title and creation date
-- as well as a conversation ID for unique identification. 
CREATE TABLE conversation(
    conversationId INT PRIMARY KEY,
    userId INT NOT NULL,
    title VARCHAR2(255) NOT NULL,
    creationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    personaId INT,
    workspaceId INT,
    FOREIGN KEY (userId) REFERENCES Users(userId),
    FOREIGN KEY (personaId) REFERENCES persona(personaId),
    FOREIGN KEY (workspaceId) REFERENCES workspace(workspaceId)
);

-- 6. message (depends on conversation)
-- This table defines a message, which represents a single message in a conversation.
-- It includes a foreign key to teh conversation it belongs to as well as  role to indicate
-- whether the message is from the user or the AI, and the content and timestamp of the message.
-- Finally, it includes a message ID for unique identification. 
CREATE TABLE message(
    messageId INT PRIMARY KEY,
    conversationId INT NOT NULL,
    role VARCHAR2(50) NOT NULL,
    content CLOB NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversationId) REFERENCES conversation(conversationId)
);

-- 7. feedback (depends on message)
-- This table defines feedback, which represents user feedback on a particular message.
-- It includes a foreign key to the message it belongs to as well as the rating and feedback,
-- and a mesage ID for unique identification. It has a 1-to-1 relationship with message. 
CREATE TABLE feedback(
    messageId INT PRIMARY KEY,
    rating INT NOT NULL,
    feedback CLOB,
    FOREIGN KEY (messageId) REFERENCES message(messageId)
);

-- 8. bookmark (depends on Users + message)
-- This table defines a bookmark, which represents a user bookmarking a particular message.
-- It includes a foreign key to the Users table to represent the user who made the bookmark
-- as well as a foreign key to the message that was bookmarked. It has a composite primary key
-- of those two attributes. 
CREATE TABLE workspaceMembership(
    userId INT NOT NULL,
    workspaceId INT NOT NULL,
    PRIMARY KEY (userId, workspaceId),
    FOREIGN KEY (userId) REFERENCES Users(userId),
    FOREIGN KEY (workspaceId) REFERENCES workspace(workspaceId)
);

-- 10. promptTemplate (depends on Users + workspace)
-- This table defines a prompt template, which represents a reusable prompt that a user can create.
-- It includes a foreign key to the Users table to represent the creator of the prompt template,
-- as well as an optional foreign key to the workspace it belongs to, if any. It also includes a
-- title and content for the prompt template and a templateId for unique identification. 
CREATE TABLE promptTemplate(
    templateId INT PRIMARY KEY,
    title VARCHAR2(255) NOT NULL,
    content CLOB NOT NULL,
    userId INT NOT NULL,
    workspaceId INT,
    FOREIGN KEY (userId) REFERENCES Users(userId),
    FOREIGN KEY (workspaceId) REFERENCES workspace(workspaceId)
);

-- 11. billingProfile (depends on Users)
-- This table defines a billing profile, which represents the billing information for a user.
-- It includes a foreign key to the users table to represent the user it belongs to, which also acts
-- as the primary key for this table. It furthermore includes the payment method and billing address.
CREATE TABLE billingProfile(
    userId INT PRIMARY KEY,
    payMethod VARCHAR2(255) NOT NULL,
    billingAddress VARCHAR2(255) NOT NULL,
    FOREIGN KEY (userId) REFERENCES Users(userId)
);

-- 12. supportTicket (depends on Users twice)
-- This table defines a support ticket, which represents a support request made by a user. 
-- It includes a ticketId for unique identification, and foreign keys to the Users table for
-- who made the request and to the agent table for which agent is assigned to the ticket. 
-- It also includes a topic, duration, and the outcome of the ticket. 
CREATE TABLE supportTicket(
    ticketId INT PRIMARY KEY,
    userId INT NOT NULL,
    agentId INT NOT NULL,
    topic VARCHAR2(255) NOT NULL,
    duration INT NOT NULL,
    outcome VARCHAR2(255) NOT NULL,
    FOREIGN KEY (userId) REFERENCES Users(userId),
    FOREIGN KEY (agentId) REFERENCES agent(agentId)
);

-- 13. invoice (depends on Users)
-- This table defines an invoice, which represents a billing invoice for a user. 
-- It includes an invoiceId for unique identification as well as a foreign key to the Users
-- table to represent which user the invoice belongs to. It includes the amount, invoice date,
-- and status of the invoice. 
CREATE TABLE invoice(
    invoiceId INT PRIMARY KEY,
    userId INT NOT NULL,
    amount NUMBER(10, 2) NOT NULL,
    invoiceDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR2(50) NOT NULL,
    FOREIGN KEY (userId) REFERENCES Users(userId)
);

--This section grants permissions to the PUBLIC role for all tables. 
GRANT SELECT, INSERT, UPDATE, DELETE ON invoice TO PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE ON supportTicket TO PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE ON billingProfile TO PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE ON promptTemplate TO PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE ON workspace TO PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE ON workspaceMembership TO PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE ON persona TO PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE ON message TO PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE ON conversation TO PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE ON membershipTier TO PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE ON Users TO PUBLIC;

-- Each of the following sections populates the DBMS with some sample data. 

-- =========================
-- 1. membershipTier
-- =========================
INSERT INTO membershipTier VALUES (1, 'Free', 50);
INSERT INTO membershipTier VALUES (2, 'Plus', 200);
INSERT INTO membershipTier VALUES (3, 'Enterprise', 1000);

-- =========================
-- 2. Users
-- =========================
INSERT INTO Users VALUES (1, 'Alice Johnson', 'alice@example.com', CURRENT_TIMESTAMP, 'English', 2);
INSERT INTO Users VALUES (2, 'Bob Smith', 'bob@example.com', CURRENT_TIMESTAMP, 'English', 1);
INSERT INTO Users VALUES (3, 'Charlie Lee', 'charlie@example.com', CURRENT_TIMESTAMP, 'Spanish', 3);
INSERT INTO Users VALUES (4, 'Dana White', 'dana@example.com', CURRENT_TIMESTAMP, 'French', 2);

-- =========================
-- 2.5 agent
-- =========================
INSERT INTO agent VALUES (1);
INSERT INTO agent VALUES (2);

-- =========================
-- 3. persona
-- =========================
INSERT INTO persona VALUES (1, 'Technical Writer', 'Provide detailed and structured explanations.', 1);
INSERT INTO persona VALUES (2, 'Beginner Helper', 'Explain concepts simply and clearly.', 2);
INSERT INTO persona VALUES (3, 'Senior Architect', 'Focus on system design and scalability.', 1);

-- =========================
-- 4. workspace
-- =========================
INSERT INTO workspace VALUES (1, 'Cloud Migration');
INSERT INTO workspace VALUES (2, 'AI Research');

-- =========================
-- 5. workspaceMembership
-- =========================
INSERT INTO workspaceMembership VALUES (1, 1);
INSERT INTO workspaceMembership VALUES (2, 1);
INSERT INTO workspaceMembership VALUES (3, 2);
INSERT INTO workspaceMembership VALUES (4, 2);

-- =========================
-- 6. conversation
-- =========================
INSERT INTO conversation VALUES (1, 1, 'AWS Setup', CURRENT_TIMESTAMP, 3, 1);
INSERT INTO conversation VALUES (2, 2, 'Python Basics', CURRENT_TIMESTAMP, 2, NULL);
INSERT INTO conversation VALUES (3, 3, 'AI Trends', CURRENT_TIMESTAMP, 1, 2);
INSERT INTO conversation VALUES (4, 1, 'Database Design', CURRENT_TIMESTAMP, NULL, NULL);

-- =========================
-- 7. message
-- =========================
INSERT INTO message VALUES (1, 1, 'USER', 'How do I set up AWS?', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (2, 1, 'AI', 'Start by creating an IAM user...', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (3, 2, 'USER', 'What is a loop?', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (4, 2, 'AI', 'A loop repeats instructions...', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (5, 3, 'USER', 'What are current AI trends?', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (6, 3, 'AI', 'AI is evolving rapidly...', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (7, 4, 'USER', 'Explain normalization', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (8, 4, 'AI', 'Normalization reduces redundancy...', CURRENT_TIMESTAMP);

-- =========================
-- 8. feedback
-- =========================
INSERT INTO feedback VALUES (2, 1, 'Very helpful');
INSERT INTO feedback VALUES (4, 1, NULL);
INSERT INTO feedback VALUES (6, 0, 'Too vague');
INSERT INTO feedback VALUES (8, 1, 'Clear explanation');

-- =========================
-- 9. bookmark
-- =========================
INSERT INTO bookmark VALUES (1, 2);
INSERT INTO bookmark VALUES (1, 8);
INSERT INTO bookmark VALUES (2, 4);
INSERT INTO bookmark VALUES (3, 6);

-- =========================
-- 10. promptTemplate
-- =========================
INSERT INTO promptTemplate VALUES (1, 'Summarize Text', 'Summarize the following:', 1, NULL);
INSERT INTO promptTemplate VALUES (2, 'Translate', 'Translate this to Spanish:', 2, NULL);
INSERT INTO promptTemplate VALUES (3, 'Legal Summary', 'Summarize legal document:', 1, 1);
INSERT INTO promptTemplate VALUES (4, 'Code Review', 'Review this code for issues:', 3, 2);

-- =========================
-- 11. billingProfile
-- =========================
INSERT INTO billingProfile VALUES (1, 'Visa', '123 Main St');
INSERT INTO billingProfile VALUES (2, 'MasterCard', '456 Elm St');
INSERT INTO billingProfile VALUES (3, 'Amex', '789 Oak Ave');

-- =========================
-- 12. invoice
-- =========================
INSERT INTO invoice VALUES (1, 1, 20.00, CURRENT_TIMESTAMP, 'PAID');
INSERT INTO invoice VALUES (2, 2, 15.00, CURRENT_TIMESTAMP, 'UNPAID');
INSERT INTO invoice VALUES (3, 3, 50.00, CURRENT_TIMESTAMP, 'UNPAID');
INSERT INTO invoice VALUES (4, 1, 25.00, CURRENT_TIMESTAMP, 'PAID');

-- =========================
-- 13. supportTicket
-- =========================
INSERT INTO supportTicket VALUES (1, 2, 1, 'Billing Issue', 30, 'RESOLVED');
INSERT INTO supportTicket VALUES (2, 3, 1, 'Model Error', 45, 'ESCALATED');
INSERT INTO supportTicket VALUES (3, 4, 2, 'Login Problem', 20, 'RESOLVED');

-- =========================
-- EXTRA MESSAGES
-- =========================

-- Conversation 1 (AWS Setup) - add more depth
INSERT INTO message VALUES (9, 1, 'USER', 'What is an IAM role?', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (10, 1, 'AI', 'An IAM role is a secure identity in AWS...', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (11, 1, 'USER', 'How do I attach policies?', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (12, 1, 'AI', 'You can attach policies via the IAM console...', CURRENT_TIMESTAMP);

-- Conversation 2 (Python Basics)
INSERT INTO message VALUES (13, 2, 'USER', 'How do functions work?', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (14, 2, 'AI', 'Functions are reusable blocks of code...', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (15, 2, 'USER', 'What is recursion?', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (16, 2, 'AI', 'Recursion is when a function calls itself...', CURRENT_TIMESTAMP);

-- Conversation 3 (AI Trends)
INSERT INTO message VALUES (17, 3, 'USER', 'What is a transformer model?', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (18, 3, 'AI', 'Transformers use attention mechanisms...', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (19, 3, 'USER', 'How is ChatGPT trained?', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (20, 3, 'AI', 'It is trained using reinforcement learning...', CURRENT_TIMESTAMP);

-- Conversation 4 (Database Design)
INSERT INTO message VALUES (21, 4, 'USER', 'What is BCNF?', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (22, 4, 'AI', 'BCNF ensures no functional dependency violations...', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (23, 4, 'USER', 'What is a foreign key?', CURRENT_TIMESTAMP);
INSERT INTO message VALUES (24, 4, 'AI', 'A foreign key links two tables together...', CURRENT_TIMESTAMP);

-- =========================
-- EXTRA FEEDBACK
-- =========================

-- Conversation 1
INSERT INTO feedback VALUES (10, 1, 'Very clear explanation');
INSERT INTO feedback VALUES (12, 1, 'Helpful AWS detail');
INSERT INTO feedback VALUES (2, 1, 'Good starter answer');

-- Conversation 2
INSERT INTO feedback VALUES (14, 1, 'Good explanation');
INSERT INTO feedback VALUES (16, 0, 'Too abstract');
INSERT INTO feedback VALUES (4, 1, 'Helpful');

-- Conversation 3
INSERT INTO feedback VALUES (18, 0, 'Too vague');
INSERT INTO feedback VALUES (20, 0, 'Not detailed enough');
INSERT INTO feedback VALUES (6, 0, 'Unclear response');

-- Conversation 4
INSERT INTO feedback VALUES (22, 1, 'Excellent explanation');
INSERT INTO feedback VALUES (24, 1, 'Very clear');
INSERT INTO feedback VALUES (8, 1, 'Great breakdown');

COMMIT;