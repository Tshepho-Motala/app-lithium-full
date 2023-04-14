'use strict';

angular.module('lithium')
.controller('BonusEditChangeModal', ["bonus", "bonusRevision", "$uibModalInstance", "$translate", "$scope", "$state", "$rootScope", "rest-casino", "notify",
	function(bonus, bonusRevision, $uibModalInstance, $translate, $scope, $state, $rootScope, casinoRest, notify) {
		var controller = this;
		controller.bonusRevision = bonusRevision;
		controller.bonus = bonus;
		
//		console.log(bonusRevision);
		
		controller.cancel = function() {
			$uibModalInstance.dismiss("cancel");
		}
		
		controller.edit = function() {
			$uibModalInstance.dismiss("cancel");
			$state.go("dashboard.bonuses.bonus.revisions.edit", {bonusId:controller.bonus.id, bonusRevisionId:controller.bonus.edit.id});
		}
		
		controller.markEdit = function() {
			casinoRest.markBonusRevisionEdit(bonus.id, bonusRevision.id).then(function(response) {
				$translate("UI_NETWORK_ADMIN.BONUS.EDIT.MARK.SUCCESSEDIT", {bonusRevisionId:bonusRevision.id}).then(function success(translate) {
					notify.success(translate);
					$uibModalInstance.close(response.plain());
				});
			}).catch(function() {
				$translate("UI_NETWORK_ADMIN.BONUS.EDIT.MARK.ERROREDIT", {bonusRevisionId:bonusRevision.id}).then(function success(translate) {
					notify.error(translate);
					$uibModalInstance.close(response.plain());
				});
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			}).finally(function () {
				
			});
		}
		
		controller.markCurrent = function(bonusRevisionId) {
			casinoRest.markBonusRevisionCurrent(bonus.id, bonusRevisionId).then(function(response) {
//				console.log(response);
				$translate("UI_NETWORK_ADMIN.BONUS.EDIT.MARK.SUCCESSCURRENT", {bonusRevisionId:bonusRevisionId}).then(function success(translate) {
					notify.success(translate);
					$uibModalInstance.close(response.plain());
				});
			}).catch(function() {
				$translate("UI_NETWORK_ADMIN.BONUS.EDIT.MARK.ERRORCURRENT", {bonusRevisionId:bonusRevisionId}).then(function success(translate) {
					notify.error(translate);
					$uibModalInstance.close(response.plain());
				});
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			}).finally(function () {
				
			});
		}
	}
]);