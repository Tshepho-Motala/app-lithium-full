"use strict";

angular.module("lithium").factory("cdn-google-rest", [
  "$log",
  "Restangular",
  "$http",
  function ($log, Restangular, http) {
    try {
      var service = {};
      var config = Restangular.withConfig(function (RestangularConfigurer) {
        RestangularConfigurer.setBaseUrl(
          "services/service-cdn-provider-google"
        );
      });

      service.create = function (domainName, name, language, content, head) {
        var template = { content: content, head: head };
        var response = config
          .all("backoffice")
          .all(domainName)
          .all("template")
          .all(name)
          .all(language)
          .post(template);

        return response;
      };

      service.uploadImage = function({ domain, lang, data }, onProgress, type='image'){
        return http.post(`services/service-cdn-provider-google/backoffice/${domain}/${type}/${lang}`, data, {
          transformRequest: angular.identity,
          headers: { "Content-Type": undefined, "Process-Data": false },
          uploadEventHandlers: {
            progress: (e) => {
              if (e.lengthComputable) {
                const progress = (e.loaded / e.total) * 100;
                onProgress(progress.toFixed(2))
              }
            },
          },
        });
      };

      service.deleteImage = function(domainName,name,language, type = 'image') {
        return config.all("backoffice")
            .all(domainName)
            .all(type)
            .one(language)
            .remove({fileName: name})
      }


      service.delete = function (domainName, name, language) {
        var response = config
          .all("backoffice")
          .all(domainName)
          .all("template")
          .all(name)
          .one(language)
          .remove();

        return response;
      };

      service.link = function (domainName, name, language) {
        var response = config
          .all("backoffice")
          .all(domainName)
          .all("template")
          .one(name)
          .one("link")
          .one(language)
          .get();

        return response;
      };

      return service;
    } catch (err) {
      $log.error(err);
      throw err;
    }
  },
]);
