/*

This is simple dog breeds reference book with intent to describe dog breeds vocations.

Each known breed definitely has a unique name and numeric average weight. Possibly, we can define an origin of the
breed and assign suitable recommended nickname, just for fun.

A vocation refers to different type of tasks for dog, at which particular breed excels. It contains unique mission column.

A breed might refer different type of missions and no chance to define which breed is the best at particular mission.

  TECH NOTES AND NAMING CONVENTION
- All tables, columns and constraints are named using "snake_case" naming convention
- All table names must be plural (e.g. "users", not "user")
- All tables (except link tables) should have an id of type BIGINT, which is a primary key.
- All primary key, foreign key, and unique constraint should be named according to the naming convention.
- All link tables should have a composite key that consists of two foreign key columns

- All primary keys should be named according to the following rule "table_name_PK"
- All foreign keys should be named according to the following rule "table_name_reference_table_name_FK"
- All alternative keys (unique) should be named according to the following rule "table_name_column_name_UQ"
  If the key is composite (e.g. consists of two columns), the name should list all column names.
  E.g. "table_name_column1_name_column2_name_UQ"

*/

CREATE TABLE IF NOT EXISTS breeds (
  id                    BIGINT,
  name                  VARCHAR(255) NOT NULL,
  average_weight        INT NOT NULL,
  origin                VARCHAR(63),
  recomended_nickname   VARCHAR(63),
  CONSTRAINT  breeds_PK PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS vocations (
  id                      BIGINT,
  mission                 VARCHAR(255) NOT NULL,
  CONSTRAINT              vocations_PK PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS breeds_vocations (
  breeds_id bigint NOT NULL,
  vocations_id bigint NOT NULL,
  CONSTRAINT breeds_vocations_breeds_FK FOREIGN KEY (breeds_id) REFERENCES breeds,
  CONSTRAINT breeds_vocations_vocations_FK FOREIGN KEY (vocations_id) REFERENCES vocations,
  CONSTRAINT breeds_vocations_breeds_id_vocations_id_UQ UNIQUE (breed_id, vocation_id)
);