USE `lithium_vb_migration`;
DELETE FROM migration_credential WHERE player_guid LIKE '%virginbet_uk%';
DELETE FROM migration_exception_record WHERE 1;
DELETE FROM progress WHERE 1;

USE `lithium_user`;
DELETE FROM user_password_hash_algorithm WHERE user_id IN (
  SELECT id FROM `user` WHERE guid LIKE '%virginbet_uk%'
);
DELETE FROM `user` WHERE guid LIKE '%virginbet_uk%';
DELETE FROM `user_active_sessions_metadata` WHERE user_id LIKE '%virginbet_uk%';


USE `lithium_limit`;
DELETE FROM reality_check_set WHERE guid LIKE '%virginbet_uk%';
DELETE FROM player_cool_off WHERE player_guid LIKE '%virginbet_uk%';
DELETE FROM user WHERE guid LIKE '%virginbet_uk%';
DELETE FROM player_limit WHERE player_guid LIKE '%virginbet_uk%';

USE `lithium_changelog`;
DELETE FROM change_log WHERE author_full_name LIKE '%virginbet_uk%';
DELETE FROM user WHERE guid LIKE '%virginbet_uk%';

USE `lithium_accounting_internal`;
DELETE FROM transaction_entry WHERE account_id in (SELECT id from account WHERE owner_id in (SELECT id FROM user WHERE guid LIKE '%virginbet_uk%'));
DELETE FROM summary_account_transaction_type WHERE account_id in (SELECT id from account WHERE owner_id in (SELECT id FROM user WHERE guid LIKE '%virginbet_uk%'));
DELETE FROM summary_account WHERE account_id in (SELECT id from account WHERE owner_id in (SELECT id FROM user WHERE guid LIKE '%virginbet_uk%'));
DELETE FROM account WHERE owner_id in (SELECT id FROM user WHERE guid LIKE '%virginbet_uk%');
DELETE FROM user WHERE guid LIKE '%virginbet_uk%';

USE `lithium_user_search`;
DELETE FROM current_account_balance WHERE user_id in (SELECT id FROM user WHERE guid LIKE '%virginbet_uk%');
DELETE FROM user WHERE guid LIKE '%virginbet_uk%';

USE `lithium_cashier`;
DELETE FROM transaction_data WHERE transaction_id in (SELECT id from transaction WHERE user_id in (SELECT id FROM user WHERE guid LIKE '%virginbet_uk%'));
# ALTER TABLE transaction DROP FOREIGN KEY F;
DELETE FROM transaction_workflow_history WHERE transaction_id in (SELECT id from transaction WHERE user_id in (SELECT id FROM user WHERE guid LIKE '%virginbet_uk%'));
DELETE FROM transaction WHERE user_id in (SELECT id FROM user WHERE guid LIKE '%virginbet_uk%');
DELETE FROM user WHERE guid LIKE '%virginbet_uk%';

