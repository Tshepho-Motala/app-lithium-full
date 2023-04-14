SELECT d.name as 'domain', l.locale2 as 'language', tkey.code as 'key', tvalue.value as 'value'
FROM translation_key_v2 tkey
         INNER JOIN translation_value_v2 tvalue ON tvalue.key_id = tkey.id
         INNER JOIN domain d ON tvalue.domain_id = d.id
         INNER JOIN language l ON tvalue.language_id = l.id
WHERE tkey.code like 'ERROR_DICTIONARY.REGISTRATION%'
  and d.name in ('default', 'livescore_nl')
  and l.locale2 in ('EN', 'NL');

select * from change_set where change_reference like 'lsplat-%'