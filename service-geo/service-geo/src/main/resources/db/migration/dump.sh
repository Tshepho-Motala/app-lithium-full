PASSWORD=mysql
mysqldump --user=root --password=$PASSWORD --complete-insert --no-data --ignore-table=schema_version lithium_geo > V01__create.sql
mysqldump --user=root --password=$PASSWORD --complete-insert --no-create-info lithium_geo file_update > V02__data.sql
mysqldump --user=root --password=$PASSWORD --complete-insert --no-create-info lithium_geo country > V03__data.sql
mysqldump --user=root --password=$PASSWORD --complete-insert --no-create-info lithium_geo admin_level1 > V04__data.sql
mysqldump --user=root --password=$PASSWORD --complete-insert --no-create-info lithium_geo admin_level2 > V05__data.sql
mysqldump --user=root --password=$PASSWORD --complete-insert --no-create-info --where="id >= 000000 and id < 050000" lithium_geo city > V06__data.sql
mysqldump --user=root --password=$PASSWORD --complete-insert --no-create-info --where="id >= 050000 and id < 100000" lithium_geo city > V07__data.sql
mysqldump --user=root --password=$PASSWORD --complete-insert --no-create-info --where="id >= 100000 and id < 150000" lithium_geo city > V08__data.sql
mysqldump --user=root --password=$PASSWORD --complete-insert --no-create-info --where="id >= 150000 and id < 200000" lithium_geo city > V09__data.sql
mysqldump --user=root --password=$PASSWORD --complete-insert --no-create-info --where="id >= 200000 and id < 250000" lithium_geo city > V10__data.sql

