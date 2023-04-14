'use strict';

angular.module('lithium').controller('AccessControlViewController', ['list', 'accessControlRest', '$uibModal', 'notify', '$translate', '$dt', 'DTOptionsBuilder', '$scope', 
function (list, accessControlRest, $uibModal, notify, $translate, $dt, DTOptionsBuilder, $scope) {
	var controller = this;
	controller.list = list;
	
	controller.addModal = function () {
		var modalInstance = $uibModal.open({
			animation: true,
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			backdrop: 'static',
			templateUrl: 'scripts/controllers/dashboard/accesscontrol/lists/addlistvalue/addlistvalue.html',
			controller: 'AccessControlAddListValueModal',
			controllerAs: 'controller',
			size: 'md cascading-modal',
			resolve: {
				list: function() { return controller.list },
				loadMyFiles: function($ocLazyLoad) {
					return $ocLazyLoad.load({
						name:'lithium',
						files: [ 'scripts/controllers/dashboard/accesscontrol/lists/addlistvalue/addlistvalue.js' ]
					})
				}
			}
		});
		
		modalInstance.result.then(function (list) {
			if (list._status === 0) {
				controller.list = list;
				controller.accessControlListTable.instance.reloadData(function(){}, false);
			}
		});
	};
	
	var baseUrl = 'services/service-access/list/'+controller.list.id+'/values/table';
	controller.accessControlListTable = $dt.builder()
	.column($dt.column('data').withTitle("Value"))
	.column($dt.columnformatdatetime('dateAdded').withTitle("Date Added"))
	.column($dt.linkscolumn("", [{ permission: "accesscontrol_add,accesscontrol_edit", permissionType:"any", permissionDomain:list.domain.name, title: "<span class=\"fa fa-times\"></span>", css: "pull-right badge bg-red", click: function(data) { controller.removeListValue(data); } }]))
	.options(baseUrl)
	.order([0, 'desc'])
	.build();
	
	controller.removeListValue = function(value) {
		accessControlRest.removeListValue(controller.list.id, value.id).then(function(list) {
			if (list._status === 0) {
				controller.list = list;
				controller.accessControlListTable.instance.reloadData(function(){}, false);
			}
		});
	}
	
	controller.toggleEnable = function() {
		accessControlRest.toggleEnable(controller.list.id).then(function(list) {
			if (list._status === 0) {
				controller.list = list;
				controller.accessControlListTable.instance.reloadData(function(){}, false);
			}
		});
	}
	
	controller.changelogs = {
		domainName: list.domain.name,
		entityId: list.id,
		restService: accessControlRest,
		reload: 0
	}
}]);
