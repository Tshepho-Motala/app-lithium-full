ALTER TABLE `document`
    ADD COLUMN `migrated` BOOLEAN DEFAULT FALSE, ALGORITHM=INPLACE, LOCK=NONE;
ALTER TABLE `document`
    ADD INDEX `idx_document_migrated` (`migrated` ASC);

