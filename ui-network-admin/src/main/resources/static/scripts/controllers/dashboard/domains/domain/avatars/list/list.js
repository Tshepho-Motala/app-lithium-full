'use strict';

angular.module('lithium')
	.controller('DomainAvatarsListController', ['domain', 'notify', '$scope', 'errors', '$dt', '$uibModal', '$translate', '$state',
	function(domain, notify, $scope, errors, $dt, $uibModal, $translate, $state) {
		var controller = this;
		var url = 'services/service-avatar/admin/avatar/'+domain.name+'/table?1=1';
		controller.domainAvatarsTable = $dt.builder()
		.column($dt.column('name').withTitle($translate("UI_NETWORK_ADMIN.DOMAIN.AVATARS.FIELDS.NAME.NAME")))
		.column(
			$dt.linkscolumn(
				"",
				[
					{ 
						permission: "avatars_*",
						permissionType: "any",
						permissionDomain: function(data) {
							console.log(data);
							return data.domain.name;
						},
						title: "GLOBAL.ACTION.OPEN",
						href: function(data) {
							return $state.href("dashboard.domains.domain.avatars.avatar", { id:data.id });
						}
					}
				]
			)
		)
		.column($dt.column('description').withTitle($translate("UI_NETWORK_ADMIN.DOMAIN.AVATARS.FIELDS.DESCRIPTION.NAME")))
		.column(
			$dt.labelcolumn(
				$translate("UI_NETWORK_ADMIN.DOMAIN.AVATARS.FIELDS.ENABLED.NAME"),
				[{lclass: function(data) {
					if (data.enabled) {
						return 'success';
					} else {
						return 'danger'
					}
				},
				text: function(data) {
					if (data.enabled) {
						return 'ENABLED';
					} else {
						return 'DISABLED';
					}
				},
				uppercase:true
				}]
			)
		)
		.column(
			$dt.labelcolumn(
				$translate("UI_NETWORK_ADMIN.DOMAIN.AVATARS.FIELDS.DEFAULT.NAME"),
				[{lclass: function(data) {
					if (data.isDefault) {
						return 'primary';
					} else {
						return 'default'
					}
				},
				text: function(data) {
					if (data.isDefault) {
						return 'DEFAULT';
					} else {
						return 'FALSE';
					}
				},
				uppercase:true
				}]
			)
		)
		.options(url)
		.build();
		
		controller.add = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/domains/domain/avatars/add/add.html',
				controller: 'DomainAvatarAddModal',
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
				controller.domainAvatarsTable.instance.reloadData(function(){}, false);
			});
		}
	}
]);
