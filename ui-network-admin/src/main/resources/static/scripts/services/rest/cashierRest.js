'use strict';

angular.module('lithium-rest-cashier', ['restangular'])
.factory('rest-cashier', ['$rootScope', 'Restangular', '$security',
	function($rootScope, Restangular, security) {
		try {
			var service = {};
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-cashier');
			});
			
			service.copy = function(copy) {
				return Restangular.copy(copy);
			}
			
			service.frontendMethods = function(type, userGuid, ipAddr, userAgent) {
				//DomainMethodController
				return config.all("cashier").all("dm").all("frontend").all(type).getList({
					userGuid: userGuid,
					ipAddr: ipAddr,
					userAgent: userAgent
				});
			}
			
			service.frontendProcessors = function(methodId, userGuid, ipAddr, userAgent) {
				//DomainMethodController
				return config.all("cashier").all("dm").all("frontend").all("processors").getList({
					methodId: methodId,
					userGuid: userGuid,
					ipAddr: ipAddr,
					userAgent: userAgent
				});
			}

			service.methodsNoImage = function() {
				//MethodController
				return config.all("cashier").all("m").getList();
			}
			service.methods = function() {
				//MethodController
				return config.all("cashier").all("m").all("image").getList();
			}
			service.processors = function(methodId, type) {
				//MethodController
				return config.all("cashier").all("p").one("method", methodId).all(type).getList();
			}

			service.domainMethods = function(domainName, type) {
				return config.all("cashier").all("dm").one("domain", domainName).all(type).all("image").getList();
			}
			service.domainMethodDeleteFull = function(domainMethod) {
				return config.all("cashier").one("dm", domainMethod.id).remove();
			}
			service.domainMethodAdd = function(domainName, dm, type) {
				var deposit = false;
				if (type === 'deposit') {
					deposit = true;
				}
				return config.all("cashier").all("dm").all("method").all(dm.method+'').post('', {
					name:dm.name,
					enabled:dm.enabled,
					deposit:deposit,
					priority:dm.priority,
					domainName:domainName
				});
			}
			service.domainMethod = function(domainMethodId) {
				return config.all("cashier").one("dm", domainMethodId).customGET("image");
			}
			service.domainMethodUpdate = function(domainMethod) {
				return config.all("cashier").all("dm").customPUT(domainMethod);
			}
			service.domainMethodUpdateMultiple = function(domainMethods) {
				return config.all("cashier").all("dm").all("multiple").customPUT(domainMethods);
			}
			service.domainMethodToggleEnabled = function(domainMethodId) {
				return config.all("cashier").one("dm", domainMethodId).customGET("enable");
			}
			service.domainMethodImage = function(domainMethodId) {
				return config.all("cashier").one("dm", domainMethodId).customGET("imageonly");
			}
			
			service.domainMethodProcessors = function(domainMethodId) {
				return config.all("cashier").one("dm", domainMethodId).all("processors").getList();
			}
			
			service.domainMethodProfile = function(domainMethodId, profileId) {
				return config.all("cashier").all("dm").all("profile").one(domainMethodId+'', profileId+'').get();
			}
			service.domainMethodProfileUpdate = function(domainMethodProfile) {
				return config.all("cashier").all("dm").all("profile").customPUT(domainMethodProfile);
			}
			service.domainMethodProfileUpdateMultiple = function(domainMethodProfiles) {
				return config.all("cashier").all("dm").all("profile").all("multiple").customPUT(domainMethodProfiles);
			}
			
			service.domainMethodUser = function(domainMethodId, userGuid) {
				return config.all("cashier").all("dm").all("user").one(domainMethodId+'').get({userGuid:userGuid});
			}
			service.domainMethodUserUpdate = function(domainMethodUser) {
				console.log(domainMethodUser);
				return config.all("cashier").all("dm").all("user").customPUT(domainMethodUser);
			}
			service.domainMethodUserUpdateMultiple = function(domainMethodUsers) {
				console.log(domainMethodUsers);
				return config.all("cashier").all("dm").all("user").all("multiple").customPUT(domainMethodUsers);
			}
			service.domainMethodAccounting = function(domainMethodId) {
				return config.all("cashier").one("dm", domainMethodId).all("totals").getList();
			}
			service.domainMethodAccountingUser = function(domainMethodId, username) {
				return config.all("cashier").one("dm", domainMethodId).all("totals").all(username).getList();
			}
			
			service.domainMethodProcessorAdd = function(domainMethodProcessor) {
				return config.all("cashier").all("dmp").all("create").post(domainMethodProcessor);
			}
			
			service.domainMethodProcessor = function(domainMethodProcessorId) {
				return config.all("cashier").one("dmp", domainMethodProcessorId).get();
			}
			service.domainMethodProcessorAccounting = function(domainMethodProcessorId) {
				return config.all("cashier").one("dmp", domainMethodProcessorId).all("totals").getList();
			}
			service.domainMethodProcessorAccountingUser = function(domainMethodProcessorId, username) {
				return config.all("cashier").one("dmp", domainMethodProcessorId).all("totals").all(username).getList();
			}
			service.domainMethodProcessorUpdate = function(domainMethodProcessor) {
				return config.all("cashier").one("dmp", domainMethodProcessor.id).customPUT(
					domainMethodProcessor,
					'update'
				);
			}
			service.domainMethodProcessorUpdateMultiple = function(domainMethodProcessors) {
				return config.all("cashier").all("dmp").all("multiple").customPUT(domainMethodProcessors);
			}
			
			service.domainMethodProcessorSaveDL = function(domainMethodProcessor) {
				return config.all("cashier").one("dmp", domainMethodProcessor.id).customPUT(
					domainMethodProcessor.domainLimits,
					'domainlimits'
				);
			}
			service.domainMethodProcessorSave = function(domainMethodProcessor, type) {
				return config.all("cashier").one("dmp", domainMethodProcessor.id).customPUT(
					domainMethodProcessor[type],
					type
				);
			}
			service.domainMethodProcessorDeleteFull = function(domainMethodProcessor) {
				return config.all("cashier").one("dmp", domainMethodProcessor.id).remove();
			}
			service.domainMethodProcessorDelete = function(domainMethodProcessor, type) {
				return config.all("cashier").one("dmp", domainMethodProcessor.id).all(type).remove();
			}
			service.domainMethodProcessorPropertyDelete = function(domainMethodProcessorId, domainMethodProcessorPropertyId) {
				return config.all("cashier").one("dmp", domainMethodProcessorId).all("prop").one(domainMethodProcessorPropertyId+'').remove();
			}
			service.domainMethodProcessorPropertiesSave = function(domainMethodProcessorId, properties) {
				return config.all("cashier").one("dmp", domainMethodProcessorId).customPUT(
					properties,
					'props'
				);
			}
			service.domainMethodProcessorProperties = function(domainMethodProcessorId) {
				return config.all("cashier").one("dmp", domainMethodProcessorId).getList("props");
			}
			service.domainMethodProcessorPropertiesNoDefaults = function(domainMethodProcessorId) {
				return config.all("cashier").one("dmp", domainMethodProcessorId).all("props").all("nodef").getList();
			}
			
			service.domainMethodProcessorUsers = function(domainMethodProcessorId) {
				return config.all("cashier").all("dmp").one("user", domainMethodProcessorId).all("bydmp").getList();
			}
			service.domainMethodProcessorsByUserNoImage = function(userGuid) {
				return config.all("cashier").all("dmp").all("user").all("byuser").getList({userGuid: userGuid});
			}
			service.domainMethodProcessorUserCreateOrUpdate = function(domainMethodProcessorUser) {
				return config.all("cashier").all("dmp").all("user").post(domainMethodProcessorUser);
			}
			service.domainMethodProcessorUserUpdateMultiple = function(domainMethodProcessorProfiles) {
				return config.all("cashier").all("dmp").all("user").all("multiple").customPUT(domainMethodProcessorProfiles);
			}
			service.domainMethodProcessorUserSave = function(domainMethodProcessorUser, type) {
				console.log(domainMethodProcessorUser);
				return config.all("cashier").one("dmp").all("user").all(domainMethodProcessorUser.id).customPUT(
					domainMethodProcessorUser[type],
					type
				);
			}
			service.domainMethodProcessorUserDelete = function(domainMethodProcessorUser, type) {
				return config.all("cashier").one("dmp").all("user").all(domainMethodProcessorUser.id).all(type).remove();
			}
			
			
			service.domainMethodProcessorsByDomainMethodProfileNoImage = function(domainMethodProfileId) {
				return config.all("cashier").all("dmp").all("profile").one("bydmprofile", domainMethodProfileId).getList();
			}
			service.domainMethodProcessorsByDomainMethodProfile = function(domainMethodProfileId) {
				return config.all("cashier").all("dmp").all("profile").one("bydmprofile", domainMethodProfileId).getList("image");
			}
			service.domainMethodProcessorsByProfileNoImage = function(profile) {
				return config.all("cashier").all("dmp").all("profile").one("byprofile", profile.id).getList();
			}
			service.domainMethodProcessorsByProfile = function(profile) {
				return config.all("cashier").all("dmp").all("profile").one("byprofile", profile.id).getList("image");
			}
			service.domainMethodProcessorProfiles = function(domainMethodProcessorId) {
				return config.all("cashier").all("dmp").all("profile").one("bydmp", domainMethodProcessorId).getList();
			}
			service.domainMethodProcessorProfileCreateOrUpdate = function(domainMethodProcessorProfile) {
				return config.all("cashier").all("dmp").all("profile").post(domainMethodProcessorProfile);
			}
			service.domainMethodProcessorProfileUpdateMultiple = function(domainMethodProcessorProfiles) {
				return config.all("cashier").all("dmp").all("profile").all("multiple").customPUT(domainMethodProcessorProfiles);
			}
			service.domainMethodProcessorProfileSave = function(domainMethodProcessorProfile, type) {
				return config.all("cashier").one("dmp").all("profile").all(domainMethodProcessorProfile.id).customPUT(
					domainMethodProcessorProfile[type],
					type
				);
			}
			service.domainMethodProcessorProfileDelete = function(domainMethodProcessorProfile, type) {
				return config.all("cashier").one("dmp").all("profile").all(domainMethodProcessorProfile.id).all(type).remove();
			}
			
			service.profiles = function(domainName) {
				return config.all("cashier").all("profile").all(domainName+'').getList();
			}
			service.profileSave = function(profile) {
				return config.all("cashier").all("profile").all(profile.domain.name).post(profile);
			}
			service.profileAdd = function(domainName, profile) {
				return config.all("cashier").all("profile").all(domainName+'').post('', {
					code:profile.code,
					name:profile.name,
					description:profile.description,
					totalDeposits:profile.profileRequirements.totalDeposits,
					totalPayouts:profile.profileRequirements.totalPayouts,
					numberDeposits:profile.profileRequirements.numberDeposits,
					numberPayouts:profile.profileRequirements.numberPayouts,
					accountActiveDays:profile.profileRequirements.accountActiveDays
				});
			}
			service.profileUpdate = function(domainName, profile) {
//				return profile.put();
				return config.all("cashier").all("profile").all(domainName).customPUT(
					'',
					profile.id, 
					{
						code:profile.code,
						name:profile.name,
						description:profile.description,
						totalDeposits:profile.profileRequirements.totalDeposits,
						totalPayouts:profile.profileRequirements.totalPayouts,
						numberDeposits:profile.profileRequirements.numberDeposits,
						numberPayouts:profile.profileRequirements.numberPayouts,
						accountActiveDays:profile.profileRequirements.accountActiveDays
					}
				);
			}
			service.profile = function(domainName, code) {
				return config.all("cashier").all("profile").all(domainName).get('code', {
					code:code
				});
			}
			service.deleteProfileById = function(domainName, profileId) {
				return config.all("cashier").all("profile").all(domainName).all(profileId).all("delete").post({});
			}
			service.profileById = function(domainName, id) {
				return config.all("cashier").all("profile").one(domainName, id).get();
			}
			
			
			service.user = function(userGuid) {
				return config.all("cashier").all("user").get('', {userGuid:userGuid});
			}
			service.userProfileUpdate = function(user, profile) {
				return config.all("cashier").all("user").all("profile").customPUT(
					'',
					'', 
					{
						userGuid:user.guid,
						profileId:profile.id
					}
				);
			}
			
			service.transaction = function(transactionId) {
				return config.all("cashier").one("transaction", transactionId).get();
			}
			service.linkedTransaction = function(transactionId) {
				return config.all("cashier").one("transaction").one("linkof", transactionId).get();
			}
			service.transactionWorkflow = function(transactionId, page, pageSize, truncate) {
				return config.all("cashier").one("transaction", transactionId).getList("workflow", {page: page, pageSize: pageSize, truncate: truncate});
			}
			service.transactionAttempts = function(transactionId) {
				return config.all("cashier").one("transaction", transactionId).getList("attempts");
			}
			service.transactionLabels = function(transactionId) {
				return config.all("cashier").one("transaction", transactionId).getList("labels");
			}
			service.transactionAttempt = function(transactionId, workflowToId) {
				return config.all("cashier").one("transaction", transactionId).one("attempt", workflowToId).get();
			}
			service.transactionDataPerStage = function(transactionId, stage) {
				return config.all("cashier").one("transaction", transactionId).one("data", stage+'').getList();
			}
			service.transactionData = function(transactionId) {
				return config.all("cashier").one("transaction", transactionId).getList("data");
			}
			service.transactionStatuses = function() {
				return config.all("cashier").one("transaction").one("statuses").getList();
			}
			service.transactionPaymentTypes = function() {
				return config.all("cashier").one("transaction").one("paymentTypes").getList();
			}
			service.changeStatus = function(domain, transactionId, status, amount, comment) {
				var qs = {comment: comment};
				if (amount !== undefined && amount !== null) qs.amount = amount;
				return config.one("admin", domain).one("changestatus", transactionId).customGET(status, qs);
			}
			service.withdrawApprovable = function(domain, transactionId, guid, currencyCode, isWithdrawalFundsReserved) {
				return config.one('admin', domain).one('changestatus', transactionId).customGET('is-enough-balance',
					{guid: guid, currencyCode: currencyCode, isWithdrawalFundsReserved: isWithdrawalFundsReserved});
			}
			service.transactionRetry = function(domain, transactionId) {
				return config.one("admin", domain).one("changestatus", transactionId).customGET('retry');
			}
			service.transactionCancel = function(domain, transactionId, comment) {
				return config.one("admin", domain).one("changestatus", transactionId).customGET('cancel', {comment: comment});
			}
			service.transactionClearProvider = function(domain, transactionId, comment) {
				return config.one("admin", domain).one("changestatus", transactionId).customGET('clearProvider', {comment: comment});
			}
			service.transactionOnHold = function(domain, transactionId, reason) {
				return config.one("admin", domain).one("changestatus", transactionId).all('on-hold').post('', {reason: reason});
			}
			service.findMethodFields = function(methodCode, transactionType, input) {
				return config.all("cashier").one("m", methodCode).one("fields", input).all(transactionType).getList();
			}
			service.addManualTransaction = function(manualTransaction) {
				return config.all("cashier").all("manual").all("transaction").post(manualTransaction);
			}
			service.executeDirectWithdrawal = function(directWithdrawalTransaction) {
				return config.all("cashier").all("direct-withdrawal").post(directWithdrawalTransaction);
			}

			service.executeManualWithdrawal = function(manualWithdrawalTransaction) {
				return config.all("cashier").all("manual-withdrawal").post(manualWithdrawalTransaction);
			}

			service.addTransactionRemark = function(transactionId, message) {
				return config.all("cashier").one("transaction", transactionId).all("add-transaction-remark").post({message: message});
			}

			service.getTransactionRemarks = function(transactionId) {
				return config.all("cashier").one("transaction", transactionId).all("get-transaction-remarks").getList();
			}

			service.getPaymentMethodsByUser = function(userGuid, domain) {
				return config.all("cashier").all("transaction").one("user").customGET('payment-methods', {userGuid: userGuid, domain: domain});
			}

			service.getPaymentMethodsByTranId = function(tranId) {
				return config.all("cashier").one("transaction", tranId).customGET('payment-methods');
			}

			service.paymentMethodStatusAll = function() {
				return config.all("cashier").all("transaction").all("payment-methods").customGET('status-all');
			}

			service.paymentMethodStatusUpdate = function(domain, id, statusId, verified, contraAccount, comment) {
				return config.all("cashier").one("transaction", domain).one("payment-methods", id).customGET('status-update', {statusId: statusId, verified: verified, contraAccount: contraAccount, comment: comment});
			}

			service.getLastXTransactionForUser = function (trId, count) {
				return config.all("cashier").all("transaction").customGET ('lastXtransactions', {trId: trId, count: count });
			}

			service.findWithdrawalDomainMethodProcessors = function (domainName) {
				return config.all("backoffice").all("cashier").all("bank-account-lookup").all("find-by-domain-name").all("withdrawal-dmps").get('', {
					domainName: domainName
				});
			}

			service.bankAccountLookup = function (domainName, processorCode, processorDescription, processorUrl, transactionId) {
				return config.all("backoffice").all("cashier").all("bank-account-lookup").all("find-by-transaction-id").get('', {
					domainName: domainName,
					processorCode: processorCode,
					processorDescription: processorDescription,
					processorUrl: processorUrl,
					transactionId: transactionId
				});
			};

			service.bankAccountLookupModule = function (bankAccountLookupRequest, processorUrl) {
				return config.all("backoffice").all("cashier").all("bank-account-lookup").all("find-by-bank-account-lookup-request").post(bankAccountLookupRequest, {
					processorUrl: processorUrl
				});
			};

			service.banks = function (processorProperties, processorUrl) {
				return config.all("backoffice").all("cashier").all("banks").post(processorProperties, {
					processorUrl: processorUrl
				});
			};

			service.withdrawalOnHold = function (guid, comment) {
				return config.all("backoffice").all("cashier").all("transaction-bulk-processing").all("proceed-by-guid").post('', {
						guid: guid,
						comment: comment,
					    code: "HOLD_PENDING_WITHDRAWALS"
					}
				);
			};

			service.withdrawalReprocess = function (guid, comment) {
				return config.all("backoffice").all("cashier").all("transaction-bulk-processing").all("proceed-by-guid").post('', {
						guid: guid,
						comment: comment,
						code: "RE_PROCESS_ON_HOLD_WITHDRAWALS"
					}
				);
			};

			service.transactionTags = function () {
				return config.all("backoffice").all("cashier").all("transaction-tags-list").getList();
			};

			service.transactionSetRFIFlag = function(transactionId) {
				return config.all("backoffice").all("cashier").all("transactions").all(transactionId).all("tags").all("RFI_RECEIVED").post('', {});
			}

			service.transactionRemoveRFIFlag = function(transactionId) {
				return config.all("backoffice").all("cashier").all("transactions").all(transactionId).all("tags").all("RFI_RECEIVED").remove({});
			}

			return service;
		} catch (err) {
			console.error(err);
			throw err;
		}
	}
]);
