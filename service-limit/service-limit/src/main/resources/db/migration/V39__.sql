DELETE FROM domain_restriction WHERE `restriction_id` IN (SELECT `id` FROM restriction WHERE code='BONUS');
DELETE FROM restriction WHERE code='BONUS';