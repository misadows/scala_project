# Req schema

# --- !Ups

CREATE TABLE Req (
    id int NOT NULL AUTO_INCREMENT,
    criteria varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE Req;
