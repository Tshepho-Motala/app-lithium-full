'use strict';

angular.module('lithium')
.controller('ConfirmEnableChangeFlagModal',
['$uibModalInstance', '$translate', 'entityId', 'flagType', 'flagValue', 'rest-progressive-feeds', 'notify', 'errors', 'bsLoadingOverlayService',
function ($uibModalInstance, $translate, entityId, flagType, flagValue, restRegisteredProgressiveFeed, notify, errors, bsLoadingOverlayService) {
	var controller = this;
	bsLoadingOverlayService.start({referenceId:controller.referenceId});
	controller.referenceId = 'addcomment-overlay';
	controller.changeFlagValueTo = flagValue === false ? true : false;
	controller.flagType = flagType;
	controller.flagValue = flagValue;
	controller.progressiveJackpotFeedRegistration = {};
	var registeredProgressiveFeedId = entityId + '';

	controller.getRegisteredProgressiveFeed = function(registeredProgressiveFeedId) {
		restRegisteredProgressiveFeed.findByProgressiveFeedRegistrationId(registeredProgressiveFeedId).then(function(progressiveJackpotFeedRegistration) {
			return progressiveJackpotFeedRegistration.plain();
		});
	}

	controller.submit = function() {
		restRegisteredProgressiveFeed.findByProgressiveFeedRegistrationId(registeredProgressiveFeedId).then(progressiveJackpotFeedRegistration =>  {
			controller.progressiveJackpotFeedRegistration = progressiveJackpotFeedRegistration.plain();
			$uibModalInstance.close(progressiveJackpotFeedRegistration);
		}).catch(function(error) {
			errors.catch($translate('UI_NETWORK_ADMIN.CASINO.REGISTERED_PROGRESSIVE_FEEDS.EDIT.ERROR'), false)(error)
		});
	};

	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);
