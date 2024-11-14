This is a hobby project that's only purpose to try some Java features.


Currently this command will start PostgreSQL, create the database productdb, and initialize the products table using init_db.sql. 
docker-compose up -d

We can verify that PostgreSQL is running by connecting to the database through a SQL client or using the command line:
docker exec -it product_data_pipeline_db psql -U productuser -d productdb

Within the PostgreSQL CLI, we can check the table structure with:
\dt;

(The init script only runs if there is no volume already, so we can run the following to be sure:
docker-compose down -v )