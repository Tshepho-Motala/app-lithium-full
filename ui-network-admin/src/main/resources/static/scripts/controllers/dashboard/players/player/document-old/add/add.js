'use strict';

angular.module('lithium')
	.controller('documentAdd', ["domainName", "username", "userid", "$uibModalInstance","$scope", "$timeout", "$translate", "$log", "$state", "$stateParams", "$http", "UserRest","notify",
	function(domainName, username, userid, $uibModalInstance, $scope, $timeout, $translate, $log, $state, $stateParams, $http, userRest, notify) {
		$scope.$parent.title = 'UI_NETWORK_ADMIN.DOCUMENT.ADD.TITLE';
		$scope.$parent.description = 'UI_NETWORK_ADMIN.DOCUMENT.ADD.DESCRIPTION';
		$scope.domainName = domainName;
		$scope.username = username;
		$scope.userid = userid;
		var controller = this;
		
		controller.model = {};
		controller.options = {};

		controller.fields = 
			[
				{
					className: "row v-reset-row ",
					fieldGroup: 
					[
						{
							className: "col-xs-8",
							key: "name",
							type: "input",
							templateOptions: {
								label: "", description: "", placeholder: "",
								required: true
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.DOCUMENT.FIELDS.NAME.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOCUMENT.FIELDS.NAME.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.DOCUMENT.FIELDS.NAME.DESCRIPTION" | translate'
							}
						},
						{
							className: "col-xs-8",
							key: "statusString",
							type: "input",
							templateOptions: {
								label: "", description: "", placeholder: "",
								required: true
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.DOCUMENT.FIELDS.STATUS.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOCUMENT.FIELDS.STATUS.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.DOCUMENT.FIELDS.STATUS.DESCRIPTION" | translate'
							}
						},
						{
							className: "col-xs-8",
							key: "documentFunction",
							type: "input",
							templateOptions: {
								label: "", description: "", placeholder: "",
								required: true,
								minlength: 2, maxlength: 35,
								focus: true
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.DOCUMENT.FIELDS.FUNCTION.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOCUMENT.FIELDS.FUNCTION.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.DOCUMENT.FIELDS.FUNCTION.DESCRIPTION" | translate'
							}
						},
						{
							className: "col-xs-8",
							key: "external",
							type: "checkbox",
							defaultValue: true,
							templateOptions: {
								label: "", description: "", placeholder: ""
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.DOCUMENT.EXTERNAL.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOCUMENT.EXTERNAL.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.DOCUMENT.EXTERNAL.DESCRIPTION" | translate'
							}
						}
					]
				}
			];
		
		
		controller.onSubmit = function() {
			userRest.createDocument($scope.domainName, controller.model.name, controller.model.statusString, controller.model.documentFunction, controller.model.external | false, $scope.userid).then(function(document) {
				if(document) {
					notify.success("UI_NETWORK_ADMIN.DOCUMENT.ADD.SUCCESS");
					$uibModalInstance.close("success");
				} else {
					notify.error("Unable to create document");
				}
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);