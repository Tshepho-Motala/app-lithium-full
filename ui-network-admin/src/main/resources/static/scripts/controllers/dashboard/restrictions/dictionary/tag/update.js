'use strict';

angular.module('lithium')
.controller('UpdateRestrictionTagModal',
['$uibModalInstance','RestrictionsRest','UserRest', 'domainRestrictionSet','parentRestrictionKey', 'rest-translate', '$userService','notify', 'bsLoadingOverlayService',
function ($uibModalInstance, restrictionsRest, rest,domainRestrictionSet,parentRestrictionKey, translateRest, $userService, notify, bsLoadingOverlayService) {
	var controller = this;
	
	controller.set = domainRestrictionSet
	controller.referenceId = 'edupdateit-restriction-tag-overlay';

	controller.model = {
		tagId: domainRestrictionSet.excludeTagId
	}

	controller.fields = [
		{
			className: "col-xs-12",
			key: "tagId",
			type: "ui-select-single",
			templateOptions : {
				required: false,
				label: "Add Tag",
				description: "",
				placeholder: "Select tag to add player",
				labelProp: 'name',
				valueProp: 'id',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.TAG.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.TAG.DESCRIPTION" | translate'
			},
			controller: ['$scope', function($scope) {
				bsLoadingOverlayService.start({referenceId:controller.referenceId});
				rest.findAllTags(domainRestrictionSet.domain.name+',').then(function(tags) {
					$scope.to.options = tags;
				}).catch(function(error) {
					errors.catch("", false)(error)
				}).finally(function() {
					bsLoadingOverlayService.stop({referenceId:controller.referenceId});
				});
			}]
		}
	]

	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel')
	}

	controller.submit = async () => {
		try {
			await restrictionsRest.updateExcludeTagId(controller.set.id, controller.model.tagId)
			const selected = controller.fields[0].templateOptions.options.find(t => t.id === controller.model.tagId);
			$uibModalInstance.close(selected)
			notify.success("UI_NETWORK_ADMIN.RESTRICTIONS.MESSAGES.ADD_TAG.SUCCESS");
		}
		catch(error) {
			notify.error("UI_NETWORK_ADMIN.RESTRICTIONS.MESSAGES.ADD_TAG.ERROR");
			console.error(error)
		}
	}

	}]);

