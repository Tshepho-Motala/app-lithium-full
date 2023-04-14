'use strict'

angular.module('lithium').controller('domainRelationshipAddController', ['$scope', '$rootScope', '$stateParams', 'notify', 'errors', 'rest-domain', 'EcosysRest',
	function($scope, $rootScope, $stateParams, notify, errors, restDomain, ecosysRest) {
        var controller = this;

		$scope.title = 'UI_NETWORK_ADMIN.ECOSYSTEMS.LINK.ADD_TITLE';
		$scope.description = 'UI_NETWORK_ADMIN.ECOSYSTEMS.LINK.ADD_DESC';
        
        controller.ecosystemId = $stateParams.id;
        controller.domainId = null;
        controller.relationshipTypeId = null;
        //Static ecosystem domain relationship types dropdown
        controller.relationshipTypes = [
            {
                "id": 1,
                "code": "ECOSYSTEM ROOT"
            },
            {
                "id": 2,
                "code": "ECOSYSTEM MUTUALLY EXCLUSIVE",
            },
            {
                "id": 3,
                "code": "ECOSYSTEM MEMBER",
            }
        ];
        controller.model = {
            enabled: true,
            deleted: false
        };
        controller.options = {};
        controller.fields = [{
            className: "row v-reset-row ",
            fieldGroup: [
                {
                    "type" : 'checkbox',
                    "key" : 'enabled',
                    "templateOptions" : {
                        "label": 'Enabled',
                    },
                    expressionProperties: {
                        'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.ENABLED.LABEL" | translate',
                        'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.ENABLED.DESCRIPTION" | translate'
                    }
                },{
                    "type" : 'checkbox',
                    "key" : 'deleted',
                    "templateOptions" : {
                        "label": 'Delete',
                    },
                    expressionProperties: {
                        'templateOptions.label': '"Delete" | translate',
                        'templateOptions.description': '" | translate'
                    }
                }
            ]
        }];
        controller.domainSelected = function (domain) {
            controller.domainId = domain.id;
            controller.selectedDomain = domain;
        };
        controller.relationshipTypeSelect = function (relationshipType) {
            controller.selectedRelationshipType = relationshipType;
            controller.relationshipTypeId = relationshipType.id;
        };

        // ecosysRest.relationshiptypes().then(function(response) {
        //     controller.relationshipTypes = response.plain();
        // });

        restDomain.findAllDomains().then(function(response) {
            controller.domains = response.plain();
        });

        controller.save = function() {
            ecosysRest.domainRelationshipAdd(controller.ecosystemId, controller.domainId, controller.relationshipTypeId).then(function(response) {
                notify.success("UI_NETWORK_ADMIN.RELATIONSHIP_TYPE.SUCCESS_MESSAGE");
            })
        }
        
    }
]);