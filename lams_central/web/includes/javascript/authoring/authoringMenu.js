﻿/**
 * This file contains methods for menu in Authoring.
 */

var MenuLib = {
		
	init : function(){
		// add jQuery UI button functionality
		$('.ui-button').button();
		$(".split-ui-button").each(function(){
			// drop down buttons
			var buttonContainer = $(this);
			var buttons = buttonContainer.children();
			
			buttons.first().button()
				   .next().button({
				text : false,
				icons : {
					primary : "ui-icon-triangle-1-s"
				}
			});
			buttonContainer.buttonset().next().hide().menu();
			
			buttons.each(function(){
				var button = $(this);
				if (!button.attr('onclick')) {
					button.click(function() {
						var menu = $(this).parent().next().show().position({
							my : "left top+2px",
							at : "left bottom",
							of : $(this).parent()
						});
						$(document).one("click", function() {
							menu.hide();
						});
						return false;
					});
				}
			});
		});
	},
	
	
	/**
	 * Run when branching is selected from menu. Allows placing branching and converge points on canvas.
	 */
	addBranching : function(){
		HandlerLib.resetCanvasMode();
		var dialog = layout.items.infoDialog.text('Place the branching point');
		dialog.dialog('open');
		
		var branchingActivity = null;
		canvas.css('cursor', 'pointer').click(function(event){
			// pageX and pageY tell event coordinates relative to the whole page
			// we need relative to canvas
			var translatedEvent = ActivityLib.translateEventOnCanvas(event),
				x = translatedEvent[0] - 6,
				y = translatedEvent[1] - 8;
			
			// if it is start point, branchingActivity is null and constructor acts accordingly
			var branchingEdge = new ActivityLib.BranchingEdgeActivity(null, null, x, y, null, null, branchingActivity);
			layout.activities.push(branchingEdge);
			
			if (branchingActivity) {
				// converge point was just place, end of function
				HandlerLib.resetCanvasMode(true);
				
				dialog.text('');
				dialog.dialog('close');
				
				setModified(true);
			} else {
				// extract main branchingActivity structure from created start point
				branchingActivity = branchingEdge.branchingActivity;
				dialog.text('Place the converge point');
			}
		});
	},
	
	
	/**
	 * Creates a new grouping activity.
	 */
	addGrouping : function() {
		HandlerLib.resetCanvasMode();
		
		canvas.css('cursor', 'url("' + layout.toolMetadata.grouping.iconPath + '"), move')
			  .click(function(event){
			// pageX and pageY tell event coordinates relative to the whole page
			// we need relative to canvas
			var translatedEvent = ActivityLib.translateEventOnCanvas(event),
				x = translatedEvent[0] - 47,
				y = translatedEvent[1] -  2;

			layout.activities.push(new ActivityLib.GroupingActivity(null, null, x, y));
			
			setModified(true);
			HandlerLib.resetCanvasMode(true);
		});
	},
	
	
	/**
	 * Creates a new annotation Region.
	 */
	addAnnotationRegion : function() {
		HandlerLib.resetCanvasMode();
		
		var dialog = layout.items.infoDialog.text('Click and hold to start drawing an annotation region');
		dialog.dialog('open');
	
		canvas.css('cursor', 'crosshair').mousedown(function(event){
			dialog.text('');
			dialog.dialog('close');
			
			var	targetElement = paper.getElementByPoint(event.pageX, event.pageY);
			
			if (targetElement) {
				// cancel
				HandlerLib.resetCanvasMode(true);
			} else {
				HandlerLib.drawRegionStartHandler(event);
			}
		});
	},
	
	
	/**
	 * Creates a new annotation label.
	 */
	addAnnotationLabel : function() {
		HandlerLib.resetCanvasMode();
		
		var dialog = layout.items.infoDialog.text('Click to add an annotation label');
		dialog.dialog('open');
	
		canvas.css('cursor', 'pointer').click(function(event){
			dialog.text('');
			dialog.dialog('close');


			var translatedEvent = ActivityLib.translateEventOnCanvas(event),
				x = translatedEvent[0],
				y = translatedEvent[1];
			
			HandlerLib.resetCanvasMode(true);
			
			DecorationLib.addLabel(x, y);
		});
	},
	
	
	/**
	 * Creates a new optional activity.
	 */
	addOptionalActivity : function() {
		HandlerLib.resetCanvasMode();
		
		var dialog = layout.items.infoDialog.text('Click to add an optional activity container.');
		dialog.dialog('open');
	
		canvas.css('cursor', 'pointer').click(function(event){
			dialog.text('');
			dialog.dialog('close');


			var translatedEvent = ActivityLib.translateEventOnCanvas(event),
				x = translatedEvent[0],
				y = translatedEvent[1];
			
			setModified(true);
			HandlerLib.resetCanvasMode(true);

			layout.activities.push(new ActivityLib.OptionalActivity(null, null, x, y));
		});
	},
	
	
	/**
	 * Creates a new floating activity.
	 */
	addFloatingActivity : function() {
		if (layout.floatingActivity) {
			// there can be only one
			return;
		}
		HandlerLib.resetCanvasMode();
		
		var dialog = layout.items.infoDialog.text('Click to add a support activity container.');
		dialog.dialog('open');
	
		canvas.css('cursor', 'pointer').click(function(event){
			dialog.text('');
			dialog.dialog('close');


			var translatedEvent = ActivityLib.translateEventOnCanvas(event),
				x = translatedEvent[0],
				y = translatedEvent[1];
			
			setModified(true);
			HandlerLib.resetCanvasMode(true);

			// do not add it to layout.activities as it behaves differently
			new ActivityLib.FloatingActivity(null, null, x, y);
			
			// there can be only one, so disable the button
			$('#floatingActivityButton').attr('disabled', 'disabled')
									 	.css('opacity', 0.2);
		});
	},
	
	
	/**
	 * Creates a new transition.
	 */
	addTransition : function() {
		HandlerLib.resetCanvasMode();
		
		var dialog = layout.items.infoDialog.text('Click on an activity');
		dialog.dialog('open');
		
		canvas.css('cursor', 'pointer').click(function(event){
			dialog.text('');
			dialog.dialog('close');
			
			var startActivity = null,
				targetElement = paper.getElementByPoint(event.pageX, event.pageY);
			
			if (targetElement) {
				startActivity = targetElement.data('parentObject');
				if (startActivity) {
					HandlerLib.drawTransitionStartHandler(startActivity, null, event.pageX, event.pageY);
				}
			}
		});
	},
	
	
	/**
	 * Creates a new gate activity.
	 */
	addGate : function() {
		HandlerLib.resetCanvasMode();
		
		canvas.css('cursor', 'url("' + layout.toolMetadata.gate.iconPath + '"), move').click(function(event){
			// pageX and pageY tell event coordinates relative to the whole page
			// we need relative to canvas
			var translatedEvent = ActivityLib.translateEventOnCanvas(event),
				x = translatedEvent[0],
				y = translatedEvent[1] + 2;
			
			layout.activities.push(new ActivityLib.GateActivity(null, null, x, y));
			
			setModified(true);
			HandlerLib.resetCanvasMode(true);
		});
	},
	
	
	/**
	 * Opens "Open sequence" dialog where an user can choose a Learning Design to load.
	 */
	openLearningDesign : function(){
		var dialog = $('#ldStoreDialog');
		// remove the directory tree, if it remained for last dialog opening
		dialog.dialog('option', {
			'title'  : 'Open sequence',
			'buttons' : dialog.dialog('option', 'buttonsLoad'),
			// it informs widgets that it is load dialog
			'dialogClass': 'ldStoreDialogLoad'
		})			   
		.dialog('open');
		
		MenuLib.initLearningDesignTree();
	},
	
	
	/**
	 * Opens "Save sequence" dialog where an user can choose where to save the Learning Design.
	 */
	saveLearningDesign : function(showDialog){
		if (!showDialog && layout.ld.learningDesignID) {
			saveLearningDesign(layout.ld.folderID, layout.ld.learningDesignID, layout.ld.title);
			return;
		}
		
		var dialog = $('#ldStoreDialog');
		// remove the directory tree, if it remained for last dialog opening
		dialog.dialog('option', {
			'title'  : 'Save sequence',
			'buttons' : dialog.dialog('option', 'buttonsSave'),
			// it informs widgets that it is saved dialog
			'dialogClass': 'ldStoreDialogSave'
		})			   
		.dialog('open');
		
		var tree = MenuLib.initLearningDesignTree();
		tree.getRoot().children[0].highlight();
	},
	
	
	/**
	 * Loads Learning Design Tree from DB
	 */
	initLearningDesignTree : function(){
		var dialog = $('#ldStoreDialog'),
			tree = dialog.dialog('option', 'ldTree'),
			rootNode = tree.getRoot();
		// remove existing folders
		$.each(rootNode.children, function(){
			tree.removeNode(this);
		});
		// (re)load user's folders and LDs
		tree.buildTreeFromObject(MenuLib.getFolderContents());
		tree.render();
		
		// expand the first (user) folder
		tree.getRoot().children[0].expand();
		
		return tree;
	},
	
	
	/**
	 * Loads subfolders and LDs from the server.
	 */
	getFolderContents : function(folderID, allowInvalidDesigns) {
		var result = null;
			
		$.ajax({
			url : LAMS_URL + 'home.do',
			data : {
				'method' : 'getFolderContents',
				'folderID' : folderID,
				'allowInvalidDesigns' : allowInvalidDesigns
			},
			cache : false,
			async: false,
			dataType : 'json',
			success : function(response) {
				result = [];

				// parse the response; extract folders and LDs
				if (response.folders) {
					$.each(response.folders, function(){
						result.push({'type'            : 'text',
								  	 'label'           : this.isRunSequencesFolder ?
								  			 				LABEL_RUN_SEQUENCES_FOLDER : this.name,
								  	 'folderID'		   : this.folderID
									 });
					});
				}
				if (response.learningDesigns) {
					$.each(response.learningDesigns, function(){
						result.push({'type'             : 'text',
						  	         'label'            : this.name,
						  	         'isLeaf'           : true,
						  	         'learningDesignId' : this.learningDesignId
							        });
					});
				}
			}
		});
		
		return result;
	},
	
	
	/**
	 * Sorts activities on canvas.
	 */
	arrangeActivities : function(){
		if ((layout.regions.length > 0 || layout.labels.length > 0)
				&& !confirm('There are annotations on the canvas.\n'
				   + 'They will be not arranged automatically, you will have to adjust them manually later.\n'
				   + 'Do you want to continue?')) {
			return;
		}

		if (layout.activities.length == 0) {
			// no activities, nothing to do
			return;
		}
		
		// just to refresh the state of canvas
		HandlerLib.resetCanvasMode(true);
		
		// activities are arranged in a grid
		var row = 0,
			// for special cases when row needs to shifted more
			forceRowY = null,
			column = 0,
			// check how many columns current paper can hold
			maxColumns = Math.floor((paper.width - layout.conf.arrangeHorizontalPadding)
					                 / layout.conf.arrangeHorizontalSpace),
			// the initial max length of subsequences is limited by paper space
			subsequenceMaxLength = maxColumns,
	        // a shallow copy of activities array without inner activities
			activitiesCopy = [],
			// just to speed up processing when there are only activities with no transitions left
			onlyDetachedLeft = false;
	
		$.each(layout.activities, function(){
			if (!this.parentActivity || !(this.parentActivity instanceof DecorationLib.Container)){
				activitiesCopy.push(this);
			}
		});
		
		// branches will not be broken into few rows; if they are long, paper will be resized
		// find the longes branch to find the new paper size
		$.each(layout.activities, function(){
			if (this instanceof ActivityLib.BranchingEdgeActivity && this.isStart) {
				// refresh branching metadata
				ActivityLib.updateBranchesLength(this.branchingActivity);
				// add start and end edges to the result
				var longestBranchLength = this.branchingActivity.longestBranchLength + 2;
				if (longestBranchLength > subsequenceMaxLength) {
					subsequenceMaxLength = longestBranchLength;
				}
			}
		});
		
		// check how many child activities are in Floating Activity, if any
		if (layout.floatingActivity && layout.floatingActivity.childActivities.length > subsequenceMaxLength) {
				subsequenceMaxLength = childActivities.length;
		}
		
		// resize paper horizontally, if needed
		if (subsequenceMaxLength > maxColumns) {
			maxColumns = subsequenceMaxLength;
			resizePaper(layout.conf.arrangeHorizontalPadding
					      + maxColumns * layout.conf.arrangeHorizontalSpace,
					      paper.height);
		}
		
		// main loop; iterate over whatever is left in the array
		while (activitiesCopy.length > 0) {
			// look for activities with transitions first; detached ones go to the very end
			var activity = null;
			if (!onlyDetachedLeft) {
				$.each(activitiesCopy, function(){
					if (this.transitions.to.length > 0) {
						activity = this;
						while (activity.transitions.to.length > 0) {
							// check if previous activity was not drawn already
							// it can happen for branching edges
							var activityLookup = activity.transitions.to[0].fromActivity;
							if (activitiesCopy.indexOf(activityLookup) == -1) {
								break;
							}
							activity = activityLookup; 
						};
						return false;
					}
				});
			}

			if (!activity) {
				// no activities with transitions left, take first detached one
				onlyDetachedLeft = true;
				activity = activitiesCopy[0];
			}
				
			// markers for complex activity processing
			var complex = null;
			
			// crawl through a sequence of activities
			while (activity) {
				if (activity instanceof ActivityLib.BranchingEdgeActivity) {
					if (activity.isStart) {
						// draw branching edges straight away and remove them from normall processing
						var branchingActivity = activity.branchingActivity,
							start = branchingActivity.start,
							end = branchingActivity.end,
							complex = {
								end : end
							},
							// can the whole branching fit in current canvas width?
							branchingFits = column + branchingActivity.longestBranchLength + 2 <= maxColumns;
						if (!branchingFits) {
							// start branching from the left side of canvas
							row++;
							if (forceRowY) {
								while (forceRowY > layout.conf.arrangeVerticalPadding + 10 + row * layout.conf.arrangeVerticalSpace) {
									row++;
								}
								forceRowY = null;
							}
							column = 0;
						}
						// store the column of converge point
						end.column = column + branchingActivity.longestBranchLength + 1;
						
						complex.branchingRow = row + Math.floor(branchingActivity.branches.length / 2);
						// edge points go to middle of rows with branches
						var startX = layout.conf.arrangeHorizontalPadding +
									 column * layout.conf.arrangeHorizontalSpace + 54,
							edgeY = layout.conf.arrangeVerticalPadding +
									complex.branchingRow * layout.conf.arrangeVerticalSpace + 17,
							endX = layout.conf.arrangeHorizontalPadding +
								   end.column * layout.conf.arrangeHorizontalSpace + 54;
						
						activitiesCopy.splice(activitiesCopy.indexOf(start), 1);
						activitiesCopy.splice(activitiesCopy.indexOf(end), 1);
						
						// start point goes to very left, end goes wherever the longes branch ends
						start.draw(startX, edgeY);
						end.draw(endX, edgeY);
	
						complex.branchingColumn = column;
						column++;

						$.each(branchingActivity.branches, function(){
							if (this.transitionFrom.toActivity == branchingActivity.end) {
								complex.emptyBranch = this;
								return false;
							}
						});
						
						if (branchingActivity.branches.length > (complex.emptyBranch ? 1 : 0)) {
							// set up branch drawing
							// skip the first branch if it is the empty one
							complex.branchIndex =
								complex.emptyBranch == branchingActivity.branches[0] ? 1 : 0;
							// next activity for normal processing will be first one from the first branch
							activity = branchingActivity.branches[complex.branchIndex].transitionFrom.toActivity;
							continue;
						} else {
							// no branches, nothing to do, carry on with normal activity processing
							activity = complex.end;
							activity.column = null;
							complex = null;
						}
					}
				} else {
					// it is a simple activity, so redraw it
					var x = layout.conf.arrangeHorizontalPadding + column * layout.conf.arrangeHorizontalSpace,
						y = layout.conf.arrangeVerticalPadding + row * layout.conf.arrangeVerticalSpace;
					
					if (activity instanceof ActivityLib.GateActivity) {
						// adjust placement for gate activity, so it's in the middle of its cell
						x += 57;
						y += 10;
					} else if (activity instanceof ActivityLib.OptionalActivity){
						x -= 20;
					}
					
					activity.draw(x, y);
					// remove the activity so we do not process it twice
					activitiesCopy.splice(activitiesCopy.indexOf(activity), 1);
					
					// learn where a tall Optional Activity has its end
					// and later start drawing activities lower than in the next row
					if (activity instanceof DecorationLib.Container && activity.childActivities.length > 1) {
						var activityEndY = activity.items.shape.getBBox().y2;
						if (!forceRowY || activityEndY > forceRowY) {
							forceRowY = activityEndY;
						}
					}
				}
				
				// find the next row and column
				column = (column + 1) % maxColumns;
				if (column == 0) {
					row++;
					// if an Optional Activity forced next activities to be drawn lower than usual
					if (forceRowY) {
						while (forceRowY > layout.conf.arrangeVerticalPadding + 10 + row * layout.conf.arrangeVerticalSpace) {
							row++;
						}
						forceRowY = null;
					}
				}
				
				// does the activity has further activities?
				if (activity.transitions.from.length > 0) {
					activity = activity.transitions.from[0].toActivity;
				} else {
					activity = null;
				}
				
				if (complex && (!activity || activity == complex.end)) {
					// end of branch
					complex.branchIndex++;

					var branches = complex.end.branchingActivity.branches;
					if (branches.length > complex.branchIndex) {
						if (branches[complex.branchIndex] == complex.emptyBranch) {
							// skip the empty branch
							complex.branchIndex++;
						}
					}
					
					if (branches.length > complex.branchIndex) {
						// there is another branch to process
						activity = branches[complex.branchIndex].transitionFrom.toActivity;
						// go back to left side of canvas and draw next branch
						row++;
						if (complex.emptyBranch && complex.branchingRow == row) {
							row++;
						}
						
						column = complex.branchingColumn + 1;
					} else {

						// no more branches, return to normal activity processing
						activity = complex.end.transitions.from.length == 0 ?
								null : complex.end.transitions.from[0].toActivity;
						column = (complex.end.column + 1) % maxColumns;
						if (column == 0) {
							row++;
						}
						if (row < complex.branchingRow) {
							row = complex.branchingRow;
						}
						complex.end.column = null;
						complex = null;
					}
				}
				
				if (!activity || activitiesCopy.indexOf(activity) == -1) {
					// next activity was already processed, so stop crawling
					break;
				}
			};
		};
		
		if (layout.floatingActivity) {
			if (column > 0) {
				// if the last activity was in the last column, there is no need for another row
				row++;
				column = 0;
			}
			var x = layout.conf.arrangeHorizontalPadding,
				y = layout.conf.arrangeVerticalPadding - 30 + row * layout.conf.arrangeVerticalSpace;
			
			layout.floatingActivity.draw(x, y);
		}
		
		// redraw transitions one by one
		$.each(layout.activities, function(){
			$.each(this.transitions.from.slice(), function(){
				ActivityLib.addTransition(this.fromActivity, this.toActivity, true);
			});
		});
		
		resizePaper();
		setModified(true);
	},
	
	
	/**
	 * Removes existing activities and prepares canvas for a new sequence.
	 */
	newLearningDesign : function(force, soft){
		// force means that user should not be asked for confirmation.
		if (!force && (layout.activities.length > 0
					  || layout.regions.length > 0
					  || layout.labels.length > 0
					  || layout.floatingActivity)
				&& !confirm('Are you sure you want to remove all existing elements?')){
			return;
		}
		
		$('#ldDescriptionDetails').slideUp();
		
		// soft means that data is manually reset, instead of simply reloading the page.
		if (soft) {
			$('#ldDescriptionFieldTitle').text('Untitled');
			CKEDITOR.instances['ldDescriptionFieldDescription'].setData(null);
			
			layout.ld = {
				'maxUIID' : 0
			};
			layout.activities = [];
			layout.regions = [];
			layout.labels = [];
			layout.floatingActivity = null;
			setModified(true);
			
			if (paper) {
				paper.clear();
			} else {
				// need to set size right away for Chrome
				paper = Raphael('canvas', canvas.width() - 5, canvas.height() - 5);
			}
			
			resizePaper();
		} else {
			// do not prompt again
			window.onbeforeunload = null;
			// full window reload so new content ID gets generated
			document.location.href = LAMS_URL + 'authoring/author.do?method=openAuthoring';
		}
	},
	
	
	/**
	 * Mark an activity as ready for pasting.
	 */
	copyActivity : function(){
		layout.items.copiedActivity = layout.items.selectedObject;
	},
	
	
	/**
	 * Make a copy of an existing activity.
	 */
	pasteActivity : function(){
		var activity = layout.items.copiedActivity;
		if (!activity) {
			return;
		}
		// check if the activity was not removed
		if (layout.activities.indexOf(activity) < 0){
			layout.items.copiedActivity = null;
			return;
		}
		// only tool activities can be copied (todo?)
		if (!(activity instanceof ActivityLib.ToolActivity)) {
			alert('Sorry, you can not paste this type of activity');
			return;
		}
		
		// find an unqiue title for the new activity
		var copyCount = 1, 
			title = 'Copy of ' + activity.title;
		while (true) {
			var sameTitleFound = false;
			$.each(layout.activities, function(){
				if (title == this.title) {
					sameTitleFound = true;
					return false;
				}
			});
			
			if (sameTitleFound) {
				copyCount++;
				title = 'Copy (' + copyCount + ') of ' + activity.title;
			} else {
				break;
			}
		};
		
		// draw the new activity next to the existing one
		var x = activity.items.shape.getBBox().x + 10,
			y = activity.items.shape.getBBox().y + 10,
			newActivity = new ActivityLib.ToolActivity(null, null, null, activity.toolID,
													   null, x, y, title);
		layout.activities.push(newActivity);
		
		if (activity.grouping) {
			// add grouping effect if the existing activity had one
			newActivity.grouping = activity.grouping;
			newActivity.draw();
		}
		
		setModified(true);
	},
	
	
	openPreview : function(){
		if (layout.modified) {
			// disabling the button does not do the trick, so we have to check it here
			return;
		}
		
		// initialize, create and enter the preview lesson
		$.ajax({
			url : LAMS_URL + 'monitoring/monitoring.do',
			data : {
				'method' : 'initializeLesson',
				'learningDesignID' : layout.ld.learningDesignID,
				'copyType' : 3,
				'lessonName' : 'Preview'
			},
			cache : false,
			dataType : 'text',
			success : function(lessonID) {
				if (!lessonID) {
					alert('Error while initialising lesson for preview');
					return;
				}
				
				$.ajax({
					url : LAMS_URL + 'monitoring/monitoring.do',
					data : {
						'method' : 'startPreviewLesson',
						'lessonID' : lessonID
					},
					cache : false,
					dataType : 'text',
					success : function() {
						// open preview pop up window
						window.open(LAMS_URL + 'home.do?method=learner&mode=preview&lessonID='+lessonID,'Preview',
									'width=920,height=700,resizable,status=yes');
					}
				});

			}
		});
	},
	
	
	/**
	 * Expands/collapses description field.
	 */
	toggleDescriptionDiv: function() {
		$('#ldDescriptionDetails').slideToggle(function(){
			$('#ldDescriptionHideTip').text($(this).is(':visible') ? '▲' : '▼');
			$('#templateContainer').height($('#ldDescriptionDiv').height() + $('#canvas').height() - 10);
		});
	},
	
	
	/**
	 * Opens a pop up for importing LD. Loads the imported LD to canvas.
	 */
	importLearningDesign : function(){
		var importWindow = window.open(LAMS_URL + 'authoring/importToolContent.do?method=import','Import',
					'width=800,height=298,resize=yes,status=yes,scrollbar=no,menubar=no,toolbar=no'),
			currentLearningDesignID = null,
			regEx = /learningDesignID=(\d+)/g,
			// since window.onload does not really work after submitting a form inside the window,
			// this trick checks periodically for changes
			loadCheckInterval = setInterval(function(){
				if (!importWindow){
					// window was closed
					clearInterval(loadCheckInterval);
					return;
				}
				var body = $('body', importWindow.document).html(),
					match = regEx.exec(body);
				// check if ID was found and it's not the same as previously
				if (match && match[1] != currentLearningDesignID) {
					currentLearningDesignID = match[1];
					// load the imported LD
					openLearningDesign(currentLearningDesignID);
				}
			}, 1000);
	},
	
	
	/**
	 * Opens a pop up for exporting LD.
	 */
	exportLearningDesign : function(){
		if (layout.modified || layout.activities.length == 0) {
			return;
		}
		
		 window.open(LAMS_URL + 'authoring/exportToolContent.do?learningDesignID=46','Export',
				 'width=712,height=298,resize=yes,status=yes,scrollbar=no,menubar=no,toolbar=no');
	}
	
	/*
	,zoom : function(){
		var zoomButton = $('#zoomButton > span');
		if (layout.isZoomed) {
			paper.setViewBox(0, 0, paper.width, paper.height, true);
			layout.isZoomed = false;
			zoomButton.text('Zoom out');
		} else {
			paper.setViewBox(-paper.width / 2, -paper.height / 2, paper.width * 2, paper.height * 2, true);
			layout.isZoomed = true;
			zoomButton.text('Cancel zoom');
		}
	}
	*/
};