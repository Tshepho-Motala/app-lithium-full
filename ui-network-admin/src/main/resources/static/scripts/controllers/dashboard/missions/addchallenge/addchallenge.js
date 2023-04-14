'use strict';

angular.module('lithium').controller('MissionsAddChallengeModal', ["mission", "challenge", "domainName", "errors", "$scope", "notify", "$uibModalInstance", "rest-casino", "PromotionRest",
function (mission, challenge, domainName, errors, $scope, notify, $uibModalInstance, casinoRest, PromotionRest) {
	var controller = this;
	
	if (challenge !== null) {
		controller.model = challenge;
	} else {
		controller.model = { rules: [] };
	}
	
	controller.fields = [
		{
			className : 'col-xs-12',
			key: "description",
			type: "textarea",
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.DESCRIPTION.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.DESCRIPTION.DESCRIPTION" | translate'
			}
		}, {
			className: "col-xs-12",
			type:"image-upload",
			key:"image",
			templateOptions:{
				type: "",
				label: "",
				required: false,
				description: "",
				maxsize: 2048, //Maximum file size in kilobytes (KB)
				minsize: 1,
				accept: "image/*",
				preview: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.ICON.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.ICON.DESCRIPTION" | translate',
			}
		}, {
			className: "col-xs-12",
			key: "reward.bonusCode", 
			type: "ui-select-single",
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				optionsAttr: 'bs-options',
				valueProp: 'bonusCode',
				labelProp: 'bonusName',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGEREWARD.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CHALLENGE.CHALLENGEREWARD.DESCRIPTION" | translate'
			},
			controller: ['$scope', function($scope) {
				if (domainName !== undefined && domainName !== null && domainName !== '') {
					casinoRest.findPublicBonusListV2(domainName, 2, 5).then(function(response) {
						$scope.to.options = response;
					});
				}
			}]
		}
	];
	
	controller.save = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		
		if (mission !== null && challenge !== null) {
			PromotionRest.modifyChallenge(mission.id, challenge.id, controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success("UI_NETWORK_ADMIN.MISSIONS.CHALLENGE.MODIFY.SUCCESS");
					$uibModalInstance.close(response);
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.MISSIONS.CHALLENGE.MODIFY.ERROR");
				errors.catch("", false)(error)
			});
		} else if (mission !== null) {
			PromotionRest.addChallenge(mission.id, controller.model).then(function(response) {
				if (response._status === 0) {
					notify.success("UI_NETWORK_ADMIN.MISSIONS.CHALLENGE.ADD.SUCCESS");
					$uibModalInstance.close(response);
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.MISSIONS.CHALLENGE.ADD.ERROR");
				errors.catch("", false)(error)
			});
		} else {
			$uibModalInstance.close(controller.model);
		}
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}
}]);