angular.module('lithium')
	.directive('buttonBar',function(){
		return {
			template: '<div class="row v-reset-row  button-bar"><div class="col-xs-12"><ng-transclude></ng-transclude></div></div>',
			transclude: true, restrict: 'E', replace: true,
		};
	})
	.directive('buttonCustom', function() {
		return {
			template: '<button type="{{btype}}" class="btn btn-{{bclass}}" ng-disabled="{{disabled}}"><i class="fa fa-{{icon}}"></i> <span>{{&nbsp; text | translate }}</span></button>',
			restrict: 'E', replace: true, scope: true,
			link: function(scope, element, attributes) {
				scope.icon = attributes.icon;
				scope.text = (attributes.text) || '';
				scope.bclass = (attributes.bclass) || 'default';
				scope.btype = (attributes.btype) || "button";
				scope.disabled = (attributes.disabled) || false;
			},
		};
	})
	.directive('buttonExpand',function(){
		return {
			template: '<button-custom icon="expand"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonCollapse',function(){
		return {
			template: '<button-custom icon="compress"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonUpdate',function(){
		return {
			template: '<button-custom btype="submit" class="success" icon="pencil" text="GLOBAL.ACTION.UPDATE" ng-disabled="disableSubmitButton"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonSubmit',function(){
		return {
			template: '<button-custom btype="submit" bclass="primary submit-button" icon="floppy-o" text="GLOBAL.ACTION.SUBMIT" ng-disabled="disableSubmitButton"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonSave',function(){
		return {
			template: '<button-custom btype="submit" bclass="primary submit-button" icon="floppy-o" text="GLOBAL.ACTION.SAVE"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonProfile', function() {
		return {
			template: '<button-custom icon="user" text="Profile"></button-custom>',
			restrict: 'E', scope: true
		}
	})
	.directive('buttonContinue',function() {
		return {
			template: '<button-custom icon="check-square-o" text="GLOBAL.ACTION.CONTINUE"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonClear',function() {
		return {
			template: '<button-custom icon="eraser" text="GLOBAL.ACTION.CLEAR"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonApply',function() {
		return {
			template: '<button-custom icon="eraser" text="GLOBAL.ACTION.APPLY"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonFilter',function() {
		return {
			template: '<button-custom icon="filter" text="GLOBAL.ACTION.FILTER"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonOverride',function() {
		return {
			template: '<button-custom icon="plus" text="GLOBAL.ACTION.OVERRIDE"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonSaveOrder',function() {
		return {
			template: '<button-custom icon="check-square-o" bclass="primary" text="GLOBAL.ACTION.SAVEORDER"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonAdd',function() {
		return {
			template: '<button-custom icon="plus" text="GLOBAL.ACTION.ADD"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonNew',function() {
		return {
			template: '<button-custom icon="plus" text="GLOBAL.ACTION.NEW"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonEdit',function() {
		return {
			template: '<button-custom icon="pencil-square-o" text="GLOBAL.ACTION.MODIFY"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonView',function() {
		return {
			template: '<button-custom icon="eye" text="GLOBAL.ACTION.VIEW"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonBack',function(){
		return {
			template: '<button-custom icon="level-up" text="GLOBAL.ACTION.BACK"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonReset',function(){
		return {
			template: '<button-custom icon="history" text="GLOBAL.ACTION.RESET"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonCancel',function(){
		return {
			template: '<button-custom icon="ban" text="GLOBAL.ACTION.CANCEL" bclass="default pull-right"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonDelete',function(){
		return {
			template: '<button-custom icon="trash-o" text="GLOBAL.ACTION.DELETE" bclass="danger"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonDisable',function(){
		return {
			template: '<button-custom icon="ban" text="GLOBAL.ACTION.DISABLE"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonEnable',function(){
		return {
			template: '<button-custom icon="check" text="GLOBAL.ACTION.ENABLE"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonChangePassword',function(){
		return {
			template: '<button-custom icon="key" text="GLOBAL.ACTION.CHANGEPASSWORD" bclass="info"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonCloseWindow',function() {
		return {
			template: '<button-custom bclass="default pull-right" icon="times"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonRefresh',function(){
		return {
			template: '<button-custom icon="refresh" text="GLOBAL.ACTION.REFRESH"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonSignout',function(){
		return {
			template: '<button-custom icon="sign-out" text="GLOBAL.ACTION.SIGNOUT" bclass="danger pull-right"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonDownloadExcel',function(){
		return {
			template: '<button-custom icon="file-excel-o" text="GLOBAL.ACTION.DOWNLOADEXCEL"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonDownload',function(){
		return {
			template: '<button-custom icon="download" text="GLOBAL.ACTION.DOWNLOADFILE"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonUpload',function(){
		return {
			template: '<button-custom icon="upload" text="GLOBAL.ACTION.UPLOADFILE"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonManage',function(){
		return {
			template: '<button-custom icon="pencil-square-o" text="UI_NETWORK_ADMIN.ECOSYSTEMS.BUTTONS.MANAGE_BUTTON"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonModify',function(){
		return {
			template: '<button-custom icon="pencil-square-o" text="UI_NETWORK_ADMIN.ECOSYSTEMS.BUTTONS.MODIFY_BUTTON""></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonRelationship',function(){
		return {
			template: '<button-custom icon="plus" text="UI_NETWORK_ADMIN.ECOSYSTEMS.BUTTONS.RELATIONSHIP_BUTTON"></button-custom>',
			restrict: 'E', scope: true
		};
	})
	.directive('buttonDownloadCsv',function(){
		return {
			template: '<button-custom icon="file-excel-o" text="GLOBAL.ACTION.EXPORTTOCSV"></button-custom>',
			restrict: 'E', scope: true
		};
	})
;

