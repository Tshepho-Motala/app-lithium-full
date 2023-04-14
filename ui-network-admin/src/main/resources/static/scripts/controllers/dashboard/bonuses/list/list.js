'use strict';

angular.module('lithium')
.controller('BonusListController', ["$translate", "$scope", "$state", "$rootScope", "$uibModal", "$dt", "$userService", "$filter","rest-casino", "$compile", "DTOptionsBuilder",
	function($translate, $scope, $state, $rootScope, $uibModal, $dt, $userService, $filter, casinoRest, $compile, DTOptionsBuilder) {
		var controller = this;

		controller.referenceId = "BonusListController_"+(Math.random()*1000);
		
		$translate("GLOBAL.FIELDS.ENABLED").then(function success(response) {
			controller.enabledText = response;
		});
		$translate("GLOBAL.FIELDS.DISABLED").then(function success(response) {
			controller.disabledText = response;
		});
		$translate("GLOBAL.BONUS.TYPE.0").then(function success(response) {
			controller.bonusType0Text = response;
		});
		$translate("GLOBAL.BONUS.TYPE.1").then(function success(response) {
			controller.bonusType1Text = response;
		});
		$translate("GLOBAL.BONUS.TYPE.2").then(function success(response) {
			controller.bonusType2Text = response;
		});
		$translate("GLOBAL.BONUS.TYPE.3").then(function success(response) {
			controller.bonusType3Text = response;
		});
		
		function editOrCurrent(bonus) {
			if (bonus.current !== null) {
				return bonus.current;
			} else if (bonus.edit !== null) {
				return bonus.edit;
			}
			return {};
		}
		
		controller.bonusTableLoad = function() {
			if (!angular.isUndefined(controller.bonusTable.instance)) {
				baseUrl = "services/service-casino/casino/bonus/table?1=1";
				baseUrl += "&enabled="+controller.selectedStatuses;
				baseUrl += "&bonusType="+controller.selectedTypes;
				baseUrl += "&domains="+controller.selectedDomains;
				controller.bonusTable.instance._renderer.options.ajax = baseUrl;
//				controller.bonusTable.instance.reloadData();
				controller.bonusTable.instance.rerender();
			}
		}
		
		controller.statuses = [{"id":"0","name":"Disabled","selected":true}, {"id":"1","name":"Enabled","selected":true}];
		controller.statusSelect = function() {
			controller.selectedStatuses = [];
			for (var d = 0; d < controller.statuses.length; d++) {
				if (controller.statuses[d].selected) 
					controller.selectedStatuses.push(controller.statuses[d].id);
			}
			if (controller.selectedStatuses.length == controller.statuses.length) {
				controller.selectedStatusesDisplay = "All Statuses";
			} else {
				controller.selectedStatusesDisplay = "Selected Statuses (" + controller.selectedStatuses.length + ")";
			}
		};
		
		controller.statusSelectAll = function() {
			for (var d = 0; d < controller.statuses.length; d++) controller.statuses[d].selected = true;
			controller.statusSelect();
		};
		controller.statusSelectAll();
		controller.statusSelect();
		
		controller.types = [{"id":0,"name":"Signup","selected":true}, {"id":1,"name":"Deposit","selected":true}, {"id":2,"name":"Trigger","selected":true}, {"id":3,"name":"Virtual Coin","selected":true}];
		controller.typeSelect = function() {
			controller.selectedTypes = [];
			for (var d = 0; d < controller.types.length; d++) {
				if (controller.types[d].selected) 
					controller.selectedTypes.push(controller.types[d].id);
			}
			if (controller.selectedTypes.length == controller.types.length) {
				controller.selectedTypesDisplay = "All Types";
			} else {
				controller.selectedTypesDisplay = "Selected Types (" + controller.selectedTypes.length + ")";
			}
		};
		
		controller.typeSelectAll = function() {
			for (var d = 0; d < controller.types.length; d++) controller.types[d].selected = true;
			controller.typeSelect();
		};
		controller.typeSelectAll();
		controller.typeSelect();
		
		controller.domains = $userService.domainsWithAnyRole(["ADMIN", "BONUS_VIEW"]);
		
		controller.domainSelect = function() {
			controller.selectedDomains = [];
			for (var d = 0; d < controller.domains.length; d++) {
				if (controller.domains[d].selected) 
					controller.selectedDomains.push(controller.domains[d].name);
			}
			if (controller.selectedDomains.length == controller.domains.length) {
				controller.selectedDomainsDisplay = "All Domains";
			} else {
				controller.selectedDomainsDisplay = "Selected Domains (" + controller.selectedDomains.length + ")";
			}
		};
		
		controller.domainSelectAll = function() {
			for (var d = 0; d < controller.domains.length; d++) controller.domains[d].selected = true;
			controller.domainSelect();
		};
		controller.domainSelectNone = function() {
			for (var d = 0; d < controller.domains.length; d++) controller.domains[d].selected = false;
			controller.domainSelect();
		};
		controller.domainSelectAll();
		controller.domainSelect();
		
		var baseUrl = "services/service-casino/casino/bonus/table?1=1";
		baseUrl += "&enabled="+controller.selectedStatuses;
		baseUrl += "&bonusType="+controller.selectedTypes;
		baseUrl += "&domains="+controller.selectedDomains;

		const dtOptions = DTOptionsBuilder.newOptions().withOption('createdRow', function(row, data, dataIndex) {
			$compile(angular.element(row).contents())($scope);
		});
		controller.bonusTable = $dt.builder()
		.column($dt.column('id').withTitle($translate("UI_NETWORK_ADMIN.BONUS.LIST.TITLE.BONUSID")))
		.column($dt.column('current.bonusCode').withTitle($translate("UI_NETWORK_ADMIN.BONUS.LIST.TITLE.CODE")).renderWith(function(data, type, full) {
			return editOrCurrent(full).bonusCode;
		}))
		.column($dt.linkscolumn("", [{ permission: "bonus_view", permissionType:"any", permissionDomain: function(data) { return editOrCurrent(data).domain.name;}, title: "GLOBAL.ACTION.OPEN", href: function(data) { return $state.href("dashboard.bonuses.bonus.view", {bonusId:data.id, bonusRevisionId:editOrCurrent(data).id}) } }]))
		.column($dt.labelcolumn($translate("UI_NETWORK_ADMIN.BONUS.LIST.TITLE.STATUS"), [{lclass: function(data) { return (editOrCurrent(data).enabled === true)?"default":"danger"; }, text: function(data) { return (editOrCurrent(data).enabled === true)?"GLOBAL.FIELDS.ENABLED":"GLOBAL.FIELDS.DISABLED"; }, uppercase:true }]))
//		.column($dt.linkscolumn($translate("UI_NETWORK_ADMIN.BONUS.LIST.TITLE.REVISIONS"), [{ permission: "bonus_edit", permissionType:"any", permissionDomain: function(data) { return data.current.domain.name;}, title: "GLOBAL.ACTION.VIEW", href: function(data) { return $state.href("dashboard.bonuses.bonus.revisions", {bonusId:data.id}) } }]))
		.column($dt.column('current.bonusName').withTitle($translate("UI_NETWORK_ADMIN.BONUS.LIST.TITLE.NAME")).renderWith(function(data, type, full) {
			return editOrCurrent(full).bonusName;
		}))
		.column(
			$dt.labelcolumn(
				$translate("UI_NETWORK_ADMIN.BONUS.LIST.TITLE.TYPE"),
				[{
					lclass: function(data) {
						switch (editOrCurrent(data).bonusType) {
							case 0:
								return "warning";
							case 1:
								return "success";
							case 2: 
								return "primary";
							case 3:
								return "info";
							default:
								return "default";
						}
					},
					text: function(data) {
						return "GLOBAL.BONUS.TYPE."+(editOrCurrent(data).bonusType);
					},
					uppercase:true
				},{
					lclass: function(data) {
						console.log(data);
						return "default";
					},
					text: function(data) {
						if (editOrCurrent(data).bonusType === 2)
							return "GLOBAL.BONUS.TYPE.TRIGGER."+(editOrCurrent(data).bonusTriggerType);
						else
							return "";
					},
					uppercase:true
				}]
			)
		)
		.column($dt.column('current.domain.name').withTitle($translate("UI_NETWORK_ADMIN.BONUS.LIST.TITLE.DOMAIN")).renderWith(function(data, type, full) {
			if (angular.isUndefined(editOrCurrent(full).domain)) {
				return "";
			} else {
				return editOrCurrent(full).domain.name;
			}
		}))
		.column($dt.emptycolumn('').renderWith(function (data, type, row, meta) {

			return '<button class="btn btn-danger" lit-if-permission="BONUS_EDIT" lit-permission-domain="' + row.current.domain.name + '" ng-click="controller.deleteBonus(' + row.id + ')"><i class="fa fa-trash-o"></i></button>';		}))
		.options(baseUrl, null, dtOptions, null)
		.build();


		controller.deleteBonus = function(id) {
			console.log("test");
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/bonuses/bonus/confirmdelete.html',
				controller: 'ConfirmNoteDeleteModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					entityId: function () {
						return id;
					},
					restService: function () {
						return casinoRest;
					},
					loadMyFiles: function ($ocLazyLoad) {
						return $ocLazyLoad.load({
							name: 'lithium',
							files: ['scripts/controllers/dashboard/bonuses/bonus/confirmdelete.js']
						})
					}
				}
			});

			modalInstance.result.then(function(response) {
				controller.bonusTableLoad();
				notify.success('UI_NETWORK_ADMIN.BONUS.NOTIFY.RESPONSE.SUCCESS');
			});
		};

		// Bonus Search TopBar
		$rootScope.provide.dropDownMenuProvider['domainList']  = () => {
			return controller.domains
		}
		$rootScope.provide.dropDownMenuProvider['domainsChange'] = (data) => {
			const domainNames = []
			data.forEach(el=> {
				domainNames.push(el.name)
			})
			controller.selectedDomains = [...domainNames]
			controller.bonusTableLoad()
		}

		$rootScope.provide.dropDownMenuProvider['tagList'] = () => {
			return controller.types
		}

		$rootScope.provide.dropDownMenuProvider['tagsChange'] = (data) => {
			const tagList = []
			data.forEach(el=> {
				tagList.push(el.id)
			})
			controller.selectedTypes = [...tagList]
			controller.bonusTableLoad()
		}

		$rootScope.provide.dropDownMenuProvider['statusList'] = () => {
			return controller.statuses
		}

		$rootScope.provide.dropDownMenuProvider['statusesChange'] = (data) => {
			const tagList = []
			data.forEach(el=> {
				tagList.push(el.id)
			})
			controller.selectedStatuses = [...tagList]
			controller.bonusTableLoad()
		}

		window.VuePluginRegistry.loadByPage("BonusSearchTopBar")
	}
]);
