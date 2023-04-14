'use strict';

angular.module('lithium')
	.controller('documentEdit', ["user", "domainName", "username", "document", "bsLoadingOverlayService", "$uibModalInstance","$scope", "$timeout", "$translate", "$log", "$state", "$stateParams", "$http", "UserRest","notify","file-upload","errors",
	function(user, domainName, username, document, bsLoadingOverlayService, $uibModalInstance, $scope, $timeout, $translate, $log, $state, $stateParams, $http, userRest, notify, fileUpload, errors) {
		$scope.$parent.title = 'UI_NETWORK_ADMIN.DOCUMENT.EDIT.TITLE';
		$scope.$parent.description = 'UI_NETWORK_ADMIN.DOCUMENT.EDIT.DESCRIPTION';
		$scope.user = user;
		$scope.domainName = domainName;
		$scope.username = username;
		var controller = this;
		
		controller.uploadFileUri= "services/service-user/"+domainName+"/users/documents/admin/saveFile";
		
		controller.model = document;
		controller.options = {};
		controller.document = {};
		controller.fields = 
			[
				{
					className: "row v-reset-row ",
					fieldGroup: 
					[
						{
							className: "col-xs-12",
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
							className: "col-xs-12",
							key: "statusName",
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
							className: "col-xs-12",
							key: "functionName",
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
							className: "col-xs-6",
							key: "archived",
							type: "checkbox",
							templateOptions: {
								label: "", description: "", placeholder: ""
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.DOCUMENT.FIELDS.ARCHIVE.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOCUMENT.FIELDS.ARCHIVE.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.DOCUMENT.FIELDS.ARCHIVE.DESCRIPTION" | translate'
							}
						},
						{
							className: "col-xs-6",
							key: "deleted",
							type: "checkbox",
							templateOptions: {
								label: "", description: "", placeholder: ""
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.DOCUMENT.FIELDS.DELETED.NAME" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.DOCUMENT.FIELDS.DELETED.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.DOCUMENT.FIELDS.DELETED.DESCRIPTION" | translate'
							}
						},
						{
							className: "col-xs-12",
							key: "account-verification-status-header",
							template: '<h4 lit-if-permission="PLAYER_VALIDATE_AGE,PLAYER_VALIDATE_ADDRESS" align="center" style="line-height:inherit; margin: 10px; font-weight: bold;">Account Verification Status</h4>',
						},
						{
							className: "row v-reset-row ",
							fieldGroup: [{
									className: "col-xs-6",
									type: "age-verification-button",
									key: "age-verification-button",
									templateOptions: {
										text: $scope.user.ageVerified === false ? 'Verify user age' : 'Unverify user age',
										onClick: function($event) {
											controller.toggleAgeVerification();
										}
									}
								},
								{
									className: "col-xs-6",
									type: "address-verification-button",
									key: "address-verification-button",
									templateOptions: {
										text: $scope.user.addressVerified === false ? 'Verify user address' : 'Unverify user address',
										onClick: function($event) {
											controller.toggleAddressVerification();
										}
									}
								}]
						}
					]
				}
			];

		controller.toggleAgeVerification = function () {
			$scope.referenceId = 'personal-overlay';
			bsLoadingOverlayService.start({referenceId: $scope.referenceId});
			userRest.toggleAgeVerification(domainName, $scope.user.id).then(function (response) {
				$scope.user.ageVerified = response.ageVerified;
				$scope.user.verificationStatus = response.verificationStatus;
				$scope.controller.fields[0].fieldGroup[6].fieldGroup[0].templateOptions.text = $scope.user.ageVerified === false ? 'Verify user age' : 'Unverify user age';
				notify.success($scope.user.ageVerified === true ? "UI_NETWORK_ADMIN.USER.AGE_VERIFIED.VALIDATION.SUCCESS" : "UI_NETWORK_ADMIN.USER.AGE_VERIFIED.INVALIDATION.SUCCESS");
			}).catch(
				errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.AGE_VERIFIED", false)
			).finally(function () {
				bsLoadingOverlayService.stop({referenceId: $scope.referenceId});
			});
		}

		controller.toggleAddressVerification = function () {
			$scope.referenceId = 'personal-overlay';
			bsLoadingOverlayService.start({referenceId: $scope.referenceId});
			userRest.toggleAddressVerification(domainName, $scope.user.id).then(function (response) {
				$scope.user.addressVerified = response.addressVerified;
				$scope.user.verificationStatus = response.verificationStatus;
				$scope.controller.fields[0].fieldGroup[6].fieldGroup[1].templateOptions.text = $scope.user.addressVerified === false ? 'Verify user address' : 'Unverify user address';
				notify.success($scope.user.addressVerified === true ? "UI_NETWORK_ADMIN.USER.ADDRESS_VERIFIED.VALIDATION.SUCCESS" : "UI_NETWORK_ADMIN.USER.ADDRESS_VERIFIED.INVALIDATION.SUCCESS");
			}).catch(
				errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.ADDRESS_VERIFIED", false)
			).finally(function () {
				bsLoadingOverlayService.stop({referenceId: $scope.referenceId});
			});
		}
		
		controller.onSubmit = function() {
			userRest.updateDocument($scope.domainName, controller.model).then(function(document) {
				if(document) {
					notify.success("UI_NETWORK_ADMIN.DOCUMENT.EDIT.SUCCESS");
					$uibModalInstance.close("success");
				} else {
					notify.error("Unable to create document");
				}
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
		
		controller.uploadFile = function(name, file) {
			//console.log("name: ", name);
			//console.log("file: ", file);
			//console.log("uri: ", controller.uploadFileUri);
			var extraKeyVal = [];
			extraKeyVal.push({"key" : "documentUuid", "value" : name})
			fileUpload.uploadFileToUrl(file, controller.uploadFileUri, extraKeyVal).then(function(response) {
				if (angular.isDefined(response)) {
					if (response.data.status != 0) {
						notify.error(response.data.message);
					} else {
						notify.success("UI_NETWORK_ADMIN.DOCUMENT.UPLOAD.SUCCESS");
						$uibModalInstance.close("success");
					}
				} else {
					notify.error("Could not upload document.");
				}
			}).catch(function(error) {
				notify.error("Could not upload document.");
			});
		}
	}
]);
