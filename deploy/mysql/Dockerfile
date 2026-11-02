FROM mysql:8.0

COPY daocheng_dp.sql /docker-entrypoint-initdb.d/01-daocheng_dp.sql

CMD ["mysqld", "--character-set-server=utf8mb4", "--collation-server=utf8mb4_0900_ai_ci", "--innodb-buffer-pool-size=128M"]
