'use strict';

angular.module('lithium')
	.controller('domainRelationshipListController', ["$scope", "$stateParams", "$state", "$translate", "$dt", "DTOptionsBuilder", "notify", "bsLoadingOverlayService", "errors", "EcosysRest",
		"$uibModal", function ($scope, $stateParams, $state, $translate, $dt, DTOptionsBuilder, notify, bsLoadingOverlayService, errors, EcosysRest,
							   $uibModal) {
			var controller = this;
			controller.model = {};
			controller.selectedEcosystem = null;

			EcosysRest.ecosystems().then(function(response) {
				let data = response.plain();
				for (let index = 0; index < data.length; index++) {
					if (data[index].id == $stateParams.id) {
						controller.model = data[index];
					}
				}
				return controller.model;
			}).then(function (result) {
				controller.ecosystemSelect(result);
			});

			controller.list = function(ecosystem) {
				EcosysRest.domainRelationship(ecosystem.name).then(function (response) {
						let data = response.plain();
						controller.domainRelationshipList = data;
					});
			}

			controller.ecosystemSelect = function (ecosystem) {
				controller.selectedEcosystem = ecosystem;
				controller.ecosystemId = ecosystem.id;
				controller.list(ecosystem);
			};

			controller.remove = function (id) {

				let deleteDomainModal = $uibModal.open({
					animation : true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/controllers/dashboard/domainrelationship/list/confirmdeleterelationship.html',
					controller: 'ConfirmDeleteEcosystemRelationship',
					controllerAs: 'controller',
					backdrop: 'static',
					size: 'md',
					resolve: {
						selectedId: function () {
							return id;
						},
						selectedEcosystemController: function () {
							return controller.selectedEcosystem;
						},
						relationShipController: function () {
							return controller;
						},
						referenceId: function() {
							return $scope.referenceId;
						},
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name: 'lithium',
								files: ['scripts/controllers/dashboard/domainrelationship/list/confirmdeleterelationship.js']
							});
						}
					}
				});

				deleteDomainModal.result.then(function(response) {
					notify.success('UI_NETWORK_ADMIN.ECOSYSTEM.MESSAGES.DELETE_DOMAIN_SUCCESS');
				});
			}

			controller.addDomainRelationship = function(ecosystem) {
				$state.go("dashboard.domainrelationship.add", { id: ecosystem});	
			}
			controller.editEcosystem = function(ecosystem) {
				$state.go("dashboard.ecosystems.view", { id: ecosystem});	
			}

			controller.updateDisableRootWelcomeEmail = function (value) {
				EcosysRest.editDisableRootWelcomeEmail(value.id, !value.disableRootWelcomeEmail);
			}
		}
	]);
