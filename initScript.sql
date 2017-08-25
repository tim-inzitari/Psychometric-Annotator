drop database documents;
create database documents;
USE documents;
create table trans(
	ID varchar(20) NOT NULL, 
    PRIMARY KEY (ID)
);
create table doc(
	ID varchar(50) NOT NULL,
    PRIMARY KEY(ID)
);
create table line(
	docID varchar(20) NOT NULL,
    transID varchar(50) NOT NULL,
    lineNo int NOT NULL,
    CONSTRAINT fk_line_doc FOREIGN KEY (docID) REFERENCES doc(ID),
    CONSTRAINT fk_line_user FOREIGN KEY (transID) REFERENCES trans(ID),
    PRIMARY KEY(docID,transID,lineNo)
);
create table word(
	docID varchar(20) NOT NULL,
    transID varchar(50) NOT NULL,
    lineNo int NOT NULL,
    wordNo int NOT NULL,
	CONSTRAINT fk_word_doc FOREIGN KEY (docID) REFERENCES doc(ID),
    CONSTRAINT fk_word_user FOREIGN KEY (transID) REFERENCES trans(ID),
    CONSTRAINT fk_word_line FOREIGN KEY (docID,transID,lineNo) REFERENCES line(docID,transID,lineNo),
	PRIMARY KEY(docID,transID,lineNo,wordNo)
);
create table letter(
	docID varchar(20) NOT NULL,
    transID varchar(50) NOT NULL,
    lineNo int NOT NULL,
    wordNo int NOT NULL,
	letterNo int NOT NULL,
    annotation character,
    CONSTRAINT fk_letter_doc FOREIGN KEY (docID) REFERENCES doc(ID),
	CONSTRAINT fk_letter_user FOREIGN KEY (transID) REFERENCES trans(ID),
    CONSTRAINT fk_letter_line FOREIGN KEY (docID,transID,lineNo) REFERENCES line(docID,transID,lineNo),
	CONSTRAINT fk_letter_wore FOREIGN KEY (docID,transID,lineNo,wordNo) REFERENCES word(docID,transID,lineNo,wordNo),
    PRIMARY KEY (docID,transID,lineNo,wordNo)
);




