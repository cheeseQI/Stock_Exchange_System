FROM openjdk:8-jdk-alpine

COPY hw4-all.jar /app/
COPY account_table.sql /docker-entrypoint-initdb.d/account_table.sql
COPY order_table.sql /docker-entrypoint-initdb.d/order_table.sql
COPY position_table.sql /docker-entrypoint-initdb.d/position_table.sql

RUN apk update && apk add postgresql && apk add postgresql-client && rm -rf /var/cache/apk/*

EXPOSE 12345

CMD ["java", "-jar", "/app/hw4-all.jar"]
