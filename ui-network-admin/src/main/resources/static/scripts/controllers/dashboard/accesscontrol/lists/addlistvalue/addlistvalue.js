'use strict';

angular.module('lithium').controller('AccessControlAddListValueModal', ["$uibModalInstance", "notify", "list", "$scope", "$translate", "accessControlRest", "GeoRest", "userFields", "UserRest",
function ($uibModalInstance, notify, list, $scope, $translate, accessControlRest, GeoRest, userFields, userRest) {
	var controller = this;

	controller.list = list;
	controller.options = {formState: {}};
	controller.model = {enabled: true, selectedUser: undefined};
	
	controller.fields = [{
		key: "data",
		type: "input",
		templateOptions: {
			label: "Data",
			description: "",
			placeholder: "",
			required: true
		}
	}];

	switch (list.listType.name.toLowerCase()) {
		case 'ip_list':
			controller.fields = [{
				key: "data",
				type: "input",
				templateOptions: {
					label: "IP Address",
					description: "",
					placeholder: "",
					required: true
				},
				validators: {
					ipAddress: {
						expression: function($viewValue, $modelValue, scope) {
							var value = $modelValue || $viewValue;
							return /^([0-9]{1,3})[.]([0-9]{1,3})[.]([0-9]{1,3})[.]([0-9]{1,3})$/.test(value);
						},
						message: '($viewValue != undefined && $viewValue != "")? $viewValue + " is not a valid IP Address" : ""'
					}
				}
			}];
			break;
		case 'ip_range':
			controller.fields = [{
				key: "ipRangeStart",
				type: "input",
				templateOptions: {
					label: "IP Range Start",
					description: "",
					placeholder: "",
					required: true
				},
				validators: {
					ipAddress: {
						expression: function($viewValue, $modelValue, scope) {
							var value = $modelValue || $viewValue;
							return /^([0-9]{1,3})[.]([0-9]{1,3})[.]([0-9]{1,3})[.]([0-9]{1,3})$/.test(value);
						},
						message: '($viewValue != undefined && $viewValue != "")? $viewValue + " is not a valid IP Address" : ""'
					}
				}
			}, {
				key: "ipRangeEnd",
				type: "input",
				templateOptions: {
					label: "IP Range End",
					description: "",
					placeholder: "",
					required: true
				},
				validators: {
					ipAddress: {
						expression: function($viewValue, $modelValue, scope) {
							var value = $modelValue || $viewValue;
							return /^([0-9]{1,3})[.]([0-9]{1,3})[.]([0-9]{1,3})[.]([0-9]{1,3})$/.test(value);
						},
						message: '($viewValue != undefined && $viewValue != "")? $viewValue + " is not a valid IP Address" : ""'
					}
				}
			}];
			break;
		case 'country_list':
		case 'country_list_profile':
			controller.fields = [{
				key : "data",
				type : "ui-select-single",
				templateOptions : {
					label : "Country",
					required : true,
					optionsAttr: 'bs-options',
					description : "",
					valueProp : 'name',
					labelProp : 'name',
					placeholder : 'Select Country',
					options : []
				},
				controller: ['$scope', function($scope) {
					GeoRest.countries().then(function(response) {
						$scope.to.options = response;
					});
				}]
			}];
			break;
		case 'state_list':
		case 'state_list_profile':
			controller.fields = [{
				key : "data",
				type : "uib-typeahead",
				templateOptions : {
					label: "State",
					description: "",
					placeholder: "",
					required : true,
					valueProp: 'name',
					labelProp: 'name',
					displayProp: 'name',
					displayOnly: false
				},
				controller: ['$scope', function($scope) {
					$scope.searchTypeAhead = function(searchValue) {
						return GeoRest.level1s(searchValue).then(function(response) {
							$scope.to.options = response;
							return response;
						});
					}
				}]
			}];
			break;
		case 'city_list':
		case 'city_list_profile':
			controller.fields = [{
				key : "data",
				type : "uib-typeahead",
				templateOptions : {
					label: "City",
					description: "",
					placeholder: "",
					required : true,
					valueProp: 'name',
					labelProp: 'name',
					displayProp: 'name',
					displayOnly: false
				},
				controller: ['$scope', function($scope) {
					$scope.searchTypeAhead = function(searchValue) {
						return GeoRest.cities(searchValue).then(function(response) {
							$scope.to.options = response;
							return response;
						});
					}
				}]
			}];
			break;
		case 'post_list':
			controller.fields = [{
				key: "data",
				type: "input",
				templateOptions: {
					label: "Postal Code", description: "", placeholder: "",
					required: false,
					minlength: 0, maxlength: 235
				},
				optionsTypes: ['editable'],
				modelOptions: {
					updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
				}
			}];
			break;
		case 'browser_list':
			controller.fields = [{
				key : "data",
				type : "ui-select-single",
				templateOptions : {
					label : "Browser",
					required : true,
					optionsAttr: 'bs-options',
					description : "",
					valueProp : 'value',
					labelProp : 'name',
					placeholder : 'Search / Select Browser',
					options : []
				},
				controller: ['$scope', function($scope) {
					accessControlRest.browsers().then(function(response) {
						$scope.to.options = response;
					});
				}]
			}];
			break;
		case 'os_list':
			controller.fields = [{
				key : "data",
				type : "ui-select-single",
				templateOptions : {
					label : "Operating System",
					required : true,
					optionsAttr: 'bs-options',
					description : "",
					valueProp : 'value',
					labelProp : 'name',
					placeholder : 'Search / Select Operating System',
					options : []
				},
				controller: ['$scope', function($scope) {
					accessControlRest.operatingSystems().then(function(response) {
						$scope.to.options = response;
					});
				}]
			}];
			break;
		case 'player_list':
			controller.fields = [];
			break;
		case 'duplicate_check':
			controller.fields = [{
				key : "data",
				type : "ui-select-single",
				templateOptions : {
					label : "Select duplicate type",
					required : true,
					optionsAttr: 'bs-options',
					description : "",
					valueProp : 'value',
					labelProp : 'name',
					placeholder : 'Possible check for duplicate',
					options : []
				},
				controller: ['$scope', function($scope) {
					accessControlRest.duplicateTypes().then(function(response) {
						$scope.to.options = response;
					});
				}]
			}];
			break;
	}

	controller.resetUserSearch = function() {
		controller.model.selectedUser = undefined;
	}

	controller.searchUsers = function(search) {
		return userRest.search(controller.list.domain.name, search).then(function(searchResult) {
			return searchResult.plain();
		}).catch(function(error) {
			errors.catch("", false)(error)
		});
	}

	$scope.$watch(function() { return controller.model.selectedUser }, function(newValue, oldValue) {
		if (newValue != oldValue) {
			controller.model.data = controller.model.selectedUser.guid;
		}
	});

	controller.save = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		if (list.listType.name === 'IP_Range') {
			controller.model.data = controller.model.ipRangeStart + '|' + controller.model.ipRangeEnd;
		}
		accessControlRest.addListValue(list.id, controller.model.data).then(function(response) {
			if (response._status === 409) {
				notify.error('This value already exists in the list.');
			} else {
				notify.success("The value was added successfully.");
			}
			$uibModalInstance.close(response);
		}, function(status) {
			notify.error("Unable to save. Please try again.");
		});
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}
}]);
