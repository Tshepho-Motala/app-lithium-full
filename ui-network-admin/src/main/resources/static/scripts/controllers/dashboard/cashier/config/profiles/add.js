'use strict';

angular.module('lithium')
	.controller('ProfileAdd', ["domainName", "$uibModalInstance", "rest-cashier", "notify", "errors", "$q",
	function(domainName, $uibModalInstance, cashierRest, notify, errors, $q) {
		var controller = this;
		controller.model = {};
		controller.options = {};
		
		controller.model.domain = {};
		controller.model.domain.name = domainName;
		
		controller.fields = [{
			className: "col-xs-12",
			type: "input",
			key: "code",
			templateOptions: {
				type: "",
				label: "",
				required: true,
				placeholder: "",
				description: "",
				minlength: 2
			},
			modelOptions: {
				updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.CODE.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.CODE.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.CODE.DESCRIPTION" | translate'
			},
			asyncValidators: {
				codeUnique: {
					expression: function($viewValue, $modelValue, scope) {
						var success = false;
						return cashierRest.profile(domainName, encodeURIComponent($viewValue)).then(function(profile) {;
							if (angular.isUndefined(profile.plain()) || (profile._status == 404) || (profile.length === 0)) {
								success = true;
							}
						}).catch(function() {
							scope.options.validation.show = true;
							errors.catch("UI_NETWORK_ADMIN.CASHIER.PROFILES.", false);
						}).finally(function () {
							scope.options.templateOptions.loading = false;
							if (success) {
								return $q.resolve("No such profile");
							} else {
								return $q.reject("Profile already exists");
							}
						});
					},
					message: '"UI_NETWORK_ADMIN.CASHIER.PROFILES.CODEEXISTS" | translate'
				}
			}
		},{
			"className" : "col-xs-12",
			"type" : "input",
			"key" : "name",
			"templateOptions" : {
				"type" : "",
				"label" : "",
				"required" : true,
				"placeholder" : "",
				"description" : "",
				"options" : []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.NAME.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.NAME.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.NAME.DESCRIPTION" | translate'
			}
		},{
			"className" : "col-xs-12",
			"type" : "input",
			"key" : "description",
			"templateOptions" : {
				"type" : "",
				"label" : "",
				"required" : false,
				"placeholder" : "",
				"description" : "",
				"options" : []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.DESC.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.DESC.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.DESC.DESCRIPTION" | translate'
			}
		}];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				notify.warning("UI_NETWORK_ADMIN.CASHIER.PROFILES.ADD.ERROR");
			} else {
				cashierRest.profileSave(controller.model).then(function(profile) {
					notify.success("UI_NETWORK_ADMIN.CASHIER.PROFILES.ADD.SUCCESS");
					$uibModalInstance.close(profile);
				}).catch(function(error) {
					notify.warning("UI_NETWORK_ADMIN.CASHIER.PROFILES.ADD.ERROR");
					errors.catch("", false)(error)
				});
			}
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);