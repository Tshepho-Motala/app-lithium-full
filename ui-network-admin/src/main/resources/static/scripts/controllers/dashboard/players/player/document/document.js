'use strict';

angular.module('lithium')
    .controller('PlayerDocumentController', ["$q", "$timeout", "$translate", "$log", "Restangular", "$state", "$stateParams", "$http", "file-upload", "$scope", "$rootScope", "user", "$userService", "rest-document",
        function ($q, $timeout, $translate, $log, Restangular, $state, $stateParams, $http, fileUpload, $scope, $rootScope, user, $userService, restDocument) {
            var controller = this;
            controller.user = user;

            $rootScope.provide.documentGeneration.data.domain = controller.user.domain.name;

            $rootScope.provide.documentGeneration['loadDocuments'] = () => {
                return new Promise((res, rej) => {
                    if ($userService.hasRoleForDomain(controller.user.domain.name, "DOCUMENT_SENSITIVE_VIEW")) {
                        restDocument.listUserDocuments(controller.user.domain.name, controller.user.guid, true).then(function (result) {
                            res(result)
                        }).catch(function (error) {
                            console.log(error);
                            rej(error)
                        })
                    } else if ($userService.hasRoleForDomain(controller.user.domain.name, "DOCUMENT_REGULAR_VIEW")) {
                        restDocument.listUserDocuments(controller.user.domain.name, controller.user.guid, false).then(function (result) {
                            res(result)
                        }).catch(function (error) {
                            console.log(error);
                            rej(error)
                        })
                    }
                })
            }

            $rootScope.provide.documentGeneration['loadDocumentFile'] = (documentFileId) => {
                return new Promise((res, rej) => {
                    restDocument.getDocumentFile(controller.user.domain.name, documentFileId).then(function (result) {
                        result.file.base64 = "data:" + result.file.mimeType + ";base64," + result.file.data;
                        res(result)
                    }).catch(function (error) {
                        console.log(error);
                        rej(error)
                    })
                })
            }

            $rootScope.provide.documentGeneration['loadAvailableDocumentTypes'] = (internalOnly) => {
                return new Promise((res, rej) => {
                    restDocument.availableTypes(controller.user.domain.name, internalOnly).then(function (result) {
                        res(result)
                    }).catch(function (error) {
                        console.log(error);
                        rej(error)
                    })
                })
            }

            $rootScope.provide.documentGeneration['loadAvailableReviewReasons'] = () => {
                return new Promise((res, rej) => {
                    restDocument.availableReviewReasons(controller.user.domain.name).then(function (result) {
                        res(result)
                    }).catch(function (error) {
                        console.log(error);
                        rej(error)
                    })
                })
            }

            $rootScope.provide.documentGeneration['updateDocument'] = (document) => {
                return new Promise((res, rej) => {
                    let sensitivity = 'regular';
                    if (document.sensitive) {
                        sensitivity = 'sensitive';
                    }
                    restDocument.updateDocument(controller.user.domain.name, document, sensitivity).then(function (result) {
                        res()
                    }).catch(function (error) {
                        console.log(error);
                        rej(error)
                    })
                })
            }

            $rootScope.provide.documentGeneration['uploadDocument'] = (file, document) => {
                return new Promise((res, rej) => {
                    let sensitivity = 'regular';
                    if (document.sensitive) {
                        sensitivity = 'sensitive';
                    }
                    restDocument.uploadDocument(controller.user.domain.name, controller.user.guid, file, document, sensitivity).then(function (result) {
                        res(result.data)
                    }).catch(function (error) {
                        console.log(error);
                        rej(error)
                    })
                })
            }

            $rootScope.provide.documentGeneration['deleteDocument'] = (documentId) => {
                return new Promise((res, rej) => {
                    restDocument.deleteDocument(controller.user.domain.name, documentId).then(function (result) {
                        res()
                    }).catch(function (error) {
                        console.log(error);
                        rej(error)
                    })
                })
            }

            $rootScope.$on("reloadDocumentList", function () {
                console.log("Need update list of documents!")
                // https://jira.livescore.com/browse/PLAT-3255
                // window.vueListener.call('refresh-documents-list');
            });

            window.VuePluginRegistry.loadByPage("dashboard/players/documents-list")
        }
    ]);
