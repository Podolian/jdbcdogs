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

create table breeds (
  id                    bigint,
  name                  varchar(255) not null,
  average_weight        int not null,
  origin                varchar(63),
  recomended_nickname   varchar(63),
  constraint  breeds_PK primary key (id),
  constraint  breeds_name_UQ unique (name)
);

create table vocations (
  id                      bigint,
  mission                 varchar(255) not null,
  constraint              vocations_PK primary key (id),
  constraint              vocations_mission_UQ unique (name)
);

create table breeds_vocations (
  breeds_id bigint not null,
  vocations_id bigint not null,
  constraint breeds_vocations_breeds_FK foreign key (breeds_id) references breeds,
  constraint breeds_vocations_vocations_FK foreign key (vocations_id) references vocations,
  constraint breeds_vocations_breeds_id_vocations_id_UQ unique (broker_id, sales_group_id)
);