ALTER TABLE lithium_cashier.transaction ADD CONSTRAINT unique_linked_transaction_id UNIQUE (linked_transaction_id), ALGORITHM INPLACE, LOCK NONE;
