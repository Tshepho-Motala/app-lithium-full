UPDATE
	user
RIGHT JOIN user_revision
	ON user_revision.user_id = user.id
SET
	user.current_id = user_revision.id
WHERE
	user.current_id IS NULL;