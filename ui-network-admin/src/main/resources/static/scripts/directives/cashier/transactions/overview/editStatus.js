'use strict';

angular.module('lithium')
    .controller('EditPMStatusModal',
        ["$scope", "$stateParams", "$state", '$uibModalInstance', 'rest-cashier', 'pm','showVerification','statuses', 'notify',
            function ($scope, $stateParams, $state, $uibModalInstance, restCashier,  pm, showVerification, statuses, notify) {
                var controller = this;
                controller.submitCalled = false;

                controller.options = {removeChromeAutoComplete:true};
                controller.model = {
                    status: pm.status.id,
                    verifiedModel: pm.verified === true ? 0 : pm.verified === false ? 1 : 2,
                    contraAccount: pm.contraAccount
                };

                controller.fields = [
                    {
                        className: 'col-xs-12',
                        key: 'method',
                        templateOptions : {
                            label : "",
                            description : "",
                            placeholder : "",
                        },
                        template:
                            '<div class="form-group">' +
                            '<label class="control-label">{{to.label}}</label>' +
                            '<table>' +
                            '   <tr>' +
                            '       <td>' +
                            '           <img style="height:50px;transform-origin: left;transform: scale(1);" src="data:'+pm.processorIcon.filetype+';base64,'+pm.processorIcon.base64+'" alt="">' +
                            '       </td>' +
                            '       <td>' +
                                        '<span>'+pm.name+'</span>' +
                            '       </td>' +
                            '   </tr>' +
                            '</table>' +
                            '<span class="description">{{to.description}}</span>' +
                            '</div>',
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.METHOD" | translate'
                        }
                    },
                    {
                        className: 'col-xs-12',
                        key: 'status',
                        type: 'ui-select-single',
                        templateOptions : {
                            label : "",
                            required : true,
                            optionsAttr: 'bs-options',
                            description : "",
                            valueProp : 'id',
                            labelProp : 'name',
                            optionsAttr: 'ui-options', "ngOptions": 'ui-options',
                            placeholder : 'Select available status type',
                            options : []
                        },
                        controller: ['$scope', function($scope) {
                            for (var i = 0; i < statuses.length; i++) {
                                if (statuses[i].name === "HISTORIC") {
                                    statuses.splice(i, 1);
                                }
                            }
                            $scope.to.options = statuses;
                        }],
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.STATUS" | translate',
                            'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.EDIT.STATUS.DESCRIPTION" | translate'
                        }
                    }
                ];

                controller.getVerifiedOptions = function() {
                    var options = [
                        {value: 0, label: 'Success'},
                        {value: 1, label: 'Fail'},
                    ];
                    if (controller.model.verifiedModel === 2) {
                        options.push({value: 2, label: 'Unverified'});
                    }
                    return options;
                }

                controller.getVerified = function() {
                    if (controller.model.verifiedModel === undefined || controller.model.verifiedModel === null) return null; // Both
                    switch (controller.model.verifiedModel) {
                        case 0:
                            return true; // Success
                        case 1:
                            return false; // Fail
                        case 2:
                            return null; // Unverified
                    }
                }

                if (showVerification) {
                    controller.fields.push(
                    {
                        className: 'col-xs-6',
                        key: 'verifiedModel',
                        type: 'ui-select-single',
                        templateOptions : {
                            label: 'Verified?',
                            valueProp: 'value',
                            labelProp: 'label',
                            optionsAttr: 'ui-options', ngOptions: 'ui-options',
                            required: true,
                            options: controller.getVerifiedOptions()
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.VERIFIED" | translate'
                        }
                    });
                    /*controller.fields.push(
                    {
                        className: 'top-space-3 col-xs-12',
                        type: 'checkbox2',
                        key: 'verified',
                        templateOptions: {
                            label: 'Verified?',
                            fontWeight: 'bold',
                            required: true
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.VERIFIED" | translate'
                        }
                    });*/
                }
                if (showVerification) {
                    controller.fields.push(
                    {
                        className: 'top-space-3 col-xs-12',
                        type: 'checkbox2',
                        key: 'contraAccount',
                        templateOptions: {
                            label: 'Contra Account?',
                            fontWeight: 'bold',
                            required: true
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.CONTRA_ACCOUNT" | translate'
                        }
                    });
                }

                controller.fields.push(
                    {
                        className: "col-xs-12",
                            key: "comment",
                        type: "input",
                        optionsTypes: ['editable'],
                        templateOptions: {
                        required : true,
                            label: "", description: "", placeholder: "",
                            minlength: 5, maxlength: 1000
                    },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.EDIT.COMMENT.NAME" | translate',
                                'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.EDIT.COMMENT.PLACEHOLDER" | translate',
                                'templateOptions.description': '"UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.EDIT.COMMENT.DESCRIPTION" | translate'
                        }
                    });

                controller.submit = function() {

                    controller.submitCalled = true;
                    if (controller.form.$invalid) {
                        angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                        notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                        return false;
                    }

                    if (controller.model.contraAccount === true && !controller.getVerified()) {
                        notify.error('"UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.EDIT.ERROR.UNVERIFIED_CONTRA_ACCOUNT" | translate');
                        return false;
                    }

                    restCashier.paymentMethodStatusUpdate($stateParams.domainName, pm.id, controller.model.status, controller.getVerified(), controller.model.contraAccount,  controller.model.comment).then(function (response) {
                        $uibModalInstance.close(response);
                    }).catch(function(error) {
                        notify.error('"UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.EDIT.ERROR.UNABLE_TO_SAVE" | translate');
                    });

                };

                controller.cancel = function() {
                    $uibModalInstance.dismiss('cancel');
                };
            }]);
