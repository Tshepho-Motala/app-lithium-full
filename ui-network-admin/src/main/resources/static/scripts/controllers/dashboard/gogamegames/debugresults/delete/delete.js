'use strict'

angular.module('lithium').controller('GoGameDebugResultsDeleteController', ['$translate', '$userService', '$filter', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest',
	function($translate, $userService, $filter, $state, $scope, errors, notify, $q, gogameGamesRest) {
		var controller = this;
		
		controller.model = {};
		
		controller.fields = [
			 {
				key: 'engineId',
				type: 'ui-select-single',
				templateOptions: {
					label: 'Engine',
					description: 'Choose the engine that you\'re deleting the debug results for',
					required: true,
					//optionsAttr: 'bs-options',
					valueProp: 'id',
					labelProp: 'id',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder: '',
					options: []
				},
				controller: ['$scope', function($scope) {
					gogameGamesRest.findAllEngines().then(function(response) {
						$scope.to.options = response.plain();
					});
				}]
			},
		];
		
		controller.onDelete = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			gogameGamesRest.removeDebugResultByEngineId(controller.model).then(function(response) {
				console.log(response);
				if (response === "Successful") {
					notify.success('UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.DELETE.SUCCESS');
					$state.go("dashboard.gogamegames.debugresults");
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.DELETE.ERROR");
				errors.catch('', false)(error)
			});
		}
	}
]);