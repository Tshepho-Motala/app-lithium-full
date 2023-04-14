'use strict';

angular.module('lithium').controller('DomainAvatarAddModal', ['$uibModalInstance', 'domain', 'notify', 'errors', 'bsLoadingOverlayService', 'AvatarRest',
function ($uibModalInstance, domain, notify, errors, bsLoadingOverlayService, avatarRest) {
	var controller = this;
	
	controller.referenceId = 'addavatar-overlay';
	
	controller.model = {};
	
	controller.fields = [
		{
			type: 'checkbox',
			key: 'enabled',
			templateOptions: {
				label: '', description: ''
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.AVATARS.FIELDS.ENABLED.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.AVATARS.FIELDS.ENABLED.DESCRIPTION" | translate'
			}
		},
		{
			type: 'checkbox',
			key: 'isDefault',
			templateOptions: {
				label: '', description: ''
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.AVATARS.FIELDS.DEFAULT.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.AVATARS.FIELDS.DEFAULT.DESCRIPTION" | translate'
			}
		},
		{
			className : 'col-xs-12',
			key: "name",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.AVATARS.FIELDS.NAME.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.AVATARS.FIELDS.NAME.DESCRIPTION" | translate'
			}
		},
		{
			className : 'col-xs-12',
			key: "description",
			type: "textarea",
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: false
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.AVATARS.FIELDS.DESCRIPTION.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.AVATARS.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
			}
		},
		{
			className: "col-xs-12",
			type:"image-upload",
			key:"image",
			templateOptions:{
				type: "",
				label: "",
				required: true,
				description: "",
				maxsize: 2048, //Maximum file size in kilobytes (KB)
				minsize: 1,
				accept: "image/*",
				preview: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.AVATARS.FIELDS.IMAGE.NAME" | translate'
			}
		}
	]
	
	controller.onSubmit = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		
		controller.model.graphicBasic = controller.model.image;
		
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		avatarRest.add(domain.name, controller.model).then(function(response) {
			if (response._status === 0) {
				notify.success('UI_NETWORK_ADMIN.DOMAIN.AVATARS.ADD.SUCCESS');
			} else {
				notify.error('UI_NETWORK_ADMIN.DOMAIN.AVATARS.ADD.ERROR');
			}
			$uibModalInstance.close(response.plain());
		}).catch(function(error) {
			errors.catch('UI_NETWORK_ADMIN.DOMAIN.AVATARS.ADD.ERROR', false);
		}).finally(function () {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);

