'use strict';

angular.module('lithium').controller('MissionsAddCategoryModal', ["domainName", "selectedCategories", "errors", "notify", "$uibModalInstance", "UserRest", "$translate",
function ( domainName,selectedCategories,  errors, notify, $uibModalInstance, userRest, $translate) {
	var controller = this;

	selectedCategories = selectedCategories || []
	const types  = [{ label: "WHITELIST", value: "whitelist" },
	{ label: "BLACKLIST", value: "blacklist" }]
	
	controller.model = {
		categories: [],
		type: 'blacklist'
	}

	controller.getTags = () => {
		return userRest.findAllTags(domainName+',').then(function(tags) {
			const selectedIds = selectedCategories.map(s => s.userCategoryId);
			const available = tags.plain().filter(t => !selectedIds.includes(t.id))
			return Promise.resolve(available)
		}).catch(function(error) {
			notify.error("UI_NETWORK_ADMIN.MISSIONS.ADD_CATEGORY.ERROR_MESSAGE");
			errors.catch("", false)(error)
		})
	}
	controller.fields = [
		{
			className : 'col-xs-12',
			key: "categories",
			type: "ui-select-multiple",
			templateOptions: {
				label: "", 
				description: "", 
				placeholder: 'Me and',
				labelProp: "name",
				valueProp: "id",
				required: true,
				options: [],
				optionsAttr: 'ui-options', 
				ngOptions: 'ui-options',
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CATEGORY.CATEGORIES.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CATEGORY.CATEGORIES.DESCRIPTION" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CATEGORY.CATEGORIES.PLACEHOLDER" | translate'
			},
			controller: ['$scope', function($scope) {
					controller.getTags().then(tags => $scope.to.options = tags)
			}]
		}, {
			className: "col-xs-12",
			type:"ui-select-single",
			key:"type",
			templateOptions:{
				type: "",
				label: "",
				placeholder: "",
				description: "",
				required: true,
				labelProp: "label",
				valueProp: "value",
				options: types,
				optionsAttr: 'ui-options', 
				ngOptions: 'ui-options',
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CATEGORY.TYPE.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.CATEGORY.TYPE.DESCRIPTION" | translate',
			}
		},
	];
	
	controller.submit = function() {

		if(!controller.form.$invalid) {
			
			$uibModalInstance.close([...selectedCategories, ...controller.model.categories.map(c => { 
				return { userCategoryId: c.id, type:  controller.model.type, name: c.name }
			})]);
		}
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}
}]);