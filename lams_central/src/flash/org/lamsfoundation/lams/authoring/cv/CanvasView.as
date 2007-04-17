﻿/***************************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0 
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ************************************************************************
 */

import org.lamsfoundation.lams.common.util.*
import org.lamsfoundation.lams.common.ui.*
import org.lamsfoundation.lams.common.style.*
import org.lamsfoundation.lams.authoring.cv.*
import org.lamsfoundation.lams.authoring.*
import org.lamsfoundation.lams.common.dict.*
import org.lamsfoundation.lams.common.mvc.*
import org.lamsfoundation.lams.common.CommonCanvasView
import com.polymercode.Draw;
import mx.controls.*
import mx.managers.*
import mx.containers.*;
import mx.events.*
import mx.utils.*


/**
*Authoring view for the canvas
* Relects changes in the CanvasModel
*/

class org.lamsfoundation.lams.authoring.cv.CanvasView extends CommonCanvasView {
	//constants:
	private var GRID_HEIGHT:Number;
	private var GRID_WIDTH:Number;
	private var H_GAP:Number;
	private var V_GAP:Number;
	
	private var _tm:ThemeManager;
	private var _cm:CanvasModel;
	//Canvas clip
	private var _canvas_mc:MovieClip;
	private var canvas_scp:ScrollPane;
    private var bkg_pnl:Panel;
	private var isRread_only:Boolean = false;
	private var read_only:MovieClip;
	private var titleBar:MovieClip;
	private var leftCurve:MovieClip;
	private var rightCurve:MovieClip;
	private var nameBG:MovieClip;
	//private var act_pnl:Panel;
	
	private var designName_lbl:Label;
	
    private var _gridLayer_mc:MovieClip;
    private var _transitionLayer_mc:MovieClip;
	private var _activityLayerComplex_mc:MovieClip;
	private var _activityLayer_mc:MovieClip;
	
	private var startTransX:Number;
	private var startTransY:Number;
	private var lastScreenWidth:Number = 1024;
	private var lastScreenHeight:Number = 768;
	
	private var _transitionPropertiesOK:Function;
    private var _canvasView:CanvasView;
    //Defined so compiler can 'see' events added at runtime by EventDispatcher
    private var dispatchEvent:Function;     
    public var addEventListener:Function;
    public var removeEventListener:Function;
	

	
	/**
	* Constructor
	*/
	function CanvasView(){
		_canvasView = this;
		_tm = ThemeManager.getInstance();
        //Init for event delegation
        mx.events.EventDispatcher.initialize(this);
	}
    
	/**
	* Called to initialise Canvas  . CAlled by the Canvas container
	*/
	public function init(m:Observable,c:Controller,x:Number,y:Number,w:Number,h:Number){
		//Invoke superconstructor, which sets up MVC relationships.
		//if(c==undefined){
		//	c==defaultController();
		//}
		super (m, c);
        //Set up parameters for the grid
		H_GAP = 10;
		V_GAP = 10;
		_cm = CanvasModel(m)
       
	   //register to recive updates form the model
		_cm.addEventListener('viewUpdate',this);
        
		MovieClipUtils.doLater(Proxy.create(this,draw)); 
    }    
    
/**
 * Recieved update events from the CanvasModel. Dispatches to relevent handler depending on update.Type
 * @usage   
 * @param   event
 */
public function viewUpdate(event:Object):Void{
		Debugger.log('Recived an Event dispather UPDATE!, updateType:'+event.updateType+', target'+event.target,4,'viewUpdate','CanvasView');
		 //Update view from info object
        //Debugger.log('Recived an UPDATE!, updateType:'+infoObj.updateType,4,'update','CanvasView');
       var cm:CanvasModel = event.target;
	   
	   switch (event.updateType){
            case 'POSITION' :
                setPosition(cm);
                break;
            case 'SIZE' :
                setSize(cm);
                break;
            case 'DRAW_ACTIVITY':
                drawActivity(event.data,cm);
                break;
            case 'REMOVE_ACTIVITY':
                removeActivity(event.data,cm);
                break;
            case 'DRAW_TRANSITION':
                drawTransition(event.data,cm);
				break;
			case 'REMOVE_TRANSITION':
				removeTransition(event.data,cm);
				break;
			case 'SELECTED_ITEM' :
                highlightActivity(cm);
                break;
			
			case 'POSITION_TITLEBAR':
				setDesignTitle(cm);
				break;
			/*
			case 'STOP_TRANSITION_TOOL':
				stopDrawingTransition(cm);
				break;
				*/
            default :
                Debugger.log('unknown update type :' + event.updateType,Debugger.CRITICAL,'update','org.lamsfoundation.lams.CanvasView');
		}

	}
	/*
	public function onRelease(){
		getController().canvasRelease(_canvas_mc);
	}
	*/
	
	/**
    * layout visual elements on the canvas on initialisation
    */
	private function draw(){
		//get the content path for the sp
		_canvas_mc = canvas_scp.content;
		//Debugger.log('_canvas_mc'+_canvas_mc,Debugger.GEN,'draw','CanvasView');
		
		bkg_pnl = _canvas_mc.createClassObject(Panel, "bkg_pnl", getNextHighestDepth());
		//set up the 
		//_canvas_mc = this;
		_gridLayer_mc = _canvas_mc.createEmptyMovieClip("_gridLayer_mc", _canvas_mc.getNextHighestDepth());
		_transitionLayer_mc = _canvas_mc.createEmptyMovieClip("_transitionLayer_mc", _canvas_mc.getNextHighestDepth());
		
		_activityLayerComplex_mc = _canvas_mc.createEmptyMovieClip("_activityLayerComplex_mc", _canvas_mc.getNextHighestDepth());
		
		_activityLayer_mc = _canvas_mc.createEmptyMovieClip("_activityLayer_mc", _canvas_mc.getNextHighestDepth());
		
		titleBar = _canvasView.attachMovie("DesignTitleBar", "titleBar", _canvasView.getNextHighestDepth())
		//var styleObj = _tm.getStyleObject('redLabel');
		var styleObj = _tm.getStyleObject('label');
		read_only = _canvasView.attachMovie('Label', 'read_only', _canvasView.getNextHighestDepth(), {_x:5, _y:titleBar._y, _visible:true, autoSize:"left", html:true, styleName:styleObj});
		//read_only.text = Dictionary.getValue('cv_readonly_lbl');
		
		//_canvas_mc.addEventListener('onRelease',this);
		bkg_pnl.onRelease = function(){
			trace('_canvas_mc.onRelease');
			Application.getInstance().getCanvas().getCanvasView().getController().canvasRelease(this);
		}
		bkg_pnl.useHandCursor = false;
		
		setDesignTitle();
		
		styleTitleBar();
		setStyles();
		
        //Debugger.log('canvas view dispatching load event'+_canvas_mc,Debugger.GEN,'draw','CanvasView');
        //Dispatch load event 
        dispatchEvent({type:'load',target:this});
	}
	
	private function setDesignTitle(cm:CanvasModel){
		//titleBar.designName_lbl.text = "<b>"+Dictionary.getValue('pi_title')+"</b>";
		var dTitle:String;
		var titleToCheck:String;
		if (isRread_only){
			dTitle = cm.getCanvas().ddm.title + " (<font color='#FF0000'>"+Dictionary.getValue('cv_readonly_lbl')+"</font>)"
			titleToCheck = cm.getCanvas().ddm.title + Dictionary.getValue('cv_readonly_lbl')
		}else {
			dTitle = cm.getCanvas().ddm.title
			titleToCheck = dTitle
		}
		if (dTitle == undefined || dTitle == null || dTitle == ""){
			dTitle = Dictionary.getValue('cv_untitled_lbl');
			titleToCheck = dTitle
		}
			
		read_only.text = dTitle;
		setSizeTitleBar(titleToCheck);
	}
	
	private function setSizeTitleBar(dTitle:String):Void{
		dTitle = StringUtils.replace(dTitle, " ", "")
		_canvasView.createTextField("designTitle", _canvasView.getNextHighestDepth(), -10000, -10000, 20, 20)
		var nameTextFormat = new TextFormat();
		nameTextFormat.bold = true;
		nameTextFormat.font = "Verdana";
		nameTextFormat.size = 12;
		
		var titleTxt = _canvasView["designTitle"];
		titleTxt.multiline = false;
		titleTxt.autoSize = true
		titleTxt.text = dTitle;
		titleTxt.setNewTextFormat(nameTextFormat);
		
		var bgWidth = titleTxt.textWidth;
		titleBar.nameBG._width = bgWidth;
		titleBar.nameBGShadow._width = bgWidth;
		titleBar.nameBG._visible  = true;
		titleBar.rightCurve._x = bgWidth+27;
		titleBar.rightCurveShadow._x = titleBar.rightCurve._x+2
		
	}
	
	
	private function positionTitleBar(cm:CanvasModel):Void{
		titleBar._y = canvas_scp._y;
		titleBar._x = (canvas_scp.width/2)-(titleBar._width/2)
		read_only._x = titleBar._x + 5;
		
	}
	
	private function styleTitleBar():Void {
		
		var titleBarBg:mx.styles.CSSStyleDeclaration = _tm.getStyleObject("BGPanel");
		var titleBarBgShadow:mx.styles.CSSStyleDeclaration = _tm.getStyleObject("BGPanelShadow");
		
		var nameBGColor:Color = new Color(titleBar.nameBG);
		var nameBGShadowColor:Color = new Color(titleBar.nameBGShadow);
		var rightCurveColor:Color = new Color(titleBar.rightCurve);
		var rightCurveShadowColor:Color = new Color(titleBar.rightCurveShadow);
		
		var bgColor:Number = titleBarBg.getStyle("backgroundColor");
		var bgShadowColor:Number = titleBarBgShadow.getStyle("backgroundColor");
		
		nameBGColor.setRGB(bgColor);
		nameBGShadowColor.setRGB(bgShadowColor);
		rightCurveColor.setRGB(bgColor);
		rightCurveShadowColor.setRGB(bgShadowColor);
		
	}
   
	public function initDrawTempTrans(){
		Debugger.log("Initialising drawing temp. Transition", Debugger.GEN, "initDrawTempTrans", "CanvasView");
		_activityLayer_mc.createEmptyMovieClip("tempTrans", _activityLayer_mc.getNextHighestDepth());
		_activityLayer_mc.attachMovie("squareHandle", "h1", _activityLayer_mc.getNextHighestDepth());
		_activityLayer_mc.attachMovie("squareHandle", "h2", _activityLayer_mc.getNextHighestDepth());
		_activityLayer_mc.h1._x = _canvas_mc._xmouse
		_activityLayer_mc.h1._y = _canvas_mc._ymouse
		trace("startTransX: "+_activityLayer_mc.h1._x)
		_activityLayer_mc.tempTrans.onEnterFrame = drawTempTrans;
		trace("startTransY: "+_activityLayer_mc.h1._y)
		
	}
	
	/**
	 * used to draw temp dotted transtion.
	 * @usage   
	 * @return  
	 */
	private function drawTempTrans(){
	   //var drawLineColor = 
	   trace("_activityLayer_mc.tempTrans: "+this)
	   trace("startTransX: "+startTransX)
	   trace("startTransY: "+startTransY)
	   trace("_parent._parent: "+_parent._parent);
	   Debugger.log("Started drawing temp. Transition", Debugger.GEN, "drawTempTrans", "CanvasView");
	   
	   this.clear();
	   
	   Debugger.log("Runtime movieclips cleared from CanvasView: clear()", Debugger.GEN, "drawTempTrans", "CanvasView");
	   
	   Draw.dashTo(this, _parent.h1._x, _parent.h1._y, _parent._parent._xmouse - 3, _parent._parent._ymouse - 3, 7, 4);
	   _parent.h2._x = _parent._parent._xmouse - 3
	   _parent.h2._y = _parent._parent._ymouse - 3
   }
	
	public function removeTempTrans(){
	   Debugger.log("Stopped drawing temp. Transition", Debugger.GEN, "removeTempTrans", "CanvasView");
		trace("stopped Drawing ")
		delete _activityLayer_mc.tempTrans.onEnterFrame;
		_activityLayer_mc.tempTrans.removeMovieClip();
		_activityLayer_mc.h1.removeMovieClip();
		_activityLayer_mc.h2.removeMovieClip();
	}
	 
	   
	/**
	 * Draws new or replaces existing activity to canvas stage.
	 * @usage   
	 * @param   a  - Activity to be drawn
	 * @param   cm - Refernce to the model
	 * @return  Boolean - successfullit
	 */
	private function drawActivity(a:Activity,cm:CanvasModel):Boolean{
		var s:Boolean = false;
		//Debugger.log('a.title:'+a.title,4,'drawActivity','CanvasView');
		//var initObj:Object = {_activity=a};
		//_global.breakpoint();
		var cvv = CanvasView(this);
		
		var cvc = getController();
		//Debugger.log('I am in drawActivity and Activity typeID :'+a+' added to the cm.activitiesDisplayed hashtable :'+newActivity_mc,4,'drawActivity','CanvasView');
		Debugger.log('I am in drawActivity and Activity typeID :'+a.activityTypeID+' added to the cm.activitiesDisplayed hashtable :'+newActivity_mc,4,'drawActivity','CanvasView');
		//take action depending on act type
		if(a.activityTypeID==Activity.TOOL_ACTIVITY_TYPE || a.isGroupActivity()){
			var newActivity_mc = _activityLayer_mc.createChildAtDepth("CanvasActivity",DepthManager.kTop,{_activity:a,_canvasController:cvc,_canvasView:cvv});
			cm.activitiesDisplayed.put(a.activityUIID,newActivity_mc);
			Debugger.log('Tool or gate activity a.title:'+a.title+','+a.activityUIID+' added to the cm.activitiesDisplayed hashtable:'+newActivity_mc,4,'drawActivity','CanvasView');
		}
		if (a.isGateActivity()){
			var newActivity_mc = _activityLayer_mc.createChildAtDepth("CanvasGateActivity",DepthManager.kTop,{_activity:a,_canvasController:cvc,_canvasView:cvv});
			cm.activitiesDisplayed.put(a.activityUIID,newActivity_mc);
			Debugger.log('Gate activity a.title:'+a.title+','+a.activityUIID+' added to the cm.activitiesDisplayed hashtable:'+newActivity_mc,4,'drawActivity','CanvasView');
		}
		if(a.activityTypeID==Activity.PARALLEL_ACTIVITY_TYPE){
			//get the children
			var children:Array = cm.getCanvas().ddm.getComplexActivityChildren(a.activityUIID);
			//var newActivity_mc = _activityLayer_mc.createChildAtDepth("CanvasParallelActivity",DepthManager.kTop,{_activity:a,_children:children,_canvasController:cvc,_canvasView:cvv});
			var newActivity_mc = _activityLayer_mc.createChildAtDepth("CanvasParallelActivity",DepthManager.kTop,{_activity:a,_children:children,_canvasController:cvc,_canvasView:cvv});
			cm.activitiesDisplayed.put(a.activityUIID,newActivity_mc);
			Debugger.log('Parallel activity a.title:'+a.title+','+a.activityUIID+' added to the cm.activitiesDisplayed hashtable :'+newActivity_mc,4,'drawActivity','CanvasView');
		}
		if(a.activityTypeID==Activity.OPTIONAL_ACTIVITY_TYPE){
			var children:Array = cm.getCanvas().ddm.getComplexActivityChildren(a.activityUIID);
			//var newActivity_mc = _activityLayer_mc.createChildAtDepth("CanvasParallelActivity",DepthManager.kTop,{_activity:a,_children:children,_canvasController:cvc,_canvasView:cvv});
			var newActivity_mc = _activityLayerComplex_mc.createChildAtDepth("CanvasOptionalActivity",DepthManager.kTop,{_activity:a,_children:children,_canvasController:cvc,_canvasView:cvv});
			cm.activitiesDisplayed.put(a.activityUIID,newActivity_mc);
			Debugger.log('Optional activity Type a.title:'+a.title+','+a.activityUIID+' added to the cm.activitiesDisplayed hashtable :'+newActivity_mc,4,'drawActivity','CanvasView');
			
		}else{
			Debugger.log('The activity:'+a.title+','+a.activityUIID+' is of unknown type, it cannot be drawn',Debugger.CRITICAL,'drawActivity','CanvasView');
		}
		
		
		
		
		
		//position
		//newActivity_mc._x = a.xCoord;
		//newActivity_mc._y = a.yCoord;
		
		//newActivity_mc._visible = true;
		
		s = true;
		
		return s;
	}
	
	/**
	 * Removes existing activity from canvas stage. DOES not affect DDM.  called by an update, so DDM change is already made
	 * @usage   
	 * @param   a  - Activity to be Removed
	 * @param   cm - Refernce to the model
	 * @return  Boolean - successfull
	 */
	private function removeActivity(a:Activity,cm:CanvasModel):Boolean{
		//Debugger.log('a.title:'+a.title,4,'removeActivity','CanvasView');
		var r = cm.activitiesDisplayed.remove(a.activityUIID);
		r.removeMovieClip();
		var s:Boolean = (r==null) ? false : true;
		return s;
	}
	
	/**
	 * Draws a transition on the canvas.
	 * @usage   
	 * @param   t  The transition to draw
	 * @param   cm  the canvas model.
	 * @return  
	 */
	private function drawTransition(t:Transition,cm:CanvasModel):Boolean{
		var s:Boolean = true;
		//Debugger.log('t.fromUIID:'+t.fromUIID+', t.toUIID:'+t.toUIID,Debugger.GEN,'drawTransition','CanvasView');
		var cvv = CanvasView(this);
		var cvc = getController();
		var newTransition_mc:MovieClip = _transitionLayer_mc.createChildAtDepth("CanvasTransition",DepthManager.kTop,{_transition:t,_canvasController:cvc,_canvasView:cvv});
		
		cm.transitionsDisplayed.put(t.transitionUIID,newTransition_mc);
		Debugger.log('drawn a transition:'+t.transitionUIID+','+newTransition_mc,Debugger.GEN,'drawTransition','CanvasView');
		return s;
		
	}
	
	/**
	 * Removes a transition from the canvas
	 * @usage   
	 * @param   t  The transition to remove
	 * @param   cm  The canvas model
	 * @return  
	 */
	private function removeTransition(t:Transition,cm:CanvasModel){
		//Debugger.log('t.uiID:'+t.transitionUIID,Debugger.CRITICAL,'removeTransition','CanvasView');
		var r = cm.transitionsDisplayed.remove(t.transitionUIID);
		r.removeMovieClip();
		var s:Boolean = (r==null) ? false : true;
		return s;
	}
	
		
	/**
    * Create a popup dialog to set transition parameters
    * @param    pos - Position, either 'centre' or an object containing x + y coordinates
    */
    public function createTransitionPropertiesDialog(pos:Object,callBack:Function){
	   //Debugger.log('Call',Debugger.GEN,'createTransitionPropertiesDialog','CanvasView');
	   var dialog:MovieClip;
	   _transitionPropertiesOK = callBack;
        //Check to see whether this should be a centered or positioned dialog
        if(typeof(pos)=='string'){
			//Debugger.log('pos:'+pos,Debugger.GEN,'createTransitionPropertiesDialog','CanvasView');
            dialog = PopUpManager.createPopUp(Application.root, LFWindow, true,{title:Dictionary.getValue('trans_dlg_title'),closeButton:true,scrollContentPath:"TransitionProperties"});
        } else {
            dialog = PopUpManager.createPopUp(Application.root, LFWindow, true,{title:Dictionary.getValue('trans_dlg_title'),closeButton:true,scrollContentPath:"TransitionProperties",_x:pos.x,_y:pos.y});
        }
        //Assign dialog load handler
        dialog.addEventListener('contentLoaded',Delegate.create(this,transitionDialogLoaded));
        //okClickedCallback = callBack;
    }
	
	/**
    * called when the transitionDialogLoaded is loaded
    */
    public function transitionDialogLoaded(evt:Object) {
        //Debugger.log('!evt.type:'+evt.type,Debugger.GEN,'dialogLoaded','CanvasView');
        //Check type is correct
        if(evt.type == 'contentLoaded'){
            //Set up callback for ok button click
            //Debugger.log('!evt.target.scrollContent:'+evt.target.scrollContent,Debugger.GEN,'dialogLoaded','CanvasView');
            evt.target.scrollContent.addEventListener('okClicked',_transitionPropertiesOK);
        }else {
            //TODO DI 25/05/05 raise wrong event type error 
        }
    }
	
	
    /**
    * Sets the size of the canvas on stage, called from update
    */
	private function setSize(cm:CanvasModel):Void{
		//positionTitleBar();
        var s:Object = cm.getSize();
		var newWidth:Number = Math.max(s.w, lastScreenWidth)
		var newHeight:Number = Math.max(s.h, lastScreenHeight)
		canvas_scp.setSize(s.w,s.h);
		bkg_pnl.setSize(newWidth,newHeight);
		
		//Create the grid.  The gris is re-drawn each time the canvas is resized.
		//var grid_mc = Grid.drawGrid(_gridLayer_mc,Math.round(s.w),Math.round(s.h),V_GAP,H_GAP);
		var grid_mc = Grid.drawGrid(_gridLayer_mc,Math.round(newWidth),Math.round(newHeight),V_GAP,H_GAP);
		
		//position bin in canvas.  
		var bin = cm.getCanvas().bin;
		bin._x = (s.w - bin._width) - 20;
		bin._y = (s.h - bin._height) - 20;
		canvas_scp.redraw(true);
		lastScreenWidth = newWidth
		lastScreenHeight = newHeight
	}
	
	/**
	 * Get the CSSStyleDeclaration objects for each component and apply them
	 * directly to the instance
	 * @usage   
	 * @return  
	 */
	private function setStyles() {
        
		var styleObj = _tm.getStyleObject('CanvasPanel');
		bkg_pnl.setStyle('styleName',styleObj);
		
		//styleObj = _tm.getStyleObject('label');
		//titleBar.designName_lbl.setStyle('styleName',styleObj);
		
		
		
    }
    
	public function setTTData(tData:Object):Void{
        
		//transTargetMC = tData.transTargetMC
		//tempTrans_mc = tData.tempTrans_mc
		//startTransX = tData.startTransX
		//startTransY = tData.startTransY
	}
	
	public function getTTData():Object{
        var tData:Object = new Object();
		//tData.transTargetMC = transTargetMC
		//tData.tempTrans_mc = tempTrans_mc
		//tData.startTransX = startTransX
		//tData.startTransY = startTransY
		return tData
	}
    /**
    * Sets the position of the canvas on stage, called from update
    * @param cm Canvas model object 
    */
	private function setPosition(cm:CanvasModel):Void{
        var p:Object = cm.getPosition();
        this._x = p.x;
        this._y = p.y;
	}
	
	public function getViewMc(testString:String):MovieClip{
		trace("passed on argument is "+testString)
		return _canvas_mc;
	}
	
	public function getTransitionLayer():MovieClip{
		return _transitionLayer_mc;
	}
	
	public function get activityLayer():MovieClip{
		return _activityLayer_mc;
	}
	
	public function showReadOnly(b:Boolean){
		isRread_only = b;
	}
	
	/**
	 * Overrides method in abstract view to ensure cortect type of controller is returned
	 * @usage   
	 * @return  CanvasController
	 */
	public function getController():CanvasController{
		var c:Controller = super.getController();
		return CanvasController(c);
	}
	
	/**
    * Returns the default controller for this view .
	* Overrides AbstractView.defaultController()
    */
    public function defaultController (model:Observable):Controller {
        return new CanvasController(model);
    }
}