'use strict';

angular.module('lithium')
.controller('groupAdd',
	["notify", "Restangular", "$rootScope", "$translate", "$log", "$dt", "$dt2", "$scope", "$state", "$stateParams", "$http", "$userService", "rest-group", 
	function(notify, Restangular, $rootScope, $translate, $log, $dt, $dt2, $scope, $state, $stateParams, $http, $userService, restGroup) {
		$scope.$parent.title = 'UI_NETWORK_ADMIN.GROUPS.ADD.HEADER.TITLE';
		$scope.$parent.description = 'UI_NETWORK_ADMIN.GROUPS.ADD.HEADER.DESCRIPTION';
		var controller = this;
		$scope.domainName = $state.params.domainName;
		
		controller.model = {};
		controller.options = {};
		
		controller.fields = [{
			className: "row v-reset-row ",
			fieldGroup: [
				{
					"key" : "domain.name",
					"type" : "input",
					"className" : "col-xs-6",
					"defaultValue" : $scope.domainName,
					"templateOptions" : {
						"label" : "Domain",
						"required" : true,
						"disabled" : true,
						"description" : "This is the domain for which the group will be created.",
					}
				},
				{
					"key" : "name",
					"type" : "input",
					"className" : "col-xs-8",
					"templateOptions" : {
						"label" : "Name",
						"required" : true,
						"description" : "Please enter the name of the group you are trying to create.",
						"placeholder" : "Group 1"
					}
				},
				{
					"key" : "description",
					"type" : "input",
					"className" : "col-xs-12",
					"defaultValue" : "",
					"templateOptions" : {
						"label" : "Description",
						"description" : "You can optionally add a short description for this group.",
						"minlength" : 2, "maxlength" : 255,
						onKeydown: function(value, options) {
							options.validation.show = false;
						},
						onBlur: function(value, options) {
							options.validation.show = true;
						}
					}
				}
			]
		}];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}

			Restangular.all('services/service-user/domain/'+$scope.domainName+'/groups').post(controller.model).then(function(response) {
				notify.success("UI_NETWORK_ADMIN.GROUP.SAVE.SUCCESS");
				$state.go("^.group.view", {
					domainName: $scope.domainName,
					groupId: response.id
				});
			});
		}
	}]
);
