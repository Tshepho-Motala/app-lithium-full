'use strict';

angular.module('lithium')
.controller('ChangePersonalModal',
['$uibModalInstance', 'user', "UserRest", "ProfileRest", 'userFields', 'notify', 'profile', 'errors', 'bsLoadingOverlayService', 'domainSettings', '$security',
function ($uibModalInstance, user, UserRest, ProfileRest, userFields, notify, profile, errors, bsLoadingOverlayService, domainSettings, $security) {
	let controller = this;
	controller.submitCalled = false;
	
	controller.options = {removeChromeAutoComplete:true};
	controller.model = user;
	controller.additionalData = angular.copy(user.additionalData);

	if (user.dobDay != null && user.dobMonth != null && user.dobYear != null) {
		user.dateOfBirth = new Date(user.dobYear, user.dobMonth - 1, user.dobDay);
	}

	controller.fields = [
		userFields.firstName,
		userFields.lastNamePrefixTypeHead(domainSettings),
		userFields.lastName,
		userFields.timezone,
		userFields.gender,
		userFields.countryTypeAhead('', 'countryCode', false, true),
		userFields.placeOfBirthInput('placeOfBirth', domainSettings),
		userFields.ibanTypeHead(domainSettings, $security.domainsWithRole("IBAN_EDIT").length > 0),
		userFields.email,
		userFields.telephoneNumber,
		userFields.cellphoneNumber
		];
	
	controller.referenceId = 'changepersonal-overlay';
	controller.submit = function() {
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			return false;
		}
		if (user.dateOfBirth != null) {
			user.dobDay = user.dateOfBirth.getDate();
			user.dobMonth = (user.dateOfBirth.getMonth() + 1);
			user.dobYear = user.dateOfBirth.getFullYear();
		}
		if (user.timezone == null) user.timezone = '-1';
		if (user.gender == null) user.gender = '-1';

		let playerBasic = angular.copy(user);
		playerBasic.domainName = playerBasic.domain.name;
		playerBasic.status = null; // On service side, the status.id somehow gets mapped to playerBasic.id - not interested to update status here
		playerBasic.additionalData = null; // Getting issues deserializing a LinkedHashMap, rather excluding it here since it is not updated on the userRest.save(
		if (!profile) {
			UserRest.save(user.domain.name, playerBasic).then(function(userResponse) {
				if (controller.additionalData && controller.additionalData['iban'] !== controller.model.additionalData['iban']) {
					UserRest.saveAdditionalDataByUserGuid(user.domain.name, user.id, {iban: controller.model.additionalData['iban']}).then(function (response) {
						userResponse.additionalData = user.additionalData;
						userResponse.additionalData['iban'] = controller.model.additionalData['iban'];
						$uibModalInstance.close(userResponse);
					}).catch(function (error) {
						errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.PERSONALSAVE", false)
					}).finally(function () {
						bsLoadingOverlayService.stop({referenceId: controller.referenceId});
					});
				} else {
					userResponse.additionalData = user.additionalData;
					$uibModalInstance.close(userResponse);
				}
			}).catch(function (error) {
				errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.PERSONALSAVE", false)
			}).finally(function () {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			});
		} else {
			ProfileRest.save(playerBasic).then(function(response) {
				$uibModalInstance.close(response);
			}).catch(
				errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.PERSONALSAVE", false)
			).finally(function () {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			});
		}
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);
