-- Drop tables in reverse dependency order
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

-- 1. membershipTier (no dependencies)
CREATE TABLE membershipTier(
    tierId INT PRIMARY KEY,
    tierName VARCHAR2(255) NOT NULL,
    messageLimit INT NOT NULL
);

-- 2. Users (depends on membershipTier)
CREATE TABLE Users(
    userId INT PRIMARY KEY,
    name VARCHAR2(255) NOT NULL,
    email VARCHAR2(255) NOT NULL UNIQUE,
    creationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    language VARCHAR2(50) NOT NULL,
    tierId INT NOT NULL,
    FOREIGN KEY (tierId) REFERENCES membershipTier(tierId)
);

CREATE TABLE agent(
    agentId INT PRIMARY KEY,
    FOREIGN KEY (agentId) REFERENCES Users(userId)
);

-- 3. persona (independent)
CREATE TABLE persona(
    personaId INT PRIMARY KEY,
    name VARCHAR2(255) NOT NULL,
    instructions CLOB NOT NULL,
    userId INT,
    FOREIGN KEY (userId) REFERENCES Users(userId)
);

-- 4. workspace (independent)
CREATE TABLE workspace(
    workspaceId INT PRIMARY KEY,
    name VARCHAR2(255) NOT NULL
);

-- 5. conversation (depends on Users, persona, workspace)
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
CREATE TABLE message(
    messageId INT PRIMARY KEY,
    conversationId INT NOT NULL,
    role VARCHAR2(50) NOT NULL,
    content CLOB NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversationId) REFERENCES conversation(conversationId)
);

-- 7. feedback (depends on message)
CREATE TABLE feedback(
    messageId INT PRIMARY KEY,
    rating INT NOT NULL,
    feedback CLOB,
    FOREIGN KEY (messageId) REFERENCES message(messageId)
);

-- 8. bookmark (depends on Users + message)
CREATE TABLE bookmark(
    userId INT NOT NULL,
    messageId INT NOT NULL,
    PRIMARY KEY (userId, messageId),
    FOREIGN KEY (userId) REFERENCES Users(userId),
    FOREIGN KEY (messageId) REFERENCES message(messageId)
);

-- 9. workspaceMembership (depends on Users + workspace)
CREATE TABLE workspaceMembership(
    userId INT NOT NULL,
    workspaceId INT NOT NULL,
    PRIMARY KEY (userId, workspaceId),
    FOREIGN KEY (userId) REFERENCES Users(userId),
    FOREIGN KEY (workspaceId) REFERENCES workspace(workspaceId)
);

-- 10. promptTemplate (depends on Users + workspace)
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
CREATE TABLE billingProfile(
    userId INT PRIMARY KEY,
    payMethod VARCHAR2(255) NOT NULL,
    billingAddress VARCHAR2(255) NOT NULL,
    FOREIGN KEY (userId) REFERENCES Users(userId)
);

-- 12. supportTicket (depends on Users twice)
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
CREATE TABLE invoice(
    invoiceId INT PRIMARY KEY,
    userId INT NOT NULL,
    amount NUMBER(10, 2) NOT NULL,
    invoiceDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR2(50) NOT NULL,
    FOREIGN KEY (userId) REFERENCES Users(userId)
);

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

COMMIT;