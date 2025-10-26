FROM flyway/flyway:latest

COPY flyway_config/flyway.conf /flyway/conf/

COPY flyway_migrations /flyway/sql/