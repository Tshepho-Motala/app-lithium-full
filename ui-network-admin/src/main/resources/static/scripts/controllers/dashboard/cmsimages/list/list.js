'use strict';

angular.module('lithium')
	.controller('ImagesListController', ["domainName", "$translate", "$log", "$dt", "$state", "$rootScope", "$uibModal", function(domainName, $translate, $log, $dt, $state, $rootScope, $uibModal) {
		
		var controller = this;
		controller.domainName = domainName;
		var baseUrl = "services/service-domain/backoffice/"+ domainName +"/asset/templates/table";
		
		controller.rowClickHandler = function(data) {
			$state.go('dashboard.cmsimages.domain.view', {
				id: data.id,
				domainName: domainName
			});
		}
		
		controller.table = $dt.builder()
			.column($dt.column('name').withTitle($translate("UI_NETWORK_ADMIN.CMS.IMAGES.FIELDS.NAME.NAME")))
			.column($dt.column('lang').withTitle($translate("UI_NETWORK_ADMIN.CMS.IMAGES.FIELDS.LANG.NAME")))
			.column($dt.column('description').withTitle($translate("UI_NETWORK_ADMIN.CMS.IMAGES.FIELDS.DESCRIPTION.NAME")))
			.options(baseUrl, controller.rowClickHandler)
			.build();
		
		controller.addTemplate = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/cmsimages/add/add.html',
				controller: 'ImagesAddController',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					domainName: function () {
						return domainName;
					},
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/cmsimages/add/add.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(template) {
				controller.table.instance.reloadData(function(){}, false);
			});
		}
		
}]);
