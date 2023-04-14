'use strict';

angular.module('lithium')
	.controller('EditPropertiesController', ["errors", "$filter", "mailRest", "notify", "domainProvider", "$uibModalInstance", 
	function(errors, $filter, mailRest, notify, domainProvider, $uibModalInstance) {
		var controller = this;
		
		controller.model = domainProvider;
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
		
		controller.findProperties = function() {
			mailRest.domainProviderPropertiesNoDefaults(domainProvider.id).then(function(props) {
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
					mailRest.domainProviderPropertyDelete(domainProvider.id, p.id).then(function(prop) {
						p = prop;
						notify.success("UI_NETWORK_ADMIN.MAIL.PROVIDERS.PROPERTIES.DELETESUCCESS");
					}).catch(function(error) {
						errors.catch("", false)(error)
					});
				}
			});
			
			console.log(controller.properties)
			
			mailRest.domainProviderPropertiesSave(domainProvider.id, controller.properties).then(function(props) {
				controller.properties = props.plain();
				angular.forEach(controller.properties, function(p) {
					if (p.id !== null) {
						p.override = true;
					} else {
						p.override = false;
					}
				});
				controller.properties = $filter('orderBy')(controller.properties, '+providerProperty.name');
				notify.success("UI_NETWORK_ADMIN.MAIL.PROVIDERS.PROPERTIES.SAVESUCCESS");
				$uibModalInstance.close(controller.properties);
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		}
	}
]);