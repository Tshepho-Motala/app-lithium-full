select *, CONVERT(argument USING utf8) from mysql.general_log;
truncate mysql.general_log;
set global log_output = "TABLE";
set global general_log = true;
set global general_log = false;
show create table mysql.general_log;
set global innodb_lock_wait_timeout = 5;
set global innodb_deadlock_detect = true;

truncate mysql.slow_log;
set global long_query_time = 0.1;
set global log_queries_not_using_indexes = false;
set global slow_query_log = true;
select *, CONVERT(sql_text USING utf8) from mysql.slow_log;
