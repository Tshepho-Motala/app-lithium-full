//package lithium.service.cashier.processor.netaxept.data.enums;
//
//import java.io.IOException;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
//
//import lithium.service.cashier.processor.netaxept.data.ParameterError;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//
//@ToString
//@JsonFormat(shape = JsonFormat.Shape.OBJECT)
//@JsonDeserialize(using = ResultCode.ResultCodeDeserializer.class)
//public enum ResultCode implements Serializable {
//	R000_000_000("000.000.000", "Transaction succeeded", false),
//	R000_000_100("000.000.100", "successful request", false),
//	R000_100_110("000.100.110", "Request successfully processed in 'Merchant in Integrator Test Mode'", false),
//	R000_100_111("000.100.111", "Request successfully processed in 'Merchant in Validator Test Mode'", false),
//	R000_100_112("000.100.112", "Request successfully processed in 'Merchant in Connector Test Mode'", false),
//	R000_100_200("000.100.200", "Reason not Specified", true),
//	R000_100_201("000.100.201", "Account or Bank Details Incorrect", true),
//	R000_100_202("000.100.202", "Account Closed", true),
//	R000_100_203("000.100.203", "Insufficient Funds", true),
//	R000_100_204("000.100.204", "Mandate not Valid", true),
//	R000_100_205("000.100.205", "Mandate Cancelled", true),
//	R000_100_206("000.100.206", "Revocation or Dispute", true),
//	R000_100_207("000.100.207", "Cancellation in Clearing Network", true),
//	R000_100_208("000.100.208", "Account Blocked", true),
//	R000_100_209("000.100.209", "Account does not exist", true),
//	R000_100_210("000.100.210", "Invalid Amount", true),
//	R000_100_211("000.100.211", "Transaction succeeded (amount of transaction is smaller then amount of pre-authorization)", true),
//	R000_100_212("000.100.212", "Transaction succeeded (amount of transaction is greater then amount of pre-authorization)", true),
//	R000_100_220("000.100.220", "Fraudulent Transaction", true),
//	R000_100_221("000.100.221", "Merchandise Not Received", true),
//	R000_100_222("000.100.222", "Transaction Not Recognized By Cardholder", true),
//	R000_100_223("000.100.223", "Service Not Rendered", true),
//	R000_100_224("000.100.224", "Duplicate Processing", true),
//	R000_100_225("000.100.225", "Credit Not Processed", true),
//	R000_100_226("000.100.226", "Cannot be settled", true),
//	R000_100_227("000.100.227", "Configuration Issue", true),
//	R000_100_228("000.100.228", "Temporary Communication Error - Retry", true),
//	R000_100_229("000.100.229", "Incorrect Instructions", true),
//	R000_100_230("000.100.230", "Unauthorised Charge", true),
//	R000_100_299("000.100.299", "Unspecified (Technical)", true),
//	R000_200_000("000.200.000", "transaction pending", true),
//	R000_200_100("000.200.100", "successfully created checkout", true),
//	R000_200_101("000.200.101", "successfully updated checkout", true),
//	R000_200_102("000.200.102", "successfully deleted checkout", true),
//	R000_200_200("000.200.200", "Transaction initialized", true),
//	R000_300_000("000.300.000", "Two-step transaction succeeded", true),
//	R000_300_100("000.300.100", "Risk check successful", true),
//	R000_300_101("000.300.101", "Risk bank account check successful", true),
//	R000_300_102("000.300.102", "Risk report successful", true),
//	R000_400_000("000.400.000", "Transaction succeeded (please review manually due to fraud suspicion)", true),
//	R000_400_010("000.400.010", "Transaction succeeded (please review manually due to AVS return code)", true),
//	R000_400_020("000.400.020", "Transaction succeeded (please review manually due to CVV return code)", true),
//	R000_400_030("000.400.030", "Transaction partially failed (please reverse manually due to failed automatic reversal)", true),
//	R000_400_040("000.400.040", "Transaction succeeded (please review manually due to amount mismatch)", true),
//	R000_400_050("000.400.050", "Transaction succeeded (please review manually because transaction is pending)", true),
//	R000_400_060("000.400.060", "Transaction succeeded (approved at merchant's risk)", true),
//	R000_400_070("000.400.070", "Transaction succeeded (waiting for external risk review)", true),
//	R000_400_080("000.400.080", "Transaction succeeded (please review manually because the service was unavailable)", true),
//	R000_400_090("000.400.090", "Transaction succeeded (please review manually due to external risk check)", true),
//	R000_400_100("000.400.100", "Transaction succeeded, risk after payment rejected", true),
//	R000_400_101("000.400.101", "card not participating/authentication unavailable", true),
//	R000_400_102("000.400.102", "user not enrolled", true),
//	R000_400_103("000.400.103", "Technical Error in 3D system", true),
//	R000_400_104("000.400.104", "Missing or malformed 3DSecure Configuration for Channel", true),
//	R000_400_105("000.400.105", "Unsupported User Device - Authentication not possible", true),
//	R000_400_106("000.400.106", "invalid payer authentication response(PARes) in 3DSecure Transaction", true),
//	R000_400_107("000.400.107", "Communication Error to VISA/Mastercard Directory Server", true),
//	R000_400_108("000.400.108", "Cardholder Not Found - card number provided is not found in the ranges of the issuer", true),
//	R000_400_200("000.400.200", "risk management check communication error", true),
//	R000_500_000("000.500.000", "Transaction succeeded - very good rating", true),
//	R000_500_100("000.500.100", "Transaction succeeded (address corrected)", true),
//	R000_600_000("000.600.000", "transaction succeeded due to external update", true),
//	R100_100_100("100.100.100", "request contains no creditcard, bank account number or bank name", true),
//	R100_100_101("100.100.101", "invalid creditcard, bank account number or bank name", true),
//	R100_100_104("100.100.104", "invalid unique id / root unique id", true),
//	R100_100_200("100.100.200", "request contains no month", true),
//	R100_100_201("100.100.201", "invalid month", true),
//	R100_100_300("100.100.300", "request contains no year", true),
//	R100_100_301("100.100.301", "invalid year", true),
//	R100_100_303("100.100.303", "card expired", true),
//	R100_100_304("100.100.304", "card not yet valid", true),
//	R100_100_305("100.100.305", "invalid expiration date format", true),
//	R100_100_400("100.100.400", "request contains no cc/bank account holder", true),
//	R100_100_401("100.100.401", "cc/bank account holder too short or too long", true),
//	R100_100_402("100.100.402", "cc/bank account holder not valid", true),
//	R100_100_500("100.100.500", "request contains no credit card brand", true),
//	R100_100_501("100.100.501", "invalid credit card brand", true),
//	R100_100_600("100.100.600", "empty CVV for VISA,MASTER, AMEX not allowed", true),
//	R100_100_601("100.100.601", "invalid CVV/brand combination", true),
//	R100_100_650("100.100.650", "empty CreditCardIssueNumber for MAESTRO not allowed", true),
//	R100_100_651("100.100.651", "invalid CreditCardIssueNumber", true),
//	R100_100_700("100.100.700", "invalid cc number/brand combination", true),
//	R100_100_701("100.100.701", "suspecting fraud, this card may not be processed", true),
//	R100_150_100("100.150.100", "request contains no Account data and no registration id", true),
//	R100_150_101("100.150.101", "invalid length for specified registration id (must be 32 chars)", true),
//	R100_150_200("100.150.200", "registration does not exist", true),
//	R100_150_201("100.150.201", "registration is not confirmed yet", true),
//	R100_150_202("100.150.202", "registration is already deregistered", true),
//	R100_150_203("100.150.203", "registration is not valid, probably initially rejected", true),
//	R100_150_204("100.150.204", "account registration reference pointed to no registration transaction", true),
//	R100_150_205("100.150.205", "referenced registration does not contain an account", true),
//	R100_150_300("100.150.300", "payment only allowed with valid initial registration", true),
//	R100_200_100("100.200.100", "bank account contains no or invalid country", true),
//	R100_200_103("100.200.103", "bank account has invalid bankcode/name account number combination", true),
//	R100_200_104("100.200.104", "bank account has invalid acccount number format", true),
//	R100_200_200("100.200.200", "bank account needs to be registered and confirmed first. Country is mandate based.", true),
//	R100_210_101("100.210.101", "virtual account contains no or invalid Id", true),
//	R100_210_102("100.210.102", "virtual account contains no or invalid brand", true),
//	R100_211_101("100.211.101", "user account contains no or invalid Id", true),
//	R100_211_102("100.211.102", "user account contains no or invalid brand", true),
//	R100_211_103("100.211.103", "no password defined for user account", true),
//	R100_211_104("100.211.104", "password does not meet safety requirements (needs 8 digits at least and must contain letters and numbers)", true),
//	R100_211_105("100.211.105", "wallet id has to be a valid email address", true),
//	R100_211_106("100.211.106", "voucher ids have 32 digits always", true),
//	R100_212_101("100.212.101", "wallet account registration must not have an initial balance", true),
//	R100_212_102("100.212.102", "wallet account contains no or invalid brand", true),
//	R100_212_103("100.212.103", "wallet account payment transaction needs to reference a registration", true),
//	R100_250_100("100.250.100", "job contains no execution information", true),
//	R100_250_105("100.250.105", "invalid or missing action type", true),
//	R100_250_106("100.250.106", "invalid or missing duration unit", true),
//	R100_250_107("100.250.107", "invalid or missing notice unit", true),
//	R100_250_110("100.250.110", "missing job execution", true),
//	R100_250_111("100.250.111", "missing job expression", true),
//	R100_250_120("100.250.120", "invalid execution parameters, combination does not conform to standard", true),
//	R100_250_121("100.250.121", "invalid execution parameters, hour must be between 0 and 23", true),
//	R100_250_122("100.250.122", "invalid execution parameters, minute and seconds must be between 0 and 59", true),
//	R100_250_123("100.250.123", "invalid execution parameters, Day of month must be between 1 and 31", true),
//	R100_250_124("100.250.124", "invalid execution parameters, month must be between 1 and 12", true),
//	R100_250_125("100.250.125", "invalid execution parameters, Day of week must be between 1 and 7", true),
//	R100_250_250("100.250.250", "Job tag missing", true),
//	R100_300_101("100.300.101", "invalid test mode (please use LIVE or INTEGRATOR_TEST or CONNECTOR_TEST)", true),
//	R100_300_200("100.300.200", "transaction id too long", true),
//	R100_300_300("100.300.300", "invalid reference id", true),
//	R100_300_400("100.300.400", "missing or invalid channel id", true),
//	R100_300_401("100.300.401", "missing or invalid sender id", true),
//	R100_300_402("100.300.402", "missing or invalid version", true),
//	R100_300_501("100.300.501", "invalid response id", true),
//	R100_300_600("100.300.600", "invalid or missing user login", true),
//	R100_300_601("100.300.601", "invalid or missing user pwd", true),
//	R100_300_700("100.300.700", "invalid relevance", true),
//	R100_300_701("100.300.701", "invalid relevance for given payment type", true),
//	R100_350_100("100.350.100", "referenced session is REJECTED (no action possible).", true),
//	R100_350_101("100.350.101", "referenced session is CLOSED (no action possible)", true),
//	R100_350_200("100.350.200", "undefined session state", true),
//	R100_350_201("100.350.201", "referencing a registration through reference id is not applicable for this payment type", true),
//	R100_350_301("100.350.301", "confirmation (CF) must be registered (RG) first", true),
//	R100_350_302("100.350.302", "session already confirmed (CF)", true),
//	R100_350_303("100.350.303", "cannot deregister (DR) unregistered account and/or customer", true),
//	R100_350_310("100.350.310", "cannot confirm (CF) session via XML", true),
//	R100_350_311("100.350.311", "cannot confirm (CF) on a registration passthrough channel", true),
//	R100_350_312("100.350.312", "cannot do passthrough on non-internal connector", true),
//	R100_350_313("100.350.313", "registration of this type has to provide confirmation url", true),
//	R100_350_314("100.350.314", "customer could not be notified of pin to confirm registration (channel)", true),
//	R100_350_315("100.350.315", "customer could not be notified of pin to confirm registration (sending failed)", true),
//	R100_350_400("100.350.400", "no or invalid PIN (email/SMS/MicroDeposit authentication) entered", true),
//	R100_350_500("100.350.500", "unable to obtain personal (virtual) account - most likely no more accounts available", true),
//	R100_350_601("100.350.601", "registration is not allowed to reference another transaction", true),
//	R100_360_201("100.360.201", "unknown schedule type", true),
//	R100_360_300("100.360.300", "cannot schedule(SD) unscheduled job", true),
//	R100_360_303("100.360.303", "cannot deschedule(DS) unscheduled job", true),
//	R100_360_400("100.360.400", "schedule module not configured for LIVE transaction mode", true),
//	R100_370_100("100.370.100", "transaction declined", true),
//	R100_370_101("100.370.101", "responseUrl not set in Transaction/Frontend", true),
//	R100_370_102("100.370.102", "malformed responseUrl in Transaction/Frontend", true),
//	R100_370_110("100.370.110", "transaction must be executed for German address", true),
//	R100_370_111("100.370.111", "system error( possible incorrect/missing input data)", true),
//	R100_370_121("100.370.121", "no or unknown ECI Type defined in Authentication", true),
//	R100_370_122("100.370.122", "parameter with null key provided in 3DSecure Authentication", true),
//	R100_370_123("100.370.123", "no or unknown verification type defined in 3DSecure Authentication", true),
//	R100_370_124("100.370.124", "unknown parameter key in 3DSecure Authentication", true),
//	R100_370_125("100.370.125", "Invalid 3DSecure Verification_ID. Must have Base64 encoding a Length of 28 digits", true),
//	R100_370_131("100.370.131", "no or unknown authentication type defined in Transaction/Authentication@type", true),
//	R100_370_132("100.370.132", "no result indicator defined Transaction/Authentication/resultIndicator", true),
//	R100_380_100("100.380.100", "transaction declined", true),
//	R100_380_101("100.380.101", "transaction contains no risk management part", true),
//	R100_380_110("100.380.110", "transaction must be executed for German address", true),
//	R100_380_201("100.380.201", "no risk management process type specified", true),
//	R100_380_305("100.380.305", "no frontend information provided for asynchronous transaction", true),
//	R100_380_306("100.380.306", "no authentication data provided in risk management transaction", true),
//	R100_380_401("100.380.401", "User Authentication Failed", true),
//	R100_380_501("100.380.501", "risk management transaction timeout", true),
//	R100_390_101("100.390.101", "purchase amount/currency mismatch", true),
//	R100_390_102("100.390.102", "PARes Validation failed", true),
//	R100_390_103("100.390.103", "PARes Validation failed - problem with signature", true),
//	R100_390_104("100.390.104", "XID mismatch", true),
//	R100_390_105("100.390.105", "Transaction rejected because of technical error in 3DSecure system", true),
//	R100_390_106("100.390.106", "Transaction rejected because of error in 3DSecure configuration", true),
//	R100_390_107("100.390.107", "Transaction rejected because cardholder authentication unavailable", true),
//	R100_390_108("100.390.108", "Transaction rejected because merchant not participating in 3DSecure program", true),
//	R100_390_109("100.390.109", "Transaction rejected because of VISA status 'U' or AMEX status 'N' or 'U' in 3DSecure program", true),
//	R100_390_110("100.390.110", "Cardholder Not Found - card number provided is not found in the ranges of the issuer", true),
//	R100_390_111("100.390.111", "Communication Error to VISA/Mastercard Directory Server", true),
//	R100_390_112("100.390.112", "Technical Error in 3D system", true),
//	R100_390_113("100.390.113", "Unsupported User Device - Authentication not possible", true),
//	R100_395_101("100.395.101", "Bank not supported for Giropay", true),
//	R100_395_102("100.395.102", "Account not enabled for Giropay e.g. test account", true),
//	R100_395_501("100.395.501", "Previously pending online transfer transaction timed out", true),
//	R100_395_502("100.395.502", "Acquirer/Bank reported timeout on online transfer transaction", true),
//	R100_396_101("100.396.101", "Cancelled by user", true),
//	R100_396_102("100.396.102", "Not confirmed by user", true),
//	R100_396_103("100.396.103", "Previously pending transaction timed out", true),
//	R100_396_104("100.396.104", "Uncertain status - probably cancelled by user", true),
//	R100_396_106("100.396.106", "User did not agree to payment method terms", true),
//	R100_396_201("100.396.201", "Cancelled by merchant", true),
//	R100_397_101("100.397.101", "Cancelled by user due to external update", true),
//	R100_397_102("100.397.102", "Rejected by connector/acquirer due to external update", true),
//	R100_400_000("100.400.000", "transaction declined (Wrong Address)", true),
//	R100_400_001("100.400.001", "transaction declined (Wrong Identification)", true),
//	R100_400_002("100.400.002", "transaction declined (Insufficient credibility score)", true),
//	R100_400_005("100.400.005", "transaction must be executed for German address", true),
//	R100_400_007("100.400.007", "System error ( possible incorrect/missing input data)", true),
//	R100_400_020("100.400.020", "transaction declined", true),
//	R100_400_021("100.400.021", "transaction declined for country", true),
//	R100_400_030("100.400.030", "transaction not authorized. Please check manually", true),
//	R100_400_039("100.400.039", "transaction declined for other error", true),
//	R100_400_040("100.400.040", "authorization failure", true),
//	R100_400_041("100.400.041", "transaction must be executed for German address", true),
//	R100_400_042("100.400.042", "transaction declined by SCHUFA (Insufficient credibility score)", true),
//	R100_400_043("100.400.043", "transaction declined because of missing obligatory parameter(s)", true),
//	R100_400_044("100.400.044", "transaction not authorized. Please check manually", true),
//	R100_400_045("100.400.045", "SCHUFA result not definite. Please check manually", true),
//	R100_400_051("100.400.051", "SCHUFA system error (possible incorrect/missing input data)", true),
//	R100_400_060("100.400.060", "authorization failure", true),
//	R100_400_061("100.400.061", "transaction declined (Insufficient credibility score)", true),
//	R100_400_063("100.400.063", "transaction declined because of missing obligatory parameter(s)", true),
//	R100_400_064("100.400.064", "transaction must be executed for Austrian, German or Swiss address", true),
//	R100_400_065("100.400.065", "result ambiguous. Please check manually", true),
//	R100_400_071("100.400.071", "system error (possible incorrect/missing input data)", true),
//	R100_400_080("100.400.080", "authorization failure", true),
//	R100_400_081("100.400.081", "transaction declined", true),
//	R100_400_083("100.400.083", "transaction declined because of missing obligatory parameter(s)", true),
//	R100_400_084("100.400.084", "transaction can not be executed for given country", true),
//	R100_400_085("100.400.085", "result ambiguous. Please check manually", true),
//	R100_400_086("100.400.086", "transaction declined (Wrong Address)", true),
//	R100_400_087("100.400.087", "transaction declined (Wrong Identification)", true),
//	R100_400_091("100.400.091", "system error (possible incorrect/missing input data)", true),
//	R100_400_100("100.400.100", "transaction declined - very bad rating", true),
//	R100_400_120("100.400.120", "authorization failure", true),
//	R100_400_121("100.400.121", "account blacklisted", true),
//	R100_400_122("100.400.122", "transaction must be executed for valid German account", true),
//	R100_400_123("100.400.123", "transaction declined because of missing obligatory parameter(s)", true),
//	R100_400_130("100.400.130", "system error (possible incorrect/missing input data)", true),
//	R100_400_139("100.400.139", "system error (possible incorrect/missing input data)", true),
//	R100_400_140("100.400.140", "transaction declined by GateKeeper", true),
//	R100_400_141("100.400.141", "Challenge by ReD Shield", true),
//	R100_400_142("100.400.142", "Deny by ReD Shield", true),
//	R100_400_143("100.400.143", "Noscore by ReD Shield", true),
//	R100_400_144("100.400.144", "ReD Shield data error", true),
//	R100_400_145("100.400.145", "ReD Shield connection error", true),
//	R100_400_146("100.400.146", "Line item error by ReD Shield", true),
//	R100_400_147("100.400.147", "Payment void and transaction denied by ReD Shield", true),
//	R100_400_148("100.400.148", "Payment void and transaction challenged by ReD Shield", true),
//	R100_400_149("100.400.149", "Payment void and data error by ReD Shield", true),
//	R100_400_150("100.400.150", "Payment void and connection error by ReD Shield", true),
//	R100_400_151("100.400.151", "Payment void and line item error by ReD Shield", true),
//	R100_400_152("100.400.152", "Payment void and error returned by ReD Shield", true),
//	R100_400_241("100.400.241", "Challenged by Threat Metrix", true),
//	R100_400_242("100.400.242", "Denied by Threat Metrix", true),
//	R100_400_243("100.400.243", "Invalid sessionId", true),
//	R100_400_260("100.400.260", "authorization failure", true),
//	R100_400_300("100.400.300", "abort checkout process", true),
//	R100_400_301("100.400.301", "reenter age/birthdate", true),
//	R100_400_302("100.400.302", "reenter address (packstation not allowed)", true),
//	R100_400_303("100.400.303", "reenter address", true),
//	R100_400_304("100.400.304", "invalid input data", true),
//	R100_400_305("100.400.305", "invalid foreign address", true),
//	R100_400_306("100.400.306", "delivery address error", true),
//	R100_400_307("100.400.307", "offer only secure methods of payment", true),
//	R100_400_308("100.400.308", "offer only secure methods of payment; possibly abort checkout", true),
//	R100_400_309("100.400.309", "confirm corrected address; if not confirmed, offer secure methods of payment only", true),
//	R100_400_310("100.400.310", "confirm bank account data; if not confirmed, offer secure methods of payment only", true),
//	R100_400_311("100.400.311", "transaction declined (format error)", true),
//	R100_400_312("100.400.312", "transaction declined (invalid configuration data)", true),
//	R100_400_313("100.400.313", "currency field is invalid or missing", true),
//	R100_400_314("100.400.314", "amount invalid or empty", true),
//	R100_400_315("100.400.315", "invalid or missing email address (probably invalid syntax)", true),
//	R100_400_316("100.400.316", "transaction declined (card missing)", true),
//	R100_400_317("100.400.317", "transaction declined (invalid card)", true),
//	R100_400_318("100.400.318", "invalid IP number", true),
//	R100_400_319("100.400.319", "transaction declined by risk system", true),
//	R100_400_320("100.400.320", "shopping cart data invalid or missing", true),
//	R100_400_321("100.400.321", "payment type invalid or missing", true),
//	R100_400_322("100.400.322", "encryption method invalid or missing", true),
//	R100_400_323("100.400.323", "certificate invalid or missing", true),
//	R100_400_324("100.400.324", "Error on the external risk system", true),
//	R100_400_325("100.400.325", "External risk system not available", true),
//	R100_400_326("100.400.326", "Risk bank account check unsuccessful", true),
//	R100_400_327("100.400.327", "Risk report unsuccessful", true),
//	R100_400_328("100.400.328", "Risk report unsuccessful (invalid data)", true),
//	R100_400_500("100.400.500", "waiting for external risk", true),
//	R100_500_101("100.500.101", "payment method invalid", true),
//	R100_500_201("100.500.201", "payment type invalid", true),
//	R100_500_301("100.500.301", "invalid due date", true),
//	R100_500_302("100.500.302", "invalid mandate date of signature", true),
//	R100_550_300("100.550.300", "request contains no amount or too low amount", true),
//	R100_550_301("100.550.301", "amount too large", true),
//	R100_550_303("100.550.303", "amount format invalid (only two decimals allowed).", true),
//	R100_550_310("100.550.310", "amount exceeds limit for the registered account.", true),
//	R100_550_311("100.550.311", "exceeding account balance", true),
//	R100_550_312("100.550.312", "Amount is outside allowed ticket size boundaries", true),
//	R100_550_400("100.550.400", "request contains no currency", true),
//	R100_550_401("100.550.401", "invalid currency", true),
//	R100_550_601("100.550.601", "risk amount too large", true),
//	R100_550_603("100.550.603", "risk amount format invalid (only two decimals allowed)", true),
//	R100_550_605("100.550.605", "risk amount is smaller than amount (it must be equal or bigger then amount)", true),
//	R100_550_701("100.550.701", "amounts not matched", true),
//	R100_550_702("100.550.702", "currencies not matched", true),
//	R100_600_500("100.600.500", "usage field too long", true),
//	R100_700_100("100.700.100", "customer.surname may not be null", true),
//	R100_700_101("100.700.101", "customer.surname length must be between 0 and 50", true),
//	R100_700_200("100.700.200", "customer.givenName may not be null", true),
//	R100_700_201("100.700.201", "customer.givenName length must be between 0 and 50", true),
//	R100_700_300("100.700.300", "invalid salutation", true),
//	R100_700_400("100.700.400", "invalid title", true),
//	R100_700_500("100.700.500", "company name too long", true),
//	R100_700_800("100.700.800", "identity contains no or invalid 'paper'", true),
//	R100_700_801("100.700.801", "identity contains no or invalid identification value", true),
//	R100_700_802("100.700.802", "identification value too long", true),
//	R100_700_810("100.700.810", "specify at least one identity", true),
//	R100_800_100("100.800.100", "request contains no street", true),
//	R100_800_101("100.800.101", "The combination of street1 and street2 must not exceed 201 characters.", true),
//	R100_800_102("100.800.102", "The combination of street1 and street2 must not contain only numbers.", true),
//	R100_800_200("100.800.200", "request contains no zip", true),
//	R100_800_201("100.800.201", "zip too long", true),
//	R100_800_202("100.800.202", "invalid zip", true),
//	R100_800_300("100.800.300", "request contains no city", true),
//	R100_800_301("100.800.301", "city too long", true),
//	R100_800_302("100.800.302", "invalid city", true),
//	R100_800_400("100.800.400", "invalid state/country combination", true),
//	R100_800_401("100.800.401", "state too long", true),
//	R100_800_500("100.800.500", "request contains no country", true),
//	R100_800_501("100.800.501", "invalid country", true),
//	R100_900_100("100.900.100", "request contains no email address", true),
//	R100_900_101("100.900.101", "invalid email address (probably invalid syntax)", true),
//	R100_900_105("100.900.105", "email address too long (max 50 chars)", true),
//	R100_900_200("100.900.200", "invalid phone number (has to start with a digit or a '+', at least 7 and max 25 chars long)", true),
//	R100_900_300("100.900.300", "invalid mobile phone number (has to start with a digit or a '+', at least 7 and max 25 chars long)", true),
//	R100_900_301("100.900.301", "mobile phone number mandatory", true),
//	R100_900_400("100.900.400", "request contains no ip number", true),
//	R100_900_401("100.900.401", "invalid ip number", true),
//	R100_900_450("100.900.450", "invalid birthdate", true),
//	R100_900_500("100.900.500", "invalid recurrence mode", true),
//	R200_100_101("200.100.101", "invalid Request Message. No valid XML. XML must be url-encoded! maybe it contains a not encoded ampersand or something similar.", true),
//	R200_100_102("200.100.102", "invalid Request. XML load missing (XML string must be sent within parameter 'load')", true),
//	R200_100_103("200.100.103", "invalid Request Message. The request contains structural errors", true),
//	R200_100_150("200.100.150", "transaction of multirequest not processed because of subsequent problems", true),
//	R200_100_151("200.100.151", "multi-request is allowed with a maximum of 10 transactions only", true),
//	R200_100_199("200.100.199", "Wrong Web Interface / URL used. Please check out the Tech Quick Start Doc Chapter 3.", true),
//	R200_100_201("200.100.201", "invalid Request/Transaction tag (not present or [partially] empty)", true),
//	R200_100_300("200.100.300", "invalid Request/Transaction/Payment tag (no or invalid code specified)", true),
//	R200_100_301("200.100.301", "invalid Request/Transaction/Payment tag (not present or [partially] empty)", true),
//	R200_100_302("200.100.302", "invalid Request/Transaction/Payment/Presentation tag (not present or [partially] empty)", true),
//	R200_100_401("200.100.401", "invalid Request/Transaction/Account tag (not present or [partially] empty)", true),
//	R200_100_402("200.100.402", "invalid Request/Transaction/Account(Customer, Relevance) tag (one of Account/Customer/Relevance must be present)", true),
//	R200_100_403("200.100.403", "invalid Request/Transaction/Analysis tag (Criterions must have a name and value)", true),
//	R200_100_404("200.100.404", "invalid Request/Transaction/Account (must not be present)", true),
//	R200_100_501("200.100.501", "invalid or missing customer", true),
//	R200_100_502("200.100.502", "invalid Request/Transaction/Customer/Name tag (not present or [partially] empty)", true),
//	R200_100_503("200.100.503", "invalid Request/Transaction/Customer/Contact tag (not present or [partially] empty)", true),
//	R200_100_504("200.100.504", "invalid Request/Transaction/Customer/Address tag (not present or [partially] empty)", true),
//	R200_100_601("200.100.601", "invalid Request/Transaction/(ApplePay|AndroidPay) tag (not present or [partially] empty)", true),
//	R200_100_602("200.100.602", "invalid Request/Transaction/(ApplePay|AndroidPay)/PaymentToken tag (not present or [partially] empty)", true),
//	R200_100_603("200.100.603", "invalid Request/Transaction/(ApplePay|AndroidPay)/PaymentToken tag (decryption error)", true),
//	R200_200_106("200.200.106", "duplicate transaction. Please verify that the UUID is unique", true),
//	R200_300_403("200.300.403", "Invalid HTTP method", true),
//	R200_300_404("200.300.404", "invalid or missing parameter", true),
//	R200_300_405("200.300.405", "Duplicate entity", true),
//	R200_300_406("200.300.406", "Entity not found", true),
//	R200_300_407("200.300.407", "Entity not specific enough", true),
//	R500_100_201("500.100.201", "Channel/Merchant is disabled (no processing possible)", true),
//	R500_100_202("500.100.202", "Channel/Merchant is new (no processing possible yet)", true),
//	R500_100_203("500.100.203", "Channel/Merchant is closed (no processing possible)", true),
//	R500_100_301("500.100.301", "Merchant-Connector is disabled (no processing possible)", true),
//	R500_100_302("500.100.302", "Merchant-Connector is new (no processing possible yet)", true),
//	R500_100_303("500.100.303", "Merchant-Connector is closed (no processing possible)", true),
//	R500_100_304("500.100.304", "Merchant-Connector is disabled at gateway (no processing possible)", true),
//	R500_100_401("500.100.401", "Connector is unavailable (no processing possible)", true),
//	R500_100_402("500.100.402", "Connector is new (no processing possible yet)", true),
//	R500_100_403("500.100.403", "Connector is unavailable (no processing possible)", true),
//	R500_200_101("500.200.101", "No target account configured for DD transaction", true),
//	R600_100_100("600.100.100", "Unexpected Integrator Error (Request could not be processed)", true),
//	R600_200_100("600.200.100", "invalid Payment Method", true),
//	R600_200_200("600.200.200", "Unsupported Payment Method", true),
//	R600_200_201("600.200.201", "Channel/Merchant not configured for this payment method", true),
//	R600_200_202("600.200.202", "Channel/Merchant not configured for this payment type", true),
//	R600_200_300("600.200.300", "invalid Payment Type", true),
//	R600_200_310("600.200.310", "invalid Payment Type for given Payment Method", true),
//	R600_200_400("600.200.400", "Unsupported Payment Type", true),
//	R600_200_500("600.200.500", "Invalid payment data. You are not configured for this currency or sub type (country or brand)", true),
//	R600_200_501("600.200.501", "Invalid payment data for Recurring transaction. Merchant or transaction data has wrong recurring configuration.", true),
//	R600_200_600("600.200.600", "invalid payment code (type or method)", true),
//	R600_200_700("600.200.700", "invalid payment mode (you are not configured for the requested transaction mode)", true),
//	R600_200_800("600.200.800", "invalid brand for given payment method and payment mode (you are not configured for the requested transaction mode)", true),
//	R600_200_810("600.200.810", "invalid return code provided", true),
//	R600_300_101("600.300.101", "Merchant key not found", true),
//	R600_300_200("600.300.200", "merchant source IP address not whitelisted", true),
//	R600_300_210("600.300.210", "merchant notificationUrl not whitelisted", true),
//	R600_300_211("600.300.211", "shopperResultUrl not whitelisted", true),
//	R700_100_100("700.100.100", "reference id not existing", true),
//	R700_100_200("700.100.200", "non matching reference amount", true),
//	R700_100_300("700.100.300", "invalid amount (probably too large)", true),
//	R700_100_400("700.100.400", "referenced payment method does not match with requested payment method", true),
//	R700_100_500("700.100.500", "referenced payment currency does not match with requested payment currency", true),
//	R700_100_600("700.100.600", "referenced mode does not match with requested payment mode", true),
//	R700_100_700("700.100.700", "referenced transaction is of inappropriate type", true),
//	R700_100_701("700.100.701", "referenced a DB transaction without explicitly providing an account. Not allowed to used referenced account.", true),
//	R700_100_710("700.100.710", "cross-linkage of two transaction-trees", true),
//	R700_300_100("700.300.100", "referenced tx can not be refunded, captured or reversed (invalid type)", true),
//	R700_300_200("700.300.200", "referenced tx was rejected", true),
//	R700_300_300("700.300.300", "referenced tx can not be refunded, captured or reversed (already refunded, captured or reversed)", true),
//	R700_300_400("700.300.400", "referenced tx can not be captured (cut off time reached)", true),
//	R700_300_500("700.300.500", "chargeback error (multiple chargebacks)", true),
//	R700_300_600("700.300.600", "referenced tx can not be refunded or reversed (was chargebacked)", true),
//	R700_300_700("700.300.700", "referenced tx can not be reversed (reversal not possible anymore)", true),
//	R700_300_800("700.300.800", "referenced tx can not be voided", true),
//	R700_400_000("700.400.000", "serious workflow error (call support)", true),
//	R700_400_100("700.400.100", "cannot capture (PA value exceeded, PA reverted or invalid workflow?)", true),
//	R700_400_101("700.400.101", "cannot capture (Not supported by authorization system)", true),
//	R700_400_200("700.400.200", "cannot refund (refund volume exceeded or tx reversed or invalid workflow?)", true),
//	R700_400_300("700.400.300", "cannot reverse (already refunded|reversed, invalid workflow or amount exceeded)", true),
//	R700_400_400("700.400.400", "cannot chargeback (already chargebacked or invalid workflow?)", true),
//	R700_400_402("700.400.402", "chargeback can only be generated internally by the payment system", true),
//	R700_400_410("700.400.410", "cannot reversal chargeback (chargeback is already reversaled or invalid workflow?)", true),
//	R700_400_420("700.400.420", "cannot reversal chargeback (no chargeback existing or invalid workflow?)", true),
//	R700_400_510("700.400.510", "capture needs at least one successful transaction of type (PA)", true),
//	R700_400_520("700.400.520", "refund needs at least one successful transaction of type (CP or DB or RB or RC)", true),
//	R700_400_530("700.400.530", "reversal needs at least one successful transaction of type (CP or DB or RB or PA)", true),
//	R700_400_540("700.400.540", "reconceile needs at least one successful transaction of type (CP or DB or RB)", true),
//	R700_400_550("700.400.550", "chargeback needs at least one successful transaction of type (CP or DB or RB)", true),
//	R700_400_560("700.400.560", "receipt needs at least one successful transaction of type (PA or CP or DB or RB)", true),
//	R700_400_561("700.400.561", "receipt on a registration needs a successfull registration in state 'OPEN'", true),
//	R700_400_562("700.400.562", "receipts can only be generated internally by the payment system", true),
//	R700_400_570("700.400.570", "cannot reference a waiting/pending transaction", true),
//	R700_400_580("700.400.580", "cannot find transaction", true),
//	R700_400_700("700.400.700", "initial and referencing channel-ids do not match", true),
//	R700_450_001("700.450.001", "cannot transfer money from one account to the same account", true),
//	R700_500_001("700.500.001", "referenced session contains too many transactions", true),
//	R700_500_002("700.500.002", "capture or preauthorization appears too late in referenced session", true),
//	R700_500_003("700.500.003", "test accounts not allowed in production", true),
//	R800_100_100("800.100.100", "transaction declined for unknown reason", true),
//	R800_100_150("800.100.150", "transaction declined (refund on gambling tx not allowed)", true),
//	R800_100_151("800.100.151", "transaction declined (invalid card)", true),
//	R800_100_152("800.100.152", "transaction declined by authorization system", true),
//	R800_100_153("800.100.153", "transaction declined (invalid CVV)", true),
//	R800_100_154("800.100.154", "transaction declined (transaction marked as invalid)", true),
//	R800_100_155("800.100.155", "transaction declined (amount exceeds credit)", true),
//	R800_100_156("800.100.156", "transaction declined (format error)", true),
//	R800_100_157("800.100.157", "transaction declined (wrong expiry date)", true),
//	R800_100_158("800.100.158", "transaction declined (suspecting manipulation)", true),
//	R800_100_159("800.100.159", "transaction declined (stolen card)", true),
//	R800_100_160("800.100.160", "transaction declined (card blocked)", true),
//	R800_100_161("800.100.161", "transaction declined (too many invalid tries)", true),
//	R800_100_162("800.100.162", "transaction declined (limit exceeded)", true),
//	R800_100_163("800.100.163", "transaction declined (maximum transaction frequency exceeded)", true),
//	R800_100_164("800.100.164", "transaction declined (merchants limit exceeded)", true),
//	R800_100_165("800.100.165", "transaction declined (card lost)", true),
//	R800_100_166("800.100.166", "transaction declined (Incorrect personal identification number)", true),
//	R800_100_167("800.100.167", "transaction declined (referencing transaction does not match)", true),
//	R800_100_168("800.100.168", "transaction declined (restricted card)", true),
//	R800_100_169("800.100.169", "transaction declined (card type is not processed by the authorization center)", true),
//	R800_100_170("800.100.170", "transaction declined (transaction not permitted)", true),
//	R800_100_171("800.100.171", "transaction declined (pick up card)", true),
//	R800_100_172("800.100.172", "transaction declined (account blocked)", true),
//	R800_100_173("800.100.173", "transaction declined (invalid currency, not processed by authorization center)", true),
//	R800_100_174("800.100.174", "transaction declined (invalid amount)", true),
//	R800_100_175("800.100.175", "transaction declined (invalid brand)", true),
//	R800_100_176("800.100.176", "transaction declined (account temporarily not available. Please try again later)", true),
//	R800_100_177("800.100.177", "transaction declined (amount field should not be empty)", true),
//	R800_100_178("800.100.178", "transaction declined (PIN entered incorrectly too often)", true),
//	R800_100_179("800.100.179", "transaction declined (exceeds withdrawal count limit)", true),
//	R800_100_190("800.100.190", "transaction declined (invalid configuration data)", true),
//	R800_100_191("800.100.191", "transaction declined (transaction in wrong state on aquirer side)", true),
//	R800_100_192("800.100.192", "transaction declined (invalid CVV, Amount has still been reserved on the customer's card and will be released in a few business days. Please ensure the CVV code is accurate before retrying the transaction)", true),
//	R800_100_195("800.100.195", "transaction declined (UserAccount Number/ID unknown)", true),
//	R800_100_196("800.100.196", "transaction declined (registration error)", true),
//	R800_100_197("800.100.197", "transaction declined (registration cancelled externally)", true),
//	R800_100_198("800.100.198", "transaction declined (invalid holder)", true),
//	R800_100_402("800.100.402", "cc/bank account holder not valid", true),
//	R800_100_403("800.100.403", "transaction declined (revocation of authorisation order)", true),
//	R800_100_500("800.100.500", "Card holder has advised his bank to stop this recurring payment", true),
//	R800_100_501("800.100.501", "Card holder has advised his bank to stop all recurring payments for this merchant", true),
//	R800_110_100("800.110.100", "duplicate transaction", true),
//	R800_120_100("800.120.100", "Rejected by Throttling.", true),
//	R800_120_101("800.120.101", "maximum number of transactions per account already exceeded", true),
//	R800_120_102("800.120.102", "maximum number of transactions per ip already exceeded", true),
//	R800_120_103("800.120.103", "maximum number of transactions per email already exceeded", true),
//	R800_120_200("800.120.200", "maximum total volume of transactions already exceeded", true),
//	R800_120_201("800.120.201", "maximum total volume of transactions per account already exceeded", true),
//	R800_120_202("800.120.202", "maximum total volume of transactions per ip already exceeded", true),
//	R800_120_203("800.120.203", "maximum total volume of transactions per email already exceeded", true),
//	R800_120_300("800.120.300", "chargeback rate per bin exceeded", true),
//	R800_120_401("800.120.401", "maximum number of transactions or total volume for configured MIDs or CIs exceeded", true),
//	R800_121_100("800.121.100", "Channel not configured for given source type. Please contact your account manager.", true),
//	R800_130_100("800.130.100", "Transaction with same TransactionId already exists", true),
//	R800_140_100("800.140.100", "maximum number of registrations per mobile number exceeded", true),
//	R800_140_101("800.140.101", "maximum number of registrations per email address exceeded", true),
//	R800_140_110("800.140.110", "maximum number of registrations of mobile per credit card number exceeded", true),
//	R800_140_111("800.140.111", "maximum number of registrations of credit card number per mobile exceeded", true),
//	R800_140_112("800.140.112", "maximum number of registrations of email per credit card number exceeded", true),
//	R800_140_113("800.140.113", "maximum number of registrations of credit card number per email exceeded", true),
//	R800_150_100("800.150.100", "Account Holder does not match Customer Name", true),
//	R800_160_100("800.160.100", "Invalid payment data for configured Shopper Dispatching Type", true),
//	R800_160_110("800.160.110", "Invalid payment data for configured Payment Dispatching Type", true),
//	R800_160_120("800.160.120", "Invalid payment data for configured Recurring Transaction Dispatching Type", true),
//	R800_160_130("800.160.130", "Invalid payment data for configured TicketSize Dispatching Type", true),
//	R800_200_159("800.200.159", "account or user is blacklisted (card stolen)", true),
//	R800_200_160("800.200.160", "account or user is blacklisted (card blocked)", true),
//	R800_200_165("800.200.165", "account or user is blacklisted (card lost)", true),
//	R800_200_202("800.200.202", "account or user is blacklisted (account closed)", true),
//	R800_200_208("800.200.208", "account or user is blacklisted (account blocked)", true),
//	R800_200_220("800.200.220", "account or user is blacklisted (fraudulent transaction)", true),
//	R800_300_101("800.300.101", "account or user is blacklisted", true),
//	R800_300_102("800.300.102", "country blacklisted", true),
//	R800_300_200("800.300.200", "email is blacklisted", true),
//	R800_300_301("800.300.301", "ip blacklisted", true),
//	R800_300_302("800.300.302", "ip is anonymous proxy", true),
//	R800_300_401("800.300.401", "bin blacklisted", true),
//	R800_300_500("800.300.500", "transaction temporary blacklisted (too many tries invalid CVV)", true),
//	R800_300_501("800.300.501", "transaction temporary blacklisted (too many tries invalid expire date)", true),
//	R800_400_100("800.400.100", "AVS Check Failed", true),
//	R800_400_101("800.400.101", "Mismatch of AVS street value", true),
//	R800_400_102("800.400.102", "Mismatch of AVS street number", true),
//	R800_400_103("800.400.103", "Mismatch of AVS PO box value fatal", true),
//	R800_400_104("800.400.104", "Mismatch of AVS zip code value fatal", true),
//	R800_400_105("800.400.105", "Mismatch of AVS settings (AVSkip, AVIgnore, AVSRejectPolicy) value", true),
//	R800_400_110("800.400.110", "AVS Check Failed. Amount has still been reserved on the customer's card and will be released in a few business days. Please ensure the billing address is accurate before retrying the transaction.", true),
//	R800_400_150("800.400.150", "Implausible address data", true),
//	R800_400_151("800.400.151", "Implausible address state data", true),
//	R800_400_200("800.400.200", "Invalid Payer Authentication in 3DSecure transaction", true),
//	R800_400_500("800.400.500", "Waiting for confirmation of non-instant payment. Denied for now.", true),
//	R800_400_501("800.400.501", "Waiting for confirmation of non-instant debit. Denied for now.", true),
//	R800_400_502("800.400.502", "Waiting for confirmation of non-instant refund. Denied for now.", true),
//	R800_500_100("800.500.100", "direct debit transaction declined for unknown reason", true),
//	R800_500_110("800.500.110", "Unable to process transaction - ran out of terminalIds - please contact acquirer", true),
//	R800_600_100("800.600.100", "transaction is being already processed", true),
//	R800_700_100("800.700.100", "transaction for the same session is currently being processed, please try again later.", true),
//	R800_700_101("800.700.101", "family name too long", true),
//	R800_700_201("800.700.201", "given name too long", true),
//	R800_700_500("800.700.500", "company name too long", true),
//	R800_800_102("800.800.102", "Invalid street", true),
//	R800_800_202("800.800.202", "Invalid zip", true),
//	R800_800_302("800.800.302", "Invalid city", true),
//	R800_800_800("800.800.800", "The payment system is currenty unavailable, please contact support in case this happens again.", true),
//	R800_800_801("800.800.801", "The payment system is currenty unter maintenance. Please apologize for the inconvenience this may cause. If you were not informed of this maintenance window in advance, contact your sales representative.", true),
//	R800_900_100("800.900.100", "sender authorization failed ", true),
//	R800_900_101("800.900.101", "invalid email address (probably invalid syntax)", true),
//	R800_900_200("800.900.200", "invalid phone number (has to start with a digit or a '+', at least 7 and max 25 chars long)", true),
//	R800_900_201("800.900.201", "unknown channel", true),
//	R800_900_300("800.900.300", "invalid authentication information", true),
//	R800_900_301("800.900.301", "user authorization failed, user has no sufficient rights to process transaction", true),
//	R800_900_302("800.900.302", "Authorization failed", true),
//	R800_900_303("800.900.303", "No token created", true),
//	R800_900_399("800.900.399", "Invalid JWT token", true),
//	R800_900_401("800.900.401", "Invalid IP number", true),
//	R800_900_450("800.900.450", "Invalid birthdate", true),
//	R900_100_100("900.100.100", "unexpected communication error with connector/acquirer", true),
//	R900_100_200("900.100.200", "error response from connector/acquirer", true),
//	R900_100_201("900.100.201", "error on the external gateway (e.g. on the part of the bank, acquirer,...)", true),
//	R900_100_202("900.100.202", "invalid transaction flow, the requested function is not applicable for the referenced transaction.", true),
//	R900_100_203("900.100.203", "error on the internal gateway", true),
//	R900_100_300("900.100.300", "timeout, uncertain result", true),
//	R900_100_310("900.100.310", "Transaction timed out due to internal system misconfiguration. Request to acquirer has not been sent.", true),
//	R900_100_400("900.100.400", "timeout at connectors/acquirer side", true),
//	R900_100_500("900.100.500", "timeout at connectors/acquirer side (try later)", true),
//	R900_100_600("900.100.600", "connector/acquirer currently down", true),
//	R900_200_100("900.200.100", "Message Sequence Number of Connector out of sync", true),
//	R900_300_600("900.300.600", "user session timeout", true),
//	R900_400_100("900.400.100", "unexpected communication error with external risk provider", true),
//	R999_999_888("999.999.888", "UNDEFINED PLATFORM DATABASE ERROR", true),
//	R999_999_999("999.999.999", "UNDEFINED CONNECTOR/ACQUIRER ERROR", true);
//	
//	ResultCode(String code) {
//		this.code = code;
//		this.fatal = false;
//	}
//	
//	ResultCode(String code, String description) {
//		this.code = code;
//		this.description = description;
//		this.fatal = false;
//	}
//	
//	ResultCode(String code, String description, Boolean fatal) {
//		this.code = code;
//		this.description = description;
//		this.fatal = fatal;
//	}
//	
//	@Getter
////	@Accessors(fluent=true)
//	private String code;
//	@Getter
////	@Accessors(fluent=true)
//	private String description;
//	@Getter
////	@Accessors(fluent = true)
//	private Boolean fatal;
//	@Setter
//	@Getter
//	private List<ParameterError> parameterErrors;
//	
//	public void addParameterError(ParameterError parameterError) {
//		if (parameterErrors == null) parameterErrors = new ArrayList<>();
//		parameterErrors.add(parameterError);
//	}
//	
//	private boolean patternMatcher(String pattern) {
//		Pattern p = Pattern.compile(pattern);
//		Matcher m = p.matcher(code);
//		return m.find();
//	}
//	
//	public boolean isSuccessful() {
//		return patternMatcher("(^(000\\.000\\.|000\\.100\\.1|000\\.[36]))");
//	}
//	public boolean isSuccessManualReview() {
//		return patternMatcher("(^(000\\.400\\.0|000\\.400\\.100))");
//	}
//	public boolean isPending() {
//		return patternMatcher("(^(000\\.200))");
//	}
//	public boolean isPendingWaiting() {
//		return patternMatcher("(^(800\\.400\\.5|100\\.400\\.500))");
//	}
//	public boolean isRejectedRisk() {
//		return patternMatcher("(^(000\\.400\\.[1][0-9][1-9]|000\\.400\\.2))");
//	}
//	public boolean isDeclined() {
//		return patternMatcher("(^(800\\.[17]00|800\\.800\\.[123]))");
//	}
//	public boolean isCommError() {
//		return patternMatcher("(^(900\\.[1234]00))");
//	}
//	public boolean isSystemError() {
//		return patternMatcher("(^(800\\.5|999\\.|600\\.1|800\\.800\\.8))");
//	}
//	public boolean isAsyncError() {
//		return patternMatcher("(^(100\\.39[765]))");
//	}
//	public boolean isExternalRisk() {
//		return patternMatcher("(^(100\\.400|100\\.38|100\\.370\\.100|100\\.370\\.11))");
//	}
//	public boolean isRejectAddrValidation() {
//		return patternMatcher("(^(800\\.400\\.1))");
//	}
//	public boolean isReject3DSecure() {
//		return patternMatcher("(^(800\\.400\\.2|100\\.380\\.4|100\\.390))");
//	}
//	public boolean isRejectBlacklist() {
//		return patternMatcher("(^(100\\.100\\.701|800\\.[32]))");
//	}
//	public boolean isRejectRiskValidation() {
//		return patternMatcher("(^(800\\.1[123456]0))");
//	}
//	public boolean isRejectConfigValidation() {
//		return patternMatcher("(^(600\\.[23]|500\\.[12]|800\\.121))");
//	}
//	public boolean isRejectRegistrationValidation() {
//		return patternMatcher("(^(100\\.[13]50))");
//	}
//	public boolean isRejectJobValidation() {
//		return patternMatcher("(^(100\\.250|100\\.360))");
//	}
//	public boolean isRejectReferenceValidation() {
//		return patternMatcher("(^(700\\.[1345][05]0))");
//	}
//	public boolean isRejectFormatValidation() {
//		return patternMatcher("(^(200\\.[123]|100\\.[53][07]|800\\.900|100\\.[69]00\\.500))");
//	}
//	public boolean isRejectAddressValidation() {
//		return patternMatcher("(^(100\\.800))");
//	}
//	public boolean isRejectContactValidation() {
//		return patternMatcher("(^(100\\.[97]00))");
//	}
//	public boolean isRejectAccountValidation() {
//		return patternMatcher("(^(100\\.100|100.2[01]))");
//	}
//	public boolean isRejectAmountValidation() {
//		return patternMatcher("(^(100\\.55))");
//	}
//	public boolean isRejectRiskManagement() {
//		return patternMatcher("(^(100\\.380\\.[23]|100\\.380\\.101))");
//	}
//	public boolean isChargeback() {
//		return patternMatcher("(^(000\\.100\\.2))");
//	}
//	
//	public static class ResultCodeDeserializer extends StdDeserializer<ResultCode> {
//		private static final long serialVersionUID = 5026094034388367371L;
//		
//		public ResultCodeDeserializer() {
//			super(ResultCode.class);
//		}
//		
//		@Override
//		public ResultCode deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
//			final JsonNode jsonNode = jp.readValueAsTree();
//			String code = jsonNode.get("code").asText();
//			
//			for (ResultCode rc:ResultCode.values()) {
//				if (rc.code.equals(code)) {
//					JsonNode peNode = jsonNode.get("parameterErrors");
//					if (peNode != null) {
//						peNode.forEach(pe -> {
//							rc.addParameterError(ParameterError.builder().name(pe.get("name").asText()).value(pe.get("value").asText()).message(pe.get("message").asText()).build());
//						});
//					}
//					return rc;
//				}
//			}
//			throw dc.mappingException("Cannot deserialize ResultCode from code "+code);
//		}
//	}
//}