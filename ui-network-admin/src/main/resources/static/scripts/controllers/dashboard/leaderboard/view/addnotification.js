'use strict';

angular.module('lithium')
	.controller('AddNotification', ["$uibModalInstance", "$userService", "notify", "errors", "LeaderboardRest", "NotificationRest", "$translate", "rest-casino", "leaderboard",
	function($uibModalInstance, $userService, notify, errors, leaderboardRest, notificationRest, $translate, restCasino, leaderboard) {
		var controller = this;
		
		controller.model = {};
		controller.model.leaderboard = leaderboard;
		controller.options = {};
		
		console.log(leaderboard);
		
		controller.types = [];
		
		var triggerTranslations = [
			'UI_NETWORK_ADMIN.LEADERBOARD.TYPE.1',
			'UI_NETWORK_ADMIN.LEADERBOARD.TYPE.2',
			'UI_NETWORK_ADMIN.LEADERBOARD.TYPE.3'
		];
		
		$translate(triggerTranslations).then(function(translations) {
			angular.forEach(translations, function(v,k) {
				this.push({id:k.slice(-1), name:v});
			}, controller.types);
		});
		
		controller.fields = [{
			key: "rank",
			className: "col-xs-12",
			type: "ui-number-mask",
			optionsTypes: ['editable'],
			templateOptions : {
				label: "",
				required: false,
				decimals: 0,
				hidesep: true,
				neg: false
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NOTIFICATION.RANK.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NOTIFICATION.RANK.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NOTIFICATION.RANK.DESC" | translate'
			}
		},{
			key: "notification", 
			className: "col-xs-12",
			type: "ui-select-single",
			optionsTypes: ['editable'],
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				optionsAttr: 'bs-options',
				valueProp: 'name',
				labelProp: 'name',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NOTIFICATION.NOTIFICATION.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NOTIFICATION.NOTIFICATION.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NOTIFICATION.NOTIFICATION.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				if (controller.model.leaderboard.domain.name) {
					notificationRest.findByDomainName(controller.model.leaderboard.domain.name).then(function(notifications) {
						console.log(notifications.plain());
						$scope.to.options = notifications.plain();
					}).catch(function(error) {
						notify.error("UI_NETWORK_ADMIN.LEADERBOARD.NOTIFICATIONS.LIST.ERROR");
						errors.catch("", false)(error)
					}).finally(function() {
					});
				}
			}]
		},{
			className: "col-xs-12",
			template: "<div><p class='subtitle fancy'><span>or</span></p></div>"
		},{
			key: "bonusCode", 
			className: "col-xs-12",
			type: "ui-select-single",
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				optionsAttr: 'bs-options',
				valueProp: 'bonusCode',
				labelProp: 'bonusName',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NOTIFICATION.BONUSCODE.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NOTIFICATION.BONUSCODE.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.LEADERBOARD.FIELDS.NOTIFICATION.BONUSCODE.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				restCasino.findPublicBonusListV2(leaderboard.domain.name, 2, 8).then(function(response) {
					$scope.to.options = response;
				});
			}]
		}];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			console.log(controller.model);
			leaderboardRest.addNotification(controller.model.leaderboard.id, controller.model.bonusCode, controller.model.notification, controller.model.rank).then(function(c) {
				notify.success("UI_NETWORK_ADMIN.LEADERBOARD.CONVERSION.ADD.SUCCESS");
				$uibModalInstance.close(c);
			}).catch(function(error) {
				console.error(error);
				notify.error("UI_NETWORK_ADMIN.LEADERBOARD.CONVERSION.ADD.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);