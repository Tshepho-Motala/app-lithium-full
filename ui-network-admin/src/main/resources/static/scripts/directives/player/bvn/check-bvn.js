'use strict';

angular.module('lithium')
    .controller('CheckBvnModal', ['user', '$uibModalInstance', '$scope', 'notify', 'BvnVerifyRest',
        function (user, $uibModalInstance, $scope, notify, bvnVerifyRest) {
            var controller = this;
            controller.options = {removeChromeAutoComplete: true};
            controller.model = {
                bvnNumber: undefined,
                status:undefined,
                message: undefined,
                formatted_dob: undefined,
                mobile: undefined,
                bvn: undefined,
                first_name: undefined,
                last_name: undefined
            };
            controller.fields = [
                {
                    className: "col-xs-12",
                    key: "bvnNumber",
                    type: "input",
                    optionsTypes: ['editable'],
                    templateOptions: {
                        label: "", description: "", placeholder: "",
                        required: true, minlength: 11, maxlength: 11, type: "number"
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.BVN.NUMBER" | translate',
                        'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.BVN.PLACEHOLDER" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.BVN.DESCRIPTION" | translate'
                    },
                },
                {
                    key: "status",
                    className: "col-xs-12",
                    type: "input",
                    templateOptions: {
                        label: "",
                        disabled: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.BVN.STATUS" | translate',
                    },
                    hideExpression: "!model.status",
                },
                {
                    key: "message",
                    className: "col-xs-12",
                    type: "input",
                    templateOptions: {
                        label: "", description: "", placeholder: "",
                        disabled: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.BVN.MESSAGE" | translate',
                    },
                    hideExpression: "!model.message",
                    elementAttributes: {
                        class: ""
                    },
                },

                {
                    key: "first_name",
                    className: "col-xs-12",
                    type: "input",
                    templateOptions: {
                        label: "", description: "", placeholder: "",
                        disabled: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.BVN.FIRSTNAME" | translate',
                    },
                    hideExpression: "!model.first_name",
                },{
                    key: "last_name",
                    className: "col-xs-12",
                    type: "input",
                    templateOptions: {
                        label: "", description: "", placeholder: "",
                        disabled: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.BVN.LASTNAME" | translate',
                    },
                    hideExpression: "!model.last_name",
                },{
                    key: "formatted_dob",
                    className: "col-xs-12",
                    type: "input",
                    templateOptions: {
                        label: "", description: "", placeholder: "",
                        disabled: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.BVN.DATEOFBIRTH" | translate',
                    },
                    hideExpression: "!model.formatted_dob",
                },{
                    key: "mobile",
                    className: "col-xs-12",
                    type: "input",
                    templateOptions: {
                        label: "", description: "", placeholder: "",
                        disabled: true
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.BVN.MOBILE" | translate',
                    },
                    hideExpression: "!model.mobile",
                }
            ];
            controller.submit = function () {
                controller.submitCalled = true;
                if (controller.form.$invalid) {
                    angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                    notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                    return false;
                }
                var checkBvnRequest = {
                    userGuid: user.guid,
                    bvn: controller.model.bvnNumber
                }
                bvnVerifyRest.verifyBvn(checkBvnRequest).then(function (response) {
                    const value = response;
                    console.log(response)
                    if (response._successful===false) {
                        notify.error(response._message);
                    } else {
                        controller.model.submitCalled=true;
                        controller.model.status = value.status;
                        controller.model.message = value.message;
                        controller.model.formatted_dob = value.data?.formatted_dob;
                        controller.model.mobile = value.data?.mobile;
                        controller.model.bvn = value.data?.bvn;
                        controller.model.first_name = value.data?.first_name;
                        controller.model.last_name = value.data?.last_name;
                        if(value.status==='false'){
                            controller.fields[2].elementAttributes.class = 'text-danger'
                        }
                    }
                }, function (status) {
                    notify.error("UI_NETWORK_ADMIN.PLAYER.BVN.ERROR");
                });
            };

            controller.cancel = function () {
                $uibModalInstance.dismiss('cancel');
            };
        }]);
