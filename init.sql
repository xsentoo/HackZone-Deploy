-- ATTENTION : FICHIER CORRIGÉ PAR [VOTRE NOM] LE [DATE]
-- Correction : L'ordre de création des tables est placé avant les commandes DELETE/INSERT
-- Il assure la cohérence des 17 défis (2 du binôme + 15 pour le reste du projet).

-- ------------------------------------------------------------
-- PARTIE 1 : TARGET APP (La Victime - Port 8081)
-- ------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS TargetDB;
USE TargetDB;

-- FORCER L'ENCODAGE DE LA SESSION POUR LA BD VICTIME (Pour la cohérence)
SET NAMES 'utf8mb4';
SET CHARACTER SET utf8mb4;

-- Table Users vulnerable a l'injection SQL
CREATE TABLE IF NOT EXISTS Users (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    secret_data VARCHAR(100) -- Contient le Flag
    );

-- Insertion des donnees de la victime
INSERT IGNORE INTO Users (username, password, secret_data) VALUES
('admin', 'admin123', 'FLAG{SQL_LEVEL_1_COMPLETED}'),
('client', '1234', 'Solde: 0 EUR');

-- --- AJOUT POUR LE NIVEAU 2 (SQL UNION) ---

-- 1. Une table normale (Les Produits)
CREATE TABLE IF NOT EXISTS Products (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(100),
    category VARCHAR(50),
    price DECIMAL(10,2)
    );

INSERT IGNORE INTO Products (name, category, price) VALUES
('T-Shirt HackZone', 'Vetements', 25.00),
('Hoodie Noir', 'Vetements', 45.00),
('Mug Developpeur', 'Accessoires', 12.50),
('Cle USB 64Go', 'Electronique', 15.00);

-- 2. Une table secrete
CREATE TABLE IF NOT EXISTS SecretConfig (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            config_name VARCHAR(50),
    config_value VARCHAR(100)
    );

INSERT IGNORE INTO SecretConfig (config_name, config_value) VALUES
('admin_email', 'admin@bankofhack.com'),
('FLAG_LEVEL_2', 'FLAG{UNION_SELECT_IS_POWERFUL}');


-- ------------------------------------------------------------
-- PARTIE 2 : HACKZONE (Le QG - Port 8080)
-- ------------------------------------------------------------
-- CRÉATION BD AVEC ENCODAGE FORCÉ
CREATE DATABASE IF NOT EXISTS HackZone CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE HackZone;

-- FORCER L'ENCODAGE DE LA SESSION POUR LES INSERTIONS (CRITIQUE POUR LES ACCENTS)
SET NAMES 'utf8mb4';
SET CHARACTER SET utf8mb4;

-- --- 1. CRÉATION DES TABLES ---
-- Table 1 : Les Utilisateurs (Hackers)
CREATE TABLE IF NOT EXISTS UserHack (
                                        userId INT AUTO_INCREMENT PRIMARY KEY,
                                        userName VARCHAR(255) NOT NULL,
    userMail VARCHAR(255) NOT NULL UNIQUE,
    userPWD VARCHAR(255) NOT NULL,
    level ENUM('deb','int','avan') DEFAULT 'deb',
    userBadge VARCHAR(255) DEFAULT 'Novice',
    point INT DEFAULT 0,
    userDate DATE DEFAULT (curdate())
    );

-- Table 2 : Le Catalogue des Attaques (Challenges)
CREATE TABLE IF NOT EXISTS Attacks (
                                       attId INT AUTO_INCREMENT PRIMARY KEY,
                                       title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    difficulty ENUM('deb','int','avan') NOT NULL,
    target_url VARCHAR(255),
    flag VARCHAR(255) NOT NULL,
    points INT DEFAULT 10
    );

-- Table 3 : Les Validations (Anti-Triche & Historique)
CREATE TABLE IF NOT EXISTS Solves (
                                      solveId INT AUTO_INCREMENT PRIMARY KEY,
                                      userId INT NOT NULL,
                                      attId INT NOT NULL,
                                      solved_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                      UNIQUE KEY unique_solve (userId, attId),
    CONSTRAINT fk_user FOREIGN KEY (userId) REFERENCES UserHack(userId) ON DELETE CASCADE,
    CONSTRAINT fk_attack FOREIGN KEY (attId) REFERENCES Attacks(attId) ON DELETE CASCADE
    );

-- --- 2. NETTOYAGE DES ANCIENNES DONNÉES (CORRECTION DE L'ERREUR D'ORDRE) ---

-- Note : Nous pouvons supprimer des données seulement après que la table Solves existe.
DELETE FROM Solves WHERE solveId > 0;
ALTER TABLE Solves AUTO_INCREMENT = 1;

DELETE FROM Attacks WHERE attId > 0;
ALTER TABLE Attacks AUTO_INCREMENT = 1;

-- Définition du message d'avertissement
SET @WARNING_MSG = ' ATTENTION : L''utilisation illégale de ces techniques sur des systèmes non autorisés est punie par la loi. Vous êtes responsable de vos actes.';


-- --- 3. INSERTION DES DONNÉES (2 Challenges du Binôme + 15 de Votre Partie) ---

-- Insertion des 2 Challenges du Binôme (SQL)
INSERT IGNORE INTO Attacks (title, description, category, difficulty, target_url, flag, points) VALUES
(
    'Injection SQL - Niveau 1 (Login Bypass)',
    'Contournez l''authentification de la page de connexion pour obtenir le Flag de l''administrateur. (Hint: Utiliser OR)',
    'SQL',
    'deb',
    'http://localhost:8081/login',
    'FLAG{SQL_LEVEL_1_COMPLETED}',
    50
),
(
    'Injection SQL - Niveau 2 (Union Select)',
    'La boutique filtre mal les catégories. Utilisez UNION SELECT pour voler les données de la table SecretConfig.',
    'SQL',
    'int',
    'http://localhost:8081/shop',
    'FLAG{UNION_SELECT_IS_POWERFUL}',
    100
);

-- Insertion des 15 Challenges de VOTRE PARTIE (3 Catégories x 5 Défis)
INSERT IGNORE INTO Attacks (title, description, category, difficulty, target_url, flag, points) VALUES

-- --- CATÉGORIE 1 : OSINT (5 Défis) ---
(
    'OSINT - Niveau 1 (Google Dorking)',
    CONCAT('Utilisez Google pour trouver un fichier backup exposé sur le site cible. Indice : site:cible.com filetype:sql', @WARNING_MSG),
    'OSINT',
    'deb',
    'https://www.google.com',
    'HACKZONE{g00gl3_d0rk1ng_b4s1c}',
    100
),
(
    'OSINT - Niveau 2 (Email Hunter)',
    CONCAT('Trouvez l''adresse email du CEO de l''entreprise fictive TechSecure Corp. Indice : Cherchez sur LinkedIn ou le site officiel.', @WARNING_MSG),
    'OSINT',
    'deb',
    'https://www.linkedin.com',
    'HACKZONE{c3o@t3chsecur3.com}',
    150
),
(
    'OSINT - Niveau 3 (Subdomain Discovery)',
    CONCAT('Trouvez 3 sous-domaines cachés de target-company.com. Outils suggérés : crt.sh ou subfinder.', @WARNING_MSG),
    'OSINT',
    'int',
    'https://crt.sh',
    'HACKZONE{admin.target-company.com}',
    200
),
(
    'OSINT - Niveau 4 (Shodan Master)',
    CONCAT('Utilisez Shodan pour trouver des caméras IP non sécurisées dans une ville donnée. Requête : port:554 country:"FR" city:"Paris".', @WARNING_MSG),
    'OSINT',
    'int',
    'https://www.shodan.io',
    'HACKZONE{sh0d4n_1s_p0w3rful}',
    250
),
(
    'OSINT - Niveau 5 (The Full Recon)',
    CONCAT('Réalisez une reconnaissance complète de mega-corp.com : version du serveur, technologies, ports ouverts.', @WARNING_MSG),
    'OSINT',
    'avan',
    'https://builtwith.com',
    'HACKZONE{full_r3c0n_m4st3r_2024}',
    300
),

-- --- CATÉGORIE 2 : BRUTE FORCE (5 Défis) ---
(
    'Brute Force - Niveau 1 (Weak Password)',
    CONCAT('Un compte admin utilise un mot de passe dans le top 100. Login : admin. Hash MD5 : 5f4dcc3b5aa765d61d8327deb882cf99.', @WARNING_MSG),
    'BRUTE_FORCE',
    'deb',
    'http://localhost:8081/login',
    'HACKZONE{adm1n_p4ssw0rd_w34k}',
    100
),
(
    'Brute Force - Niveau 2 (Hash Cracker)',
    CONCAT('Crackez ce hash MD5 : 098f6bcd4621d373cade4e832627b4f6. Wordlist : rockyou.txt', @WARNING_MSG),
    'BRUTE_FORCE',
    'deb',
    'https://crackstation.net',
    'HACKZONE{h4sh_cr4ck3d_md5}',
    150
),
(
    'Brute Force - Niveau 3 (SSH Bruteforce)',
    CONCAT('Serveur SSH mal configuré. Cible : **localhost:2222**, User : root. Utilisez Hydra avec common_passwords.txt. Mot de passe : **root**.', @WARNING_MSG),
    'BRUTE_FORCE',
    'int',
    'http://localhost:8081/ssh-challenge',
    'HACKZONE{ssh_brut3f0rc3_succ3ss}',
    200
),
(
    'Brute Force - Niveau 4 (ZIP Password Recovery)',
    CONCAT('Une archive ZIP protégée contient un document secret. Utilisez fcrackzip ou John the Ripper.', @WARNING_MSG),
    'BRUTE_FORCE',
    'int',
    'http://localhost:8081/download/secret_docs.zip',
    'HACKZONE{z1p_p4ssw0rd_r3c0v3r3d}',
    250
),
(
    'Brute Force - Niveau 5 (Rainbow Table Attack)',
    CONCAT('Utilisez des rainbow tables pour craquer rapidement des hashes SHA-1.', @WARNING_MSG),
    'BRUTE_FORCE',
    'avan',
    'https://crackstation.net',
    'HACKZONE{r41nb0w_t4bl3_4tt4ck}',
    300
),

-- --- CATÉGORIE 3 : NETWORK ANALYSIS (5 Défis) ---
(
    'Analyse Réseau - Niveau 1 (Packet Sniffer)',
    CONCAT('Capturez le trafic HTTP et trouvez le mot de passe envoyé en clair. Fichier : traffic.pcap.', @WARNING_MSG),
    'NETWORK_ANALYSIS',
    'deb',
    'http://localhost:8081/download/traffic.pcap',
    'HACKZONE{p4ck3t_sn1ff3d_http}',
    100
),
(
    'Analyse Réseau - Niveau 2 (FTP Credentials)',
    CONCAT('Un utilisateur s''est connecté à un serveur FTP. Trouvez ses identifiants dans ftp_capture.pcap. Filtre : ftp.', @WARNING_MSG),
    'NETWORK_ANALYSIS',
    'deb',
    'http://localhost:8081/download/ftp_capture.pcap',
    'HACKZONE{ftp_us3r:ftp_p4ss}',
    150
),
(
    'Analyse Réseau - Niveau 3 (ARP Spoofing Detection)',
    CONCAT('Analysez le trafic réseau et détectez une attaque ARP Spoofing dans arp_attack.pcap.', @WARNING_MSG),
    'NETWORK_ANALYSIS',
    'int',
    'http://localhost:8081/download/arp_attack.pcap',
    'HACKZONE{4rp_sp00f1ng_d3t3ct3d}',
    200
),
(
    'Analyse Réseau - Niveau 4 (DNS Tunneling)',
    CONCAT('Des données sont exfiltrées via des requêtes DNS. Décodez le message caché dans dns_exfil.pcap.', @WARNING_MSG),
    'NETWORK_ANALYSIS',
    'int',
    'http://localhost:8081/download/dns_exfil.pcap',
    'HACKZONE{dns_tunn3l1ng_3xf1l}',
    250
),
(
    'Analyse Réseau - Niveau 5 (SSL/TLS Decryption)',
    CONCAT('Déchiffrez le trafic HTTPS capturé avec la clé privée fournie. Fichiers : encrypted_traffic.pcap et server.key.', @WARNING_MSG),
    'NETWORK_ANALYSIS',
    'avan',
    'http://localhost:8081/download/encrypted_traffic.pcap',
    'HACKZONE{ssl_d3crypt3d_m4st3r}',
    300
);

-- ------------------------------------------------------------
-- PARTIE 3 : PERMISSIONS DOCKER
-- ------------------------------------------------------------
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';
FLUSH PRIVILEGES;