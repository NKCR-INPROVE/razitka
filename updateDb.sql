ALTER TABLE Exemplar ADD obrazek2 VARCHAR(255);
ALTER TABLE Exemplar ADD obrazek2_FULL VARCHAR(255);
ALTER TABLE Exemplar ADD obrazek2_MEDIUM VARCHAR(255);
ALTER TABLE Exemplar ADD obrazek2_PROPS TEXT;

CREATE SEQUENCE Kniha_ID_SQ INCREMENT BY 1 START WITH 1 MINVALUE 0;

CREATE SEQUENCE Account_ID_SQ INCREMENT BY 1 START WITH 1 MINVALUE 0;

CREATE TABLE Kniha (
                       Kniha_ID SERIAL NOT NULL,
                       UPDATE_TIMESTAMP TIMESTAMP NOT NULL,
                       signatura VARCHAR(255),
                       sys VARCHAR(255),
                       Exemplar_ID INT,
                       PRIMARY KEY (Kniha_ID));

CREATE INDEX Kniha_Exemplar_ID_REFIDX ON Kniha (Exemplar_ID);

CREATE TABLE Accounts (
                          Account_ID SERIAL NOT NULL,
                          UPDATE_TIMESTAMP TIMESTAMP NOT NULL,
                          principal VARCHAR(128),
                          hashedPassword VARCHAR(255),
                          fullName VARCHAR(255),
                          email VARCHAR(255),
                          roles VARCHAR(255),
                          PRIMARY KEY (Account_ID));

CREATE UNIQUE INDEX principal_IDX ON Accounts (principal);

ALTER TABLE Kniha ADD CONSTRAINT Kniha_Exemplar_ID_FK FOREIGN KEY (Exemplar_ID) REFERENCES Exemplar (Exemplar_ID);

//dalsi update

ALTER TABLE Exemplar ADD label VARCHAR(255);
ALTER TABLE Exemplar ADD hidden BOOLEAN;
update exemplar set hidden = false;

//pridani Zdroje

CREATE SEQUENCE Zdroj_ID_SQ INCREMENT BY 1 START WITH 1 MINVALUE 0;

CREATE TABLE Zdroj (
                       Zdroj_ID SERIAL NOT NULL,
                       UPDATE_TIMESTAMP TIMESTAMP NOT NULL,
                       zdroj VARCHAR(255),
                       Exemplar_ID INT,
                       PRIMARY KEY (Zdroj_ID));

CREATE INDEX Zdroj_Exemplar_ID_REFIDX ON Zdroj (Exemplar_ID);

ALTER TABLE Zdroj ADD CONSTRAINT Zdroj_Exemplar_ID_FK FOREIGN KEY (Exemplar_ID) REFERENCES Exemplar (Exemplar_ID);


//UPDATE 2023
ALTER TABLE Exemplar ADD velikost VARCHAR(255);
ALTER TABLE Exemplar ADD napis_ascii VARCHAR(255);
ALTER TABLE Exemplar ADD prijmeni_ascii VARCHAR(255);
ALTER TABLE Exemplar ADD instituce_ascii VARCHAR(255);
ALTER TABLE Exemplar ADD mesto_ascii VARCHAR(255);

//UPDATE 2024
ALTER TABLE Kniha ADD poznamka TEXT;
