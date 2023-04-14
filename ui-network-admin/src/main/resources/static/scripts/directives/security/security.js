/**
* This was copied from https://github.com/concretesolutions/ng-security/blob/ebdf3c3cfbeac9f7c76dffadc15e3a3e27fc9aec/dist/ngsecurity.js
* addapting to fit into lithium model.
*/
angular
	.module('litSecurity')
	//.directive('litIfAuthenticated', ifAuthenticated)
	//.directive('litIfAnonymous', ifAnonymous)
	.directive('litIfPermission', ifPermission)
	.directive('litIfDomainsPermission', ifDomainsPermission)
	.directive('litAdminPermission', adminPermission)
	//.directive('litIfPermissionModel', ifPermissionModel)
	.directive('litEnabledPermission', enabledPermission);
	
	//ifAuthenticated.$inject = ['$security', '$animate', '$rootScope'];
	//ifAnonymous.$inject = ['$security', '$animate', '$rootScope'];
	ifPermission.$inject = ['$security', '$rootScope'];
	ifDomainsPermission.$inject = ['$userService', '$security', '$rootScope'];
	adminPermission.$inject = ['$security', '$rootScope'];
	//ifPermissionModel.$inject = ['$security', '$parse'];
	enabledPermission.$inject = ['$security'];
	
	/**
	function ifAuthenticated ($security, $animate, $rootScope) {
		var directive = {
			multiElement: true,
			transclude: 'element',
			priority: 600,
			terminal: true,
			restrict: 'A',
			$$tlb: true,
			link: link
		};
		return directive;
		
		function link (scope, element, attrs, ctrl, transclude) {
			var render = new RenderHandler(scope, element, attrs, ctrl, transclude, $animate);
			scope.deregister = $rootScope.$on('authChanged', function (event, status) {
				render.handle(status);
			});
			scope.$on('$destroy', function() {
				scope.deregister();
			});
			render.handle($security.isAuthenticated());
		}
	}
	
	function ifAnonymous ($security,  $animate,  $rootScope) {
		var directive = {
			multiElement: true,
			transclude: 'element',
			priority: 600,
			terminal: true,
			restrict: 'A',
			$$tlb: true,
			link: link
		};
		return directive;
		
		function link (scope, element, attrs, ctrl, transclude) {
			var render = new RenderHandler(scope, element, attrs, ctrl, transclude, $animate);
			scope.deregister = $rootScope.$on('authChanged', function (event, status) {
				render.handle(!status);
			});
			scope.$on('$destroy', function(){
				scope.deregister();
			});
			render.handle(!$security.isAuthenticated());
		}
	}
	*/
	
	function adminPermission($security, $rootScope) {
		var directive = {
			link: link,
			restrict: 'A'
		};
		return directive;
		
		function link (scope, element, attrs) {
			scope.security = $security;
			var defaultStyle = element.css('display'),
			permissions = ['ADMIN'],
			permissionDomain = attrs.litPermissionDomain,
			permissionCheck = attrs.litPermissionCheck;
			
			scope.$watch(function () {
				return scope.security.getPermissions(permissionDomain);
			}, function () {
				if (scope.security.getPermissionValidation('ALL')(permissions, permissionDomain, permissionCheck)) {
					element.css('display', defaultStyle);
				} else {
					element.css('display', 'none');
				}
			}, true);
			console.log(scope.security.user.roles);
		}
	}
	
	function ifPermission($security, $rootScope) {
		/** interface */
		var directive = {
			link: link,
			restrict: 'A'
		};
		return directive;
		
		/** implementation */
		function link (scope, element, attrs) {
			scope.security = $security;
			var defaultStyle = element.css('display'),
			permissionType = attrs.litPermissionType,
			permissions = attrs.litIfPermission.split(','),
			permissionDomain = attrs.litPermissionDomain,
			permissionCheck = attrs.litPermissionCheck;
			
			scope.$watch(function () {
				return scope.security.getPermissions(permissionDomain);
			}, function () {
				// FIXME: We sometimes receive a JSON string here which prevents this from working as expected.
				//		  I'm quickfixing for now instead of finding and fixing in several screens.
				try {
					if (angular.isUndefined(permissionDomain)) {
						permissionDomain = scope.security.domainName();
					} else {
						permissionDomain = JSON.parse(permissionDomain);
						permissionDomain = permissionDomain.name;
					}
				} catch (error) {
					// Then this isn't a JSON string. We can just proceed as normal.
				}

				if (scope.security.hasAdminRoleForDomain(permissionDomain)) {
					element.css('display', defaultStyle);
				} else {
					permissions.push("ADMIN");
					if (scope.security.getPermissionValidation(permissionType)(permissions, permissionDomain, permissionCheck)) {
						element.css('display', defaultStyle);
					} else {
						element.css('display', 'none');
					}
				}
			}, true);
		}
	}

	function ifDomainsPermission($userService, $security, $rootScope) {
		var directive = {
			link: link,
			restrict: 'A'
		};
		return directive;

		function link (scope, element, attrs) {
			scope.security = $security;
			var defaultStyle = element.css('display'),
				permissionType = attrs.litPermissionType,
				permissions = attrs.litIfDomainsPermission.split(','),
				permissionDomains = $userService.domainsWithAnyRole(permissions),
				permissionCheck = attrs.litPermissionCheck;

			let permissionFound = false;
			for (let i = 0; i < permissionDomains.length; i++)
			{
				if (scope.security.hasAdminRoleForDomain(permissionDomains[i].name)) {
					permissionFound = true;
					break;
				} else {
					permissions.push("ADMIN");
					if (scope.security.getPermissionValidation(permissionType)(permissions, permissionDomains[i].name, permissionCheck)) {
						permissionFound = true;
						break;
					} else {
						permissionFound = false;
					}
				}
			}
			if (permissionFound)
				element.css('display', defaultStyle);
			else
				element.css('display', 'none');
		}
	}

	/**
	function ifPermissionModel ($security, $parse) {
		var directive = {
			link: link,
			restrict: 'A'
		};
		return directive;
		
		function link (scope, element, attrs) {
			var defaultStyle = element.css('display'),
			permissionType = attrs.litPermissionType;
			
			var updateElement = function (permissions) {
				if ($security.hasPermission(permissions) || $security.getPermissionValidation(permissionType)(permissions)) {
					element.css('display', defaultStyle);
				} else {
					element.css('display', 'none');
				}
			};
			
			scope.$watch(function () {
				return $parse(attrs.litIfPermissionModel)(scope);
			}, function (permissions) {
				updateElement(permissions);
			});
			
			scope.$watch(function () {
				return $security.getPermissions();
			}, function () {
				var permissions = $parse(attrs.litIfPermissionModel)(scope);
				updateElement(permissions);
			}, true);
		}
	}
	*/
	
	function enabledPermission($security) {
		/** interface */
		var directive = {
			link: link,
			restrict: 'A'
		};
		return directive;
		
		/** implementation */
		function link (scope, element, attrs) {
			scope.security = $security;
			var permissionType = attrs.litPermissionType,
			permissions = attrs.litEnabledPermission.split(','),
			permissionDomain = attrs.litPermissionDomain,
			permissionCheck = attrs.litPermissionCheck;
			
			scope.$watch(function () {
				return scope.security.getPermissions(permissionDomain);
			}, function () {
				if (scope.security.hasAdminRoleForDomain(permissionDomain)) {
					element.removeAttr('disabled');
					element.removeAttr('ng-disabled');
				} else {
					permissions.push("ADMIN");
					if (scope.security.getPermissionValidation(permissionType)(permissions, permissionDomain, permissionCheck)) {
						element.removeAttr('disabled');
						element.removeAttr('ng-disabled');
					} else {
						element.attr('disabled', 'true');
						element.attr('ng-disabled', 'true');
					}
				}
			}, true);
		}
	}
	
	// render class
	function RenderHandler(scope, element, attrs, ctrl, transclude, $animate) {
		var block, childScope, previousElements;
		this.handle = function (expression) {
			if (expression) {
				if (!childScope) {
					transclude(function(clone, newScope) {
						childScope = newScope;
						clone[clone.length++] = document.createComment(' end lit-securityIf');
						// Note: We only need the first/last node of the cloned nodes.
						// However, we need to keep the reference to the jqlite wrapper as
						// it might be changed later
						// by a directive with templateUrl when its template arrives.
						block = {
							clone: clone
						};
						$animate.enter(clone, element.parent(), element);
					});
				}
			} else {
				if (previousElements) {
					previousElements.remove();
					previousElements = null;
				}
				if (childScope) {
					childScope.$destroy();
					childScope = null;
				}
				if (block) {
					previousElements = getBlockNodes(block.clone);
					$animate.leave(previousElements).then(function() {
						previousElements = null;
					});
					block = null;
				}
			}
		};
		
		function getBlockNodes(nodes) {
			var node = nodes[0];
			var endNode = nodes[nodes.length - 1];
			var blockNodes;
			
			for (var i = 1; node !== endNode && (node = node.nextSibling); i++) {
				if (blockNodes || nodes[i] !== node) {
					if (!blockNodes) {
						blockNodes = jqLite(slice.call(nodes, 0, i));
					}
					blockNodes.push(node);
				}
			}
			return blockNodes || nodes;
		}
	};
