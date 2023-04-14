'use strict';

angular.module('lithium')
	.controller('PlayersCategoryViewDetails', ["UserRest", "$translate", "$uibModal", "notify", "category", "$dt", "$state", "$rootScope", "DTOptionsBuilder",
	function(userRest, $translate, $uibModal, notify, category, $dt, $state, $rootScope, DTOptionsBuilder) {
		var controller = this;
		controller.model = category;
		controller.model.domainName = category.domain.name;
		controller.options = {};
		
		controller.fields = [{
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
				'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYERS.CATEGORIES.FIELDS.NAME.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYERS.CATEGORIES.FIELDS.NAME.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYERS.CATEGORIES.FIELDS.NAME.DESC" | translate'
			}
		},{
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
				'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYERS.CATEGORIES.FIELDS.DESCRIPTION.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYERS.CATEGORIES.FIELDS.DESCRIPTION.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYERS.CATEGORIES.FIELDS.DESCRIPTION.DESC" | translate'
			}
		},{
			"className":"col-xs-12 form-group",
			"type":"input",
			"key":"domain.name",
			"templateOptions":{
				"type":"",
				"label":"",
				"required":true,
				"disabled": true,
				"placeholder":"",
				"description":"",
				"options":[]
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYERS.CATEGORIES.FIELDS.DOMAINNAME.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYERS.CATEGORIES.FIELDS.DOMAINNAME.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYERS.CATEGORIES.FIELDS.DOMAINNAME.DESC" | translate'
			},
			controller: ['$scope', function($scope) {
				$scope.options.templateOptions.options = controller.availableDomains;
			}]
		}];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			userRest.categoryAddUpdate(controller.model).then(function(category) {
				notify.success("UI_NETWORK_ADMIN.PUSHMSG.PROVIDERS.ADD.SUCCESS");
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.PUSHMSG.PROVIDERS.ADD.ERROR");
				errors.catch("", false)(error)
			});
		}
	}
]);