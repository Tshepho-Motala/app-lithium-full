'use strict';

angular.module('lithium')
	.controller('DomainCurrenciesListController', ['domain', 'notify', '$scope', 'errors', '$dt', '$uibModal','$state', '$translate', 'rest-accounting-internal',
	function(domain, notify, $scope, errors, $dt, $uibModal, $state, $translate, accountingInternalRest) {
		var controller = this;
		var url = 'services/service-accounting-provider-internal/currencies/domain/'+domain.name+'/table?1=1';
		url += "&c="+domain.currency
		controller.domainCurrenciesTable = $dt.builder()
		.column($dt.column('currency.code').withTitle($translate('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.CODE')))
		.column($dt.column('name').withTitle($translate('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.NAME')))
		.column($dt.column('symbol').withTitle("Symbol"))
		.column(
			$dt.linkscolumn(
				"",
				[
					{ 
						permission: "domain_currencies_*",
						permissionType: "any",
						permissionDomain: function(data) {
							return data.domain.name;
						},
						title: "GLOBAL.ACTION.OPEN",
						href: function(data) {
							return $state.href("dashboard.domains.domain.currencies.currency", { id:data.id });
						}
					}
				]
			)
		)
		.column(
			$dt.labelcolumn(
				$translate('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.TYPE.TITLE'),
				[{lclass: function(data) {
					if (data.currency.real) {
						return 'success';
					} else {
						return 'primary'
					}
				},
				text: function(data) {
					if (data.currency.real) {
						return 'UI_NETWORK_ADMIN.DOMAIN.CURRENCY.TYPE.REAL';
					} else {
						return 'UI_NETWORK_ADMIN.DOMAIN.CURRENCY.TYPE.VIRTUAL';
					}
				},
				uppercase:true
				}]
			)
		)
		.column(
			$dt.labelcolumn(
				"",
				[{lclass: function(data) {
					if (data.isDefault) {
						return 'success';
					} else {
						return ''
					}
				},
				text: function(data) {
					if (data.isDefault) {
						return 'DEFAULT';
					} else {
						return '';
					}
				},
				uppercase:true
				}]
			)
		)
		.options(url)
		.order([0, 'asc'])
		.build();
		
		controller.add = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/domains/domain/currencies/add/add.html',
				controller: 'DomainCurrencyAddModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					domain: function () {
						return domain;
					}
				}
			});
			
			modalInstance.result.then(function(result) {
				controller.domainCurrenciesTable.instance.reloadData(function(){}, false);
			});
		}
		
		controller.setAsDefault = function(domainCurrency) {
			$translate('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.SETASDEFAULT.CONFIRM').then(function(response) {
				if (window.confirm(response)) {
					accountingInternalRest.setAsDefault(domain.name, domainCurrency.id).then(function(response) {
						if (response._status !== 0) {
							notify.error('Could not set as default');
						} else {
							notify.success('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.SETASDEFAULT.SUCCESS');
							controller.domainCurrenciesTable.instance.reloadData(function(){}, false);
						}
					}).catch(function() {
						errors.catch('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.SETASDEFAULT.ERROR', false);
					}).finally(function() {
					});
				}
			}).catch(function() {
				notify.error('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.SETASDEFAULT.ERROR');
			});
		}
		
		
		controller.deleteDomainCurrency = function(domainCurrency) {
			$translate('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.DELETE.CONFIRM').then(function(response) {
				if (window.confirm(response)) {
					accountingInternalRest.deleteDomainCurrency(domain.name, domainCurrency.id).then(function(response) {
						if (response._status !== 0) {
							notify.error('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.DELETE.ERROR');
						} else {
							notify.success('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.DELETE.SUCCESS');
							controller.domainCurrenciesTable.instance.reloadData(function(){}, false);
						}
					}).catch(function() {
						errors.catch('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.DELETE.ERROR', false);
					}).finally(function() {
					});
				}
			}).catch(function() {
				notify.error('UI_NETWORK_ADMIN.DOMAIN.CURRENCY.DELETE.ERROR');
			});
		}
	}
]);
