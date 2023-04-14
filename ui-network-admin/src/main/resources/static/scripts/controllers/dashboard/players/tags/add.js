'use strict';

angular.module('lithium')
	.controller('TagAdd', ["$uibModalInstance", "$userService", "notify", "errors", "UserRest",
	function($uibModalInstance, $userService, notify, errors, userRest) {
		var controller = this;
		
		controller.availableDomains = $userService.playerDomainsWithAnyRole(["ADMIN", "PLAYER_*"]);
		
		controller.model = {};
		controller.options = {};
		
		controller.fields = [
			{
				type: 'checkbox',
				key: 'dwhVisible',
				templateOptions: {
					label: '', description: ''
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DWHVISIBLE.TITLE" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DWHVISIBLE.DESC" | translate'
				}
			},
			{
			"className":"col-xs-12",
			"type":"input",
			"key":"name",
			"templateOptions":{
				"type":"",
				"label":"",
				"required":true,
				"placeholder":"",
				"description":"",
				"options":[]
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.NAME.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.NAME.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.NAME.DESC" | translate'
			}
		},
			{
			"className":"col-xs-12",
			"type":"input",
			"key":"description",
			"templateOptions":{
				"type":"",
				"label":"",
				"required":true,
				"placeholder":"",
				"description":"",
				"options":[]
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DESCRIPTION.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DESCRIPTION.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DESCRIPTION.DESC" | translate'
			}
		},
			{
			"className":"col-xs-12 form-group",
			"type":"ui-select-single",
			"key":"domainName",
			"templateOptions":{
				"label":"",
				"placeholder":"",
				"description":"",
				"required":true,
				"optionsAttr": "bs-options",
				"valueProp": "name",
				"labelProp": "name",
				"options":[]
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DOMAINNAME.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DOMAINNAME.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DOMAINNAME.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				$scope.options.templateOptions.options = controller.availableDomains;
			}]
		}
		];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			userRest.tagAddUpdate(controller.model).then(function(tag) {
				notify.success("UI_NETWORK_ADMIN.PLAYERS.TAGS.ADD.SUCCESS");
				$uibModalInstance.close(tag);
			}).catch(function(error) {
				console.error(error.data.message);
			if (error.status == 400) {
				notify.error($translate.instant('UI_NETWORK_ADMIN.PLAYERS.TAGS.ADD.ERROR.BAD_REQUEST'));
			} else if (error.status == 409) {
				notify.error($translate.instant('UI_NETWORK_ADMIN.PLAYERS.TAGS.ADD.ERROR.CONFLICT'));
			} else if (error.status == 550) {
				notify.error($translate.instant('UI_NETWORK_ADMIN.PLAYERS.TAGS.ADD.ERROR.DOMAIN'));
			}
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);