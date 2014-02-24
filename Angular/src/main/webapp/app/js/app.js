'use strict';

// This file lists what to show per given url and which controller is
// responsible for handling request per file
// The array lists the modules that angularPOC depends on
// The controllers are defined in their own files to provide better graining of
// resources
// The controllers are introduced in each controller with
// var xxxControllers = angular.module('xxxControllers', []);
// <= These modules are referred to here
var angularPOC = angular.module('angularPOC',
		[ 'ngRoute', 'MainControllers', 'DetailsControllers' ]).config(
		[ '$routeProvider', '$locationProvider',
				function($routeProvider, $locationProvider) {

					// Have the view logic to open page based on the url
					// ajeer [Angular Jave EE Rest]
					$routeProvider.when('/ajeer', {
						templateUrl : '/Angular/app/views/main.html',
						controller : 'MainController'
					});
					// Note that you must provide the id part in the url for
					// this to be invoked at all
					$routeProvider.when('/ajeer/details/:personId', {
						templateUrl : '/Angular/app/views/details.html',
						controller : 'DetailsController'
					});
					$routeProvider.otherwise({
						redirectTo : '/ajeer'
					});
					// $locationProvider.html5Mode(true);
				} ]);