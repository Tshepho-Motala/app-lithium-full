'use strict'

angular.module('lithium').controller('NotificationsViewController', ['notification', '$translate', 'notify', 'NotificationRest',
	function(notification, $translate, notify, notificationRest) {
		var controller = this;
		
		controller.model = notification.plain();

		controller.isPullChannel = (notificationChannel) => {
			return "PULL" === `${notificationChannel.channel.name}`.toUpperCase();
		}

		controller.templateName = (notificationChannel) => {
			return controller.isPullChannel(notificationChannel) ? 'N/A': notificationChannel.templateName;
		}

		controller.templateLang = (notificationChannel) => {
			return controller.isPullChannel(notificationChannel) ? 'N/A': notificationChannel.templateLang;
		}

		controller.isSystemNotification = () => {
			return angular.isDefined(controller.model.systemNotification) && controller.model.systemNotification === true;
		}
	}
]);
