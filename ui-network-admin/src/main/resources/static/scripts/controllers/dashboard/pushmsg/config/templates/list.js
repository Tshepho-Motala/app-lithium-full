'use strict';

angular.module('lithium').controller('PushMsgTemplates', ["domainName", "$translate", "$log", "$dt", "$state", "$rootScope", "$uibModal",
	function(domainName, $translate, $log, $dt, $state, $rootScope, $uibModal) {
		var controller = this;
		var baseUrl = "services/service-pushmsg/"+ domainName +"/pushmsgtemplates/table";
		
		controller.table = $dt.builder()
			.column($dt.column('name').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.NAME.NAME")))
			.column($dt.column('current.providerTemplateId').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.PROVIDERTEMPLATEID.NAME")))
			.column($dt.linkscolumn("", [{ title: "GLOBAL.ACTION.OPEN", href: function(data) { return $state.href("dashboard.pushmsg.config.templates.view", {id: data.id, domainName: domainName}) } }]))
			.column($dt.labelcolumn($translate("UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.ENABLED.TABLE"), [{lclass: function(data) { return (data.enabled === true)?"default":"danger"; }, text: function(data) { return (data.enabled === true)?"GLOBAL.FIELDS.ENABLED":"GLOBAL.FIELDS.DISABLED"; }, uppercase:true }]))
			.options(baseUrl)
			.build();
		
		controller.addTemplate = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/pushmsg/config/templates/add.html',
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
								'scripts/controllers/dashboard/pushmsg/config/templates/add.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(template) {
				controller.table.instance.reloadData(function(){}, false);
			});
		}
	}
]);