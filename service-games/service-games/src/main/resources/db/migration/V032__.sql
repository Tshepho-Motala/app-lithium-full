UPDATE `game`
SET `commercial_name` = `name`
WHERE `commercial_name` is null;

UPDATE `game`
SET `name` = (@temp := `name`), `name` = `commercial_name`, `commercial_name` = @temp
WHERE `name` <> `commercial_name`;

ALTER TABLE `game`
MODIFY commercial_name varchar(255) not null;