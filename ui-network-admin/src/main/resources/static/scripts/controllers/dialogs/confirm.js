'use strict';

angular.module('lithium')
	.controller('ConfirmationDialog', ['$uibModalInstance', '$scope', 'title', 'message', 'confirmButtonText',
     'cancelButtonText', 'onConfirm','confirmIcon', 'confirmType',
		function ($uibModalInstance , $scope, title, message, confirmButtonText, cancelButtonText, onConfirm, confirmIcon, confirmType) {
			var controller = this;

            const defaultCancelAction = () => {
                $uibModalInstance.dismiss('cancel');
            }

            $scope.title = title
            $scope.message = message
            $scope.confirmButtonText = confirmButtonText || 'GLOBAL.ACTION.SUBMIT'
            $scope.cancelButtonText = cancelButtonText || 'GLOBAL.ACTION.CANCEL'
            $scope.icon =  confirmIcon || "check"
            $scope.confirmType = confirmType || 'primary'

            controller.onConfirm = onConfirm
            controller.onCancel = defaultCancelAction

}]);
