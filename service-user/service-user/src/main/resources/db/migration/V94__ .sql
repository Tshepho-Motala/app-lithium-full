UPDATE `role`
SET description = 'Revoke player exclusion',
    name        = 'Exclusion Revoke',
    role        = 'EXCLUSION_REVOKE'
WHERE role = 'EXCLUSION_CLEAR'
;