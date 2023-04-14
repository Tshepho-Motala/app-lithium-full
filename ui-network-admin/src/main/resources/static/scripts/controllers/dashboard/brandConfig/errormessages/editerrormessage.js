'use strict';

angular.module('lithium')
.controller('EditErrorMessageModal',
['$uibModalInstance', '$translate', 'domainName', 'translationData', 'rest-translate', '$userService', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'domainSpecific', 'toggleErrorMessage',
function ($uibModalInstance, $translate, domainName, translationData, translateRest, $userService, $scope, notify, errors, bsLoadingOverlayService, domainSpecific, toggleErrorMessage) {
	var controller = this;
	
	controller.options = {};
	controller.auth = $userService.domainsWithAnyRole(["ADMIN", "ERROR_MESSAGES_EDIT"])
	controller.model = {
		keyId: translationData.id,
		defaultValue: '',
		langValues: [],
		languages: [],
		newTranslation: {
			locale2: $translate.instant('UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.EDIT.SELECT_LANGUAGE'),
			value: ''
		},
		toggleErrorMessage: toggleErrorMessage
	};

	controller.fields = [
		{
			type : "input",
			key : "comment",
			templateOptions : {
				label : controller.model.toggleErrorMessage.label,
				placeholder : controller.model.toggleErrorMessage.placeholder,
				required : true
			}
		}
	];
	
	controller.init = function() {
		//Get default translation value and all languages translated for the selected domain
		translateRest.getTranslationsById(translationData.id, domainName).then(function(response) {
			let translations = response;
			let t = controller.model.langValues.length;
			let obj = [];
			controller.translatedLanguages = [];
			for (let i = 0; i < translations.length; i++) {
				if (translations[i].domainName != 'default') {
					obj[t] = {
						keyId: translations[i].keyId,
						language: translations[i].language,
						valueId: translations[i].valueId,
						value: translations[i].value,
						edit: false,
						canDelete: !(domainSpecific === true && translations[i].language === 'en')
					}
					t++;
					controller.translatedLanguages.push(translations[i].language);
				} else {
					controller.model.keyId = translations[i].keyId;
					controller.model.defaultValue = translations[i].value;
				}
			}
			controller.model.langValues = obj;

			//Gets all the languages used to created or assign a new translation to for the selected domain
			translateRest.getAllLanguages().then(function(response) {
				let obj = [];
				for (let i = 0; i < response.length; i++) {
					if (!controller.translatedLanguages.includes(response[i].locale2))
						obj.push({
							name: response[i].description,
							locale2: response[i].locale2
						});
				}
				controller.model.languages = obj;
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		}).catch(function(error) {
			errors.catch("", false)(error)
		});
	}
	
	controller.init();
	
	controller.referenceId = 'editerrormessage-overlay';

	controller.changed = false;
	controller.add = function() {
		if (controller.model.newTranslation.locale2 != 'Select a language' && controller.model.newTranslation.value != '') {
			bsLoadingOverlayService.start({referenceId:controller.referenceId});
			//Add a new translation for this key on the selected language and selected domain
			translateRest.addTranslation(controller.model.keyId, domainName, controller.model.newTranslation.locale2, controller.model.newTranslation.value).then(function(response) {
				console.log(response);
				controller.model.langValues[controller.model.langValues.length] = {
					keyId: response.keyId,
					language: response.language,
					valueId: response.valueId,
					value: response.value,
					edit: false,
					editValueClass: 'col-xs-8',
					canDelete: !(domainSpecific === true && response.language === 'en')
				}
				controller.model.newTranslation = {
					locale2: $translate.instant('UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.EDIT.SELECT_LANGUAGE'),
					value: ''
				}
				notify.success("UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.EDIT.SUCCESS_STORED");
				let obj = []
				let t = 0;
				for (let i = 1; i < controller.model.languages.length ; i++) {
					if (controller.model.languages[i].locale2 != response.language) {
						obj[t] = controller.model.languages[i];
						t++;
					}
				}
				controller.model.languages = obj;
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		}
		controller.changed = true;
	};

	controller.editMode = function(data) {
		if (controller.auth.length > 0)
			data.edit = true;
	}

	controller.edit = function(data) {
		translateRest.editTranslation(domainName, data.valueId, data.value).then(function(response) {
			notify.success("UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.EDIT.SUCCESS_EDITED");
		}).catch(function(error) {
			errors.catch("", false)(error)
		});;
		data.edit = false;
		controller.changed = true;
	}

	controller.delete = function(data) {
		translateRest.deleteTranslation(domainName, data.valueId).then(function(response) {
			let obj = [];
			for (let i = 0; i < controller.model.langValues.length; i++) {
				if (data.valueId != controller.model.langValues[i].valueId) {
					obj.push(controller.model.langValues[i])
				}
			}
			controller.model.langValues = obj;
			if(controller.model.langValues.length === 0) {
				controller.closeModal('cancel');
			}
			notify.success("UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.EDIT.SUCCESS_REMOVED");
		}).catch(function(error) {
			errors.catch("", false)(error)
		});
		controller.changed = true;
	}

	controller.closeModal = function(action) {
		if (controller.model.toggleErrorMessage.enabled) {
			controller.model.toggleErrorMessage.action = action
			controller.model.toggleErrorMessage.comment = controller.model.comment;
			if ((!controller.model.comment || controller.model.comment == '') && action !== 'cancel') {
				controller.fields[0].validation.errorExistsAndShouldBeVisible = true;
				controller.fields[0].validation.show = true;
			} else {
				controller.fields[0].validation.errorExistsAndShouldBeVisible = false;
				controller.fields[0].validation.show = false;
				$uibModalInstance.close(controller.model.toggleErrorMessage);
			}
		} else {
			$uibModalInstance.close(controller.model.toggleErrorMessage);
		}
	}
}]);
