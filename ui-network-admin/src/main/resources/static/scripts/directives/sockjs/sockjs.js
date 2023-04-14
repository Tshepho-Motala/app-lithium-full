/**
 * Copied in part from https://github.com/bendrucker/angular-sockjs/blob/master/src/index.js
 * 
 * 
 */

'use strict';

(function() {
	var sockjsModule;

	sockjsModule = angular.module('lithium');

	sockjsModule
	.value('SockJS', window.SockJS)
	.provider('socketFactory', function() {
		// expose to provider
		this.$get = function(SockJS, $timeout) {
			var asyncAngularify = function(socket, callback) {
				return callback ? function() {
					console.log(callback);
					var args = arguments;
					$timeout(function() {
						callback.apply(socket, args);
					}, 0);
				} : angular.noop;
			};
			return function socketFactory(options) {
				options = options || {};
				console.log(options);
				var socket = options.socket || new SockJS(options.url);
				console.log(socket)
				var wrappedSocket = {
					callbacks : {},
					setHandler : function(event, callback) {
						console.log(event);
						socket['on' + event] = asyncAngularify(socket, callback);
						return this;
					},
					removeHandler : function(event) {
						delete socket['on' + event];
						return this;
					},
					send : function() {
						return socket.send.apply(socket, arguments);
					},
					close : function() {
						return socket.close.apply(socket, arguments);
					}
				};
				return wrappedSocket;
			};
		};
		this.$get.$inject = [ 'SockJS', '$timeout' ];
	});
}).call(this);