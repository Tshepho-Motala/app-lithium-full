'use strict'

angular.module('lithium').controller('GoGameReelSetAddController', ['$translate', '$uibModal', '$userService', '$filter', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest',
	function($translate, $uibModal, $userService, $filter, $state, $scope, errors, notify, $q, gogameGamesRest) {
		var controller = this;
		
		controller.model = { config: { id: null } };
		controller.reels = null;
		
		controller.fields = [
			{
				key: "name",
				type: "input",
				templateOptions: {
					label: "Name",
					description: "The name of the reel set",
					placeholder: "",
					required: true
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.NAME" | translate',
//					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.PLACEHOLDER" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.DESCRIPTION" | translate'
//				}
			}, {
				key: "description",
				type: "textarea",
				templateOptions: {
					label: 'Description',
					description: "Provide a short description of the reel set",
					placeholder: ""
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.NAME" | translate',
//					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.PLACEHOLDER" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
//				}
			}, {
				key: 'config.id',
				type: 'ui-select-single',
				templateOptions: {
					label: 'Reels Generation Configuration',
					required: true,
					optionsAttr: 'bs-options',
					description: "The reel generation configuration to use",
					valueProp: 'id',
					labelProp: 'name',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder: '',
					options: []
				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.DESCRIPTION" | translate'
//				},
				controller: ['$scope', function($scope) {
					gogameGamesRest.findreelsgenconfigs().then(function(response) {
						$scope.to.options = response.plain();
					});
				}]
			}
		]
		
		$scope.$watch(function() { return controller.model.config.id }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				if (newValue !== undefined && newValue !== null) {
					console.log("Config changed.")
					controller.reels = null;
				}
			}
		});
		
		controller.setupReels = function() {
			controller.reelPositions = [];
			controller.maxReelLen = 0;
			
			for (var i = 0; i < 5; i++) {
				if (controller.reels[0][i].symbols.length > controller.maxReelLen)
					controller.maxReelLen = controller.reels[0][i].symbols.length;
			}
			
			for (var i = 0; i < controller.maxReelLen; i++) {
				controller.reelPositions.push(i + 1);
			}
		}
		
		controller.generateReels = function() {
			if (controller.model.config === undefined || controller.model.config === null ||
					controller.model.config.id === undefined || controller.model.config.id === null || controller.model.config.id === '') {
				notify.warning('Invalid configuration. Cannot generate reels');
				return;
			}
			
			gogameGamesRest.generateReels(controller.model.config.id).then(function(response) {
				console.log(response);
				if (response._status === 0) {
					controller.reels = response.plain();
					controller.setupReels();
					notify.success('Successfully generated a reel set');
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error('Could not generate reels');
				errors.catch('', false)(error)
			});
		}
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			if (controller.reels === undefined || controller.reels === null) {
				notify.warning("No reels to save");
				return false;
			}
			
			var reelSet = {
				name: controller.model.name,
				description: controller.model.description,
				configId: controller.model.config.id,
				json: JSON.stringify(controller.reels)
			};
			
			gogameGamesRest.addReelSet(reelSet).then(function(response) {
				console.log(response);
				if (response._status === 0) {
					notify.success('Successfully saved reel set');
					$state.go("dashboard.gogamegames.reelsets.view", { id:response.id });
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("Could not save reel set");
				errors.catch('', false)(error)
			});
		}
	}
]);