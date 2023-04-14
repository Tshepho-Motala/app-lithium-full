INSERT IGNORE INTO `change_log_type` VALUES (last_insert_id(), 'comment');
INSERT IGNORE INTO `change_log_entity` VALUES (last_insert_id(), 'user.comment');