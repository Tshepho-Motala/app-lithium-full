'use strict';

angular.module('lithium').controller('DocumentTypes', ['$rootScope', 'domainName', 'domain', '$dt', '$translate', 'DTOptionsBuilder', '$state', '$security', "rest-document",
    function ($rootScope, domainName, domain, $dt, $translate, DTOptionsBuilder, $state, $security, restDocument) {

        var controller = this;
        controller.data = {
            domainName: domainName,
            domain: domain
        };

        function convertEntityToModel(documentType) {
            if (documentType.iconBase64 !== undefined && documentType.iconBase64 !== null && documentType.iconBase64.length > 0) {
                documentType.iconBase64 = "data:" + documentType.iconType + ";base64," + documentType.iconBase64
            }
            if (documentType.iconName !== undefined && documentType.iconName !== null && documentType.iconName.length > 0) {
                documentType.icon = new File([], documentType.iconName, {type: documentType.iconType})
            }
        }

        $rootScope.provide.documentGeneration.data.domain = controller.data.domainName;

        $rootScope.provide.documentGeneration['loadDocumentTypes'] = () => {
            return new Promise((res, rej) => {
                if (controller.data.domainName !== undefined && controller.data.domainName !== null) {
                    restDocument.listDocumentTypes(controller.data.domainName).then(function (result) {
                        for (let i = 0; i < result.length; i++) {
                            convertEntityToModel(result[i]);
                        }
                        res(result)
                    }).catch(function (error) {
                        console.log(error);
                        rej(error)
                    })
                }
            })
        }
        $rootScope.provide.documentGeneration['saveDocumentType'] = (documentType) => {
            return new Promise((res, rej) => {
                if (documentType.iconBase64 !== undefined && documentType.iconBase64 !== null && documentType.iconBase64.length > 0) {
                    const index = documentType.iconBase64.indexOf(",")
                    documentType.iconBase64 = documentType.iconBase64.substring(index + 1)
                }
                restDocument.saveDocumentType(controller.data.domainName, documentType).then(function (result) {
                    convertEntityToModel(result)
                    res(result)
                }).catch(function (error) {
                    console.log(error);
                    rej(error)
                })

            })
        }

        controller.reload = function () {
            if (controller.data.domain != null)
                controller.changelogs = {
                    domainName: controller.data.domainName,
                    entityId: controller.data.domain.id,
                    restService: restDocument,
                    reload: 0
                }
        };

        controller.reload();

        window.VuePluginRegistry.loadByPage("dashboard/brand/config/doc-types")
    }]);
