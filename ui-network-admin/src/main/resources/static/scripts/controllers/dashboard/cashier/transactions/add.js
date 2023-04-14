'use strict';

angular.module('lithium')
.controller('CashierTranAddController',
["$scope", "$stateParams", "$state", "$userService", "rest-cashier", "notify", "errors", "UserRest", "rest-casino",
function($scope, $stateParams, $state, $userService, cashierRest, notify, errors, userRest, casinoRest) {
	$scope.setDescription("UI_NETWORK_ADMIN.CASHIER.TAB.ADDTRANS");
	var controller = this;
	
	var dmpId = -1;
	var domainMethodId = -1;
	
	var username = "";
	
	controller.selectedUser = undefined;
	controller.selectedBonus = undefined;
	controller.inputFields = [];
	controller.outputFields = [];
	
	controller.transactionTypes = [{ id: 1, name: "Deposit" }, { id: 2, name: "Withdraw" }];
	controller.selectedTransactionType = controller.transactionTypes[0];
	
	controller.findDomainMethods = function() {
		controller.selectedMethod = undefined;
		controller.domainMethods = undefined;
		controller.selectedProcessor = undefined;
		controller.domainMethodProcessors = undefined;
		dmpId = -1;
		domainMethodId = -1;
		
		cashierRest.domainMethods($stateParams.domainName, controller.selectedTransactionType.name.toLowerCase()).then(function(dm) {
			controller.domainMethods = dm.plain();
			controller.domainMethods.unshift({id:-1, name:'...'});
			angular.forEach(controller.domainMethods, function(domainMethod, index) {
				if (domainMethod.enabled != null && domainMethod.enabled != undefined && !domainMethod.enabled) {
					controller.domainMethods.splice(index, 1);
				}
			});
		}).catch(function(error) {
			errors.catch("", false)(error)
		});
	}
	
	controller.findDomainMethods();
	
	controller.findDomainMethodProcessors = function(dm) {
		controller.selectedProcessor = undefined;
		controller.domainMethodProcessors = undefined;
		dmpId = -1;
		if (dm != -1) {
			cashierRest.domainMethodProcessors(dm).then(function(dmps) {
				controller.domainMethodProcessors = dmps.plain();
				controller.domainMethodProcessors.unshift({id:-1, name:'...'});
				angular.forEach(controller.domainMethodProcessors, function(domainMethodProcessor, index) {
					if (domainMethodProcessor.enabled != null && domainMethodProcessor.enabled != undefined && !domainMethodProcessor.enabled) {
						controller.domainMethodProcessors.splice(index, 1);
					}
				});
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		}
	}
	
	controller.resetUserSearch = function() {
		controller.noPlayerChosen = false;
		controller.selectedBonus = undefined;
		controller.availableBonuses = undefined;
		controller.selectedUser = undefined;
		username = "";
	}
	controller.searchUsers = function(userGuid) {
		controller.noPlayerChosen = false;
		controller.selectedBonus = undefined;
		controller.availableBonuses = undefined;
		return userRest.search($stateParams.domainName, userGuid).then(function(searchResult) {
			return searchResult.plain();
		}).catch(function(error) {
			errors.catch("", false)(error)
		});
	};
	
	controller.loadAvailableBonuses = function() {
		if (controller.selectedUser != undefined && controller.selectedUser != null) {
			casinoRest.availableBonuses($stateParams.domainName, 1, controller.selectedUser.guid)
			.then(function(bonuses) {
				controller.availableBonuses = bonuses.plain();
				controller.availableBonuses.unshift({bonusId:-1, bonusName:'...'});
				console.log(controller.availableBonuses);
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		}
	}
	
	controller.listTransactions = function() {
		$state.transitionTo('dashboard.cashier.transactions.list', {
			domainName: $stateParams.domainName,
			tranType: "deposit"
		});
	}
	
	$scope.$watch(function() { return controller.selectedTransactionType }, function(newValue, oldValue) {
		if (newValue != oldValue) {
			controller.findDomainMethods();
		}
	});
	
	$scope.$watch(function() { return controller.selectedMethod }, function(newValue, oldValue) {
		if (newValue != oldValue) {
			if (newValue == undefined || newValue.id == -1) {
				controller.selectedMethodInputFields = [];
				controller.selectedMethodOutputFields = [];
				return;
			}
			cashierRest.findMethodFields(controller.selectedMethod.method.code, controller.selectedTransactionType.name.toLowerCase(), true).then(function(fields) {
				controller.selectedMethodInputFields = fields.plain();
				console.log(controller.selectedMethodInputFields);
				var inputFields = [];
				for (var i = 0; i < controller.selectedMethodInputFields.length; i++) {
					var field = controller.selectedMethodInputFields[i];
					if (field.code != null && field.code != "" && field.code != "csid") {
						inputFields.push(
							{
								type: "input",
								key: field.code,
								templateOptions: {
									type: "",
									label: field.name,
									required: false,
									placeholder: "",
									description: ""
								}
							}
						);
					}
				}
				controller.inputFields = [];
				controller.inputFields = inputFields;
			});
			cashierRest.findMethodFields(controller.selectedMethod.method.code, "deposit", false).then(function(fields) {
				controller.selectedMethodOutputFields = fields.plain();
				var outputFields = [];
				for (var i = 0; i < controller.selectedMethodOutputFields.length; i++) {
					var field = controller.selectedMethodOutputFields[i];
					if (field.code != null && field.code != "") {
						outputFields.push(
							{
								type: "input",
								key: field.code,
								templateOptions: {
									type: "",
									label: field.name,
									required: false,
									placeholder: "",
									description: ""
								}
							}
						);
					}
				}
				controller.outputFields = [];
				controller.outputFields = outputFields;
			});
		}
	});
	
	controller.processorFields = [
		{
			type: "input",
			key: "processorReference",
			templateOptions: {
				type: "",
				label: "",
				required: true,
				placeholder: "",
				description: "",
				options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL.DEPOSIT.FIELDS.PROCESSORREFERENCE.NAME" | translate'
			}
		}
	]
	
	controller.transactionFields = [
		{
			key: "amount",
			type: "ui-money-mask",
			optionsTypes: ["editable"],
			templateOptions : {
				label: "",
				description: "",
				required: true,
				addFormControlClass: true,
				min: "0.01"
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL.DEPOSIT.FIELDS.AMOUNT.NAME" | translate'
			}
		}
	]
	
	controller.findValue = function(field) {
		var fieldValue = null;
		angular.forEach(controller.model, function(value, key) {
			if (key === field) {
				fieldValue = value;
			}
		});
		return fieldValue;
	}
	
	controller.save = function() {
		controller.bonusError = '';
		
		if (controller.form.$invalid) {
			if (controller.selectedUser === undefined || controller.selectedUser === null) controller.noPlayerChosen = true;
			if (controller.selectedMethod === undefined || controller.selectedMethod === null || controller.selectedMethod.id === -1) controller.noMethodChosen = true;
			if (controller.selectedProcessor === undefined || controller.selectedProcessor === null || controller.selectedProcessor.id === -1) controller.noProcessorChosen = true;
			
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		
		if (controller.selectedUser === undefined || controller.selectedUser === null) {
			controller.noPlayerChosen = true;
			if (!controller.form.$invalid) {
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
		}
		
		if (controller.selectedMethod === undefined || controller.selectedMethod === null || controller.selectedMethod.id === -1) {
			controller.noMethodChosen = true;
			if (!controller.form.$invalid) {
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
		}
		
		if (controller.selectedProcessor === undefined || controller.selectedProcessor === null || controller.selectedProcessor.id === -1) {
			controller.noProcessorChosen = true;
			if (!controller.form.$invalid) {
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
		}
		
		var manualTransaction = {};
		manualTransaction.domainMethodProcessor = controller.selectedProcessor;
		manualTransaction.processorReference = controller.model.processorReference;
		manualTransaction.user = controller.selectedUser;
		if (controller.selectedTransactionType.id === 1 && controller.selectedBonus != undefined && controller.selectedBonus != null && controller.selectedBonus.bonusId !== -1) {
			manualTransaction.bonusId = controller.selectedBonus.bonusId;
		}
		manualTransaction.amount = controller.model.amount;
		
		var fields = [];
		for (var k = 0; k < controller.selectedMethodInputFields.length; k++) {
			fields.push({
				key: controller.selectedMethodInputFields[k].code,
				stage: controller.selectedMethodInputFields[k].stage.number,
				input: controller.selectedMethodInputFields[k].input,
				value: controller.findValue(controller.selectedMethodInputFields[k].code)
			});
		}
		for (var i = 0; i < controller.selectedMethodOutputFields.length; i++) {
			fields.push({
				key: controller.selectedMethodOutputFields[i].code,
				stage: controller.selectedMethodOutputFields[i].stage.number,
				input: controller.selectedMethodOutputFields[i].input,
				value: controller.findValue(controller.selectedMethodOutputFields[i].code)
			});
		}
		
		manualTransaction.fields = fields;
		
		manualTransaction.transactionType = controller.selectedTransactionType.name.toUpperCase();
		
		cashierRest.addManualTransaction(manualTransaction).then(function(response) {
			if (typeof(response) === 'number') {
				notify.success("The transaction was added successfully.");
				$state.go("dashboard.cashier.transaction", {
					tranId: response,
					domainName: $stateParams.domainName
				});
			}
			if (!response._successful) {
				if (response._status === 504) {
					controller.bonusError = response._data2;
					notify.error("There was a problem creating the transaction. Please review the errors and try again.");
				}
				if (response._status === 500) {
					notify.error(response._data2);
				}
			}
		}).catch(function(error) {
			errors.catch("", false)(error)
		});
	}
}]);