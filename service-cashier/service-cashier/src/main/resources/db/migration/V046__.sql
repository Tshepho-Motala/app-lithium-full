/*!40101 SET NAMES utf8 */;
/*!40103 SET TIME_ZONE='+00:00' */;

# 1. Create a new table "transaction_payment_type" with columns "id" and "payment_type";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS lithium_cashier.`transaction_payment_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `payment_type` varchar(255),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

# 2. Get data from "transaction.payment_type" with distinct and insert them into
# "transaction_payment_type.payment_type"
INSERT INTO lithium_cashier.transaction_payment_type (payment_type)
SELECT DISTINCT lithium_cashier.transaction.payment_type
FROM lithium_cashier.transaction
WHERE lithium_cashier.transaction.payment_type IS NOT NULL;

# 3. Update data in "transaction.payment_type" obtaining data from
# "transaction_payment_type.id"
UPDATE lithium_cashier.transaction
INNER JOIN lithium_cashier.transaction_payment_type ON transaction.payment_type = transaction_payment_type.payment_type
SET transaction.payment_type = transaction_payment_type.id
WHERE transaction.payment_type = transaction_payment_type.payment_type;

# 4. Alter "payment_type" column in "transaction" table into "payment_type_id",
# thus we are making a relation between "transaction" & "transaction_payment_type";
ALTER TABLE lithium_cashier.transaction
CHANGE payment_type payment_type_id bigint(20);

# 5. Get appropriate ids from "transaction_payment_type" by "payment_type" name and
# update all matchings in "transaction.payment_type_id":
#
#     'transaction':
#     id payment_type_id
#     1  1
#     2  2
#     'transaction_payment_type':
#     id payment_type
#     1  Card
#     2  USSD
#     3  Bank
#
# 5. When new transaction is created -> createOrUpdate pattern gets existed data or
# populates unknown data.