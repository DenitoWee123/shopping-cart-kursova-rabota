CREATE SCHEMA IF NOT EXISTS "${app_schema}";
GRANT USAGE ON SCHEMA "${app_schema}" TO "${app_db_user}";
ALTER SCHEMA "${app_schema}" OWNER TO "${app_db_user}";

GRANT CONNECT ON DATABASE "${POSTGRES_DB}" TO "${app_db_user}";

ALTER DEFAULT PRIVILEGES FOR ROLE "${app_db_user}" IN SCHEMA "${app_schema}"
   GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO "${app_db_user}";

ALTER DEFAULT PRIVILEGES FOR ROLE "${app_db_user}" IN SCHEMA "${app_schema}"
   GRANT USAGE, SELECT ON SEQUENCES TO "${app_db_user}";

DO
$do$
BEGIN
   RAISE NOTICE 'Permissions grant logic executed for user: ${app_db_user}.';
END
$do$;