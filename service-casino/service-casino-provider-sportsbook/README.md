# Sportsbook transaction label tieback to casino and accounting

###### SPORTS_RESERVE:<br/>
**lithium_accounting_internal**:<br/>
Tran id &#8594; 387665<br/>
Transaction label (transaction_id) &#8594; 797120371

**lithium_casino_sportsbook.reservation**:<br/>
reserve_id &#8594; 797120371<br/>
accounting_transaction_id &#8594; 387665

###### SPORTS_RESERVE_CANCEL:<br/>
**lithium_accounting_internal**:<br/>
Tran id &#8594; 387655<br/>
Transaction label (transaction_id) &#8594; 796913946_CANCEL

**lithium_casino_sportsbook.reservation**:<br/>
reserve_id &#8594; 796913946<br/>
reservation_cancel_id &#8594; 575

**lithium_casino_sportsbook.reservation_cancel** (575)<br/>
accounting_transaction_id &#8594; 387655

###### SPORTS_RESERVE_RETURN:<br/>
**lithium_accounting_internal**:<br/>
Tran id &#8594; 387355<br/>
Transaction label (transaction_id) &#8594; 796914004_COMMIT

**lithium_casino_sportsbook.reservation**:<br/>
reserve_id &#8594; 796914004<br/>
reservation_commit_id &#8594; 26536

**lithium_casino_sportsbook.reservation_commit** (26536)<br/>
accounting_transaction_id &#8594; 387355

###### SPORTS_LOSS:<br/>
**lithium_accounting_internal**:<br/>
Tran id &#8594; 387670<br/>
Transaction label (transaction_id) &#8594; 637375858527605073:2057295931:0<br/>
_Bet ID:<br/>
Request ID:<br/>
Idx of settlement entry in list (multi settlement, not necessary, possibly used for uniqueness)_

**lithium_casino_sportsbook.bet**:<br/>
bet_id &#8594; 637375858527605073

**lithium_casino_sportsbook.settlement**:<br/>
request_id &#8594; 2057295931

###### SPORTS_WIN:<br/>
**lithium_accounting_internal**:<br/>
Tran id &#8594; 387672<br/>
Transaction label (transaction_id) &#8594; 637362860094286146:2057760234:0<br/>
_Bet ID:<br/>
Request ID:<br/>
Idx of settlement entry in list (multi settlement, not necessary, possibly used for uniqueness)_

**lithium_casino_sportsbook.bet**:<br/>
bet_id &#8594; 637362860094286146

**lithium_casino_sportsbook.settlement**:<br/>
request_id &#8594; 2057760234


