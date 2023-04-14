'use strict'

angular.module('lithium').controller('DomainSettingsViewController', ['domain', 'domainRevision', '$translate', 'notify', '$uibModal', '$state', 'rest-domain','EcosysRest',
	function(domain, domainRevision, $translate, notify, $uibModal, $state, domainRest, EcosysRest) {
		var controller = this;
		
		controller.domain = domain;
		console.log(controller.domain);
		controller.domainRevision = domainRevision;
		controller.rootDomainName = null;
		controller.rootDomainSetting = [];

		EcosysRest.ecosystemRelationshiplistByDomainName(domain.name).then(function (response) {
			if (angular.isDefined(response)) {
				controller.ecosystemRelationshipList = response.plain();
				for (var i = 0; i < controller.ecosystemRelationshipList.length; i++) {
					if (controller.ecosystemRelationshipList[i].relationship.code === 'ECOSYSTEM_ROOT') {
						controller.rootDomainName = controller.ecosystemRelationshipList[i].domain.name;
					}
				}
			}

			if (controller.rootDomainName != null  && domain.name !=  controller.rootDomainSetting ) {
				domainRest.findCurrentDomainSettings(controller.rootDomainName).then(function (response) {
					for (var i = 0; i < response.plain().length; i++) {
						var lbv = response.plain()[i];
						controller.rootDomainSetting.push({label: lbv.labelValue.label.name, value: lbv.labelValue.value});
					}
				});
			}
		});



		controller.add = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/domains/domain/settings/add/add.html',
				controller: 'DomainSettingsAddModal',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					domainName: function() {
						return domain.name;
					},
					rootDomainName: function() {
						return controller.rootDomainName;
					},
					rootDomainSetting: function() {
						return controller.rootDomainSetting
					},
					domainRest: function () {
						return domainRest;
					},
					domainRevision: function () {
						return angular.copy(controller.domainRevision);
					}
				}
			});
			
			modalInstance.result.then(function(domainRevision) {
				 $state.go('dashboard.domains.domain.settings.view', {domainRevisionId: domainRevision.id}, {reload: true});
			});
		}
	}
]);
