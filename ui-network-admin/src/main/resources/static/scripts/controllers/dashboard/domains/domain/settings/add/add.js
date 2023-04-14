'use strict';

angular.module('lithium').controller('DomainSettingsAddModal', ['domainName', 'domainRevision', '$uibModalInstance', 'notify', 'errors', 'rest-domain', 'rootDomainName','rootDomainSetting',
function (domainName, domainRevision, $uibModalInstance, notify, errors, domainRest, rootDomainName, rootDomainSetting) {
	var controller = this;
	
	console.log("domainRevision", domainRevision);
	
	controller.adding = !!domainRevision;
	controller.settings = [];
	controller.updateIndex = null;
	if (controller.adding
		&& !!domainRevision.labelValueList) {
		for (var i = 0; i < domainRevision.labelValueList.length; i++) {
			var lbv = domainRevision.labelValueList[i];
			console.log(lbv);
			controller.settings.push({label: lbv.labelValue.label.name, value: lbv.labelValue.value});
		}
	}
	
	controller.addSetting = function() {
		controller.settings.push({label: null, value: null});
	}
	
	controller.removeSetting = function($index) {
		controller.updateIndex = $index;
		controller.settings.splice($index, 1);
	}

	controller.removeRootDomainSetting = function(setting) {
		var removeIndex = rootDomainSetting.map(function(item) { return item.label; }).indexOf(setting);
		if (removeIndex > -1) {
			rootDomainSetting.splice(removeIndex, 1);
		}
	}

	controller.addRootDomainSetting = function () {
		domainRest.addDomainSettings(rootDomainName, rootDomainSetting).then(function(response) {
			if (response._status === 0) {
				// Do nothing
			} else {
				notify.warning(response._message);
			}
		}).catch(function(error) {
			notify.error('UI_NETWORK_ADMIN.DOMAIN.ROOT_SETTING.ERROR');
			errors.catch('', false)(error)
		});
	}

	controller.save = function() {
		domainRest.addDomainSettings(domainName, controller.settings).then(function(response) {
			console.log("Settings:",controller.settings);
			if (response._status === 0) {
				notify.success('UI_NETWORK_ADMIN.DOMAIN.SETTING.SUCCESS');
				$uibModalInstance.close(response);
			} else {
				notify.warning(response._message);
			}
		}).catch(function(error) {
			notify.error('UI_NETWORK_ADMIN.DOMAIN.SETTING.ERROR');
			errors.catch('', false)(error)
		});

		if ((rootDomainName != null) && (rootDomainName != domainName) &&
			controller.settings[controller.settings.length - 1].label === 'leaderboard_push_domain_link_opt_out') {
			var rootDomainLbv = controller.settings[controller.settings.length - 1];
			controller.removeRootDomainSetting('leaderboard_push_domain_link_opt_out');
			rootDomainSetting.push({label: rootDomainLbv.label, value: rootDomainLbv.value});
			controller.addRootDomainSetting();
		}
		if (domainRevision.labelValueList[controller.updateIndex] != undefined) {
			if ((rootDomainName != null) && (rootDomainName != domainName) &&
				domainRevision.labelValueList[controller.updateIndex].label.name === 'leaderboard_push_domain_link_opt_out') {
				controller.removeRootDomainSetting('leaderboard_push_domain_link_opt_out');
				controller.addRootDomainSetting();
			}
		}
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);

