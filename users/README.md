Steps to run this directly on your machine:

### 1: Install Postgres

Either run the docker image
```
docker run -d \
  --name users-db \
  -p 5432:5432 \
  -e POSTGRES_DB=users \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=123456 \
  postgres:15-alpine
```
or install directly on your machine: https://www.postgresql.org/download/linux/debian/

### 2: Run it
Using this command
```
./gradlew build bootRun
```