'use strict';

angular.module('lithium')
	.controller('EditPropertiesController', ["errors", "$filter", "rest-sms", "notify", "domainProvider", "$uibModalInstance", 
	function(errors, $filter, smsRest, notify, domainProvider, $uibModalInstance) {
		var controller = this;
		
		controller.model = domainProvider;
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
		
		controller.findProperties = function() {
			smsRest.domainProviderPropertiesNoDefaults(domainProvider.id).then(function(props) {
				controller.properties = props.plain();
				angular.forEach(controller.properties, function(p) {
					if (p.id !== null) {
						p.override = true;
					} else {
						p.override = false;
					}
				});
				controller.properties = $filter('orderBy')(controller.properties, '+providerProperty.name');
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		}
		controller.findProperties();
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			angular.forEach(controller.properties, function(p) {
				if ((p.override) && (p.id === null)) {
					p.id = -1;
				}
				if ((!p.override) && (p.id !== null)) {
					smsRest.domainProviderPropertyDelete(domainProvider.id, p.id).then(function(prop) {
						p = prop;
						notify.success("UI_NETWORK_ADMIN.SMS.PROVIDERS.PROPERTIES.DELETESUCCESS");
					}).catch(function(error) {
						errors.catch("", false)(error)
					});
				}
			});
			
			console.log(controller.properties)
			
			smsRest.domainProviderPropertiesSave(domainProvider.id, controller.properties).then(function(props) {
				controller.properties = props.plain();
				angular.forEach(controller.properties, function(p) {
					if (p.id !== null) {
						p.override = true;
					} else {
						p.override = false;
					}
				});
				controller.properties = $filter('orderBy')(controller.properties, '+providerProperty.name');
				notify.success("UI_NETWORK_ADMIN.SMS.PROVIDERS.PROPERTIES.SAVESUCCESS");
				$uibModalInstance.close(controller.properties);
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		}
	}
]);