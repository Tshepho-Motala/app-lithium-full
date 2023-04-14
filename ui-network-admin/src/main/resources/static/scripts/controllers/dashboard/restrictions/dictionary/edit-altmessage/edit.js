'use strict';

angular.module('lithium')
.controller('EditAltMessageModal',
['$uibModalInstance','RestrictionsRest', 'domainRestrictionSet','parentRestrictionKey', 'rest-translate', '$userService','notify', 'bsLoadingOverlayService',
function ($uibModalInstance, restrictionsRest,domainRestrictionSet,parentRestrictionKey, translateRest, $userService, notify, bsLoadingOverlayService) {
	var controller = this;
	
	controller.set = domainRestrictionSet
	controller.referenceId = 'edit-alt-errormessage-overlay';

	controller.altMessageKey =  () => `${parentRestrictionKey}.${controller.currentIteration}`

	controller.translatedMessage = null;

	controller.currentIteration = controller.set.altMessageCount;

	controller.resolveTranslationKey  = async () => {
		try {
			const translationId = await translateRest.getKeyIdByCode(controller.altMessageKey(), controller.set.domain.name)
			const response =  await translateRest.getTranslationsById(translationId,controller.set.domain.name)
			const translation = response.plain().find(t => t.domainName === controller.set.domain.name)

			if(translation) {
				controller.translatedMessage = translation.value
			}
		}
		catch(e) {
			console.error(e)
		}

	}

	controller.increment = async() => {
		await controller.submit(controller.set.id, 'INCREMENT')
	}

	controller.decrement = async () => {
		await controller.submit(controller.set.id, 'DECREMENT')
	}

	controller.submit = async (restrictionSetId, action) => {
		try {
			let response = await restrictionsRest.updateAltMessageCount(restrictionSetId, action)
			controller.set = response.plain()
			
			controller.currentIteration = controller.set.altMessageCount
			controller.resolveTranslationKey()
		}
		catch(e) {
			notify.error(`Failed to ${action.toLowerCase()} altMessageCount`)
			console.error(e)
		}
	}

	controller.next = () => {
		if(controller.canNavigateForward()) {
			controller.currentIteration++;
			controller.resolveTranslationKey()
		}
	}

	controller.prev = () => {
		if(controller.canNavigateBack()) {
			controller.currentIteration--;
			controller.resolveTranslationKey()
		}
	}

	controller.canNavigateBack = () => {
		return controller.currentIteration > 1
	}

	controller.canNavigateForward = () => {
		return controller.currentIteration < controller.set.altMessageCount;
	}
	

	controller.cancel = function() {
		$uibModalInstance.close(controller.set)
	}

	controller.resolveTranslationKey()
}]);
