
SELECT * 
FROM bet b

LEFT OUTER JOIN bet_selection bs
	LEFT OUTER JOIN settlement_selection ss
		LEFT OUTER JOIN settlement s
			LEFT OUTER JOIN settlement_result sr
            ON s.settlement_result_id = sr.id
        ON ss.settlement_id = s.id
	ON ss.bet_selection_id = bs.id
ON bs.bet_id = b.id

WHERE settlement_transaction_id IS NOT NULL
-- WHERE b.id = 1