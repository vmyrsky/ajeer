'use strict';

// This file lists what to show per given url and which controllers are
// responsible for handling requests per file
// The array lists the MODULES that angularPOC depends on
// The controllers are defined in their own files to provide better graining of
// resources (Note: reference the module name, not the controller name)
// The controllers are introduced in each controller with
// var xxxControllers = angular.module('xxxControllers', []);
// <= These module names are referred in here
var angularPOC = angular.module(
		'angularPOC',
		[ 'ngRoute', 'MessageControllers', 'MainControllers',
				'DetailControllers', 'AngularTranslate' ]).config(
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