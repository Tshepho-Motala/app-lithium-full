'use strict';

angular.module('lithium')
    .controller('SetUserRestrictionModal', ['user', '$translate', '$uibModalInstance', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'UserRestrictionsRest',
            function (user,$translate, $uibModalInstance, $scope, notify, errors, bsLoadingOverlayService, rest) {
                var controller = this;

                controller.options = {};
                controller.model = {};

                controller.fields = [
                    {
                        className: 'col-xs-12',
                        key: "domainRestrictionSet",
                        type: "ui-select-single",
                        templateOptions: {
                            label: "Restriction Set",
                            description: "Choose the restriction set that you want to place on the user",
                            required: true,
                            optionsAttr: 'bs-options',
                            valueProp: 'value',
                            labelProp: 'label',
                            optionsAttr: 'ui-options', "ngOptions": 'ui-options',
                            placeholder: '',
                            options: [],
                            order:1
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.FIELDS.RESTRICTIONSET.NAME" | translate',
                            'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.FIELDS.RESTRICTIONSET.DESCRIPTION" | translate'
                        },
                        controller: ['$scope', function($scope) {
                            rest.getEligibleRestrictionSetsForUser(user.domain.name, user.guid).then(function(response) {
                                var options = [];
                                var response = response.plain();
                                for (var i = 0; i < response.length; i++) {
                                    var opt = response[i];
                                    options.push({label: opt.name, value: opt});
                                }
                                $scope.to.options = options;
                            });
                        }]
                    },
                    {
                        className: 'col-xs-12',
                        key: "subordinate",
                        type: "ui-select-single",
                        templateOptions: {
                            label: "",
                            description: "",
                            required: true,
                            optionsAttr: 'bs-options',
                            valueProp: 'value',
                            labelProp: 'label',
                            optionsAttr: 'ui-options', "ngOptions": 'ui-options',
                            placeholder: '',
                            options: [],
                            order: 2
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.FIELDS.RESTRICTION_SUBORDINATE.NAME" | translate',
                            'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.FIELDS.RESTRICTION_SUBORDINATE.DESCRIPTION" | translate'
                        },
                        hideExpression: '!model.domainRestrictionSet || !model.domainRestrictionSet.altMessageCount'
                    },
                    {
                        
                        className: "col-xs-12",
                        key: "comment",
                        type: "textarea",
                        templateOptions: {
                            label: "", description: "", placeholder: "",
                            required: true, 
                            minlength: 5, 
                            maxlength: 65535,
                            order:3
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.NAME" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.PLACEHOLDER" | translate',
                            'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.DESCRIPTION" | translate'
                        }
                    },
                ];

                controller.referenceId = 'setuserrestriction-overlay';
                controller.submit = function() {
                    bsLoadingOverlayService.start({referenceId:controller.referenceId});
                    if (controller.form.$invalid) {
                        angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                        notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                        bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                        return false;
                    }

                    rest.set(user.domain.name, user.guid, controller.model.domainRestrictionSet.id, user.id, controller.model.comment, controller.model.subordinate).then(function(response) {
                        if (response._status === 1) {
                            notify.error('UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.ADD.FAILURE');
                        } else if(response._status === 200) {
                            notify.success("UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.ADD.SUCCESS");
                            $uibModalInstance.close(response.plain());
                        } else {
                            notify.error('UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.ADD.ERROR');
                        }
                    }).catch(function() {
                        errors.catch('', false);
                    }).finally(function() {
                        bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                    });
                };

                controller.cancel = function() {
                    $uibModalInstance.dismiss('cancel');
                };

                controller.getCompsBlockSubordinates = (count) => {
                    return angular.range(1, count).map(altCount => ({ label: $translate.instant("UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.SUBORDINATE.MESSAGE."+altCount), value: altCount  }))
                }

                controller.getCasinoBlockSubordinates = (altMessageCount) => {
                    var subordinateOptions = angular.range(1, altMessageCount).map(altCount => ({ label: $translate.instant("UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.CASINO_BLOCK.SUBORDINATE.MESSAGE."+altCount), value: altCount  }))
                    subordinateOptions.push({ label: $translate.instant("UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.CASINO_BLOCK.SUBORDINATE.MESSAGE.INDEFINITELY"), value: null })
                    return subordinateOptions;
                }

                controller.getSortedFields = ()=> {
                    return controller.fields.sort(function compare( a, b ) {
                        if ( a.templateOptions.order < b.templateOptions.order ){
                          return -1;
                        }
                        if ( a.templateOptions.order > b.templateOptions.order ){
                          return 1;
                        }
                        return 0;
                      }
                    )
                }

                controller.isInterventionCompsBlock = () => {

                    if(!controller.model.domainRestrictionSet) {
                        return false;
                    }

                    const name  = controller.model.domainRestrictionSet.name.toUpperCase().replaceAll(new RegExp(/\s+/,"gm"), "_");
                    return name === "INTERVENTION_COMPS_BLOCK";
                }

                controller.isSystemRestrictionCasinoBlock = () => {

                    if(!controller.model.domainRestrictionSet) {
                        return false;
                    }

                    const name  = controller.model.domainRestrictionSet.name.toUpperCase().replaceAll(new RegExp(/\s+/,"gm"), "_");
                    return name === "INTERVENTION_CASINO_BLOCK" || "PLAYER_CASINO_BLOCK";
                }

                controller.addSubordinateControl = () => {
                    controller.fields.push(
                        {
                            className: 'col-xs-12',
                            key: "subordinate",
                            type: "ui-select-single",
                            templateOptions: {
                                label: "",
                                description: "",
                                required: true,
                                optionsAttr: 'bs-options',
                                valueProp: 'value',
                                labelProp: 'label',
                                optionsAttr: 'ui-options', "ngOptions": 'ui-options',
                                placeholder: '',
                                options: [],
                                order:2
                            },
                            expressionProperties: {
                                'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.FIELDS.RESTRICTION_SUBORDINATE.NAME" | translate',
                                'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.FIELDS.RESTRICTION_SUBORDINATE.DESCRIPTION" | translate'
                            },
                            
                    })
                }

                $scope.$watch(() => controller.model['domainRestrictionSet'], (newVal, oldVal) => {
                    if(newVal && (controller.isInterventionCompsBlock())) {

                        const fields = controller.getSortedFields();
                        const subordinates = controller.getCompsBlockSubordinates(newVal.altMessageCount)
                        fields.forEach(field => {
                            if(field.key === 'subordinate') {
                                field.templateOptions.options = subordinates
                                controller.model.subordinate = subordinates.length === 0 ? null: 1;
                            }
                        });
                        return;
                    } else if(controller.model.domainRestrictionSet && controller.model.domainRestrictionSet.altMessageCount
                    && controller.isSystemRestrictionCasinoBlock()) {
                        // controller.addSubordinateControl();
                        const subordinates = controller.getCasinoBlockSubordinates(newVal.altMessageCount);
                        controller.fields.forEach(field => {
                            if(field.key === 'subordinate') {
                                field.templateOptions.options = subordinates
                                controller.model.subordinate = subordinates.length === 0 ? null: 1;
                            }
                        });
                        return;
                    }
                    controller.model.subordinate = null;

                })
            }
        ]
    );
