drop database documents;
create database documents;

USE documents;
create table trans(
	ID varchar(20) NOT NULL,
    PRIMARY KEY (ID)
);
create table doc(
	ID int,
	URN varchar(100) NOT NULL,
	used boolean DEFAULT false,
    PRIMARY KEY(ID)
);

create table line(
	docID int NOT NULL,
    lineNo int NOT NULL,
    URN varchar(150),
    used boolean DEFAULT false,
    CONSTRAINT fk_line_doc FOREIGN KEY (docID) REFERENCES doc(ID) ON DELETE CASCADE,
    PRIMARY KEY(docID,lineNo)
);

create table word(
	docID int NOT NULL,
    lineNo int NOT NULL,
    wordNo int NOT NULL,
    URN varchar(150),
    annotation varchar(150),
    used boolean DEFAULT false,
	CONSTRAINT fk_word_doc FOREIGN KEY (docID) REFERENCES doc(ID) ON DELETE CASCADE,
    CONSTRAINT fk_word_line FOREIGN KEY (docID,lineNo) REFERENCES line(docID,lineNo) ON DELETE CASCADE,
	PRIMARY KEY(docID,lineNo,wordNo)
);

create table letter(
    docID int NOT NULL,
    lineNo int NOT NULL,
    wordNo int NOT NULL,
    letterNo int NOT NULL,
    URN varchar(150),
    CONSTRAINT fk_trans_letter_doc FOREIGN KEY (docID) REFERENCES doc(ID) ON DELETE CASCADE,
    CONSTRAINT fk_letter_line FOREIGN KEY (docID,lineNo) REFERENCES line(docID,lineNo)ON DELETE CASCADE,
    CONSTRAINT fk_letter_wore FOREIGN KEY (docID,lineNo,wordNo) REFERENCES word(docID,lineNo,wordNo)ON DELETE CASCADE,
    PRIMARY KEY (docID,lineNo,wordNo,letterNo)
);

create table annotation(
   transID varchar(20) Not NULL,
   docID int NOT NULL,
   lineNo int NOT NULL,
   wordNo int NOT NULL,
   letterNo int NOT NULL,
   annoValue character,
   timer int,
   difficulty double,
   CONSTRAINT fk_annotation_user FOREIGN KEY (transID) REFERENCES trans(ID) ON DELETE CASCADE,
   CONSTRAINT fk_annotation_doc FOREIGN KEY (docID) REFERENCES doc(ID) ON DELETE CASCADE,
   CONSTRAINT fk_annotation_line FOREIGN KEY (docID,lineNo) REFERENCES line(docID,lineNo) ON DELETE CASCADE,
   CONSTRAINT fk_annotation_word FOREIGN KEY (docID,lineNo,wordNo) REFERENCES word(docID,lineNo,wordNo) ON DELETE CASCADE,
   CONSTRAINT fk_annotation_letter FOREIGN KEY (docID,lineNo,wordNo,letterNo) REFERENCES letter(docID,lineNo,wordNo,letterNo) ON DELETE CASCADE,
   PRIMARY KEY (transID,docID,lineNo,wordNo,letterNo)
);


#INSERT INTO doc(ID,URN) VALUES (0,"urn:cite2:hmt:vaimg.v1:VA012RN_0013");
#INSERT INTO doc(ID,URN) VALUES (1,"urn:cite2:hmt:vaimg.v1:VA012RND_0892");
#INSERT INTO doc(ID,URN) VALUES (2,"urn:cite2:hmt:vaimg.v1:VA012RUV_0893");
#INSERT INTO doc(ID,URN) VALUES (3,"urn:cite2:hmt:vaimg.v1:VA012RUVD_0894");
#INSERT INTO doc(ID,URN) VALUES (4,"urn:cite2:hmt:vaimg.v1:VA012RUVD_0895");
#INSERT INTO doc(ID,URN) VALUES (5,"urn:cite2:hmt:vaimg.v1:VA012VN_0514");
#INSERT INTO doc(ID,URN) VALUES (0,"urn:cite2:ecod:vaimg.v1:einstift_018r");


#INSERT INTO trans(ID) VALUES ("TEST");

#INSERT INTO unusedDocuments(docID,transID) VALUES (0,"TEST");
#INSERT INTO unusedDocuments(docID,transID) VALUES (1,"TEST");
#INSERT INTO unusedDocuments(docID,transID) VALUES (2,"TEST");
#INSERT INTO unusedDocuments(docID,transID) VALUES (3,"TEST");
#INSERT INTO unusedDocuments(docID,transID) VALUES (4,"TEST");
#INSERT INTO unusedDocuments(docID,transID) VALUES (5,"TEST");
