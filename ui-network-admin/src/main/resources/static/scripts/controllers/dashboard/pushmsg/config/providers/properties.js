'use strict';

angular.module('lithium')
	.controller('EditPropertiesController', ["errors", "$filter", "rest-pushmsg", "notify", "domainProvider", "$uibModalInstance", "$rootScope", 
	function(errors, $filter, pushmsgRest, notify, domainProvider, $uibModalInstance, $rootScope) {
		var controller = this;
		
		controller.model = domainProvider;
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
		
		controller.findProperties = function() {
			pushmsgRest.domainProviderPropertiesNoDefaults(domainProvider.id).then(function(props) {
				controller.properties = props.plain();
				angular.forEach(controller.properties, function(p) {
					if (p.id !== null) {
						p.override = true;
					} else {
						p.override = false;
					}
				});
				controller.properties = $filter('orderBy')(controller.properties, '+providerProperty.name');
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		}
		controller.findProperties();
		
		controller.oneSignal = function() {
			var email = $rootScope.principal.email || '';
			var OneSignal = window.OneSignal || [];
			var appId = controller.properties[0].value;
			if (appId === null) appId = controller.properties[0].providerProperty.defaultValue;
			console.log(appId);
			OneSignal.push(function() {
				OneSignal.init({
					appId : appId,
					autoRegister : false,
					notifyButton : {
						enable : false,
					},
					allowLocalhostAsSecureOrigin : true,
				});
				OneSignal.on('subscriptionChange', function (isSubscribed) {
					console.log("The user's subscription state is now:", isSubscribed);
				});
			});
			OneSignal.push(["getTags", function(tags) {
				console.log("OneSignal getTags:");
				console.log(tags);
			}]);
			
			OneSignal.showHttpPrompt();
			OneSignal.getUserId(function(id){console.log(id)});
			
			OneSignal.push(function() {
				OneSignal.sendTags({
					username: $rootScope.principal.username,
					domainname: $rootScope.principal.domainName,
					firstname: $rootScope.principal.firstName,
					lastname: $rootScope.principal.lastName,
					email: email,
					external_user_id: $rootScope.principal.guid
				}).then(function(tagsSent) {
					// Callback called when tags have finished sending
					console.log(tagsSent);
				});
				OneSignal.setExternalUserId($rootScope.principal.guid);
//				OneSignal.setEmail(email);
			});
		}
		
		controller.onSubmit = function() {
			controller.oneSignal();
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			angular.forEach(controller.properties, function(p) {
				if ((p.override) && (p.id === null)) {
					p.id = -1;
				}
				if ((!p.override) && (p.id !== null)) {
					pushmsgRest.domainProviderPropertyDelete(domainProvider.id, p.id).then(function(prop) {
						p = prop;
						notify.success("UI_NETWORK_ADMIN.SMS.PROVIDERS.PROPERTIES.DELETESUCCESS");
					}).catch(function(error) {
						errors.catch("", false)(error)
					});
				}
			});
			
			console.log(controller.properties)
			
			pushmsgRest.domainProviderPropertiesSave(domainProvider.id, controller.properties).then(function(props) {
				controller.properties = props.plain();
				angular.forEach(controller.properties, function(p) {
					if (p.id !== null) {
						p.override = true;
					} else {
						p.override = false;
					}
				});
				controller.properties = $filter('orderBy')(controller.properties, '+providerProperty.name');
				notify.success("UI_NETWORK_ADMIN.SMS.PROVIDERS.PROPERTIES.SAVESUCCESS");
				$uibModalInstance.close(controller.properties);
			}).catch(function(error) {
				errors.catch("", false)(error)
			});
		}
	}
]);