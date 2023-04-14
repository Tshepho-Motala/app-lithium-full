'use strict';

angular.module('lithium')
.controller('TagEdit', ["tag", "$uibModalInstance", "$userService", "notify", "errors", "UserRest", "$state", "$http", '$uibModal',
	function(tag, $uibModalInstance, $userService, notify, errors, userRest, $state, $http, $uibModal) {
		let controller = this;
		
		controller.availableDomains = $userService.playerDomainsWithAnyRole(["ADMIN", "PLAYER_*"]);
		
		controller.model = tag;
		controller.model.domainName = tag.domain.name;
		controller.options = {};
		controller.players = 0;

		$http.get("services/service-user/backoffice/players/tag/view/" + controller.model.id + "/players/count").then(function (response) {
			controller.players = response.data.data;
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
			"type":"ui-select-single",
			"key":"domainName",
			"templateOptions":{
				"label":"",
				"placeholder":"",
				"description":"",
				"disabled": true,
				"dataAllowClear": false,
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
		}];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			userRest.tagAddUpdate(controller.model).then(function(tag) {
				notify.success("UI_NETWORK_ADMIN.PLAYERS.TAGS.EDIT.SUCCESS");
				console.debug(tag);
				$uibModalInstance.close(tag);
			}).catch(function(error) {
			if (error.status === 400) {
				notify.error($translate.instant('UI_NETWORK_ADMIN.PLAYERS.TAGS.EDIT.ERROR.BAD_REQUEST'));
			} else if (error.status === 409) {
				notify.error($translate.instant('UI_NETWORK_ADMIN.PLAYERS.TAGS.EDIT.ERROR.CONFLICT'));
			}
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};

		controller.edit = function() {
			$uibModalInstance.dismiss('cancel');
			$state.go("dashboard.players.tag.view.players", {
				id: tag.id,
				domainName: tag.domainName,
				dwhVisible: tag.dwhVisible,
				name: tag.name,
				description: tag.description
			});
		}

		controller.deleteTag = function () {
			let modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/players/tags/confirmdelete.html',
				controller: 'ConfirmNoteDeleteModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					entityId: function () {
						return controller.model.id;
					},
					restService: function () {
						return userRest;
					},
					loadMyFiles: function ($ocLazyLoad) {
						return $ocLazyLoad.load({
							name: 'lithium',
							files: ['scripts/controllers/dashboard/players/tags/confirmdelete.js']
						})
					}
				}
			});

			modalInstance.result.then(function () {
				notify.success('UI_NETWORK_ADMIN.TAG.NOTIFY.RESPONSE.SUCCESS');
				$uibModalInstance.close();
			});
		};
	}
]);