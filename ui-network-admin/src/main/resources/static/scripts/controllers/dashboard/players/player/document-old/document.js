'use strict';

angular.module('lithium')
	.controller('PlayerDocumentOldController', ["$q", "$timeout", "$translate", "$log", "Restangular", "$state", "$stateParams", "$http", "UserRest","notify", "file-upload","$scope","$rootScope", "user", "$uibModal", "$userService",
	function($q, $timeout, $translate, $log, Restangular, $state, $stateParams, $http, userRest, notify, fileUpload, $scope, $rootScope, user, $uibModal, $userService) {
		var controller = this;

		var gameId = $stateParams.gameId;

		controller.documents = {};
		controller.user = user;
		controller.downloadFileUri= "services/service-user/"+user.domain.name+"/users/documents/downloadFile?documentUuid=";
		controller.model = {};
		controller.external = 1;

		controller.reload = function(external) {
			if(external == 1) {
				userRest.documentsExternal(controller.user.domain.name, controller.user.id)
					.then(function(documentList) {
						controller.documents = documentList;
						controller.external = 1;
						},function(response) {
							notify.error("problem getting documents for user");
						}
					);
			} else {
				userRest.documentsInternal(controller.user.domain.name, controller.user.id)
				.then(function(documentList) {
					controller.documents = documentList;
					controller.external = 0;
					},function(response) {
						notify.error("problem getting documents for user");
					}
				);
			}
		}

		if( $userService.hasRoleForDomain(controller.user.domain.name, "PLAYER_DOCUMENT_INTERNAL_LIST")) {
			controller.external = 0;
		} //if

		if( $userService.hasRoleForDomain(controller.user.domain.name, "PLAYER_DOCUMENT_EXTERNAL_LIST")) {
			controller.external = 1;
		} //if

		controller.reload(controller.external);

		controller.download = function(uuid, page) {
			window.location = controller.downloadFileUri + uuid + "&page=" + page;
		}

		controller.resetModel = function() {
			controller.model = angular.copy(controller.originalModel);
		}

		controller.documentAddModal = function () {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				backdrop: 'static',
				templateUrl: 'scripts/controllers/dashboard/players/player/document-old/add/add.html',
				controller: 'documentAdd',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					domainName: function () {
						return controller.user.domain.name;
					},
					username: function () {
						return controller.user.username;
					},
					userid: function () {
						return controller.user.id;
					},
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/players/player/document-old/add/add.js'
							]
						})
					}
				}
			});

			modalInstance.result.then(function() {
				controller.reload(controller.external);
			})
		}

		$rootScope.$on("reloadDocumentList", function(){
			controller.reload(controller.external);
		});

		controller.documentEditModal = function (document) {
			$scope.document = document;
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				backdrop: 'static',
				templateUrl: 'scripts/controllers/dashboard/players/player/document-old/edit/edit.html',
				controller: 'documentEdit',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					user: function () {
						return controller.user;
					},
					domainName: function () {
						return controller.user.domain.name;
					},
					username: function () {
						return controller.user.username;
					},
					document: function() {
						return $scope.document;
					},
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/players/player/document-old/edit/edit.js'
							]
						})
					}
				}
			});

			modalInstance.result.then(function() {
				controller.reload(controller.external);
			})
		}
	}
]);
