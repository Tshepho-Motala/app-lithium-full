'use strict';

angular.module('lithium')
	.controller('TemplatesListController', ["domainName", "$translate", "$log", "$dt", "$state", "$rootScope", "$uibModal", function(domainName, $translate, $log, $dt, $state, $rootScope, $uibModal) {
		
		var controller = this;
		controller.domainName = domainName;
		var baseUrl = "services/service-domain/"+ domainName +"/templates/table";
		
		controller.rowClickHandler = function(data) {
			$state.go('dashboard.templates.domain.view', {
				id: data.id,
				domainName: domainName
			});
		}
		
		controller.table = $dt.builder()
			.column($dt.column('name').withTitle($translate("UI_NETWORK_ADMIN.TEMPLATES.FIELDS.NAME.NAME")))
			.column($dt.column('lang').withTitle($translate("UI_NETWORK_ADMIN.TEMPLATES.FIELDS.LANG.NAME")))
			.column($dt.column('enabled').withTitle($translate("UI_NETWORK_ADMIN.TEMPLATES.FIELDS.ENABLED.NAME")))
			.column($dt.column('current.description').withTitle($translate("UI_NETWORK_ADMIN.TEMPLATES.FIELDS.DESCRIPTION.NAME")))
			.options(baseUrl, controller.rowClickHandler)
			.build();
		
		controller.addTemplate = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/templates/add/add.html',
				controller: 'TemplatesAddController',
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
								'scripts/controllers/dashboard/templates/add/add.js'
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
