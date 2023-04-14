'use strict';

angular.module('lithium')
    .controller('DocumentVerificationModal', ['user', '$uibModalInstance', '$scope', '$state', '$translate', 'notify', 'DocumentVerifyRest',
        function (user, $uibModalInstance, $scope, $state, $translate, notify,  documentVerifyRest) {
            var controller = this;
            controller.options = {removeChromeAutoComplete: true};
            controller.model = {
                report: '',
                frontUploading: false,
                backUploading: false,
                checkPending: false
            };
            controller.fields = [
                {
                    key: "report",
                    className: "col-xs-12",
                    type: "textarea",
                    templateOptions: {
                        label: '',
                        description: '',
                        placeholder: '',
                        // maxlength: 65535,
                        // cols: 10,
                        rows: 10,
                        require: true,
                        disabled: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.DOCUMENT_UPLOAD_VERIFY.FIELDS.INFORMATION_WINDOW.LABEL" | translate',
                        'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.DOCUMENT_UPLOAD_VERIFY.FIELDS.INFORMATION_WINDOW.PLACE_HOLDER" | translate'
                    },
                }
            ];
            controller.upload = function (file, side) {
                controller.setUploadButton(side, true)
                if (file != null) {
                    documentVerifyRest.uploadDocument(user.domain.name, user.guid, side, file)
                        .then(function (response) {
                            const value = response
                            console.log(response)
                            if (response._successful === false) {
                                controller.logToReport("Fail to upload " + side + " side of document: " + response._message)
                                notify.error("Fail to upload " + side + " side of document: " + response._message)
                            } else {
                                controller.logToReport("Uploaded " + side + " side of document: " + value)
                                notify.success("Uploaded " + side + " side of document: " + value)
                            }
                            controller.setUploadButton(side, false)
                        }, function (status) {
                            controller.logToReport("Can't upload " + side + " side of document: " + status)
                            notify.error("Can't upload " + side + " side of document: " + status);
                            controller.setUploadButton(side, false)
                        });
                }
            }

            controller.setUploadButton = function (side, value) {
                if ("front" === side) {
                    controller.model.frontUploading = value
                }
                if ("back" === side) {
                    controller.model.backUploading = value
                }
            }

            controller.submitCheck = function () {
                controller.model.checkPending = true;
                documentVerifyRest.submitCheck(user.domain.name, user.guid)
                    .then(function (response) {
                        const value = response;
                        console.log(response)
                        if (response._successful === false) {
                            controller.logToReport("Fail to submit check: " + response._message)
                            notify.error("Fail to submit check: " + response._message);
                        } else {
                            controller.logToReport("Check submit requested, status: " + value)
                            notify.success("Check submit requested, status: " + value);
                        }
                        controller.model.checkPending = false;
                    }, function (status) {
                        console.log(status);
                        controller.logToReport("Can't submit check: " + status)
                        notify.error("Can't submit check: " + status);
                        controller.model.checkPending = false;
                    });
            };

            controller.cancel = function () {
                $uibModalInstance.dismiss('cancel');
            };
            controller.closeAndNavigate = function () {
                $uibModalInstance.dismiss('cancel');
                $state.go("dashboard.players.player.kyc", {}, {reload: true});
            };

            controller.logToReport = function (message) {
                controller.model.report = controller.model.report + message + "\n"
            }
        }]);
