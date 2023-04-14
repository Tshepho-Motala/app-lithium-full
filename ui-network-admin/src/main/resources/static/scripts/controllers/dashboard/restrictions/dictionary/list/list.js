'use strict'

angular.module('lithium').controller('RestrictionsListController', ['$state', '$scope', '$rootScope', '$filter', '$dt', 'DTOptionsBuilder', '$translate', '$userService',
    function($state, $scope, $rootScope, $filter, $dt, DTOptionsBuilder, $translate, $userService) {
        var controller = this;

        controller.legendCollapsed = true;
        controller.model = {};
        controller.fields = [
            {
                className: 'col-md-3 col-xs-12',
                key: 'enabled',
                type: 'ui-select-single',
                templateOptions : {
                    label: 'Enabled',
                    valueProp: 'value',
                    labelProp: 'label',
                    optionsAttr: 'ui-options', ngOptions: 'ui-options',
                    options: [
                        {value: 0, label: 'Both'},
                        {value: 1, label: 'Enabled'},
                        {value: 2, label: 'Disabled'},
                    ]
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.ENABLED.NAME" | translate'
                }
            }
        ];

        controller.domains = $userService.domainsWithAnyRole(["ADMIN", "RESTRICTIONS_*"]);

        controller.domainSelect = function() {
            controller.selectedDomains = [];
            for (var d = 0; d < controller.domains.length; d++) {
                if (controller.domains[d].selected)
                    controller.selectedDomains.push(controller.domains[d].name);
            }
            if (controller.selectedDomains.length == controller.domains.length) {
                controller.selectedDomainsDisplay = "Domain";
            } else {
                controller.selectedDomainsDisplay = "Selected (" + controller.selectedDomains.length + ")";
            }
        };

        controller.domainSelectAll = function() {
            for (var d = 0; d < controller.domains.length; d++) controller.domains[d].selected = true;
            controller.domainSelect();
        };

        controller.domainSelectAll();

        controller.commaSeparatedSelectedDomains = function() {
            var s = '';
            for (var i = 0; i < controller.selectedDomains.length; i++) {
                if (s.length > 0) s += ',';
                s += controller.selectedDomains[i];
            }
            return s;
        }

        controller.toggleLegendCollapse = function() {
            controller.legendCollapsed = !controller.legendCollapsed;
        }

        controller.resetFilter = function(collapse) {
            if (collapse) {
                controller.toggleLegendCollapse();
            }
            controller.model.enabled = null;
            controller.applyFilter(true);
        }

        controller.applyFilter = function(toggle) {
            if (toggle === true) {
                controller.toggleLegendCollapse();
            }
            controller.refresh();
        }

        controller.isEnabled = function(isEnabled) {
            if (isEnabled === undefined || isEnabled === null) return null; // Both
            switch (isEnabled) {
                case 0:
                    return null; // Both
                case 1:
                    return true; // Enabled entries
                case 2:
                    return false; // Disabled entries
            }
        }

        var baseUrl = "services/service-limit/backoffice/restrictions/table?1=1";
        var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'desc']]);
        controller.table = $dt.builder()
        .column($dt.column('id').withTitle('ID').notVisible())
        .column($dt.column('domain.name').withTitle($translate('UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.DOMAIN.NAME')))
        .column($dt.column('name').withTitle($translate('UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.NAME.NAME')))
        .column(
            $dt.labelcolumn(
                '',
                [{lclass: function(data) {
                        if (data.systemRestriction) return 'warning';
                        return '';
                    },
                    text: function(data) {
                        if (data.systemRestriction) return 'SYSTEM';
                        return '';
                    },
                    uppercase: true
                }]
            )
        )
        .column(
            $dt.labelcolumn(
                $translate('UI_NETWORK_ADMIN.RESTRICTIONS.FIELDS.ENABLED.NAME'),
                [{lclass: function(data) {
                        if (data.enabled) return 'success';
                        return 'danger';
                    },
                    text: function(data) {
                        if (data.enabled) return 'GLOBAL.STATE.ENABLED';
                        return 'GLOBAL.STATE.DISABLED';
                    },
                    uppercase: true
                }]
            )
        )
        .column(
            $dt.linkscolumn(
                "",
                [
                    {
                        permission: "restrictions_*",
                        permissionType: "any",
                        permissionDomain: function(data) {
                            return data.domain.name;
                        },
                        title: "GLOBAL.ACTION.OPEN",
                        href: function(data) {
                            return $state.href("dashboard.restrictions.dictionary.view", { id:data.id });
                        }
                    }
                ]
            )
        )
        .options(
            {
                url: baseUrl,
                type: 'GET',
                data: function(d) {
                    d.domains = controller.commaSeparatedSelectedDomains(),
                    d.enabled = controller.isEnabled(controller.model.enabled)
                }
            },
            null,
            dtOptions,
            null
        )
        .build();

        controller.refresh = function() {
            controller.table.instance.rerender(true)
        }


       // Domain select
        $rootScope.provide.dropDownMenuProvider['domainList']  = () => {
            return controller.domains
        }
        $rootScope.provide.dropDownMenuProvider['domainsChange'] = (data) => {
            const domainNames = []
            data.forEach(el=> {
                domainNames.push(el.name)
            })
            controller.selectedDomains = domainNames
            controller.selectedDomainsCommaSeperated = domainNames.join(',')
            controller.refresh();
        }

        window.VuePluginRegistry.loadByPage("DomainSelect")
    }
]);
