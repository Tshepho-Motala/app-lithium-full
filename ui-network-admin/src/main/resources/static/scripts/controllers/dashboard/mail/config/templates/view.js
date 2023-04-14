'use strict';

angular.module('lithium')
	.controller('EmailTemplate', ["template", "EmailTemplateRest", "domainName", "notify", "$translate", "$log", "$scope", "$state", "$q", "errors", "$uibModal",
	function(template, rest, domainName, notify, $translate, $log, $scope, $state, $q, errors, $uibModal) {
		var controller = this;
		controller.model = template;
		controller.changelogs = {
			domainName: domainName,
			entityId: template.id,
			restService: rest,
			reload: 0
		}
		
		controller.test = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/mail/config/templates/test.html',
				controller: 'TemplateTest',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					template: function () {
						return template;
					},
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/mail/config/templates/test.js'
							]
						})
					}
				}
			});
		}
	}
]);