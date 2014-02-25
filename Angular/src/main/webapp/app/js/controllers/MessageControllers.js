'use strict';

var MessageControllers = angular.module('MessageControllers', []);

var angularPOC = angular.module('angularPOC');
angularPOC.controller('MessageController', [ '$scope', '$routeParams',
		'ShareDataService', function($scope, $routeParams, ShareDataService) {

//			$scope.ShareDataService = ShareDataService;
			$scope.messages = ShareDataService.getMessages();

//			$scope.$watch('ShareDataService.messages', function(newVal, oldVal, scope) {
//				alert(JSON.stringify(newVal));
//				scope.messages = [{'text': "Errrr", type:"OK"}];
//			});

			$scope.getMessages = function() {
				
				$scope.messages = ShareDataService.getMessages();
//				alert(JSON.stringify($scope.messages));
			}

			$scope.clearMessages = function() {
				$scope.messages = [];
			};

			$scope.hasMessages = function() {
				$scope.messages = ShareDataService.getMessages();
				return ($scope.messages != 'undefined' && $scope.messages > 0);
			};

		} ]);