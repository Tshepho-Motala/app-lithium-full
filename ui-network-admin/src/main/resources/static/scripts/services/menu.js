'use strict';

angular.module('lithium')
.factory('$menu', ['$state', '$translate', '$log', "$security", "$filter",
	function($state, $translate, $log, $security, $filter) {
		var service = {};
		service.menuItems = {};
		
		service.setActive = function() {
			var setActiveChild = function(grandparent, parent, child) {
				child.active = false;
				child.expanded = false;
				if ($state.includes(child.route)) {
					child.active = true;
					child.expanded = true;
					parent.active = true;
					parent.expanded = true;
					if (grandparent != null) {
						grandparent.active = true;
						grandparent.expanded = true;
					}
				}
				angular.forEach(child.children, function(grandchild) {
					setActiveChild(parent, child, grandchild);
				});
			};
			
			angular.forEach(service.menuItems, function(parent) {
				parent.active = false;
				parent.expanded = false;
				if ($state.includes(parent.route)) {
					parent.active = true;
				}
				angular.forEach(parent.children, function(child) {
					if (child.route === "disabled") {
						child.disabled = true;
					}
					setActiveChild(null, parent, child);
				});
			});
		};
		
		service.add = function add(type, roles, id, route, titleKey, icon, parent) {
			if(angular.isUndefined(parent)) parent = null;
			var canAddMenu = false;
			if (type === 'OPEN') {
				canAddMenu = true;
			} else {
				if (roles === '') roles = "admin";
				angular.forEach(roles.split(','), function(role) {
					if (type === 'TREE') {
//						console.log(id+" :: "+route);
						if ($security.hasRoleInTree($filter("uppercase")(role.trim()))) {
							canAddMenu = true;
						}
					} else {
						if ($security.hasRole($filter("uppercase")(role.trim()))) {
							canAddMenu = true;
						}
					}
				});
			}
			if (canAddMenu) {
				var host = service.menuItems;
				if (parent) {
					if (!parent.children) parent.children = {};
					host = parent.children;
				}
				host[id] = { id, route, titleKey, icon };
				return host[id];
			}
		}
		
		service.destroy = function() {
			service.menuItems = {};
		}
		
		return service;
	}
]);