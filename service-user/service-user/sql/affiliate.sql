SELECT u.id, u.username , u.email, ur.id, urlv.id, l.id, l.name, lv.id, lv.value
FROM lithium_user.`user` u
LEFT OUTER JOIN lithium_user.user_revision ur ON u.current_id = ur.id
LEFT OUTER JOIN lithium_user.user_revision_label_value urlv ON ur.id = urlv.user_revision_id
LEFT OUTER JOIN lithium_user.label_value lv ON urlv.id = lv.id
LEFT OUTER JOIN lithium_user.label l ON lv.label_id = l.id
WHERE l.name IN (
	'affiliateGuid',
	'affiliateSecondaryGuid1',
	'affiliateSecondaryGuid2',
	'affiliateSecondaryGuid3'
	)
-- AND username = 'lsmedia024';
