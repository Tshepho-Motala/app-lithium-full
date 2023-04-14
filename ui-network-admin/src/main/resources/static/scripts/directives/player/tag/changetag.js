'use strict';

angular.module('lithium')
.controller('ChangeTagModal', ['$uibModalInstance', 'domainName', 'user', "UserRest", 'userFields', 'notify', 'bsLoadingOverlayService', 'errors',
function ($uibModalInstance, domainName, user, userRest, userFields, notify, bsLoadingOverlayService, errors) {
	var vm = this;
	vm.submitCalled = false;
	vm.options = {removeChromeAutoComplete:true};
	vm.model = user;
	
	vm.referenceId = "ChangeTagModal_"+(Math.random()*1000);
	
	vm.fields = [{
		className: "col-xs-12",
		key: "userTag",
		type: "ui-select-single",
		templateOptions : {
			required: false,
			dataAllowClear: false,
			label: "Add Tag",
			description: "",
			placeholder: "Select tag to add player",
			valueProp: 'id',
			labelProp: 'name',
			groupBy: 'domain.name',
			optionsAttr: 'ui-options', ngOptions: 'ui-options',
			options: []
		},
//		expressionProperties: {
//			'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.MISSIONREWARD.NAME" | translate',
//			'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.MISSIONREWARD.DESCRIPTION" | translate'
//		},
		controller: ['$scope', function($scope) {
			bsLoadingOverlayService.start({referenceId:vm.referenceId});
			userRest.findAllTags(domainName+',').then(function(tags) {
				$scope.to.options = tags;
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.PUSHMSG.PROVIDERS.ADD.ERROR");
				errors.catch("", false)(error)
			}).finally(function() {
				bsLoadingOverlayService.stop({referenceId:vm.referenceId});
			});
		}]
	}];
	
	vm.remove = function(uc) {
		bsLoadingOverlayService.start({referenceId:vm.referenceId});
		userRest.tagRemovePlayer(uc.id, user.username).then(function(response) {
			bsLoadingOverlayService.stop({referenceId:vm.referenceId});
			$uibModalInstance.close(response);
		}, function(status) {
			notify.error("Unable to save. Please try again.");
		});
	}
	
	vm.submit = function() {
		bsLoadingOverlayService.start({referenceId:vm.referenceId});
		if (vm.model.userTag == undefined) {
			notify.warning("UI_NETWORK_ADMIN.TAGS.RESPONSE.ERROR");
			bsLoadingOverlayService.stop({referenceId:vm.referenceId});
			return false;
		}
		userRest.tagAddPlayer(vm.model.userTag, user.username).then(function(response) {
			bsLoadingOverlayService.stop({referenceId:vm.referenceId});
			$uibModalInstance.close(response);
		}, function(status) {
			notify.error("Unable to save. Please try again.");
		});
	};
	
	vm.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);
