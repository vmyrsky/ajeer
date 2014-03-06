angular.module('AngularTranslate', [ 'pascalprecht.translate' ]).config(
		function($translateProvider) {
			
			$translateProvider.useStaticFilesLoader({
		          prefix: '/Angular/app/js/i18n/',
		          suffix: '.json'
		        });
			
			// Note: For better syntax readability, I have marked only the text
			// leaf 'nodes' with apostrophes + have only them capitalized
			// To register Finnish translation table
//			$translateProvider.translations('fi', {
//				'SELECT_LANG' : 'Valitse kieli',
//				'FI' : 'SUOMEKSI',
//				'EN' : 'In English',
//				links : {
//					'MAIN' : 'pääsivu',
//					'DETAILS' : 'lisätiedot'
//				}
//			});
//			// To register English translation table
//			$translateProvider.translations('en', {
//				'SELECT_LANG' : 'Choose language',
//				'FI' : 'Suomeksi',
//				'EN' : 'IN ENGLISH',
//				links : {
//					'MAIN' : 'main page',
//					'DETAILS' : 'details page'
//				}
//			});
			// which language to use by default?
			$translateProvider.preferredLanguage('en');
		});
