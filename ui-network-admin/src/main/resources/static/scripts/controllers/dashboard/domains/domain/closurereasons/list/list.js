'use strict';

angular.module('lithium')
    .controller('DomainClosureReasonsListController', ['domain', 'notify', '$scope', 'errors', '$dt', '$uibModal', '$state', '$translate',
        function (domain, notify, $scope, errors, $dt, $uibModal, $state, $translate) {
            var controller = this;

            controller.hideDeleted = true;
            controller.reasonsSelect = function () {
                controller.selectedReasons = ['All reasons', 'Active reasons'];
                if (controller.hideDeleted) {
                    controller.selectedReasonsDisplay = "Active reasons";
                } else {
                    controller.selectedReasonsDisplay = "All reasons";
                }
            };
            controller.reasonSelectAll = function () {
                controller.hideDeleted = false;
                controller.reasonsSelect();
            };
            controller.reasonSelectActive = function () {
                controller.hideDeleted = true;
                controller.reasonsSelect();
            };
            controller.reasonSelectActive();


            var url = 'services/service-user/backoffice/' + domain.name + '/closure-reasons-crud/table?1=1&hideDeleted=' + controller.hideDeleted;
            controller.domainClosureReasonsTable = $dt.builder()
                .column($dt.column('id').withTitle($translate('UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.FIELDS.ID.NAME')))
                .column(
                    $dt.linkscolumn(
                        "",
                        [
                            {
                                permission: "CLOSURE_REASONS_*,ADMIN",
                                permissionType: "any",
                                permissionDomain: domain.name,
                                title: "GLOBAL.ACTION.OPEN",

                                href: function (data) {
                                    return $state.href("dashboard.domains.domain.closurereasons.view", {id: data.id});
                                }
                            }
                        ]
                    )
                )
                .column(
                    $dt.labelcolumn(
                        "",
                        [
                            {
                                text: function (data) {
                                    return (data.deleted === true) ? "Deleted" : "";
                                },
                                class: "danger"
                            }
                        ]
                    )
                )
                .column($dt.column('description').withTitle($translate('UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.FIELDS.DESCRIPTION.NAME')))
                .options(url)
                .order([0, 'asc'])
                .build();

            controller.add = function () {
                var modalInstance = $uibModal.open({
                    animation: true,
                    ariaLabelledBy: 'modal-title',
                    ariaDescribedBy: 'modal-body',
                    backdrop: 'static',
                    templateUrl: 'scripts/controllers/dashboard/domains/domain/closurereasons/add/add.html',
                    controller: 'DomainClosureReasonAddModal',
                    controllerAs: 'controller',
                    size: 'md',
                    resolve: {
                        domain: function () {
                            return domain;
                        }
                    }
                });

                modalInstance.result.then(function (result) {
                    controller.domainClosureReasonsTable.instance.reloadData(function () {
                    }, false);
                });
            }

            controller.tableReload = function() {
                if (!angular.isUndefined(controller.domainClosureReasonsTable.instance)) {
                    url = 'services/service-user/backoffice/' + domain.name + '/closure-reasons-crud/table?1=1';
                    url += "&hideDeleted=" + controller.hideDeleted;
                    controller.domainClosureReasonsTable.instance._renderer.options.ajax = url;
                    controller.domainClosureReasonsTable.instance.rerender();
                }
            }
        }
    ]);
