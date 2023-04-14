"use strict";

angular.module("lithium").controller("CmsAssetController", [
  "$rootScope",
  "CmsAssetRest",
  "rest-provider",
  "cdn-google-rest",
  "$userService",
  "$http",
  function (
    $rootScope,
    cmsAssetRest,
    providerRest,
    cdnRest,
    $userService,
    $http
  ) {
    var controller = this;

    $rootScope.provide["cmsProvider"] = {};

    $rootScope.provide.pageHeaderProvider.getDomains = () => {
      return new Promise(async (resolve, reject) => {
        const domains = await $userService.domainsWithAnyRole(["GAME_TILE_MANAGE", "BANNER_IMAGE_MANAGE"])
        resolve(domains.map(d => {
          return {...d, title: d.name}
        }))
      })
    }

    $rootScope.provide.cmsProvider.getAssets = (request) => {
      return $http
        .post(
          `services/service-cdn-cms/backoffice/${request.domain}/cms-assets/table`,
          {
              page: request.page,
              size: request.size,
              sortBy: request.sortBy,
              sortOrder: request.sortOrder,
              data: {
                  type: request.type
              }
          }
        );
    };

    $rootScope.provide.cmsProvider.getProviderConfig = async (domain) => {
      const providers = await providerRest.listByType(domain, "CDN");

      const enabledProviders = providers.filter((p) => p.enabled);

      if (enabledProviders.length > 0) {
        const provider = enabledProviders.sort(
          (a, b) => b.priority - a.priority
        )[0];

        let props = provider.properties.map((pp) => {
          let custom = {};
          custom[pp.name] = pp.value;
          return custom;
        });

        props = Object.assign(...props);

        return props;
      }

      return {};
    };

    $rootScope.provide.cmsProvider.upload = async (domain, data, onProgress) => {
      let obj = {};
      data.forEach((value, key) => (obj[key] = value));
      await cmsAssetRest.add(obj, domain);
      await cdnRest.uploadImage(
        { domain, data, lang: "en" },
        onProgress,
        "cms-asset"
      );
    };

    $rootScope.provide.cmsProvider.deleteImage = async (domain, image) => {
        await cmsAssetRest.delete(domain, image.id)
        await cdnRest.deleteImage(domain, image.url.split('/').pop(), 'en', 'cms-asset')
    }

    $rootScope.provide.cmsProvider.findAssetByNameAndDomainAndType = async (name, domain, type) => {
      let response = (await cmsAssetRest.findByNameAndDomainAndType(name,domain, type)).plain();

      return !Array.isArray(response) ? response: null;
    }

    window.VuePluginRegistry.loadByPage("dashboard/cmsassets");
  },
]);
