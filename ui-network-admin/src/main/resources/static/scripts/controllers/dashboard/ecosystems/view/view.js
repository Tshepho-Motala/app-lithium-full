'use strict';

angular.module('lithium')
	.controller('ecosystemViewController', ["ecosystems", "$scope", "$stateParams", "$translate", "$dt", "notify", "errors", "DTOptionsBuilder", "EcosysRest", "bsLoadingOverlayService",
	function(ecosystems, $scope, $stateParams, $translate, $dt, notify, errors, DTOptionsBuilder, EcosysRest, bsLoadingOverlayService) {
		var controller = this;
		$scope.title = 'UI_NETWORK_ADMIN.ECOSYSTEMS.TITLE.HEADER';
		$scope.description = 'UI_NETWORK_ADMIN.ECOSYSTEMS.TITLE.DESC';

		controller.ecosystems = ecosystems;
		controller.model = {};
		controller.modelOriginal = angular.copy($stateParams.selectedEcosystem);
		controller.options = { formState: { readOnly: true } };
		controller.fields_personal = [{
			className: "col-xs-12",
			key: "name",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, minlength: 2, maxlength: 35, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.ECOSYSTEMS.FIELDS.ECOSYSTEM_NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ECOSYSTEMS.FIELDS.ECOSYSTEM_NAME" | translate',
			}
		},{
			key : "id",
			type : "input",
			templateOptions : {
				type: "hidden"
			}
		},{
			key : "deleted",
			type : "input",
			templateOptions : {
				type: "hidden"
			}
		},{
			key : "enabled",
			type : "input",
			templateOptions : {
				type: "hidden"
			}
		},{
			className: "col-xs-12",
			key: "displayName",
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
				'templateOptions.label': '"UI_NETWORK_ADMIN.ECOSYSTEMS.FIELDS.ECOSYSTEM_DISPLAY_NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ECOSYSTEMS.FIELDS.ECOSYSTEM_DISPLAY_NAME" | translate',
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
				'templateOptions.label': '"UI_NETWORK_ADMIN.ECOSYSTEMS.FIELDS.ECOSYSTEM_DESCRIPTION" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ECOSYSTEMS.FIELDS.ECOSYSTEM_DESCRIPTION" | translate',
			}
		}];
		
		controller.fields = [{
			className: "row v-reset-row ",
			fieldGroup: [{
				className: "col-md-12",
				fieldGroup: controller.fields_personal
			}]
		}];
        controller.enable = function(enabled) {
			//TO DO: enable ecosystem
			controller.model.enabled = enabled;
			EcosysRest.addModifyEcosystems(controller.model).then(function(response) {
				notify.success("SUCCESS");
			}).catch(function(error) {
				console.error(error);
				notify.error("ERROR");
				errors.catch("", false)(error)
			});
		}

		EcosysRest.ecosystems().then(function(response) {
			var data = response.plain();
			for (let index = 0; index < data.length; index++) {
				if (data[index].id == $stateParams.id) {
					controller.model = data[index];	
				}	
			}
		});

		controller.save = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			if (controller.form.$valid) {
				EcosysRest.addModifyEcosystems(controller.model).then(function() {
					notify.success("SUCCESS");
					$scope.dataMaster = angular.copy(controller.model);
					controller.options.formState.readOnly = true;
				}, function(response) {
					notify.warning("FAILED");
				});
			}
		}
		
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
	}
]);