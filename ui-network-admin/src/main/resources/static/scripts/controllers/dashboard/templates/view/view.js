'use strict';

angular.module('lithium')
.controller('TemplatesViewController', ["template", "TemplatesRest", 'rest-provider', 'cdn-google-rest', "domainName", "notify", "$translate", "$log", "$scope", "$state", "$q","$security", "errors", "$uibModal",
	function(template, rest, provider, cdn, domainName, notify, $translate, $log, $scope, $state, $q,$security, errors, $uibModal) {
		var controller = this;

		//CDN requirements
		$scope.cdnUrl = "";
		$scope.cdnEnabled = false;
		$scope.loading = false;
		$scope.showDeleteButton = false;
		$scope.showUnpublishButton = false;

		controller.model = template;

		controller.changelogs = {
			domainName: template.domain.name,
			entityId: template.id,
			restService: rest,
			reload: 0
		}
		controller.cmschangelogs = {
			domainName: template.domain.name,
			entityId: template.id,
			restService: rest,
			reload: 0
		}

		//Check the providers to see if we have a CDN provider enabled
		checkProviders();
		
		

		controller.onPublish = function() {
			if(!controller.model.current.content) {
				notify.warning("UI_NETWORK_ADMIN.TEMPLATES.INVALID.PUBLISH");
				return false;
			}
			if(! controller.model.enabled) {
				notify.error("UI_NETWORK_ADMIN.TEMPLATES.ENABLED.PUBLISH");
				return false;
			}
			cdn.create(controller.model.domain.name, controller.model.name, controller.model.lang, controller.model.current.content, controller.model.current.head).then(function(response) {
				if(response._status === 500) {
					notify.error(response._message);
				} else {
					notify.success("UI_NETWORK_ADMIN.TEMPLATES.SUCCESS.PUBLISH");
					checkTemplateOnCdn();
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.TEMPLATES.ERRORS.PUBLISH");
				errors.catch("", false)(error)
			});
		}

		controller.onDelete = function() {
			$translate('UI_NETWORK_ADMIN.TEMPLATES.CONFIRM.DELETE').then(function(message) {
				if (window.confirm(message)) {
					rest.delete(template.domain.name, template.id).then(function (response) {
						const next = $state.href("dashboard.templates.domain.list", { domainName: template.domain.name})
						
						if(response._status === 0) 
							notify.success("UI_NETWORK_ADMIN.TEMPLATES.SUCCESS.DELETE");
						else
							notify.error("UI_NETWORK_ADMIN.TEMPLATES.ERRORS.DELETE");
						
						window.location.assign(next)

					}).catch(function(error) {
						notify.error("UI_NETWORK_ADMIN.TEMPLATES.ERRORS.DELETE");
						errors.catch("", false)(error)
					});
				}
			})
		}


		controller.onUnpublish = function() {
			cdn.delete(controller.model.domain.name, controller.model.name, controller.model.lang).then(function(response) {
				if(response._status === 500) {
					notify.error(response._message);
				} else {
					notify.success("UI_NETWORK_ADMIN.TEMPLATES.SUCCESS.UNPUBLISH");
					checkTemplateOnCdn();
				}
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.TEMPLATES.ERRORS.UNPUBLISH");
				errors.catch("", false)(error)
			});
		}

		function checkProviders() {
			$scope.loading = true

			provider.listByType(controller.model.domain.name, "CDN").then(function (provResponse) {
				angular.forEach(provResponse, function (prov) {
					$scope.cdnEnabled = prov.enabled;
				});
			}).then(checkTemplateOnCdn)
				.catch(function (error) {
				errors.catch("", false)(error)
			}).finally(() => {
				$scope.loading = false
			});
		}

		function checkTemplateOnCdn() {
			$scope.loading = true
			if ($scope.cdnEnabled && ($security.hasRoleInTree("TEMPLATES_PUBLISH")||$security.hasAdminRole())) {
				cdn.link(controller.model.domain.name, controller.model.name, controller.model.lang).then(function (response) {
					if (response._status === 404) {
						$scope.cdnUrl = "";
						$scope.showDeleteButton = true
						$scope.showUnpublishButton = false
					}
					if (!response._status) {
						$scope.cdnUrl = response;
						$scope.showDeleteButton = false
						$scope.showUnpublishButton = true
					}
				}).catch(function (error) {
					errors.catch("", false)(error)
				}).finally(() => {
					$scope.loading = false
				})
			}
		}
	}
]);
