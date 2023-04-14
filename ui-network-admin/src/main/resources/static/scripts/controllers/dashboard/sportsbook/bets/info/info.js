'use strict';

angular.module('lithium').controller('betInfo', ['betData', '$uibModalInstance',
    function (betData, $uibModalInstance) {
        const controller = this;
        controller.betInfomation = betData;
        controller.selections = controller.betInfomation.selections;

        controller.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);
