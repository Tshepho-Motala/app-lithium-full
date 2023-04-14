'use strict';

angular.module('lithium')
    .factory('DocumentVerifyRest', ['$log', 'Restangular',
        function ($log, Restangular) {
            try {
                var service = {};

                var rest = function () {
                    return Restangular.withConfig(function (RestangularConfigurer) {
                        RestangularConfigurer.setBaseUrl("services/service-kyc-provider-onfido/backoffice");
                    });
                }
                service.uploadDocument = function (domainName, userGuid, side, file) {
                    var fd = new FormData();
                    fd.append('file', file);
                    fd.append('userGuid', userGuid);
                    fd.append('side', side);
                    return rest().all(domainName).all("upload-document")
                        .withHttpConfig({transformRequest: angular.identity})
                        .customPOST(fd, undefined, undefined, {'Content-Type': undefined});
                }
                service.submitCheck = function (domainName, userGuid) {
                    return rest().all(domainName).all("submit-check").post('',{userGuid: userGuid, reportType: "document_with_address_information"});
                }
                return service;
            } catch (err) {
                $log.error(err);
                throw err;
            }
        }
    ]);