'use strict';

angular.module('lithium')
	.controller('EditPropertiesController', ["errors", "$filter", "rest-cashier", "notify", "domainMethod", "dmpId", "$uibModalInstance", 
	function(errors, $filter, cashierRest, notify, domainMethod, dmpId, $uibModalInstance) {
		var controller = this;
		
		controller.model = domainMethod;
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
		
		controller.findProperties = function() {
			cashierRest.domainMethodProcessorPropertiesNoDefaults(dmpId).then(function(props) {
				controller.properties = props.plain();
				angular.forEach(controller.properties, function(p) {
					if (p.id !== null) {
						p.override = true;
					} else {
						p.override = false;
					}
				});
				controller.properties = $filter('orderBy')(controller.properties, '+processorProperty.name');
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		}
		controller.findProperties();
		
		controller.override = function(p, override) {
			if (override === false) p.value = p.processorProperty.defaultValue;
		}
		
		controller.onSubmit = function() {
			cashierRest.domainMethodProcessorPropertiesSave(dmpId, controller.properties).then(function(props) {
				controller.properties = props.plain();
				notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.PROPERTIES.SAVESUCCESS");
				$uibModalInstance.close(controller.properties);
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		}
	}
]);