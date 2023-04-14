'use strict';

angular.module('lithium')
	.controller('PlayersTagViewDetails', ["UserRest", "$translate", "$uibModal", "notify", "tag", "$dt", "$state", "$rootScope", "DTOptionsBuilder", '$http',
	function(userRest, $translate, $uibModal, notify, tag, $dt, $state, $rootScope, DTOptionsBuilder, $http) {
		let controller = this;
		controller.model = {};
		controller.options = {};

		$http.get("services/service-user/backoffice/players/tag/view/" + tag.id).then(function (response) {
			controller.model = response.data.data;
			controller.model.domainName = response.data.data.domain.name;
		});
		
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
				'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DESCRIPTION.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DESCRIPTION.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DESCRIPTION.DESC" | translate'
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
				'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DOMAINNAME.TITLE" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DOMAINNAME.PLACE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYERS.TAGS.FIELDS.DOMAINNAME.DESC" | translate'
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
			userRest.tagAddUpdate(controller.model).then(function(tag) {
				notify.success("UI_NETWORK_ADMIN.PUSHMSG.PROVIDERS.ADD.SUCCESS");
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.PUSHMSG.PROVIDERS.ADD.ERROR");
				errors.catch("", false)(error)
			});
		}
	}
]);