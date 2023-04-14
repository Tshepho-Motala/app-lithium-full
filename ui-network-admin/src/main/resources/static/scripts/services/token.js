'use strict';

angular.module('litSecurity', [])
.factory('$security', securityFactory);

securityFactory.$inject = ['$rootScope', '$q', '$http', '$window', '$filter', 'jwtHelper'];

function securityFactory($rootScope, $q, $http, $window, $filter, jwtHelper) {
	var urlBase = 'services/service-user/security';
	var security = {};
	var jwtUser;
	
	security.init = function(token) {
		if (!token) throw "Invalid token";
		var tokenPayload = jwtHelper.decodeToken(token);
		
		var jwtUserToken = tokenPayload.jwtUser;
		try {
			var strData = atob(jwtUserToken);
			var charData = strData.split('').map(function(x){return x.charCodeAt(0);});
			var binData = new Uint8Array(charData);
			var data = pako.inflate(binData);
			var strData = String.fromCharCode.apply(null, new Uint16Array(data));
			jwtUser = angular.fromJson(strData);

			if (!jwtUser) throw "Invalid token";
		} catch (error) {
			throw "Invalid token";
		}
		
		security.user = {
			id: jwtUser.i,
			username: jwtUser.u,
			firstName: jwtUser.f,
			lastName: jwtUser.l,
			email: jwtUser.e,
			guid: jwtUser.g,
			domainId: jwtUser.di,
			domainName: jwtUser.dn,
			roles: rolesgather(jwtUser.dn, false),
			apiToken: jwtUser.a
		}
		angular.forEach(tokenPayload.authorities, function(role) {
			security.user.roles.push(role);
		});
		return security.user;
	}
	
	security.id = function () {
		return jwtUser.i;
	}
	security.guid = function() {
		return jwtUser.g;
	}
	security.username = function () {
		return jwtUser.u;
	}
	security.firstName = function () {
		return jwtUser.f;
	}
	security.lastName = function () {
		return jwtUser.l;
	}
	security.email = function () {
		return jwtUser.e;
	}
	security.domainId = function () {
		return jwtUser.di;
	}
	security.domainName = function () {
		return jwtUser.dn;
	}
	security.domainDisplayName = function () {
		return jwtUser.ddn;
	}
	security.playersDomain = function () {
		return jwtUser.ds.find( domain => domain.n === jwtUser.dn).pd;
	}
	
	/// Added for security.js
	security.getPermissions = function(permissionDomain) {
		if (permissionDomain == null) {
			return security.roles();
		} else {
			return security.rolesForDomain(permissionDomain);
		}
	}
	
	security.getPermissionValidation = function(permissionType) {
		var validations = {
			'ANY': security.hasAnyPermission,
			'ALL': security.hasAllPermission
		};
		if (!permissionType) {
			permissionType = 'ANY';
		}
		return validations[permissionType];
	}
	
	security.hasAllPermission = function(permissionsRequired, permissionDomain, permissionCheck) {
		if (permissionDomain == null) {
			permissionDomain = security.domainName();
		}
		var exists = true;
		if (!!permissionsRequired) {
			angular.forEach(permissionsRequired, function (permission) {
				// FIXME: Hack for now because I don't have a way to solve https://playsafe.atlassian.net/browse/LIVESCORE-1429?focusedCommentId=62268
				//		  without entirely removing the permission check, because we don't have a domain to pass in here.
				if (permissionDomain === 'check-tree') {
					if (!security.hasRoleInTree($filter('uppercase')(permission))) {
						exists = false;
					}
				} else {
					if (!security.hasRoleForDomain(permissionDomain, ($filter('uppercase')(permission)))) {
						exists = false;
					}
				}
//				if (permissionCheck === 'ALL') {
//					console.log("Checking down");
//					if (!security.hasRoleOnlyDescendingForDomain(permissionDomain, permission)) {
//						exists = false;
//					}
//				}
			});
		} else {
			exists = false;
		}
		return exists;
	}
	
	security.hasAnyPermission = function(permissionsRequired, permissionDomain, permissionCheck) {
		if (permissionDomain == null) {
			permissionDomain = security.domainName();
		}
		var exists = false;
		if (!!permissionsRequired) {
			angular.forEach(permissionsRequired, function (permission) {
				// FIXME: Hack for now because I don't have a way to solve https://playsafe.atlassian.net/browse/LIVESCORE-1429?focusedCommentId=62268
				//		  without entirely removing the permission check, because we don't have a domain to pass in here.
				if (permissionDomain === 'check-tree') {
					if (security.hasRoleInTree($filter('uppercase')(permission))) {
						exists = true;
					}
				} else {
					if (security.hasRoleForDomain(permissionDomain, ($filter('uppercase')(permission)))) {
						exists = true;
					}
				}
//				if (permissionCheck === 'ALL') {
//					//console.log("Checking down :: "+permissionDomain+" : "+permission+" : "+(security.hasRoleOnlyDescendingForDomain(permissionDomain, permission)));
//					if (security.hasRoleOnlyDescendingForDomain(permissionDomain, permission)) {
//						exists = true;
//					}
//				}
			});
		}
		return exists;
	}
	
	///////////////////////////////////
	
	security.roles = function() {
		return security.user.roles;
	}
	
	security.rolesForDomain = function(domainname) {
		return rolesgather(domainname, false);
	}
	
	security.domains = function() {
		var flatlist = [];
		var domains = checkarray(jwtUser.ds);
		angular.forEach(domains, function(value) {
			this.push({ name: value.n });
		}, flatlist);
		return flatlist;
	}
	
	security.domains = function(domainName, includePlayerDomains = true) {
		var flatlist = [];
		var domains = checkarray(jwtUser.ds);
		var found = false;
		angular.forEach(domains, function(value) {
			if (value.n === domainName) {
				found = true;
			}
			if (found) {
				if ((!value.pd) || (value.pd && includePlayerDomains)) {
					this.push({ name: value.n });
				}
			}
		}, flatlist);
		return flatlist;
	}
	
	security.domain = function(domainName) {
		return finddomain(domainName);
	}
	
	security.domainsWithRole = function(rolename) {
		var domainswithrole = [];
		var domains = checkarray(jwtUser.ds);
		angular.forEach(domains, function(value) {
			if (security.hasRoleForDomain(value.n, rolename)) {
				let ecosystemRelationshipType = null;
				if (value.ert === 'r') {
					ecosystemRelationshipType = 'root'
				} else if (value.ert === 'me') {
					ecosystemRelationshipType = 'exclusive'
				} else if (value.ert === 'm') {
					ecosystemRelationshipType = 'member'
				}
				this.push({ name: value.n, pd: value.pd, displayName: value.dn, ecosystemRelationshipType: ecosystemRelationshipType, inEcosystem: !!value.ert });
			}
		}, domainswithrole);
		return domainswithrole;
	}
	
	security.hasRoleInTree = function(rolename) {
		var found = false;
		var domains = checkarray(jwtUser.ds);
		angular.forEach(domains, function(domain) {
			if (security.hasRoleForDomain(domain.n, rolename)) {
//				console.log(rolename+' FOUND FOR '+domain.n);
				found = true;
//			} else {
//				console.log(rolename+' not found for '+domain.n);
			}
		});
		return found;
	}
	
	security.hasRoleOnlyDescending = function(rolename) { //descending flag on current domain
		return security.hasRoleOnlyDescendingForDomain(jwtUser.dn, rolename);
	}
	security.hasRoleOnlyDescendingForDomain = function(domainname, rolename) {
		//console.log(domainname);
		//console.log(rolename);
		var domain = finddomain(domainname);
		if (domain == null) {
			return false;
		} else {
			console.log(domain);
			var role = findrole(domain.r, rolename);
			console.log(role);
			if ((role!=null) && (role.d)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	security.hasRole = function(rolename) {
		var found = false;
		angular.forEach(security.user.roles, function(role) {
			if (rolename === $filter("uppercase")(role.trim())) {
				found = true;
			} else {
				found = false;
			}
		});
		if (!found) {
			return security.hasRoleForDomain(jwtUser.dn, rolename);
		} else {
			return true;
		} 
	}
	
	security.hasAdminRole = function() {
		return security.hasRoleForDomain(jwtUser.dn, "ADMIN");
	}
	
	security.hasAdminRoleForDomain = function(domainname) {
		return security.hasRoleForDomain(domainname, "ADMIN");
	}

	security.isExperimentalFeatures = function() {
		return security.hasRole("EXPERIMENTAL_FEATURES_ENABLED") && !security.hasAdminRole();
	}
	
	security.hasRoleForDomain = function(domainname, rolename) {
//		console.log(domainname, rolename);
		var domain = finddomain(domainname);
		if (domain == null) {
			return false;
		} else {
			var role = findrole(domain.r, rolename);
			if ((role!=null) && (role.s)) {
				return true;
			} else {
				var parentdomain = finddomain(domain.p);
				if (parentdomain == null) {
					return false;
				} else {
					var parentrole = findrole(parentdomain.r, rolename);
					if (parentrole != null) {
						return parentrole.d;
					} else {
						return security.hasRoleForDomain(parentdomain.n, rolename);
					}
				}
			}
		}
	}
	
	function rolesgather(domainname, busyTraversing) {
		var allroles = [];
		var domain = finddomain(domainname);
		if (domain == null) {
			return allroles;
		} else {
			var roles = checkarray(domain.r);
			angular.forEach(roles, function(role) {
				if ((role.s) && (!busyTraversing)) {
					this.push(role.n);
				}
				if ((role.d) && (busyTraversing)) {
					this.push(role.n);
				}
			}, allroles);
			var parentdomainname = domain.p;
			if (parentdomainname == null) {
				parentdomainname = "";
			}
			var parentdomain = finddomain(parentdomainname);
			if (parentdomain != null) {
				var parentroles = checkarray(parentdomain.r);
				angular.forEach(parentroles, function(role) {
					if (role.d) {
						this.push(role.n);
					}
				}, allroles);
				if (parentdomain.p != null) {
					allroles.push(rolesgather(parentdomain.p, true));
				}
			}
		}
		return allroles;
	}
	
	function findrole(roles, rolename) {
		if (!rolename) return;
		var roles = checkarray(roles);
		var role;
		angular.forEach(roles, function(value) {
			if (value.n && value.n.toLowerCase() === rolename.toLowerCase()) {
				role = value;
			} else if (value.n && value.n.toLowerCase() === 'admin') {
				role = value;
				//console.log("Found ADMIN Role, "+role+" GRANTED");
			} else if (rolename.endsWith("_*")) {
				var rolenameLocal = rolename.replace("*", "");
				if (value.n && value.n.toLowerCase().startsWith(rolenameLocal.toLowerCase())) {
//					console.log("Partial Match Found! "+value.n+"::"+rolename);
					if (value.s) {
						role = value;
//						console.log(role);
					}
				}
			}
		});
		return role;
	}
	
	function finddomain(domainname) {
		if (!domainname) return;
		var domains = checkarray(jwtUser.ds);
		var domain;
		angular.forEach(domains, function(value, key) {
			if (value.n.toLowerCase() === domainname.toLowerCase()) {
				domain = value;
			}
		});
		return domain;
	}
	
	function checkarray(array) {
		if (typeof array != "undefined" && array != null && array.length > 0){
			return array;
		} else {
			return [];
		}
	}
	
	return security;
	/*{
		init: security.init,
		hasRole: security.hasRole,
		domainsWithRole: security.domainsWithRole,
		user: user
	}*/
}