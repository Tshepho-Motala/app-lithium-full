'use strict';

angular.module('lithium')
	.controller('GroupViewRoles', ["group", "$uibModal", "notify", "$rootScope", "$dt", "$translate", "$log", "$scope", "$state", "$stateParams", "$http", "rest-group", 
	function(group, $uibModal, notify, $rootScope, $dt, $translate, $log, $scope, $state, $stateParams, $http, restGroup) {
		var controller = this;
		$scope.domainName = $state.params.domainName;
		$scope.groupId = $state.params.groupId;
		$state.params.groupName = group.name;
		
		controller.getGrds = function() {
			restGroup.grds($scope.domainName, $scope.groupId).then(function(response) {
				controller.grds = response.domains;
			}, function(response) {
				controller.grds = [];
				notify.warning("Could not find group roles.");
			});
		}
		
		controller.removeRole = function(id) {
			//Restangular.all('services/service-user/domain/'+$scope.domainName+'/group/'+$scope.groupId+'/removeRole/'+id).post().then(function() {
			restGroup.removeRole($scope.domainName, $scope.groupId, id).then(function() {
				notify.success("UI_NETWORK_ADMIN.GROUP.SAVE.SUCCESS");
				controller.getGrds();
			}, function(response) {
				notify.warning("Could not save the new group.");
			});
		}
		
		controller.grdAddModal = function () {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/groups/group/roles/add.html',
				controller: 'GRDAddModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'lg',
				resolve: {
					items: function () {
						return controller.items;
					},
					domainName: function () {
						return $scope.domainName;
					},
					group: function () {
						return group;
					}
				}
			});
			modalInstance.result.then(function (response) {
				if (response != undefined) {
//					Restangular.all('services/service-user/domain/'+$scope.domainName+'/group/'+$scope.groupId+'/addrole').all(response.domainid).post(response.roles).then(function() {
					restGroup.addRole($scope.domainName, $scope.groupId, response.domainid, response.roles).then(function() {
						console.log(response);
						controller.getGrds();
						notify.success("UI_NETWORK_ADMIN.GROUP.SAVE.SUCCESS");
					}, function(response) {
						notify.warning("Could not save the new group.");
					});
				}
			}, function () {
				controller.getGrds();
			});
		};
		
		controller.edit = function() {
			controller.options.formState.readOnly = false;
			controller.fields_personal[1].templateOptions.focus = true;
		}
		
		controller.cancel = function() {
			controller.reset();
			controller.options.formState.readOnly = true;
		}
		
		controller.reset = function() {
			controller.model = angular.copy(controller.modelOriginal);
		}
		
		controller.changeDescending = function(id, value) {
			restGroup.grdUpdateDescending($scope.domainName, $scope.groupId, id, value).then(function() {
				notify.success("UI_NETWORK_ADMIN.GROUP.UPDATE.SUCCESS");
			}, function(response) {
				notify.warning("UI_NETWORK_ADMIN.GROUP.UPDATE.FAIL");
			});
		}
		
		controller.changeSelfApplied = function(id, value) {
			restGroup.grdUpdateSelfApplied($scope.domainName, $scope.groupId, id, value).then(function() {
				notify.success("UI_NETWORK_ADMIN.GROUP.UPDATE.SUCCESS");
			}, function(response) {
				notify.warning("UI_NETWORK_ADMIN.GROUP.UPDATE.FAIL");
			});
		}
	}
]).controller('GRDAddModal', 
function ($uibModalInstance, $dt, $translate, $userService, notify, items, domainName, group) {
	var controller = this;
	controller.items = items;
	controller.domainName = domainName;
	controller.groupId = group.id;
	controller.grdbasic = {};
	
	///////formly attempt
	controller.options = {};
	controller.roles = [];
	controller.modalresult = [];
	controller.showSearch = false;
	
	controller.fields_personal = [{
		className: "col-xs-12",
		key: "domain.name",
		type: "ui-select-single",
		templateOptions: {
			label: "", description: "", placeholder: "", required: true, options: [],
			valueProp: 'name', labelProp: 'name', groupBy: 'superName', 
			optionsAttr: 'ui-options', ngOptions: 'ui-options'
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.GROUPS.ROLES.FIELDS.DOMAIN.NAME" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GROUPS.ROLES.FIELDS.DOMAIN.SELECT" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.GROUPS.ROLES.FIELDS.DOMAIN.DESCRIPTION" | translate'
		},
		controller: ['$scope', '$security', 'rest-group', function($scope, $security, restGroup) {
			$scope.to.options = $security.domains(domainName, true);
			$scope.$watch('model.domain.name', function (newValue, oldValue, theScope) {
				if (newValue != undefined) {
					controller.modalresult.domainid = newValue;
					restGroup.roles(controller.domainName, controller.groupId, newValue).then(function(response) {
						controller.roles = response;
						if (response.length > 0) {
							controller.showSearch = true;
						} else {
							controller.showSearch = false;
						}
					}, function(response) {
						controller.roles = [];
						controller.showSearch = false;
						notify.error("Could not find group roles.");
					});
				} else {
					controller.modalresult.domainid = undefined;
					controller.roles = [];
					controller.showSearch = false;
				}
			});
		}]
	}];
	
	controller.fields = [{
		className: "row v-reset-row ",
		fieldGroup: [{
			className: "col-md-12",
			fieldGroup: controller.fields_personal
		}]
	}];
	
	controller.changeCatSelfApplied = function(catId, value) {
		angular.forEach(controller.roles, function(r) {
			if (r.category.id === catId) {
				r.self = value;
			}
		});
	}
	controller.changeCatDescending = function(catId, value) {
		angular.forEach(controller.roles, function(r) {
			if (r.category.id === catId) {
				r.child = value;
			}
		});
	}
	
	controller.addRoles = function() {
		if (controller.roles.length > 0) {
			notify.success("UI_NETWORK_ADMIN.GROUP.SAVE.SUCCESS");
			controller.modalresult.roles = controller.roles.plain();
			$uibModalInstance.close(controller.modalresult);
		} else {
			$uibModalInstance.close();
		}
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
	
	
});
