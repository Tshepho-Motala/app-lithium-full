'use strict';

angular.module('lithium')
.controller('BonusRevisionsListController', ["bonus", "$translate", "$scope", "$state", "$rootScope", "$uibModal", "$dt", "DTOptionsBuilder", "$userService", "$compile", "$filter", "rest-casino",
	function(bonus, $translate, $scope, $state, $rootScope, $uibModal, $dt, DTOptionsBuilder, $userService, $compile, $filter, casinoRest) {
		var controller = this;
		if (bonus.length === 0) {
			console.log("bonus missing");
		}
		var baseUrl = "services/service-casino/casino/bonus/revisions/table?bonusId="+bonus.id;
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('createdRow', function (row, data, index) {
			if (((bonus.current !== null)) && (bonus.current.id === data.id)) {
				angular.element(row).addClass('success');
			} else if ((bonus.edit !== null) && (bonus.edit.id === data.id)) {
				angular.element(row).addClass('info');
			}
			//row.setAttribute("ng-class", "{ 'label-primary': "+current+"}");
			//row.setAttribute("ng-click", "controller.clicker("+data.id+")");
			$compile(row)($scope);
		});
		
		function editOrCurrent(bonus) {
			if (bonus.current !== null) {
				return bonus.current;
			} else if (bonus.edit !== null) {
				return bonus.edit;
			}
			return {};
		}
		
		function statusHtml(data, type, full, meta) {
			var parentDom = $(meta.settings.aoData[meta.row].anCells[meta.col]);
			if ((bonus.current !== null) && (bonus.current.id === data.id)) {
				$translate("UI_NETWORK_ADMIN.BONUS.LABEL.CURRENT").then(function success(response) {
					parentDom.html('<span style="display:list-item;line-height:inherit;" class="label label-success">'+$filter('uppercase')(response)+'</span>');
				});
			} else if ((bonus.edit !== null) && (bonus.edit.id === data.id)) {
				$translate("UI_NETWORK_ADMIN.BONUS.LABEL.EDIT").then(function success(response) {
					parentDom.html(
						'<span style="line-height:inherit;" class="label label-info">'+$filter('uppercase')(response)+'</span>&nbsp;'+
						'<span style="line-height:inherit;" class="label label-primary">'+$filter('uppercase')(bonus.editUser)+'</span>'
					);
				});
			} else {
				parentDom.html("");
			}
			return "";
		}
		
		controller.revisionTable = $dt.builder()
			.column($dt.column('id').withTitle($translate("UI_NETWORK_ADMIN.BONUS.LIST.TITLE.ID")))
			.column($dt.emptycolumnrenderwith("", statusHtml))
//			.column($dt.labelcolumn($translate("UI_NETWORK_ADMIN.BONUS.LIST.TITLE.STATUS"), [{lclass: function(data) { return (data.enabled === true)?"default":"danger"; }, text: function(data) { return (data.enabled === true)?"GLOBAL.FIELDS.ENABLED":"GLOBAL.FIELDS.DISABLED"; }, uppercase:true }]))
			.column($dt.column('bonusCode').withTitle($translate("UI_NETWORK_ADMIN.BONUS.LIST.TITLE.CODE")))
			.column($dt.linkscolumn("", [{ permission: "bonus_view", permissionType:"any", permissionDomain: function(data) { return data.domain.name;}, title: "GLOBAL.ACTION.VIEW", condition: function(data) { if ((bonus.edit === null)||(bonus.edit.id!==data.id)) { return true; } else { return false; }}, href: function(data) { return $state.href("dashboard.bonuses.bonus.view.summary", {bonusId:bonus.id, bonusRevisionId:data.id}) } }]))
			.column($dt.linkscolumn("", [{ permission: "bonus_edit", permissionType:"any", permissionDomain: function(data) { return data.domain.name;}, title: "GLOBAL.ACTION.MODIFY", condition: function(data) { if ((bonus.edit !== null)&&(bonus.edit.id===data.id)) { return true; } else { return false; }}, href: function(data) { if ((bonus.edit !== null)&&(bonus.edit.id===data.id)) { return $state.href("dashboard.bonuses.edit", {bonusId:bonus.id, bonusRevisionId:data.id}); } else { return $state.href("."); } }, click: function(data) { if ((bonus.edit !== null)&&(bonus.edit.id!==data.id)) { return controller.editChangeModal(data); } } }]))
//			.column($dt.labelcolumn($translate("UI_NETWORK_ADMIN.BONUS.LIST.TITLE.STATUS"), [{lclass: function(data) { return (data.enabled === true)?"default":"danger"; }, text: function(data) { return (data.enabled === true)?"GLOBAL.FIELDS.ENABLED":"GLOBAL.FIELDS.DISABLED"; }, uppercase:true }]))
			.column($dt.column('bonusName').withTitle($translate("UI_NETWORK_ADMIN.BONUS.LIST.TITLE.NAME")))
//			.column($dt.labelcolumn($translate("UI_NETWORK_ADMIN.BONUS.LIST.TITLE.TYPE"), [{lclass: function(data) { return (data.bonusType === 1)?"success":"warning"; }, text: function(data) { return (data.bonusType === 1)?"GLOBAL.BONUS.TYPE.1":"GLOBAL.BONUS.TYPE.0"; }, uppercase:true }]))
			.column($dt.column('domain.name').withTitle($translate("UI_NETWORK_ADMIN.BONUS.LIST.TITLE.DOMAIN")))
			.options(baseUrl, null, dtOptions, null)
			.order([[ 0, "desc" ]])
			.build();
		
		controller.changelogs = {
			domainName: editOrCurrent(bonus).domain.name,
			entityId: bonus.id,
			restService: casinoRest,
			reload: 0
		}
		
		controller.copyRevision = function(bonusRevision) {
//			console.log(bonusRevision);
			casinoRest.copyBonusRevision(bonusRevision.id).then(function(response) {
//				console.log(response);
				controller.revisionTable.instance.reloadData();
				controller.changelogs.reload += 1;
			}).catch(function() {
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			}).finally(function () {
				
			});
		}
		
		controller.editChangeModal = function(bonusRevision) {
//			console.log("controller.editChangeModal");
			var modalInstance = $uibModal.open({
				animation: false,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/bonuses/bonus/edit/editchangemodal.html',
				controller: 'BonusEditChangeModal',
				resolve: {
					bonus: bonus,
					bonusRevision: bonusRevision,
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/bonuses/bonus/edit/editchangemodal.js'
							]
						})
					}
				},
				controllerAs: 'controller',
				size: 'lg'
			});
			
			modalInstance.result.then(function(response) {
//				console.log("Modal Closed", response);
				if (angular.isDefined(response)) bonus = response;
//				controller.revisionTable.instance._renderer.options.ajax = baseUrl;
				controller.revisionTable.instance.rerender();
				controller.changelogs.reload += 1;
//				controller.model = model;
				//refresh bonus list
			});
		}
	}
]);
