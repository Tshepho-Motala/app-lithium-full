'use strict';

angular.module('lithium')
	.controller('PlayerCommentsController', ["domain", "user", "UserRest", "$translate", "$uibModal", "notify", "$log", "$dt", "$state", "$rootScope", "DTOptionsBuilder",
	function(domain, user, UserRest, $translate, $uibModal, notify, $log, $dt, $state, $rootScope, DTOptionsBuilder) {
		var controller = this;
		var baseUrl=  "services/service-user/"+user.domain.name+"/comments/"+user.id+"/table";
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [0, 'desc']);
		controller.commentsTable = $dt.builder()
		.column($dt.columnformatdate('changeDate').withTitle($translate("UI_NETWORK_ADMIN.COMMENTS.COMMENTED_ON")))
		.column($dt.column('authorGuid').withTitle($translate("UI_NETWORK_ADMIN.COMMENTS.COMMENT_BY")))
		.column($dt.column('comments').withTitle($translate("UI_NETWORK_ADMIN.COMMENTS.COMMENT")))
		.options(baseUrl, null, dtOptions, null)
		.build();
		
		controller.addModal = function() {
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
					domainName: function() { return user.domain.name; },
					entityId: function() { return user.id; },
					restService: function() { return UserRest; },
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [ 'scripts/directives/comment/addcomment.js' ]
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				notify.success("Comment added successfully");
				$state.reload();
			});
		};
	}
]);
