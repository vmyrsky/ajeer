'use strict';

var angularPOC = angular.module('angularPOC');
angularPOC.controller('MessageController', [ '$scope', '$routeParams',
		'$timeout', '$translate', 'ShareDataService',
		function($scope, $routeParams, $timeout, $translate, ShareDataService) {

			$scope.languageOptions = [ "fi_FI", "en_US" ];
			$scope.language = $scope.languageOptions[0];
			$scope.shareService = ShareDataService;
			$scope.messages = ShareDataService.messages;

			$scope.$watch(function() {
				return ShareDataService.getMessages();
			}, function(newVal, oldVal, scope) {
				$scope.messages = newVal;
			});

			$scope.setMessage = function(messageText, messageType) {

				// timeout prevents digest problems
				// $timeout(function() {
				messages = [];
				messages.push({
					'text' : messageText,
					'type' : messageType
				});
				// });
			},

			$scope.addMessage = function(messageText, messageType) {

				// timeout prevents digest problems
				$timeout(function() {
					messages.push({
						'text' : messageText,
						'type' : messageType
					});
				});
			},

			$scope.clearMessages = function() {
				ShareDataService.clearMessages();
				$scope.getMessages();
			};

			$scope.hasMessages = function() {
				$scope.messages = ShareDataService.getMessages();
				return ($scope.messages != 'undefined' && $scope.messages > 0);
			};

			$scope.chooseLanguage = function(lang) {
				$scope.language = lang;
				// $translate.use(($translate.use() === 'en_EN') ? 'fi_FI' :
				// 'en_EN');
				$translate.use($scope.language);
			};

		} ]);