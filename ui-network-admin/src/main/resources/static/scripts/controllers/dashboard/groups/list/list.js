'use strict';

angular.module('lithium').controller(
	'groupListController', [
		"domain",
		"$scope",
		"$state",
		"$translate",
		"$http",
		"notify",
		"rest-group", "errors", "bsLoadingOverlayService",
		function(domain, $scope, $state, $translate, $http, notify, restGroup, errors, bsLoadingOverlayService) {
			var controller = this;
			$translate('UI_NETWORK_ADMIN.GROUPS.LIST.TITLE', {domainName:domain.dn}).then(function(title) {
				$scope.$parent.title = title;
			});
			$scope.$parent.description = 'UI_NETWORK_ADMIN.GROUPS.LIST.DESCRIPTION';
			$scope.domainName = $state.params.domainName;
			$scope.referenceId = 'groups-list-overlay';
			
			controller.list = function() {
				bsLoadingOverlayService.start({referenceId:$scope.referenceId});
				console.log("start");
				restGroup.list($scope.domainName).then(function(groups) {
					controller.groups = groups;
					return groups;
				}).catch(
					errors.catch("UI_NETWORK_ADMIN.GROUPS.ERRORS.LIST", false)
				).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
				});
			}
			controller.list();
			
			controller.changeEnabled = function(id, enabled) {
				bsLoadingOverlayService.start({referenceId:$scope.referenceId});
				restGroup.enabled($scope.domainName, id, enabled).then(function() {
					controller.list();
					notify.success("UI_NETWORK_ADMIN.GROUP.SAVE.SUCCESS");
				}).catch(
					errors.catch("UI_NETWORK_ADMIN.GROUP.SAVE.FAIL", false)
				).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
				});
			}
			controller.remove = function(id) {

				$translate('UI_NETWORK_ADMIN.GROUP.DELETE.CONFIRM').then(function(response) {
					if (window.confirm(response)) {
						restGroup.remove($scope.domainName, id).then(function() {
							controller.list();
							notify.success("UI_NETWORK_ADMIN.GROUP.DELETE.SUCCESS");
						}).catch(
							errors.catch("UI_NETWORK_ADMIN.GROUP.DELETE.FAIL", false)
						);
					}
				}).catch(function() {
					notify.error('UI_NETWORK_ADMIN.GROUP.DELETE.FAIL');
				});
			}
		}
	]
);