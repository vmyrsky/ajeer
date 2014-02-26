'use strict';

var MessageControllers = angular.module('MessageControllers', []);

var angularPOC = angular.module('angularPOC');
angularPOC.controller('MessageController', [
		'$scope',
		'$routeParams',
		'$timeout',
		'ShareDataService',
		function($scope, $routeParams, $timeout, ShareDataService) {

			$scope.shareService = ShareDataService;
			$scope.messages = ShareDataService.messages;

			$scope.$watch(function() { return ShareDataService.getMessages(); }, function(newVal, oldVal,
					scope) {
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

		} ]);