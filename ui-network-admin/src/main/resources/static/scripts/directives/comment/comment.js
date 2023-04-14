'use strict';

angular.module('lithium').directive('comment', function() {
	return {
		templateUrl:'scripts/directives/comment/comment.html',
		scope: {
			data: "="
		},
		restrict: 'E',
		replace: true,
		controller: ['$q', '$uibModal', '$scope', 'notify', 'errors', 'bsLoadingOverlayService',
		function($q, $uibModal, $scope, notify, errors, bsLoadingOverlayService) {
			$scope.referenceId = 'last-comment-overlay';
			$scope.loadLastComment = function() {
				if($scope.data.showLastCommentRole){
					bsLoadingOverlayService.start({referenceId:$scope.referenceId});
					$scope.data.restService.lastComment($scope.data.domainName, $scope.data.entityId).then(function(response) {
						$scope.data.lastComment = response;
					}).catch(
						//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.LASTCOMMENT", false)
					).finally(function () {
						bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
					});
				}
			};

			$scope.addComment = function() {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/directives/comment/addcomment.html',
					controller: 'AddCommentModal',
					controllerAs: 'controller',
					backdrop: 'static',
					size: 'md',
					resolve: {
						domainName: function() { return $scope.data.domainName; },
						entityId: function() { return $scope.data.entityId; },
						restService: function() { return $scope.data.restService; },
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: [ 'scripts/directives/comment/addcomment.js' ]
							})
						}
					}
				});

				modalInstance.result.then(function(response) {
					$scope.data.changelogs.reload += 1;
					$scope.loadLastComment();
					notify.success("Comment added successfully");
				});
			};

			$scope.loadLastComment();
		}]
	}
});
