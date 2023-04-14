'use strict'

angular.module('lithium').controller('GrantBonusModal', ['$uibModalInstance', '$translate', 'rest-casino', 'user', 'bsLoadingOverlayService', 'errors', 'notify', 'symbol', 'bonusCodes', 'rest-domain',
	function($uibModalInstance, $translate, casinoRest, user, bsLoadingOverlayService, errors, notify, symbol, bonusCodes, domainRest) {
		var controller = this;
		controller.maxPayout = 125000;
		controller.invalidAmountMessage = "";
		
		controller.model = {
			bonusType: "trigger",
			bonusCode: null,
			amount: 0,
			description : null,
			noteText: null,
		}

		domainRest.findCurrentDomainSetting(user.domain.name, "maximum_bonus_payout")
			.then(function (response) {
				if (response !== undefined && response !== null && response._status === 0) {
					if (response.labelValue !== undefined && response.labelValue !== null && response.labelValue.value != null && response.labelValue.value.trim() !== "") {
						controller.maxPayout = response.labelValue.value;
						controller.invalidAmountMessage = $translate.instant('UI_NETWORK_ADMIN.PLAYER.BONUS.GRANT_BONUS.FIELDS.AMOUNT.INVALID_AGAINST_DOMAIN') + response.labelValue.value;
					} else {
						controller.invalidAmountMessage = $translate.instant('UI_NETWORK_ADMIN.BONUSES.GRANT_MASS.CASH_BONUSES.FIELDS.AMOUNT.INVALID') + controller.maxPayout;
					}
				}
			});
		
		controller.fields = [
			{
				className: 'col-xs-12',
				key: 'bonusType',
				type: 'select',
				defaultValue: 'trigger',
				templateOptions: {
					label: 'Bonus Type',
					description: 'Choose the bonus type',
					required: true,
					options: [
						{
							name: 'Cash Bonus',
							value: 'trigger'
						}
					]
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.BONUS.GRANT_BONUS.FIELDS.BONUS_TYPE.LABEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.BONUS.GRANT_BONUS.FIELDS.BONUS_TYPE.DESC" | translate'
				}
			},
			{
				className: 'col-xs-12',
				key: 'bonusCode',
				type: 'select',
				templateOptions: {
					label: 'Bonus Code',
					description: 'Choose the bonus code',
					required: true,
					options: bonusCodes
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.BONUS.GRANT_BONUS.FIELDS.BONUS_CODE.LABEL" | translate | decodeURIComponent',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.BONUS.GRANT_BONUS.FIELDS.BONUS_CODE.DESC" | translate'
				}
			},
			{
				className: 'col-xs-12',
				key: 'amount',
				type: 'ui-money-mask',
				templateOptions: {
					label: 'Amount',
					description: 'Enter the amount',
					required: true,
					symbol: symbol
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.BONUS.GRANT_BONUS.FIELDS.AMOUNT.LABEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.BONUS.GRANT_BONUS.FIELDS.AMOUNT.DESC" | translate'
				},
				validators: {
					pattern: {
						expression: function($viewValue, $modelValue, scope) {
							return $modelValue <= 0 ? false : $modelValue <= controller.maxPayout;
						},
						message: function () {
							return controller.invalidAmountMessage;
						}
					}
				}
			},
			{
				className: 'col-xs-12',
				key: 'description',
				type: 'input',
				templateOptions: {
					label: 'Description',
					description: 'Enter the description',
					required: false
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.BONUS.GRANT_BONUS.FIELDS.DESC.LABEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.BONUS.GRANT_BONUS.FIELDS.DESC.DESC" | translate'
				}
			},
			{
				className: 'col-xs-12',
				key: 'noteText',
				type: 'textarea',
				templateOptions: {
					label: 'Note Text',
					description: 'Enter the note text',
					required: false
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.BONUS.GRANT_BONUS.FIELDS.NOTE_TEXT.LABEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.BONUS.GRANT_BONUS.FIELDS.NOTE_TEXT.DESC" | translate'
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
			if (controller.model.bonusType === 'trigger') {
				bsLoadingOverlayService.start({referenceId:controller.referenceId});
				casinoRest.registerTriggerBonusv3(user.domain.name, 'cash-bonus', controller.model.bonusCode, user.guid, controller.model.amount, null, controller.model.description, controller.addAutoText(controller.model.noteText)).then(function(response) {
					//console.log(response);
					if (angular.isDefined(response)) {
						if (response._status != 0) {
							notify.error('UI_NETWORK_ADMIN.PLAYER.BONUS.GRANT_BONUS.ERROR');
							notify.error(response._message);
						} else {
							notify.success('UI_NETWORK_ADMIN.PLAYER.BONUS.GRANT_BONUS.SUCCESS');
							$uibModalInstance.close(response);
						}
					}
				}).catch(function(error) {
					console.log(error);
					errors.catch('UI_NETWORK_ADMIN.PLAYER.BONUS.GRANT_BONUS.ERROR', false);
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
				}).finally(function() {
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
				});
			}
		}

		controller.addAutoText = function (noteText) {
			return noteText;
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);
