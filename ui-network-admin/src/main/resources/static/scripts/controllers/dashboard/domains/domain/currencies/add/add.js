'use strict';

angular.module('lithium').controller('DomainCurrencyAddModal', ['$uibModalInstance', 'domain', 'notify', 'rest-accounting-internal', 'errors',
function ($uibModalInstance, domain, notify, accountingInternalRest, errors) {
	var controller = this;
	
	controller.model = {code: ""};
	
	controller.step = 1;
	
	controller.next = function() {
		controller.model.code = controller.model.code.toUpperCase();
		controller.model.name = controller.model.code;
		accountingInternalRest.findCurrencyByCode(controller.model.code).then(function(response) {
			var plain = response.plain();
			if (plain.length === 0) {
				controller.step += 1;
				return;
			}
			controller.dbcurrency = plain;
			controller.model.code = controller.dbcurrency.code;
			controller.model.name = controller.dbcurrency.name;
			controller.step += 1;
		}).catch(function() {
			errors.catch('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.ADD.ERROR', false);
		}).finally(function(){
		});
	}
	
	controller.back = function() {
		controller.step -= 1;
		controller.dbcurrency = null;
		controller.model.code = "";
		controller.model.name = "";
	}
	
	controller.save = function() {
		controller.model.name = controller.model.name.toUpperCase();
		accountingInternalRest.saveDomainCurrency(domain.name, controller.model).then(function(response) {
			if (response._status !== 0) {
				notify.error(response._message);
			} else {
				notify.success('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.ADD.SUCCESS');
			}
			$uibModalInstance.close(response.plain());
		}).catch(function() {
			errors.catch('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.ADD.ERROR', false);
		}).finally(function() {
		});
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);

