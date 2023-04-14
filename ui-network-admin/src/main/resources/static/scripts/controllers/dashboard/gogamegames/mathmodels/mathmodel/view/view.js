'use strict'

angular.module('lithium').controller('GoGameMathModelViewController', ['mathModel', 'mathModelRevision', '$translate', 'notify', 'Lightbox',
	function(mathModel, mathModelRevision, $translate, notify, Lightbox) {
		var controller = this;
		
		controller.model = mathModel.plain();
		controller.mathModelRevision = mathModelRevision;
		
		controller.parseJSONStringsToObjs = function() {
			if (controller.mathModelRevision.stops !== undefined && controller.mathModelRevision.stops !== null) {
				if (typeof controller.mathModelRevision.stops === 'string') {
					controller.mathModelRevision.stops = JSON.parse(controller.mathModelRevision.stops);
				}
			} else {
				controller.mathModelRevision.stops = [];
			}
			if (controller.mathModelRevision.lines !== undefined && controller.mathModelRevision.lines !== null) {
				if (typeof controller.mathModelRevision.lines === 'string') {
					controller.mathModelRevision.lines = JSON.parse(controller.mathModelRevision.lines);
				}
			} else {
				controller.mathModelRevision.lines = [];
			}
			if (controller.mathModelRevision.paytables !== undefined && controller.mathModelRevision.paytables !== null) {
				if (typeof controller.mathModelRevision.paytables === 'string') {
					controller.mathModelRevision.paytables = JSON.parse(controller.mathModelRevision.paytables);
				}
			} else {
				controller.mathModelRevision.paytables = [];
			}
			if (controller.mathModelRevision.featureConfigs !== undefined && controller.mathModelRevision.featureConfigs !== null) {
				if (typeof controller.mathModelRevision.featureConfigs === 'string') {
					controller.mathModelRevision.featureConfigs = JSON.parse(controller.mathModelRevision.featureConfigs);
				}
			} else {
				controller.mathModelRevision.featureConfigs = {};
			}
			if (controller.mathModelRevision.reels !== undefined && controller.mathModelRevision.reels !== null) {
				if (typeof controller.mathModelRevision.reels === 'string') {
					controller.mathModelRevision.reels = JSON.parse(controller.mathModelRevision.reels);
				}
			} else {
				controller.mathModelRevision.reels = [];
			}
			if (controller.mathModelRevision.reelsProbabilities !== undefined && controller.mathModelRevision.reelsProbabilities !== null) {
				if (typeof controller.mathModelRevision.reelsProbabilities === 'string') {
					controller.mathModelRevision.reelsProbabilities = JSON.parse(controller.mathModelRevision.reelsProbabilities);
				}
			} else {
				controller.mathModelRevision.reelsProbabilities = [];
			}
			if (controller.mathModelRevision.fsReels !== undefined && controller.mathModelRevision.fsReels !== null) {
				if (typeof controller.mathModelRevision.fsReels === 'string') {
					controller.mathModelRevision.fsReels = JSON.parse(controller.mathModelRevision.fsReels);
				}
			} else {
				controller.mathModelRevision.fsReels = [];
			}
			if (controller.mathModelRevision.fsReelsProbabilities !== undefined && controller.mathModelRevision.fsReelsProbabilities !== null) {
				if (typeof controller.mathModelRevision.fsReelsProbabilities === 'string') {
					controller.mathModelRevision.fsReelsProbabilities = JSON.parse(controller.mathModelRevision.fsReelsProbabilities);
				}
			} else {
				controller.mathModelRevision.fsReelsProbabilities = [];
			}
			if (controller.mathModelRevision.rtReels !== undefined && controller.mathModelRevision.rtReels !== null) {
				if (typeof controller.mathModelRevision.rtReels === 'string') {
					controller.mathModelRevision.rtReels = JSON.parse(controller.mathModelRevision.rtReels);
				}
			} else {
				controller.mathModelRevision.rtReels = [];
			}
			if (controller.mathModelRevision.rtReelsProbabilities !== undefined && controller.mathModelRevision.rtReelsProbabilities !== null) {
				if (typeof controller.mathModelRevision.rtReelsProbabilities === 'string') {
					controller.mathModelRevision.rtReelsProbabilities = JSON.parse(controller.mathModelRevision.rtReelsProbabilities);
				}
			} else {
				controller.mathModelRevision.rtReelsProbabilities = [];
			}
			if (controller.mathModelRevision.mlReels !== undefined && controller.mathModelRevision.mlReels !== null) {
				if (typeof controller.mathModelRevision.mlReels === 'string') {
					controller.mathModelRevision.mlReels = JSON.parse(controller.mathModelRevision.mlReels);
				}
			} else {
				controller.mathModelRevision.mlReels = [];
			}
			if (controller.mathModelRevision.mlReelsProbabilities !== undefined && controller.mathModelRevision.mlReelsProbabilities !== null) {
				if (typeof controller.mathModelRevision.mlReelsProbabilities === 'string') {
					controller.mathModelRevision.mlReelsProbabilities = JSON.parse(controller.mathModelRevision.mlReelsProbabilities);
				}
			} else {
				controller.mathModelRevision.mlReelsProbabilities = [];
			}
		}
		
		controller.setupReelsBaseGame = function() {
			controller.reelPositionsBG = [];
			controller.maxReelLenBG = [];
			
			for (var k = 0; k < controller.mathModelRevision.reels.length; k++) {
				controller.maxReelLenBG.push(0);
				var reelSet = controller.mathModelRevision.reels[k];
				for (var i = 0; i < 5; i++) {
					controller.reelPositionsBG.push([]);
					if (reelSet[i].symbols.length > controller.maxReelLenBG[k])
						controller.maxReelLenBG[k] = reelSet[i].symbols.length;
				}
				for (var i = 0; i < controller.maxReelLenBG[k]; i++) {
					controller.reelPositionsBG[k].push(i + 1);
				}
			}
		}
		
		controller.setupReelsFreespin = function() {
			controller.reelPositionsFS = [];
			controller.maxReelLenFS = [];
			
			for (var k = 0; k < controller.mathModelRevision.fsReels.length; k++) {
				controller.maxReelLenFS.push(0);
				var reelSet = controller.mathModelRevision.fsReels[k];
				for (var i = 0; i < 5; i++) {
					controller.reelPositionsFS.push([]);
					if (reelSet[i].symbols.length > controller.maxReelLenFS[k])
						controller.maxReelLenFS[k] = reelSet[i].symbols.length;
				}
				for (var i = 0; i < controller.maxReelLenFS[k]; i++) {
					controller.reelPositionsFS[k].push(i + 1);
				}
			}
		}
		
		controller.setupReelsRetrigger = function() {
			controller.reelPositionsRT = [];
			controller.maxReelLenRT = [];
			
			for (var k = 0; k < controller.mathModelRevision.rtReels.length; k++) {
				controller.maxReelLenRT.push(0);
				var reelSet = controller.mathModelRevision.rtReels[k];
				for (var i = 0; i < 5; i++) {
					controller.reelPositionsRT.push([]);
					if (reelSet[i].symbols.length > controller.maxReelLenRT[k])
						controller.maxReelLenRT[k] = reelSet[i].symbols.length;
				}
				for (var i = 0; i < controller.maxReelLenRT[k]; i++) {
					controller.reelPositionsRT[k].push(i + 1);
				}
			}
		}

		controller.setupReelsMegalink = function() {
			controller.reelPositionsML = [];
			controller.maxReelLenML = [];

			for (var k = 0; k < controller.mathModelRevision.mlReels.length; k++) {
				controller.maxReelLenML.push(0);
				var reelSet = controller.mathModelRevision.mlReels[k];
				for (var i = 0; i < reelSet.length; i++) {
					controller.reelPositionsML.push([]);
					if (reelSet[i].symbols.length > controller.maxReelLenML[k])
						controller.maxReelLenML[k] = reelSet[i].symbols.length;
				}
				for (var i = 0; i < controller.maxReelLenML[k]; i++) {
					controller.reelPositionsML[k].push(i + 1);
				}
			}
		}
		
		controller.parseJSONStringsToObjs();
		if (controller.mathModelRevision.reels.length > 0)
			controller.setupReelsBaseGame();
		if (controller.mathModelRevision.fsReels.length > 0)
			controller.setupReelsFreespin();
		if (controller.mathModelRevision.rtReels.length > 0)
			controller.setupReelsRetrigger();
		if (controller.mathModelRevision.mlReels.length > 0)
			controller.setupReelsMegalink();
	}
]);
