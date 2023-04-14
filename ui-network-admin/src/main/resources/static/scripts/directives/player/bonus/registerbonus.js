'use strict'

angular.module('lithium').controller('RegisterBonusModal', ['$uibModalInstance', 'rest-casino', 'userEventRest', 'user', 'bsLoadingOverlayService', 'errors', 'notify',
	function($uibModalInstance, casinoRest, userEventRest, user, bsLoadingOverlayService, errors, notify) {
		var controller = this;
		
		controller.model = {
			type: '',
			code: '',
			depositAmount: 0,
			customFreeMoneyAmount : null,
			customBonusTokenAmount : null,
			description : null,
			csvfile : null
		}
		
		controller.fields = [
			{
				className: 'col-xs-12',
				key: 'type',
				type: 'select',
				defaultValue: 'Signup',
				templateOptions: {
					label: 'Bonus Type',
					description: 'Choose the bonus type',
					required: true,
					options: [
						{
							name: 'Signup',
							value: 'signup'
						},
						{
							name: 'Deposit',
							value: 'deposit'
						},
						{
							name: 'Trigger',
							value: 'trigger'
						},
						{
							name: 'Virtual Coin',
							value: 'bonustoken'
						}
					]
				}
			},
			{
				className: 'col-xs-12',
				key: 'code',
				type: 'input',
				templateOptions: {
					label: '',
					description: ''
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.FIELDS.CODE.LABEL" | translate',
					'templateOptions.placeholder': '"" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.FIELDS.CODE.DESC" | translate'
				}
			},
			{
				className: 'col-xs-12',
				key: 'depositAmount',
				type: 'ui-money-mask',
				templateOptions: {
					label: '',
					description: '',
					required: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.FIELDS.DEPOSITAMOUNT.LABEL" | translate',
					'templateOptions.placeholder': '"" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.FIELDS.DEPOSITAMOUNT.DESC" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					return scope.model.type != 'deposit';
				}
			},
			{
				className: 'col-xs-12',
				key: 'customFreeMoneyAmount',
				type: 'ui-money-mask',
				templateOptions: {
					label: '',
					description: '',
					required: false
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.CUSTOM_FREE_MONEY.AMOUNT" | translate',
					'templateOptions.placeholder': '"" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.CUSTOM_FREE_MONEY.AMOUNT" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					return scope.model.type !== 'trigger';
				}
			},
			{
				className: 'col-xs-12',
				key: 'customBonusTokenAmount',
				type: 'ui-money-mask',
				templateOptions: {
					label: '',
					description: '',
					required: false
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.CUSTOM_BONUS_TOKEN.AMOUNT" | translate',
					'templateOptions.placeholder': '"" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.CUSTOM_BONUS_TOKEN.AMOUNT" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					return scope.model.type !== 'bonustoken';
				}
			},
			{
				className: 'col-xs-12',
				key: 'description',
				type: 'input',
				templateOptions: {
					label: '',
					description: '',
					required: false
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.FIELDS.DESC.LABEL" | translate',
					'templateOptions.placeholder': '"" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.FIELDS.DESC.DESC" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					return scope.model.type !== 'trigger';
				}
			}
		];
		
		controller.referenceId = 'register-bonus-overlay';
		
		controller.submit = function() {
			bsLoadingOverlayService.start({referenceId:controller.referenceId});
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				bsLoadingOverlayService.stop({referenceId:controller.referenceId});
				return false;
			}
			if (controller.model.type === 'signup') {
				bsLoadingOverlayService.start({referenceId:controller.referenceId});
				casinoRest.registerSignupBonus(controller.model.code, user.guid, false).then(function(response) {
					if (angular.isDefined(response)) {
						if (response._status != 0) {
							notify.error('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR');
							notify.error(response._message);
						} else {
							notify.success('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.SIGNUP.SUCCESS');
							$uibModalInstance.close(response);
						}
					}
				}).catch(
					errors.catch('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR', false)
				).finally(function() {
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
				});
			} else if (controller.model.type === 'deposit') {
				bsLoadingOverlayService.start({referenceId:controller.referenceId});
				var depositAmountInCents = (Math.round(controller.model.depositAmount * 100));
				userEventRest.registerUserEvent(user.domain.name, user.guid.split("/")[1], 'MANUAL_DEPOSIT_BONUS_DEP', "Manual deposit bonus allocation: deposit amount", depositAmountInCents).then(function(response) {
					controller.registerDepositBonus(response.id);
				}).catch(
					errors.catch('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR', false)
				).finally(function() {
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
				});
			} else if (controller.model.type === 'trigger') {
				bsLoadingOverlayService.start({referenceId:controller.referenceId});
				casinoRest.registerTriggerBonusv2(controller.model.code, user.guid, controller.model.customFreeMoneyAmount, null, controller.model.description).then(function(response) {
					//console.log(response);
					if (angular.isDefined(response)) {
						if (response._status != 0) {
							notify.error('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR');
							notify.error(response._message);
						} else {
							notify.success('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.SUCCESS');
							$uibModalInstance.close(response);
						}
					}
				}).catch(function() {
					errors.catch('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR', false);
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
				}).finally(function() {
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
				});
			} else if (controller.model.type === 'bonustoken') {
				bsLoadingOverlayService.start({referenceId:controller.referenceId});
				casinoRest.registerBonusTokenBonusv2(controller.model.code, user.guid, controller.model.customBonusTokenAmount).then(function(response) {
					console.log(response);
					if (angular.isDefined(response)) {
						if (response._status != 0) {
							notify.error('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR');
							notify.error(response._message);
						} else {
							notify.success('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.SUCCESS');
							$uibModalInstance.close(response);
						}
					}
				}).catch(function() {
					errors.catch('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR', false);
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
				}).finally(function() {
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
				});
			}
		}
		
		controller.registerDepositBonus = function(userEventId) {
			casinoRest.registerDepositBonus(controller.model.code, user.guid, userEventId, false).then(function(response) {
				if (angular.isDefined(response)) {
					if (response._status != 0) {
						notify.error('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR');
						notify.error(response._message);
					} else {
						$uibModalInstance.close(response);
						angular.forEach(response._data2, function(msg) {
							console.log("the message: ", msg);
							notify.success(msg);
						});
						notify.success('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.DEPOSIT.SUCCESS');
					}
				}
			}).catch(
				errors.catch('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR', false)
			).finally(function() {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);
