'use strict';

angular.module('lithium').controller('PushMsgUsers', ["domainName", "$translate", "$log", "$dt", "$state", "$rootScope", "$uibModal",
	function(domainName, $translate, $log, $dt, $state, $rootScope, $uibModal) {
		var controller = this;
		var baseUrl = "services/service-pushmsg/"+ domainName +"/pushmsgusers/table";
		
		controller.table = $dt.builder()
		.column($dt.column('guid').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.USERS.USER")))
		.column($dt.columnsize('externalUsers').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.USERS.DEVICE")))
		.column($dt.linkscolumn("", [{ title: "GLOBAL.ACTION.VIEW", click: function(data) { controller.viewDetails(data.guid) } }]))
//			.column($dt.labelcolumn($translate("UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.ENABLED.TABLE"), [{lclass: function(data) { return (data.enabled === true)?"default":"danger"; }, text: function(data) { return (data.enabled === true)?"GLOBAL.FIELDS.ENABLED":"GLOBAL.FIELDS.DISABLED"; }, uppercase:true }]))
		.options(baseUrl)
		.build();
		
		controller.viewDetails = function(guid) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/pushmsg/config/users/details.html',
				controller: 'PushMsgUserDetails',
				controllerAs: 'controller',
				size: 'lg cascading-modal',
				backdrop: 'static',
				resolve: {
					guid: function () {
						return guid;
					},
					domainName: function () {
						return domainName;
					},
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/pushmsg/config/users/details.js'
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