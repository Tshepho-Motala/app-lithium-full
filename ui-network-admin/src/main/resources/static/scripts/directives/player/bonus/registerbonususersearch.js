'use strict';

angular.module('lithium')
.provider('registerbonususersearch', function() {
	this.$get = function() {
		var publicMethods = {
			registerBonus:function() {
				console.log("provider called");
				return this;
			}
		}
		return publicMethods;
	};
})
.directive('registerbonususersearch', function(registerbonususersearch) {
	return {
		template: '',
//		templateUrl:'scripts/directives/player/bonus/registerbonususersearch.html',
		scope: {
			bonusrevision: "="
		},
		link: function(scope) {
			registerbonususersearch.registerBonus = scope.registerBonus;
		},
		restrict: 'AE',
		replace: true,
		controller: ['$q', '$uibModal', '$scope', 'rest-casino', 'userEventRest', 'UserRest', 'errors', 'bsLoadingOverlayService', 'notify', '$timeout',
		function($q, $uibModal, $scope, casinoRest, userEventRest, userRest, errors, bsLoadingOverlayService, notify, $timeout) {
			var me = this;
			var modalInstance;
			
			$scope.registerBonus = function() {
//				console.log("in directive");
				modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					backdrop: 'static',
					templateUrl:'scripts/directives/player/bonus/registerbonususersearchmodal.html',
					controller: 'RegisterBonusUserSearchModalController',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						bonusRevision: function() {
							return $scope.bonusrevision;
						}
					}
				});
				
				return modalInstance.result;
//				return modalInstance.result.then(function(response) {
//					if (response != null) console.log(response);
//					return response;
//				});
			};
		}]
	}
}).controller('RegisterBonusUserSearchModalController',
['$uibModalInstance', 'bonusRevision', 'rest-casino', 'bsLoadingOverlayService', 'errors', 'notify', 'userEventRest', 'UserRest', "file-upload","$locale", "rest-domain",
function ($uibModalInstance, bonusRevision, casinoRest, bsLoadingOverlayService, errors, notify, userEventRest, userRest, fileUpload, $locale, domainRest) {
	var controller = this;
	
	controller.bonusRevision = bonusRevision;
	controller.referenceId = bonusRevision.bonusCode+"_"+(Math.random()*100);
	controller.selectedUser = {};

	domainRest.findByName(controller.bonusRevision.domain.name).then(function(response) {
		controller.domainInfo = response.plain();
	});
	
	controller.cancel = function() {
		$uibModalInstance.dismiss();
	}
	controller.searchPlayer = function(searchValue) {
		return userRest.search(bonusRevision.domain.name, searchValue).then(function(response) {
			console.log("Player search response: ", response.plain());
			return response.plain();
		});
	}
	controller.registerDepositBonus = function(userEventId) {
		casinoRest.registerDepositBonusv2(bonusRevision.bonusCode, controller.selectedUser.guid, userEventId, controller.bonusPrerequisites, controller.bonusRevision.id).then(function(response) {
			if (angular.isDefined(response)) {
				if (response._status != 0) {
					notify.error('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR');
					notify.error(response._message);
				} else {
					$uibModalInstance.close(response);
					angular.forEach(response._data2, function(msg) {
						console.log("the message: ", msg);
						notify.success(msg);
					});
					notify.success('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.DEPOSIT.SUCCESS');
				}
			}
		}).catch(function() {
			errors.catch('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR', false);
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		}).finally(function() {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	}
	controller.registerTriggerBonus = function(customFreeMoneyAmount, csvfile) {
		if (csvfile != null) {
			csvFileUpload(csvfile, "services/service-casino/casino/bonus/manual/register/trigger/v2/csv");
		} else {
			casinoRest.registerTriggerBonusv2(bonusRevision.bonusCode, controller.selectedUser.guid, customFreeMoneyAmount, bonusRevision.id).then(function(response) {
				console.log(response);
				if (angular.isDefined(response)) {
					if (response._status != 0) {
						notify.error('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR');
						notify.error(response._message);
					} else {
						notify.success('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.SUCCESS');
						$uibModalInstance.close(response);
					}
				}
			}).catch(function() {
				errors.catch('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR', false);
				bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			}).finally(function() {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			});
		}
	}

	controller.registerBonusTokenBonus = function(customBonustokenAmount, csvfile) {
		if (csvfile != null) {
			csvFileUpload(csvfile, "services/service-casino/casino/bonus/manual/register/bonus-token/v2/csv");
		} else {
			casinoRest.registerBonusTokenBonusv2(bonusRevision.bonusCode, controller.selectedUser.guid, customBonustokenAmount, bonusRevision.id).then(function(response) {
				console.log(response);
				if (angular.isDefined(response)) {
					if (response._status != 0) {
						notify.error('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR');
						notify.error(response._message);
					} else {
						notify.success('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.SUCCESS');
						$uibModalInstance.close(response);
					}
				}
			}).catch(function() {
				errors.catch('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR', false);
				bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			}).finally(function() {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			});
		}
	}
	
	controller.registerPlayer = function() {
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		console.log(bonusRevision);
		if (bonusRevision.bonusType === 1) {
			// Deposit Bonus
			var depositAmountInCents = (Math.round(controller.depositAmount * 100));
//			console.log(depositAmountInCents, controller.depositAmount);
			if ((angular.isUndefined(controller.depositAmount)) || (depositAmountInCents <= 0)) {
				errors.catch('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR', false);
				notify.error('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR');
				bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			} else {
				userEventRest.registerUserEvent(bonusRevision.domain.name, controller.selectedUser.guid.split('/')[1], 'MANUAL_DEPOSIT_BONUS_DEP', "Manual deposit bonus allocation: deposit amount", depositAmountInCents).then(function(response) {
					controller.registerDepositBonus(response.id);
				}).catch(function() {
					errors.catch('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR', false);
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
				}).finally(function() {
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
				});
			}
		} else if (bonusRevision.bonusType === 0) {
			// Signup Bonus
			casinoRest.registerSignupBonusv2(bonusRevision.bonusCode, controller.selectedUser.guid, controller.bonusPrerequisites, bonusRevision.id).then(function (response) {
				if (angular.isDefined(response)) {
					if (response._status != 0) {
						notify.error('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR');
						notify.error(response._message);
					} else {
						notify.success('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.SIGNUP.SUCCESS');
						$uibModalInstance.close(response);
					}
				}
			}).catch(function () {
				errors.catch('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR', false);
				bsLoadingOverlayService.stop({referenceId: controller.referenceId});
			}).finally(function () {
				bsLoadingOverlayService.stop({referenceId: controller.referenceId});
			});
		} else if (bonusRevision.bonusType === 3) {
			//Bonus token bonus
			controller.registerBonusTokenBonus(controller.customBonusTokenAmount, controller.csvfile);
		} else {
			// Trigger Bonus
			controller.registerTriggerBonus(controller.customFreeMoneyAmount, controller.csvfile);
		}
	}

	function csvFileUpload(csvfile, url) {
		var extraKeyVal = [];
		extraKeyVal.push({"key": "bonusCode", "value": bonusRevision.bonusCode});
		extraKeyVal.push({"key": "revisionId", "value": bonusRevision.id});
		fileUpload.uploadFileToUrl(csvfile, url, extraKeyVal).then(function(response) {
			console.log('csv processing response: ' ,response);
			if (angular.isDefined(response)) {
				if (response.status != 200) {
					notify.error('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR');
					notify.error(response._message);
				} else {
					notify.success('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.SUCCESS');
					$uibModalInstance.close(response);
				}
			}
		}).catch(function() {
			errors.catch('UI_NETWORK_ADMIN.PLAYER.BONUS.REGISTER.ERROR', false);
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		}).finally(function() {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	}
}]);
