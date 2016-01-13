#ReqData SCHEMA

# --- !Ups
CREATE TABLE ReqData (
    id int NOT NULL AUTO_INCREMENT,
    data varchar(255) NOT NULL,
    req int NOT NULL,
    FOREIGN KEY (req) REFERENCES Req(id),
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE ReqData;