ALTER TABLE `game` ADD COLUMN `progressive_jackpot` BIT NOT NULL DEFAULT 0;
ALTER TABLE `game` ADD COLUMN `networked_jackpot_pool` BIT NOT NULL DEFAULT 0;
ALTER TABLE `game` ADD COLUMN `local_jackpot_pool` BIT NOT NULL DEFAULT 0;

