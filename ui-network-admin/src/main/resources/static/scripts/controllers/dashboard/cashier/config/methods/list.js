'use strict';

angular.module('lithium')
.controller('CashierMethodsListController',
["rest-cashier", "UserRest", "$filter", "domainMethods", "$scope", "$stateParams", "$uibModal", "notify", "errors", "Lightbox", "rest-cashier-dmp", "rest-cashier-dm", 'rest-domain',
function(cashierRest, userRest, $filter, domainMethods, $scope, $stateParams, $uibModal, notify, errors, Lightbox, cashierDmpRest, cashierDmRest, domainRest) {
	var controller = this;
	controller.domain = $stateParams.domainName;

	if ($stateParams.type === 'deposit') {
		$scope.setDescription("UI_NETWORK_ADMIN.CASHIER.TAB.METHODS.DEPOSIT");
	} else {
		$scope.setDescription("UI_NETWORK_ADMIN.CASHIER.TAB.METHODS.WITHDRAWAL");
	}
	controller.domainMethods = domainMethods;
	controller.tab = [];
	controller.dmCollapsed = [];
	controller.dmpCollapsed = [];
	controller.processorOrderChanged = [];
	controller.legendCollapsed = true;
	controller.viewas = 'domain';
	controller.deposit_amount = 150;
	controller.currencySymbol = ''
	controller.tabs = [
		{ templateUrl:'scripts/controllers/dashboard/cashier/config/methods/feestab.html', type: 'fees', title: "UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.FEES.TITLE", roles: "ADMIN,CASHIER_CONFIG,CASHIER_CONFIG_VIEW" },
		{ templateUrl:'scripts/controllers/dashboard/cashier/config/methods/limitstab.html', type: 'limits', title: "UI_NETWORK_ADMIN.CASHIER.PROCESSORS.FIELDS.LIMITS.TITLE", roles: "ADMIN,CASHIER_CONFIG,CASHIER_CONFIG_VIEW" }
	];

	controller.changelogs = {
		domainName: $stateParams.domainName,
		entityId: $stateParams.type === 'deposit',
		restService: cashierDmRest,
		reload: 1
	}

	controller.setTab = function(tab, dmpId) {
		controller.tab[dmpId] = tab;
	}
	controller.openLightBox = function(src) {
		var image = [{
			'url': src
		}]
		Lightbox.openModal(image, 0);
	}

	controller.addMethod = function() {
		var modalInstance = $uibModal.open({
			animation: true,
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			templateUrl: 'scripts/controllers/dashboard/cashier/config/methods/addmethod.html',
			controller: 'addMethod',
			controllerAs: 'controller',
			size: 'md cascading-modal',
			backdrop: 'static',
			resolve: {
				type: function() {
					return $stateParams.type;
				},
				domainName: function() {
					return $stateParams.domainName;
				},
				loadMyFiles:function($ocLazyLoad) {
					return $ocLazyLoad.load({
						name:'lithium',
						files: [
							'scripts/controllers/dashboard/cashier/config/methods/addmethod.js'
						]
					})
				}
			}
		});

		modalInstance.result.then(function(dm) {
			controller.reload();
		});
	}

	controller.addProcessor = function(dm) {
		var modalInstance = $uibModal.open({
			animation: true,
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			templateUrl: 'scripts/controllers/dashboard/cashier/config/methods/addprocessor.html',
			controller: 'addProcessor',
			controllerAs: 'controller',
			size: 'md cascading-modal',
			backdrop: 'static',
			resolve: {
				type: function() {
					return $stateParams.type;
				},
				domainMethod: function() {
					return dm;
				},
				domainName: function() {
					return $stateParams.domainName;
				},
				loadMyFiles:function($ocLazyLoad) {
					return $ocLazyLoad.load({
						name:'lithium',
						files: [
							'scripts/controllers/dashboard/cashier/config/methods/addprocessor.js'
						]
					})
				}
			}
		});

		modalInstance.result.then(function(dmp) {
			controller.reload();
			controller.editProperties(dm, dmp.id)
		});
	}

	controller.override = function(type, dmp, dm) {
		switch (controller.viewas) {
			case 'user': {
				if (dmp.dmpu === null) {
					dmp.dmpu = {};
					dmp.dmpu.id = null;
					dmp.dmpu.user = {};
					dmp.dmpu.user.guid = controller.selectedUser.guid;
					dmp.dmpu.domainMethodProcessor = {};
					dmp.dmpu.domainMethodProcessor.id = dmp.id;
				}
				if ((angular.isUndefined(dmp.dmpu[type])) || (dmp.dmpu[type] === null)) {
					dmp.dmpu[type] = {};
					initLimitsOrFees(type, dmp.dmpu[type]);
				}
				break;
			}
			case 'profile': {
				if (dmp.dmpp === null) {
					dmp.dmpp = {};
					dmp.dmpp.id = null;
					dmp.dmpp.profile = {};
					dmp.dmpp.profile.id = controller.selectedProfile.id;
					dmp.dmpp.domainMethodProcessor = {};
					dmp.dmpp.domainMethodProcessor.id = dmp.id;
					if (dmp.dmpp[type] === null) {
						dmp.dmpp[type] = {};
						initLimitsOrFees(type, dmp.dmpp[type]);
					}
				}
				if ((angular.isUndefined(dmp.dmpp[type])) || (dmp.dmpp[type] === null)) {
					dmp.dmpp[type] = {};
					initLimitsOrFees(type, dmp.dmpp[type]);
				}
				break;
			}
			default: {
				if (dmp === null) dmp = {};
				dmp.enabled = true;
				dmp.weight = 0;
				dmp[type] = {};
				initLimitsOrFees(type, dmp[type]);
				if (dmp.domainLimits === null) dmp.domainLimits = {};
				initLimits(dmp.domainLimits);
			}
		}
	}
	function initLimitsOrFees(type, obj) {
		if (type === 'fees') {
			initFees(obj);
		} else if (type === 'limits') {
			initLimits(obj);
		}
	}
	function initLimits(obj) {
		obj.id = null;
		obj.minAmount = null;
		obj.maxAmount = null;
		obj.maxAmountDay = null;
		obj.maxAmountWeek = null;
		obj.maxAmountMonth = null;
		obj.maxTransactionsDay = null;
		obj.maxTransactionsWeek = null;
		obj.maxTransactionsMonth = null;
	}
	function initFees(obj) {
		obj.id = null;
		obj.flat = null;
		obj.percentage = null;
		obj.minimum = null;
		obj.strategy = '1';
	}

	controller.overrideSave = function(type, dmp, dm) {
		switch (controller.viewas) {
			case 'user': {
				if (dmp.dmpu.id === null) {
					cashierRest.domainMethodProcessorUserCreateOrUpdate(dmp.dmpu, type).then(function(p) {
						dmp.dmpu = p.plain();
						if (p.fees) {
							dm.hasDMPUFees = true;
						}
						if (p.limits) {
							dm.hasDMPULimits = true;
						}
						notify.success("UI_NETWORK_ADMIN.CASHIER.OVERRIDES.SUCCESS");
					}).catch(function(error) {
						errors.catch("", false)(error)
					});
				} else {
					cashierRest.domainMethodProcessorUserSave(dmp.dmpu, type).then(function(p) {
						dmp.dmpu[type] = p[type];
						if (p.fees) {
							dm.hasDMPUFees = true;
						}
						if (p.limits) {
							dm.hasDMPULimits = true;
						}
						notify.success("UI_NETWORK_ADMIN.CASHIER.OVERRIDES.SUCCESS");
					}).catch(function(error) {
						errors.catch("", false)(error)
					});
				}
				break;
			}
			case 'profile': {
				if (dmp.dmpp.id === null) {
					cashierRest.domainMethodProcessorProfileCreateOrUpdate(dmp.dmpp).then(function(p) {
						dmp.dmpp = p.plain();
						if (p.fees) {
							dm.hasDMPPFees = true;
						}
						if (p.limits) {
							dm.hasDMPPLimits = true;
						}
						notify.success("UI_NETWORK_ADMIN.CASHIER.OVERRIDES.SUCCESS");
					}).catch(function(error) {
						errors.catch("", false)(error)
					});
				} else {
					cashierRest.domainMethodProcessorProfileSave(dmp.dmpp, type).then(function(p) {
						dmp.dmpp[type] = p[type];
						if (p.fees) {
							dm.hasDMPPFees = true;
						}
						if (p.limits) {
							dm.hasDMPPLimits = true;
						}
						notify.success("UI_NETWORK_ADMIN.CASHIER.OVERRIDES.SUCCESS");
					}).catch(function(error) {
						errors.catch("", false)(error)
					});
				}
				break;
			}
			default: {
				cashierRest.domainMethodProcessorSave(dmp, type).then(function(p) {
					dmp[type] =  angular.copy(p[type]);
					if (dmp.fees) {
						dm.hasDMPFees = true;
						dmp.fees.strategy = dmp.fees.strategy == 1 ? '1' : '2';
					}
					if (dmp.limits) {
						dm.hasDMPLimits = true;
					}
					if (type === 'limits') {
						cashierRest.domainMethodProcessorSaveDL(dmp).then(function(pp) {
							console.log(pp.plain());
							dmp.domainLimits = pp.domainLimits;
							notify.success("UI_NETWORK_ADMIN.CASHIER.OVERRIDES.SUCCESS");
						});
					} else {
						notify.success("UI_NETWORK_ADMIN.CASHIER.OVERRIDES.SUCCESS");
					}
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}
		}
		dmp.changelog.reload++;
	}
	controller.overrideDelete = function(type, dmp, dm) {
		switch (controller.viewas) {
			case 'user': {
				if (dmp.dmpu[type].id !== null) {
					cashierRest.domainMethodProcessorUserDelete(dmp.dmpu, type).then(function() {
						if (!dmp.dmpu.fees) {
							dm.hasDMPUFees = false;
						}
						if (!dmp.dmpu.limits) {
							dm.hasDMPULimits = false;
						}
					}).catch(function(error) {
						errors.catch("", false)(error)
					});
				}
				dmp.dmpu[type] = null;
				break;
			}
			case 'profile': {
				if (dmp.dmpp[type].id !== null) {
					cashierRest.domainMethodProcessorProfileDelete(dmp.dmpp, type).then(function() {
						if (!dmp.dmpp.fees) {
							dm.hasDMPPFees = false;
						}
						if (!dmp.dmpp.limits) {
							dm.hasDMPPLimits = false;
						}
					}).catch(function(error) {
						errors.catch("", false)(error)
					});
				}
				dmp.dmpp[type] = null;
				break;
			}
			default: {
				cashierRest.domainMethodProcessorDelete(dmp, type).then(function() {
					dmp[type] = null;
					if (!dmp.fees) {
						dm.hasDMPFees = false;
					}
					if (!dmp.limits) {
						dm.hasDMPLimits = false;
					}
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}
		}
		dmp.changelog.reload++;
	}

	controller.deleteProcessor = function(dmp) {
		var modalInstance = $uibModal.open({
			animation: true,
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			templateUrl: 'scripts/controllers/dashboard/cashier/config/methods/deleteprocessor.html',
			controller: 'DeleteProcessorController',
			controllerAs: 'controller',
			size: 'md cascading-modal card-danger-shadow',
			backdrop: 'static',
			resolve: {
				dmp: function() {
					return angular.copy(dmp);
				},
				loadMyFiles:function($ocLazyLoad) {
					return $ocLazyLoad.load({
						name:'lithium',
						files: [
							'scripts/controllers/dashboard/cashier/config/methods/deleteprocessor.js'
						]
					})
				}
			}
		});

		modalInstance.result.then(function(dmp) {
			controller.reload();
		});
	}

	controller.deleteMethod = function(dm) {
		var modalInstance = $uibModal.open({
			animation: true,
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			templateUrl: 'scripts/controllers/dashboard/cashier/config/methods/deletemethod.html',
			controller: 'DeleteMethodController',
			controllerAs: 'controller',
			size: 'md cascading-modal card-danger-shadow',
			backdrop: 'static',
			resolve: {
				dm: function() {
					return angular.copy(dm);
				},
				loadMyFiles:function($ocLazyLoad) {
					return $ocLazyLoad.load({
						name:'lithium',
						files: [
							'scripts/controllers/dashboard/cashier/config/methods/deletemethod.js'
						]
					})
				}
			}
		});

		modalInstance.result.then(function(dmp) {
			controller.reload();
		});
	}

	controller.populateDomainMethodProcessors = function(domainMethod) {
		cashierRest.domainMethodProcessors(domainMethod.id).then(function(dmps) {
			domainMethod.domainMethodProcessors = dmps.plain();
			angular.forEach(domainMethod.domainMethodProcessors, function(dmp, $index) {
				controller.tab[dmp.id] = controller.tabs[0];
				controller.dmpCollapsed[dmp.id] = true;
				if (dmp.fees) {
					domainMethod.hasDMPFees = true;
				}
				if (dmp.limits) {
					domainMethod.hasDMPLimits = true;
				}
				if ((dmp.limits) && (!dmp.domainLimits)) {
					dmp.domainLimits = {};
					initLimits(dmp.domainLimits);
				}
				cashierRest.domainMethodProcessorAccounting(dmp.id).then(function(list) {
					dmp.accountingDay = list.plain()[0]['day'];
					dmp.accountingWeek = list.plain()[0]['week'];
					dmp.accountingMonth = list.plain()[0]['month'];
					dmp.accountingLastMonth = list.plain()[0]['lastmonth'];
				});
				dmp.changelog = {domainName: $stateParams.domainName, entityId: dmp.id, restService: cashierDmpRest, reload: 0, collapsed: false};
			});
		}).catch(function(error) {
			errors.catch("", false)(error)
		});
	}

	controller.exampleFees = function(dmp) {
		controller.exampleFees(dmp, controller.deposit_amount);
	}

	controller.exampleFees = function(dmp, amount) {
		if (!amount) amount = controller.deposit_amount;
		dmp.fe = {};
		switch (controller.viewas) {
			case 'user': {
				if (dmp.fees) {
					dmp.fe.perc= dmp.dmpp.fees.percentage;
					dmp.fe.perc_amount = Big(amount).times(Big(dmp.dmpu.fees.percentage).div(100)).round(2, 1).toString();
					dmp.fe.flatDec = (!dmp.dmpu.fees.flatDec)?Big(0).toString():dmp.dmpu.fees.flatDec;
					dmp.fe.minimumDec = (!dmp.dmpu.fees.minimumDec)?Big(0).toString():dmp.dmpu.fees.minimumDec;
					dmp.fe.fee_amount = Big(dmp.fe.flatDec).plus(Big(dmp.fe.perc_amount)).toString();
					if (dmp.fe.minimumDec >= dmp.fe.fee_amount) {
						dmp.fe.total_amount = Big(amount).minus(Big(dmp.fe.minimumDec)).toString();
					} else {
						dmp.fe.total_amount = Big(amount).minus(Big(dmp.fe.fee_amount)).toString();
					}
				}
				break;
			}
			case 'profile': {
				if (dmp.fees) {
					dmp.fe.perc = dmp.dmpp.fees.percentage;
					dmp.fe.perc_amount = Big(amount).times(Big(dmp.dmpp.fees.percentage).div(100)).round(2, 1).toString();
					dmp.fe.flatDec = (!dmp.dmpp.fees.flatDec)?Big(0).toString():dmp.dmpp.fees.flatDec;
					dmp.fe.minimumDec = (!dmp.dmpp.fees.minimumDec)?Big(0).toString():dmp.dmpp.fees.minimumDec;
					dmp.fe.fee_amount = Big(dmp.fe.flatDec).plus(Big(dmp.fe.perc_amount)).toString();
					if (dmp.fe.minimumDec >= dmp.fe.fee_amount) {
						dmp.fe.total_amount = Big(amount).minus(Big(dmp.fe.minimumDec)).toString();
					} else {
						dmp.fe.total_amount = Big(amount).minus(Big(dmp.fe.fee_amount)).toString();
					}
				}
				break;
			}
			default: {
				if (dmp.fees) {
					dmp.fe.perc = (!dmp.fees.percentage)?Big(0).toString():dmp.fees.percentage;
					dmp.fe.perc_amount = Big(amount).times(Big(dmp.fe.perc).div(100)).round(2, 1).toString();
					dmp.fe.flatDec = (!dmp.fees.flatDec)?Big(0).toString():dmp.fees.flatDec;
					dmp.fe.minimumDec = (!dmp.fees.minimumDec)?Big(0).toString():dmp.fees.minimumDec;
					dmp.fe.fee_amount = Big(dmp.fe.flatDec).plus(Big(dmp.fe.perc_amount)).toString();
					if (dmp.fe.minimumDec >= dmp.fe.fee_amount) {
						dmp.fe.total_amount = Big(amount).minus(Big(dmp.fe.minimumDec)).toString();
					} else {
						dmp.fe.total_amount = Big(amount).minus(Big(dmp.fe.fee_amount)).toString();
					}
				}
				break;
			}
		}
	}

	angular.forEach(controller.domainMethods, function(dm) {
		controller.dmCollapsed[dm.id] = true;
		controller.populateDomainMethodProcessors(dm);
		cashierRest.domainMethodAccounting(dm.id).then(function(list) {
			dm.accountingDay = list.plain()[0]['day'];
			dm.accountingWeek = list.plain()[0]['week'];
			dm.accountingMonth = list.plain()[0]['month'];
			dm.accountingLastMonth = list.plain()[0]['lastmonth'];
		}).catch(function(error) {
			errors.catch("", false)(error)
		});
	});

	controller.collapseDM = function(domainMethodId) {
		controller.dmCollapsed[domainMethodId] = !controller.dmCollapsed[domainMethodId];
	}
	controller.collapseDMP = function(domainMethodProcessorId) {
		controller.dmpCollapsed[domainMethodProcessorId] = !controller.dmpCollapsed[domainMethodProcessorId];
	}

	controller.treeOptions = {
		accept: function(sourceNodeScope, destNodesScope, destIndex) {
			if (sourceNodeScope.$treeScope != destNodesScope.$treeScope) {
				controller.methodOrderChanged = false;
				return false;
			} else {
				controller.methodOrderChanged = true;
				notify.warning("UI_NETWORK_ADMIN.CASHIER.METHODS.EDIT.ORDERCHANGED", {ttl: 30000});
				return true;
			}
		}
	};

	controller.treeOptions2 = {
		accept: function(sourceNodeScope, destNodesScope, destIndex) {
			if (sourceNodeScope.$treeScope != destNodesScope.$treeScope) {
				if (!sourceNodeScope.$modelValue.domainMethod) {
					controller.processorOrderChanged[sourceNodeScope.$modelValue.id] = false;
				} else {
					controller.processorOrderChanged[sourceNodeScope.$modelValue.domainMethod.id] = false;
				}
				return false;
			} else {
				controller.processorOrderChanged[sourceNodeScope.$modelValue.domainMethod.id] = true;
				notify.warning("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.EDIT.ORDERCHANGED", {ttl: 30000});
				return true;
			}
		}
	};

	controller.reload = function() {
		controller.methodOrderChanged = false;
		controller.processorOrderChanged = [];
		cashierRest.domainMethods($stateParams.domainName, $stateParams.type).then(function(methods) {
			controller.domainMethods = methods.plain();
			angular.forEach(controller.domainMethods, function(dm) {
				controller.populateDomainMethodProcessors(dm);
				cashierRest.domainMethodAccounting(dm.id).then(function(list) {
					dm.accountingDay = list.plain()[0]['day'];
					dm.accountingWeek = list.plain()[0]['week'];
					dm.accountingMonth = list.plain()[0]['month'];
					dm.accountingLastMonth = list.plain()[0]['lastmonth'];
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			});
		}).catch(function(error) {
			errors.catch("", false)(error)
		});
	}

	controller.saveProcessorOrder = function(dm) {
		var total = dm.domainMethodProcessors.length;
		switch (controller.viewas) {
			case 'user': {
				var domainMethodProcessorsUser = [];
				angular.forEach(dm.domainMethodProcessors, function(dmp, $index) {
					if (dmp.dmpu === null) controller.override('', dmp, dm);
					dmp.dmpu.weight = ((total - $index)*0.1);
					domainMethodProcessorsUser.push(dmp.dmpu);
				});
				cashierRest.domainMethodProcessorUserUpdateMultiple(domainMethodProcessorsUser).then(function(dmpus) {
					controller.findProcessorsByUser(dm);
					notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.EDIT.ORDERCHANGEDSUCCESS");
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
				controller.methodOrderChanged = false;
				controller.processorOrderChanged = [];
				break;
			}
			case 'profile': {
				var domainMethodProcessorProfiles = [];
				angular.forEach(dm.domainMethodProcessors, function(dmp, $index) {
					if (dmp.dmpp === null) controller.override('', dmp, dm);
					dmp.dmpp.weight = ((total - $index)*0.1);
					domainMethodProcessorProfiles.push(dmp.dmpp);
				});
				cashierRest.domainMethodProcessorProfileUpdateMultiple(domainMethodProcessorProfiles).then(function(dmpps) {
					controller.findProcessorsByProfileSelected(dm);
					notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.EDIT.ORDERCHANGEDSUCCESS");
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
				controller.methodOrderChanged = false;
				controller.processorOrderChanged = [];
				break;
			}
			default: {
				angular.forEach(dm.domainMethodProcessors, function(dmp, $index) {
					dmp.weight = ((total - $index)*0.1);
				});
				cashierRest.domainMethodProcessorUpdateMultiple(dm.domainMethodProcessors).then(function(dmps) {
					dm.domainMethodProcessors = angular.extend(dm.domainMethodProcessors, dmps.plain());
					angular.forEach(dm.domainMethodProcessors, function(dmp) {
						dmp.changelog = {domainName: $stateParams.domainName, entityId: dmp.id, restService: cashierDmpRest, reload: 0, collapsed: false};
					});
					notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.EDIT.ORDERCHANGEDSUCCESS");
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
				controller.methodOrderChanged = false;
				controller.processorOrderChanged = [];
			}
		}
	}

	controller.saveMethodOrder = function() {
		switch (controller.viewas) {
			case 'user': {
				var domainMethodsUser = [];
				angular.forEach(controller.domainMethods, function(dm, $index) {
					dm.domainMethodUser.priority = $index;
					domainMethodsUser.push(dm.domainMethodUser);
				});

				cashierRest.domainMethodUserUpdateMultiple(domainMethodsUser).then(function(dmsuser) {
					controller.findDomainMethodUser();
					notify.success("UI_NETWORK_ADMIN.CASHIER.METHODS.EDIT.ORDERCHANGEDSUCCESS");
				}).catch(function(error) {
					errors.catch("", false)(error)
				});

				break;
			}
			case 'profile': {
				var domainMethodProfiles = [];
				angular.forEach(controller.domainMethods, function(dm, $index) {
					dm.domainMethodProfile.priority = $index;
					domainMethodProfiles.push(dm.domainMethodProfile);
				});
				cashierRest.domainMethodProfileUpdateMultiple(domainMethodProfiles).then(function(dmprofiles) {
					controller.findDomainMethodProfileSelected();
					notify.success("UI_NETWORK_ADMIN.CASHIER.METHODS.EDIT.ORDERCHANGEDSUCCESS");
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
				break;
			}
			default: {
				angular.forEach(controller.domainMethods, function(dm, $index) {
					dm.priority = $index;
				});
				cashierRest.domainMethodUpdateMultiple(controller.domainMethods).then(function(dms) {
					controller.domainMethods = dms.plain();
					angular.extend(controller.domainMethods, dms.plain());
					angular.forEach(controller.domainMethods, function(dm) {
						controller.populateDomainMethodProcessors(dm);
					});
					notify.success("UI_NETWORK_ADMIN.CASHIER.METHODS.EDIT.ORDERCHANGEDSUCCESS");
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}
		}
		controller.methodOrderChanged = false;
		controller.processorOrderChanged = [];
	}

	controller.toggleProcessor = function(dm, dmp) {
		switch (controller.viewas) {
			case 'user': {
				if (dmp.dmpu === null) controller.override('', dmp, dm);
				if (angular.isUndefined(dmp.dmpu.enabled)) {
					if ((dmp.dmpp === null) || (angular.isUndefined(dmp.dmpp.enabled))) {
						dmp.dmpu.enabled = dmp.enabled;
					} else {
						dmp.dmpu.enabled = dmp.dmpp.enabled;
					}
				}
				dmp.dmpu.enabled = !dmp.dmpu.enabled;
				cashierRest.domainMethodProcessorUserCreateOrUpdate(dmp.dmpu).then(function(p) {
					dmp.dmpu = p.plain();
					if (dmp.dmpu.enabled) {
						notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.EDIT.ENABLED");
					} else {
						notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.EDIT.DISABLED");
					}
				}).catch(function (error) {
					errors.catch("", false)(error)
				});
				break;
			}
			case 'profile': {
				if (dmp.dmpp === null) controller.override('', dmp, dm);
				if (angular.isUndefined(dmp.dmpp.enabled)) {
					dmp.dmpp.enabled = dmp.enabled;
				}
				dmp.dmpp.enabled = !dmp.dmpp.enabled;
				cashierRest.domainMethodProcessorProfileCreateOrUpdate(dmp.dmpp).then(function (p) {
					dmp.dmpp = p.plain();
					if (dmp.dmpp.enabled) {
						notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.EDIT.ENABLED");
					} else {
						notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.EDIT.DISABLED");
					}
				}).catch(function (error) {
					errors.catch("", false)(error)
				});
				break;
			}
			default: {
				dmp.enabled = !dmp.enabled;
				if (!dmp.enabled && dmp.active !== null) dmp.active = false;
				cashierRest.domainMethodProcessorUpdate(dmp).then(function (p) {
					angular.extend(dmp, p.plain());
					if (dmp.enabled) {
						notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.EDIT.ENABLED");
					} else {
						notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.EDIT.DISABLED");
					}
				}).catch(function (error) {
					errors.catch("", false)(error)
				});
			}
		}
		dmp.changelog.reload++;
	}

	controller.toggleProcessorDisplay = function (dmp) {
		dmp.active = !dmp.active;
		cashierRest.domainMethodProcessorUpdate(dmp).then(function (p) {
			angular.extend(dmp, p.plain());
			if (dmp.active) {
				notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.MENU.DISPLAY_ACTIVATED");
			} else {
				notify.success("UI_NETWORK_ADMIN.CASHIER.PROCESSORS.MENU.DISPLAY_DEACTIVATED");
			}
		}).catch(function (error) {
			errors.catch("", false)(error)
		});
		dmp.changelog.reload++;
	}

	controller.setDefaultMethod = function (domainMethod) {
		angular.forEach(controller.domainMethods, function (dm, $index) {
			if (domainMethod.id === dm.id) {
				dm.feDefault = true;
			} else {
				dm.feDefault = false;
			}
		});
		cashierRest.domainMethodUpdateMultiple(controller.domainMethods).then(function (dms) {
			controller.domainMethods = dms.plain();
			angular.forEach(controller.domainMethods, function (dm) {
				controller.populateDomainMethodProcessors(dm);
			});
		}).catch(function (error) {
			errors.catch("", false)(error)
		}).then(function () {
			controller.domainMethods = $filter('orderBy')(controller.domainMethods, '+priority');
		});
	}

	controller.unSetDefaultMethod = function (domainMethod) {
		angular.forEach(controller.domainMethods, function (dm, $index) {
			if (domainMethod.id === dm.id) {
				dm.feDefault = false;
			}
		});
		cashierRest.domainMethodUpdateMultiple(controller.domainMethods).then(function (dms) {
			controller.domainMethods = dms.plain();
			angular.forEach(controller.domainMethods, function (dm) {
				controller.populateDomainMethodProcessors(dm);
			});
		}).catch(function (error) {
			errors.catch("", false)(error)
		}).then(function () {
			controller.domainMethods = $filter('orderBy')(controller.domainMethods, '+priority');
		});
	}

	controller.toggleMethod = function (domainMethod) {
		switch (controller.viewas) {
			case 'user': {
				console.log(domainMethod);
				if (angular.isUndefined(domainMethod.domainMethodUser.enabled) || domainMethod.domainMethodUser.enabled === null) {
					if (domainMethod.domainMethodProfile === null || angular.isUndefined(domainMethod.domainMethodProfile.enabled) || domainMethod.domainMethodProfile.enabled === null) {
						domainMethod.domainMethodUser.enabled = domainMethod.enabled;
					} else {
						domainMethod.domainMethodUser.enabled = domainMethod.domainMethodProfile.enabled;
					}
				}
				domainMethod.domainMethodUser.enabled = !domainMethod.domainMethodUser.enabled;
				cashierRest.domainMethodUserUpdate(domainMethod.domainMethodUser).then(function (dmuser) {
					domainMethod.domainMethodUser = dmuser.plain();
					if (domainMethod.domainMethodUser.enabled) {
						notify.success("UI_NETWORK_ADMIN.CASHIER.METHODS.EDIT.ENABLED");
					} else {
						notify.success("UI_NETWORK_ADMIN.CASHIER.METHODS.EDIT.DISABLED");
					}
				}).catch(function (error) {
					errors.catch("", false)(error)
				});
				break;
			}
			case 'profile': {
				if (angular.isUndefined(domainMethod.domainMethodProfile.enabled)) {
					domainMethod.domainMethodProfile.enabled = domainMethod.enabled;
				}
				domainMethod.domainMethodProfile.enabled = !domainMethod.domainMethodProfile.enabled;
				cashierRest.domainMethodProfileUpdate(domainMethod.domainMethodProfile).then(function (dmprofile) {
					domainMethod.domainMethodProfile = dmprofile.plain();
					if (domainMethod.domainMethodProfile.enabled) {
						notify.success("UI_NETWORK_ADMIN.CASHIER.METHODS.EDIT.ENABLED");
					} else {
						notify.success("UI_NETWORK_ADMIN.CASHIER.METHODS.EDIT.DISABLED");
					}
				}).catch(function (error) {
					errors.catch("", false)(error)
				});
				break;
			}
			default: {
				var enabled;
				angular.forEach(controller.domainMethods, function(dm, $index) {
					if (domainMethod.id === dm.id) {
						dm.enabled = !dm.enabled;
						enabled = dm.enabled;
					}
				});
				cashierRest.domainMethodUpdateMultiple(controller.domainMethods).then(function(dms) {
					controller.domainMethods = dms.plain();
					angular.forEach(controller.domainMethods, function(dm) {
						controller.populateDomainMethodProcessors(dm);
					});
					if (enabled) {
						notify.success("UI_NETWORK_ADMIN.CASHIER.METHODS.EDIT.ENABLED");
					} else {
						notify.success("UI_NETWORK_ADMIN.CASHIER.METHODS.EDIT.DISABLED");
					}
				}).catch(function(error) {
					errors.catch("", false)(error)
				}).then(function() {
					controller.domainMethods = $filter('orderBy')(controller.domainMethods, '+priority');
				});
			}
			controller.reload();
		}
	}

	controller.editMethod = function(domainMethod) {
		var modalInstance = $uibModal.open({
			animation: true,
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			templateUrl: 'scripts/controllers/dashboard/cashier/config/methods/editmethod.html',
			controller: 'EditMethodController',
			controllerAs: 'controller',
			size: 'md cascading-modal',
			backdrop: 'static',
			resolve: {
				domainMethod: function() {
					return angular.copy(domainMethod);
				},
				loadMyFiles:function($ocLazyLoad) {
					return $ocLazyLoad.load({
						name:'lithium',
						files: [
							'scripts/controllers/dashboard/cashier/config/methods/editmethod.js'
						]
					})
				}
			}
		});

		modalInstance.result.then(function(dm) {
			angular.extend(domainMethod, dm);
			controller.reload();
		});
	}

	controller.editProcessor = function(domainMethodProcessor) {
		var modalInstance = $uibModal.open({
			animation: true,
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			templateUrl: 'scripts/controllers/dashboard/cashier/config/methods/editprocessor.html',
			controller: 'EditProcessorController',
			controllerAs: 'controller',
			size: 'md cascading-modal',
			backdrop: 'static',
			resolve: {
				dmp: function() {
					return angular.copy(domainMethodProcessor);
				},
				loadMyFiles:function($ocLazyLoad) {
					return $ocLazyLoad.load({
						name:'lithium',
						files: [
							'scripts/controllers/dashboard/cashier/config/methods/editprocessor.js'
						]
					})
				}
			}
		});
		modalInstance.result.then(function(dmp) {
			angular.extend(domainMethodProcessor, dmp);
			domainMethodProcessor.changelog.reload++;
		});
	}

	controller.editProperties = function(domainMethod, dmpId) {
		var modalInstance = $uibModal.open({
			animation: true,
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			templateUrl: 'scripts/controllers/dashboard/cashier/config/methods/properties.html',
			controller: 'EditPropertiesController',
			controllerAs: 'controller',
			size: 'lg cascading-modal',
			backdrop: 'static',
			resolve: {
				domainMethod: function() {
					return domainMethod;
				},
				dmpId: function() {
					return dmpId;
				},
				loadMyFiles:function($ocLazyLoad) {
					return $ocLazyLoad.load({
						name:'lithium',
						files: [
							'scripts/controllers/dashboard/cashier/config/methods/properties.js'
						]
					})
				}
			}
		});

		modalInstance.result.then(function(result) {
		});
	}

	controller.profileGroupFn = function(item) {
		if (item.name[0] >= 'N' && item.name[0] <= 'Z')
			return 'From N - Z';
		if (item.name[0] >= 'A' && item.name[0] <= 'M')
			return 'From A - M';
	};

	controller.toggleLegendCollapse = function() {
		controller.findProfiles();
		controller.legendCollapsed = !controller.legendCollapsed;
	}

	controller.resetFilter = function(toggle) {
		controller.reload();
		controller.selectedProfile = null;
		controller.selectedUser = null;
		controller.userProfile = null;
		controller.viewas = 'domain';
		controller.viewasdesc = null;
		if (toggle === true) {
			controller.toggleLegendCollapse();
		}
	}

	controller.clearOverrides = function() {
		angular.forEach(controller.domainMethods, function(dm) {
			angular.forEach(dm.domainMethodProcessors, function(dmp) {
				dmp.dmpu = null;
				dmp.dmpp = null;
			})
			dm.hasDMPUFees = false;
			dm.hasDMPULimits = false;
			dm.hasDMPPFees = false;
			dm.hasDMPPLimits = false;
			dm.domainMethodProfile = null;
			dm.domainMethodUser = null;
		});
	}

	controller.applyFilter = function(viewas) {
		controller.userProfile = null;
		controller.toggleLegendCollapse();
		controller.clearOverrides();
		if (controller.selectedProfile !== null && controller.selectedUser === null) {
			controller.findDomainMethodProfileSelected();
			controller.viewas = 'profile';
		}
		if (controller.selectedProfile === null && controller.selectedUser !== null) {
			cashierRest.user(controller.selectedUser.guid).then(function(user) {
				controller.userProfile = user.profile;
			}).catch(function(error) {
				controller.userProfile = null;
				errors.catch("", false)(error)
			}).then(function() {
				if (controller.userProfile !== null) {
					controller.findDomainMethodProfile(controller.userProfile);
				}
			}).then(function() {
				controller.findDomainMethodUser();
				controller.viewas = 'user';
			}).then(function() {
				angular.forEach(controller.domainMethods, function(dm) {
					cashierRest.domainMethodAccountingUser(dm.id, controller.selectedUser.username).then(function(list) {
						dm.accountingUserDay = list.plain()[0]['day'];
						dm.accountingUserWeek = list.plain()[0]['week'];
						dm.accountingUserMonth = list.plain()[0]['month'];
						dm.accountingUserLastMonth = list.plain()[0]['lastmonth'];
					});
					angular.forEach(dm.domainMethodProcessors, function(dmp) {
						cashierRest.domainMethodProcessorAccountingUser(dmp.id, controller.selectedUser.username).then(function(list) {
							dmp.accountingUserDay = list.plain()[0]['day'];
							dmp.accountingUserWeek = list.plain()[0]['week'];
							dmp.accountingUserMonth = list.plain()[0]['month'];
							dmp.accountingUserLastMonth = list.plain()[0]['lastmonth'];
						});
					});
				});
			});
		}
	}

	controller.searchUsers = function(userGuid) {
		return userRest.search($stateParams.domainName, userGuid).then(function(searchResult) {
			return searchResult.plain();
		}).catch(function(error) {
			errors.catch("", false)(error)
		});
	};

	controller.findProcessorsByUser = function(dm) {
		cashierRest.domainMethodProcessorsByUserNoImage(controller.selectedUser.guid).then(function(dmpus) {
			angular.forEach(dmpus.plain(), function(dmpu) {
				angular.forEach(dm.domainMethodProcessors, function(dmp) {
					if (dmp.id === dmpu.domainMethodProcessor.id) {
						dmp.dmpu = dmpu;
						if (dmpu.fees) {
							dm.hasDMPUFees = true;
						}
						if (dmpu.limits) {
							dm.hasDMPULimits = true;
						}
					}
				});
			});
		}).catch(function(error) {
			errors.catch("", false)(error)
		}).then(function() {
			if (controller.selectedProfile === null && controller.selectedUser !== null) {
				dm.domainMethodProcessors = $filter('orderBy')(dm.domainMethodProcessors, '-dmpu.weight');
			}
		});
	}

	controller.findDomainMethodUser = function() {
		angular.forEach(controller.domainMethods, function(dm) {
			if (controller.userProfile === null) {
				dm.domainMethodProfile = null;
			}
			cashierRest.domainMethodUser(dm.id, controller.selectedUser.guid).then(function(domainMethodUser) {
				if ($filter('isEmpty')(domainMethodUser.plain())) {
					dm.domainMethodUser = {};
					dm.domainMethodUser.id = null;
					dm.domainMethodUser.domainMethod = {}
					dm.domainMethodUser.domainMethod.id = dm.id;
					dm.domainMethodUser.user = {};
					dm.domainMethodUser.user.guid = controller.selectedUser.guid;
				} else {
					dm.domainMethodUser = domainMethodUser.plain();
				}
			}).catch(function(error) {
				errors.catch("", false)(error)
			}).then(function() {
				controller.domainMethods = $filter('orderBy')(controller.domainMethods, '+domainMethodUser.priority');
				controller.findProcessorsByUser(dm);
			});
		});
		cashierRest.frontendMethods($stateParams.type, controller.selectedUser.guid, '', '').then(function(domainMethods) {
			controller.cashierMethods = domainMethods.plain();
			angular.forEach(controller.cashierMethods, function(cm) {
				cashierRest.frontendProcessors(cm.domainMethodId, controller.selectedUser.guid, '', '').then(function(processors) {
					cm.processors = processors.plain();
					cm.processor = cm.processors[0];
				});
			});
		}).catch(function(error) {
			errors.catch("", false)(error)
		});
	}

	controller.findDomainMethodProfileSelected = function() {
		controller.findDomainMethodProfile(controller.selectedProfile);
	}

	controller.findDomainMethodProfile = function(profile) {
		angular.forEach(controller.domainMethods, function(dm) {
			cashierRest.domainMethodProfile(dm.id, profile.id).then(function(domainMethodProfile) {
				if ($filter('isEmpty')(domainMethodProfile.plain())) {
					dm.domainMethodProfile = {};
					dm.domainMethodProfile.id = null;
					dm.domainMethodProfile.domainMethod = {}
					dm.domainMethodProfile.domainMethod.id = dm.id;
					dm.domainMethodProfile.profile = {};
					dm.domainMethodProfile.profile.id = profile.id;
					dm.domainMethodProfile.priority = dm.priority;
				} else {
					dm.domainMethodProfile = domainMethodProfile.plain();
				}
			}).catch(function(error) {
				errors.catch("", false)(error)
			}).then(function() {
				controller.domainMethods = $filter('orderBy')(controller.domainMethods, '+domainMethodProfile.priority');
				controller.findProcessorsByProfile(dm, profile);
			});
		});
	}

	controller.findProcessorsByProfileSelected = function(dm) {
		controller.findProcessorsByProfile(dm, controller.selectedProfile);
	}

	controller.findProcessorsByProfile = function(dm, profile) {
		cashierRest.domainMethodProcessorsByProfileNoImage(profile).then(function(dmpps) {
			angular.forEach(dmpps.plain(), function(dmpp) {
				angular.forEach(dm.domainMethodProcessors, function(dmp) {
					if (dmp.id === dmpp.domainMethodProcessor.id) {
						dmp.dmpp = dmpp;
						if (dmpp.fees) {
							dm.hasDMPPFees = true;
						}
						if (dmpp.limits) {
							dm.hasDMPPLimits = true;
						}
					}
				});
			});
		}).catch(function(error) {
			errors.catch("", false)(error)
		}).then(function() {
			if (controller.selectedProfile !== null && controller.selectedUser === null) {
				dm.domainMethodProcessors = $filter('orderBy')(dm.domainMethodProcessors, '-dmpp.weight');
			}
		});
	}

	controller.findProfiles = function() {
		cashierRest.profiles($stateParams.domainName).then(function(profiles) {
			controller.profiles = $filter('orderBy')(profiles.plain(), '+name');
		}).catch(function(error) {
			errors.catch("", false)(error)
		});
	}

	controller.collapseDmpChangeLog = function(dmp) {
		dmp.changelog.collapsed = !dmp.changelog.collapsed;
	}

	controller.getCurrencyMethod = async () => {
		await domainRest.findByName($stateParams.domainName).then(function (domain) {
			controller.currencySymbol =  domain.currencySymbol;
		})
	}

	controller.getCurrency = async () => {
		return controller.currencySymbol
	}

	controller.getCurrencyMethod()

	controller.resetUser = () => {
		controller.selectedUser = null
	}
}]);
