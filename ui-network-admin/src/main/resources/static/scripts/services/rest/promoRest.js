'use strict';

angular.module('lithium')
.factory('PromotionRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			
			service.baseUrl = 'services/service-promo/backoffice';
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl(service.baseUrl);
			});
			
			service.findUserPromotionById = function(userPromotionId) {
				return config.all('user-promotions').one(userPromotionId).get();
			}
			
			service.create = function(promotion) {
				return config.all('promotions').all('create').post(promotion);
			}
			
			service.view = function(id) {
				return config.all('promotion').one(id).get();
			}
			
			service.findRevision = function(promotionId, promotionRevisionId) {
				return config.all('promotion').all(promotionId).one('revision', promotionRevisionId).get();
			}
			
			service.edit = function(promotionId) {
				return config.all('promotion').one(promotionId, 'modify').get();
			}
			
			service.editPost = function(promotionId, promotion) {
				return config.all('promotion').all(promotionId).all('modify').post(promotion);
			}
			
			service.editAndSaveCurrent = function(promotionId, promotion) {
				return config.all('promotion').all(promotionId).all('modifyAndSaveCurrent').post(promotion);
			}
			
			service.addChallenge = function(promotionId, challenge) {
				return config.all('promotion').all(promotionId).all('addChallenge').post(challenge);
			}
			
			service.removeChallenge = function(promotionId, challengeId) {
				return config.all('promotion').all(promotionId).all("removeChallenge").all(challengeId).remove();
			}
			
			service.modifyChallenge = function(promotionId, challengeId, challenge) {
				return config.all('promotion').all(promotionId).all("modifyChallenge").all(challengeId).post(challenge);
			}
			
			service.addChallengeRule = function(promotionId, challengeId, rule) {
				return config.all('promotion').all(promotionId).one("challenge", challengeId).all('addChallengeRule').post(rule);
			}
			
			service.removeChallengeRule = function(promotionId, challengeId, challengeRuleId) {
				return config.all('promotion').all(promotionId).one("challenge", challengeId).all("removeChallengeRule").all(challengeRuleId).remove();
			}
			
			service.modifyChallengeRule = function(promotionId, challengeId, challengeRuleId, rule) {
				return config.all('promotion').all(promotionId).one("challenge", challengeId).all("modifyChallengeRule").all(challengeRuleId).post(rule);
			}

			service.addUserCategories = function(promotionId, categories) {
				return config.all('promotion').all(promotionId).one("user-categories").all('add').post(categories);
			}

			service.deleteUserCategory = function(promotionId, userCategoryId) {
				return config.all('promotion').all(promotionId).one("user-categories").all(userCategoryId).remove();
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);