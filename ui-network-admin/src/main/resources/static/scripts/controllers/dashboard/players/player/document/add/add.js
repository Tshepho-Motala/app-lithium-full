'use strict';

angular.module('lithium')
	.controller('documentUpload', ["domainName", "userguid", "$uibModalInstance","$scope", "$timeout", "$translate", "$log", "$state", "$stateParams", "$http", "notify", "$rootScope", "rest-document",
	function(domainName, userguid, $uibModalInstance, $scope, $timeout, $translate, $log, $state, $stateParams, $http, notify, $rootScope, restDocument) {

		var controller = this;
		controller.domainName = domainName;
		controller.userid = userguid;

		$rootScope.provide.documentGeneration.data.domain = controller.domainName;

		$rootScope.provide.documentGeneration['loadAvailableDocumentTypes'] = (internalOnly) => {
			return new Promise((res, rej) => {
				restDocument.availableTypes(controller.domainName, internalOnly).then(function (result) {
					res(result)
				}).catch(function (error) {
					console.log(error);
					rej(error)
				})
			})
		}

		$rootScope.provide.documentGeneration['loadAvailableReviewReasons'] = () => {
			return new Promise((res, rej) => {
				restDocument.availableReviewReasons(controller.domainName).then(function (result) {
					res(result)
				}).catch(function (error) {
					console.log(error);
					rej(error)
				})
			})
		}

		$rootScope.provide.documentGeneration['uploadDocument'] = (file, document) => {
			return new Promise((res, rej) => {
				let sensitivity = 'regular';
				if (document.sensitive) {
					sensitivity = 'sensitive';
				}
				restDocument.uploadDocument(controller.domainName, controller.userid, file, document, sensitivity).then(function (result) {

					notify.success("UI_NETWORK_ADMIN.DOCUMENT.ADD.SUCCESS");
					$uibModalInstance.close("success");

					res(result.data)
				}).catch(function (error) {
					console.log(error);
					notify.error("Unable to create document");
					rej(error)
				})
			})
		}

		$rootScope.provide.documentGeneration['closeUploadDialog'] = () => {
			controller.cancel();
		}

		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};

		window.VuePluginRegistry.loadByPage("dashboard/players/document-upload-quick-action")
	}
]);