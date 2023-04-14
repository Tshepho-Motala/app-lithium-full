'use strict';

angular.module('lithium')
	.controller('EmailTemplates', ["domainName", "$translate", "$log", "$dt", "$state", "$rootScope", "$uibModal", function(domainName, $translate, $log, $dt, $state, $rootScope, $uibModal) {
		
		var controller = this;
		var baseUrl = "services/service-mail/"+ domainName +"/emailtemplates/table";
		
		controller.rowClickHandler = function(data) {
			$state.go('dashboard.mail.config.templates.view', {
				id: data.id,
				domainName: domainName
			});
		}
		
		controller.table = $dt.builder()
			.column($dt.column('name').withTitle($translate("UI_NETWORK_ADMIN.EMAILTEMPLATE.FIELDS.NAME.NAME")))
			.column($dt.column('lang').withTitle($translate("UI_NETWORK_ADMIN.EMAILTEMPLATE.FIELDS.LANG.NAME")))
			.column($dt.column('enabled').withTitle($translate("UI_NETWORK_ADMIN.EMAILTEMPLATE.FIELDS.ENABLED.NAME")))
			.column($dt.column('current.subject').withTitle($translate("UI_NETWORK_ADMIN.EMAILTEMPLATE.FIELDS.SUBJECT.NAME")))
			.column($dt.columnformatdatetime('updatedOn').withTitle($translate("UI_NETWORK_ADMIN.EMAILTEMPLATE.FIELDS.UPDATED_ON.NAME")))
			.options(baseUrl, controller.rowClickHandler)
			.build();
		
		controller.addTemplate = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/mail/config/templates/add.html',
				controller: 'TemplateAdd',
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
								'scripts/controllers/dashboard/mail/config/templates/add.js'
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