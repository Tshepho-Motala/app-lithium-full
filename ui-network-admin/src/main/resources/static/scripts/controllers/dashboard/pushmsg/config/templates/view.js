'use strict';

angular.module('lithium')
	.controller('PushMsgTemplate', ["template", "PushMsgTemplateRest", "rest-pushmsg", "domainName", "notify", "$translate", "$uibModal", "$scope", "$state", "$q", "errors",
	function(template, pmTemplateRest, pmRest, domainName, notify, $translate, $uibModal, $scope, $state, $q, errors) {
		var controller = this;
		controller.model = template;
		
//		controller.test = function() {
//			pmRest.test(domainName, template.name, ['luckybetz/riaans1']).then(function(list) {
//				console.log(list);
//			});
//		}
		
		controller.test = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/pushmsg/config/templates/test.html',
				controller: 'TemplateTest',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					template: function () {
						return template;
					},
					domainName: function () {
						return domainName;
					},
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/pushmsg/config/templates/test.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function() {
			});
		}
		
		controller.changelogs = {
			domainName: domainName,
			entityId: template.id,
			restService: pmTemplateRest,
			reload: 0
		};
	}
]);