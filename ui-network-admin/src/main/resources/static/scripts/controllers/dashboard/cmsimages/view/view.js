'use strict';

angular.module('lithium')
.controller('ImagesViewController', ["image", "AssetTemplatesRest", 'rest-provider', 'cdn-google-rest', "domainName", "notify", "$translate", "$log", "$scope", "$state", "$q", "errors", "$uibModal",
	function(image, rest, provider, cdn, domainName, notify, $translate, $log, $scope, $state, $q, errors, $uibModal) {
		var controller = this;

		//CDN requirements
		$scope.cdnUrl = "";
		$scope.cdnEnabled = false;
		$scope.loading = false;
		$scope.showDeleteButton = false;
		$scope.showUnpublishButton = false;

		controller.model = image;

		controller.changelogs = {
			domainName: image.domain.name,
			entityId: image.id,
			restService: rest,
			reload: 0
		}

		//Check the providers to see if we have a CDN provider enabled
		checkProviders();

	

		controller.onDelete = function() {

			$translate('UI_NETWORK_ADMIN.CMS.IMAGES.CONFIRM.DELETE').then(function(message) {
				if (window.confirm(message)) {
					const next = $state.href("dashboard.cmsimages.domain.list", { domainName: image.domain.name})

					cdn.deleteImage(image.domain.name,image.name,image.lang).then((response) => {
					rest.delete(image.domain.name, image.id).then(function (response) {
						notify.success("UI_NETWORK_ADMIN.CMS.IMAGES.SUCCESS.DELETE");
						window.location.assign(next)
					}).catch(function(error) {
						notify.error("UI_NETWORK_ADMIN.CMS.IMAGES.ERRORS.DELETE");
						window.location.assign(next);
					});
				})
				}
			})
		
		}


		

		async function checkProviders() {
			$scope.loading = true

			try {

				const providers = await provider.listByType(controller.model.domain.name, "CDN")
			
				const enabledProviders = providers.filter(p => p.enabled);

				if(enabledProviders.length > 0) {

					$scope.cdnEnabled = true;
					$scope.showDeleteButton = true

					const provider = enabledProviders.sort((a,b) =>  b.priority - a.priority)[0]
					let props = provider.properties.map(pp => {
						let custom = {}
						custom[pp.name] = pp.value
						return custom
						
					})

					props = Object.assign(...props)

					const prefix = props.bucketImagePrefix.replace("{lang}", image.lang)

					$scope.cdnUrl = `${props.uri}${prefix}/${image.name}`;
				
				}
			}
			catch(error) {
				errors.catch("", false)(error)
			}
			finally {
				$scope.loading = false
			}
		}
	}
]);
