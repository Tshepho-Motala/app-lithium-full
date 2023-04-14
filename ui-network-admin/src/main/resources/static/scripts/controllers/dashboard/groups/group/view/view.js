'use strict';

angular.module('lithium')
	.controller('GroupView', ["group", "notify", "$translate", "$scope", "$state", "rest-group", 
	function(group, notify, $translate, $scope, $state, restGroup) {
		var controller = this;
		$scope.domainName = $state.params.domainName;
		$scope.groupId = $state.params.groupId;
		$state.params.groupName = group.name;
		
		controller.model = group;
		controller.modelOriginal = angular.copy(group);
		controller.options = { formState: { readOnly: true } };
		
		controller.fields_personal = [{
			className: "col-xs-12",
			key: "domain.name",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 2, maxlength: 35, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GROUPS.VIEW.FIELDS.DOMAIN.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GROUPS.VIEW.FIELDS.DOMAIN.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.GROUPS.VIEW.FIELDS.DOMAIN.DESCRIPTION" | translate'
			}
		},{
			key : "id",
			type : "input",
			templateOptions : {
				type: "hidden"
			}
		},{
			className: "col-xs-12",
			key: "name",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 2, maxlength: 35, disabled: true,
				onKeydown: function(value, options) {
					options.validation.show = false;
				},
				onBlur: function(value, options) {
					options.validation.show = true;
				}
			},
			modelOptions: {
				updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GROUPS.VIEW.FIELDS.GROUP.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GROUPS.VIEW.FIELDS.GROUP.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.GROUPS.VIEW.FIELDS.GROUP.DESCRIPTION" | translate'
			}
		},{
			className: "col-xs-12",
			key: "description",
			type: "input",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: false, minlength: 2, maxlength: 255,
				onKeydown: function(value, options) {
					options.validation.show = false;
				},
				onBlur: function(value, options) {
					options.validation.show = true;
				}
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GROUPS.VIEW.FIELDS.DESCRIPTION.LABEL" | translate',
				'templateOptions.placeholder': '',
				'templateOptions.description': '"UI_NETWORK_ADMIN.GROUPS.VIEW.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
			}
		}];
		
		controller.fields = [{
			className: "row v-reset-row ",
			fieldGroup: [{
				className: "col-md-12",
				fieldGroup: controller.fields_personal
			}]
		}];
		
		controller.edit = function() {
			controller.options.formState.readOnly = false;
			controller.fields_personal[1].templateOptions.focus = true;
		}
		
		controller.cancel = function() {
			controller.reset();
			controller.options.formState.readOnly = true;
		}
		
		controller.reset = function() {
			controller.model = angular.copy(controller.modelOriginal);
		}
		
		controller.save = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			if (controller.form.$valid) {
				restGroup.save($scope.domainName, $scope.groupId, controller.model).then(function() {
					notify.success("UI_NETWORK_ADMIN.GROUPS.SAVE.SUCCESS");
					$scope.dataMaster = angular.copy(controller.model);
					controller.options.formState.readOnly = true;
				}, function(response) {
					notify.warning("UI_NETWORK_ADMIN.GROUPS.SAVE.FAIL");
				});
			}
		}
		
		controller.changelogs = {
			domainName: $scope.domainName,
			entityId: group.id,
			restService: restGroup,
			reload: 0
		}
	}
]);
