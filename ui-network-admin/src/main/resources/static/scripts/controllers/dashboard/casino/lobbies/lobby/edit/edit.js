'use strict'

angular.module('lithium').controller('CasinoLobbyEditController', ['lobby', '$state', '$scope', 'errors', 'notify', 'CasinoCMSRest',
	function(lobby, $state, $scope, errors, notify, rest) {
		var controller = this;
		controller.lobby = lobby;
		$scope.setDescription('UI_NETWORK_ADMIN.CASINO.LOBBIES.EDIT.TITLE');

		controller.model = lobby.edit;

		controller.fields = [
			{
				key: 'description',
				type: 'textarea',
				templateOptions: {
					required: true,
					cols: 5,
					rows: 5,
					maxlength: 2000
				},
				optionsTypes: ['editable'],
				modelOptions: {
					updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.CASINO.LOBBIES.ADD.FIELDS.DESCRIPTION.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CASINO.LOBBIES.ADD.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
				}
			}, {
				key: 'json',
				type: 'json-editor',
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.CASINO.LOBBIES.ADD.FIELDS.JSON.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.CASINO.LOBBIES.ADD.FIELDS.JSON.DESCRIPTION" | translate'
				},
				templateOptions: {
					required: true,
					height: '400px'
				}
			}
		]

		controller.onContinue = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}

			try {
				JSON.parse(controller.model.json);
			} catch (e) {
				notify.warning('UI_NETWORK_ADMIN.CASINO.LOBBIES.INVALID_JSON');
				return false;
			}

			rest.modifyLobbyPost(lobby.domain.name, lobby.id, controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success('UI_NETWORK_ADMIN.CASINO.LOBBIES.EDIT.SAVE_SUCCESS');
					$state.go('dashboard.casino.lobbies.lobby.view', { domainName:response.domain.name, lobbyId:response.id, lobbyRevisionId:response.current.id });
				} else {
					notify.warning(response._message);
				}

			}).catch(function(error) {
				notify.error('UI_NETWORK_ADMIN.CASINO.LOBBIES.EDIT.SAVE_FAIL');
				errors.catch('', false)(error)
			});
		}
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning('GLOBAL.RESPONSE.FORM_ERRORS');
				return false;
			}

			try {
				JSON.parse(controller.model.json);
			} catch (e) {
				notify.warning('UI_NETWORK_ADMIN.CASINO.LOBBIES.INVALID_JSON');
				return false;
			}

			rest.modifyAndSaveCurrentLobby(lobby.domain.name, lobby.id, controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success('UI_NETWORK_ADMIN.CASINO.LOBBIES.EDIT.MODIFY_SUCCESS');
					$state.go('dashboard.casino.lobbies.lobby.view', { domainName:response.domain.name, lobbyId:response.id, lobbyRevisionId:response.current.id });
				} else {
					notify.warning(response._message);
				}

			}).catch(function(error) {
				notify.error('UI_NETWORK_ADMIN.CASINO.LOBBIES.EDIT.MODIFY_FAIL');
				errors.catch('', false)(error)
			});
		}
	}
]);