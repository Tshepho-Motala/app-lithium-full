'use strict';

angular.module('lithium')
	.controller('SMSTemplate', ["template", "SMSTemplateRest", "domainName", "notify", "$translate", "$uibModal", "$scope", "$state", "$q", "errors",
	function(template, rest, domainName, notify, $translate, $uibModal, $scope, $state, $q, errors) {
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
				templateUrl: 'scripts/controllers/dashboard/sms/config/templates/test.html',
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
								'scripts/controllers/dashboard/sms/config/templates/test.js'
							]
						})
					}
				}
			});
		}
	}
]);