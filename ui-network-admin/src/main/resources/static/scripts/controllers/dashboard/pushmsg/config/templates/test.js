'use strict';

angular.module('lithium')
	.controller('TemplateTest', ["template", "domainName", "$uibModalInstance", "PushMsgTemplateRest", "notify", "errors", "rest-pushmsg",
	function(template, domainName, $uibModalInstance, rest, notify, errors, pmRest) {
		var controller = this;
		
		controller.model = template;
		
		controller.searchUsers = function(userGuid) {
			return rest.searchUser(domainName, userGuid).then(function(searchResult) {
				return searchResult.plain();
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		};
		
		controller.selectUser = function(user) {
			console.log(user);
		}
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			console.log(domainName, template.name, [controller.selectedUser]);
			pmRest.test(domainName, template.name, [controller.selectedUser]).then(function() {
				notify.success("UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.SUCCESS.ADD");
				$uibModalInstance.close();
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.ERRORS.ADD");
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);