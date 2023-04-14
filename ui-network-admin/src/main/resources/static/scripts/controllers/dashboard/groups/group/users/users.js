'use strict';

angular.module('lithium')
	.controller('GroupViewUsers', ["group", "$uibModal", "Restangular", "notify", "$dt", "$translate", "$log", "$scope", "$state", "$stateParams", "$http", "rest-group", 
	function(group, $uibModal, Restangular, notify, $dt, $translate, $log, $scope, $state, $stateParams, $http, restGroup) {
		var controller = this;
		$scope.domainName = $state.params.domainName;
		$scope.groupId = $state.params.groupId;
		$state.params.groupName = group.name;
		
		var baseUrl = "services/service-user/domain/"+$scope.domainName+"/group/"+$scope.groupId+"/users/table?1=1";
		controller.usersTable = $dt.builder()
		.column($dt.column('username').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.USERNAME.NAME")))
//		.column($dt.linkscolumn("", [{ title: "GLOBAL.ACTION.OPEN", href: function(data) { return $state.href("dashboard.user", {id:data.id}) } }]))
		.column($dt.column('domain.name').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.DOMAIN.NAME")))
		.column($dt.column('firstName').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.FIRSTNAME.NAME")))
		.column($dt.column('lastName').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.LASTNAME.NAME")))
		.column($dt.linkscolumn("", [{ permission: "GROUP_USERS_REMOVE", permissionType:"any", permissionDomain:$scope.domainName, title: "<span class=\"fa fa-times\"></span>", css: "btn btn-danger", click: function(data) { controller.removeUser(data.id); } }]))
		.options(baseUrl, null)
		.build();
		
		controller.userAddModal = function () {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/groups/group/users/add.html',
				controller: 'GroupUserAddModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'lg',
				resolve: {
					domainName: function () {
						return $scope.domainName;
					},
					items: function () {
						return controller.items;
					}
				}
			});
			
			modalInstance.result.then(function (selectedItem) {
				Restangular.all('services/service-user/domain/'+$scope.domainName+'/group/'+$scope.groupId+'/users/add/'+selectedItem).post().then(function() {
					notify.success("UI_NETWORK_ADMIN.GROUP.SAVE.SUCCESS");
					controller.usersTable.instance.reloadData(function(){}, false);
				}, function(response) {
					notify.warning("Could not add user to group.");
				});
			}, function () {
//				$log.info('Modal dismissed at: ' + new Date());
			});
		};
		
		controller.removeUser = function(id) {
			Restangular.all('services/service-user/domain/'+$scope.domainName+'/group/'+$scope.groupId+'/users/remove/'+id).post().then(function() {
				notify.success("UI_NETWORK_ADMIN.GROUP.SAVE.SUCCESS");
				controller.usersTable.instance.reloadData(function(){}, false);
			}, function(response) {
				notify.warning("Could not add user to group.");
			});
		}
		
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
		
		controller.save = function() {
			if (controller.form.$valid) {
				Restangular.all('services/service-user/domain/'+$scope.domainName+'/group/'+$scope.groupId).post(controller.model).then(function() {
					notify.success("UI_NETWORK_ADMIN.GROUP.SAVE.SUCCESS");
					$scope.dataMaster = angular.copy(controller.model);
				}, function(response) {
					notify.warning("Could not save the new group.");
				});
			}
		}
	}
]).controller('GroupUserAddModal', function ($uibModalInstance, $http, domainName) {
	var controller = this;
	
	controller.searchUsers = function(val) {
		return $http.get('services/service-user/'+domainName+'/users', {
			params: {
				search: val
			}
		}).then(function(response) {
			return response.data.map(function(item) {
				return item.username;
			});
		});
	};
	
	controller.ok = function() {
		$uibModalInstance.close(controller.asyncSelected);
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
});
