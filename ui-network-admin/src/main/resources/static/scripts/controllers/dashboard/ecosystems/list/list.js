'use strict';

angular.module('lithium')
	.controller('ecosystemListController', ["ecosystems", "$scope", "$state", "$translate", "$dt", "DTOptionsBuilder", "EcosysRest",
	"$uibModal", "notify",
	function(ecosystems, $scope, $state, $translate, $dt, DTOptionsBuilder, ecosysRest, $uibModal, notify) {
		var controller = this;
		controller.model = {};
		controller.ecosystems = ecosystems;
		controller.ecosystemList = controller.ecosystems.plain();
		
		$scope.openEcosystem = function(data) {
			$state.go("dashboard.ecosystems.view", { id: data.id });
		}

		$scope.manageEcosystemRelationships = function (data) {
			$state.go("dashboard.domainrelationship.list", {id: data.id});
		}

		$scope.deleteEcosystem = function(data, isDeleted) {
			data.deleted = isDeleted;
			let modalInstance  = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/ecosystems/list/confirmdeleteecosystem.html',
				controller: 'ConfirmDeleteEcosystem',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					data : function() {
						return data;
					},
					ecosystemRest : function () {
						return ecosysRest;
					},
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name: 'lithium',
							files: ['scripts/controllers/dashboard/ecosystems/list/confirmdeleteecosystem.js']
						});
					}
				}
			});
			modalInstance.result.then(function(response) {
				controller.refresh();
				notify.success('UI_NETWORK_ADMIN.ECOSYSTEM.MESSAGES.DELETE_ECOSYSTEM_SUCCESS');
			});
		}

		controller.reloadEcosystems = function() {
			ecosysRest.ecosystems().then(function(response){
				controller.ecosystemList = response.plain();
			})
		}
		controller.refresh = function(){
			controller.reloadEcosystems();
		}
	}
]);