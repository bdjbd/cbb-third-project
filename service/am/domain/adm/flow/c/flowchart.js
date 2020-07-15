//---图形对象---------
var tasks=[];
var lines=[];
var allTasks={};
var allVertices={};
function Task(id,name,type,x,y,width,height,style,doneUrl,noticeUrl,tooltip,warning,actionId){
	this.id=id;//1,999
	this.name=name?name:'task ' + id;
	this.type=type;//2人工,3分支,999结束
	this.x=x;
	this.y=y;
	this.width=width;
	this.height=height;

	this.style = style;
	this.doneUrl = doneUrl;
  this.noticeUrl = noticeUrl;
	this.tooltip = tooltip;
  this.warning = warning;
  this.actionId = actionId;

	allTasks['task'+this.id]=this;
}
Task.prototype.getTaskUrl=function(){
	if(this.type=='2'){//人工任务
		return '/adm/manualtask.do?m=e&tid='+this.id;
	}else{
		return '/adm/splittask.do?m=e&tid='+this.id;
	}
}
Task.prototype.getMonitorUrl=function(){
  if(this.doneUrl){
    return this.doneUrl;
  }else if(this.noticeUrl){
    return this.noticeUrl;
  }else{
    return null;
  }
}
Task.prototype.isValidSource=function(){
  //是否可作为连线起点：（1）分支任务（2）具有可用动作编号的人工任务
	return this.type=='3' || (this.type=='2' && this.actionId);
}
Task.prototype.write=function(parent,isMonitor){
	var cell = graph.insertVertex(parent, 'task'+this.id,this.name, this.x, this.y, this.width, this.height,this.style);
	allVertices[this.id]=cell;
  if(this.warning){
    //warning.png不闪烁
    var overlay = new mxCellOverlay(new mxImage('/domain/adm/flow/c/images/warning.gif', 16, 16),this.warning);
    graph.addCellOverlay(cell, overlay);
  }
}
function Line(id,name,source,target,style){
	this.id=id;
	this.name=name;
	this.source=source;
	this.target=target;
	this.style=style;
}
Line.prototype.same=function(sourceTaskId,targetTaskId){
	return this.source==sourceTaskId && this.target==targetTaskId;
}
Line.prototype.write=function(){
	graph.insertEdge(parent, this.id,this.name, allVertices[this.source], allVertices[this.target],this.style);
}
//---画图------------
var container = $('flowchart');
mxEvent.disableContextMenu(container);
var model = new mxGraphModel();
var graph = new mxGraph(container, model);
var parent = graph.getDefaultParent();
//形状的默认样式
var style = [];
style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
style[mxConstants.STYLE_STROKECOLOR] = 'gray';
style[mxConstants.STYLE_ROUNDED] = true;
style[mxConstants.STYLE_FILLCOLOR] = '#EEEEEE';
//style[mxConstants.STYLE_GRADIENTCOLOR] = 'white';
style[mxConstants.STYLE_FONTCOLOR] = '#774400';
style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
style[mxConstants.STYLE_FONTSIZE] = '12';
style[mxConstants.STYLE_FONTSTYLE] = 1;
graph.getStylesheet().putDefaultVertexStyle(style);

//是否可移动
graph.isCellMovable = function(cell){return cell.isVertex();};//|| cell.isEdge()
//是否可选择
graph.isCellSelectable=function(cell){return cell.isVertex() || cell.isEdge();};
//标签是否可移动
graph.isLabelMovable=function(cell){return false;};
//标签是否可修改
graph.isCellsEditable=function(cell){return false;};

//graph.isCellDeletable=function(cell){return cell.id.indexOf('task')==0;};

//鼠标移上的提示
graph.setTooltips(true);
graph.getTooltipForCell = function(cell){
	return allTasks[cell.id].tooltip;
}
//移动单位（默认为10，纵向移动单位某些情形下减半）
//graph.gridSize = 10;
//指导线
mxGraphHandler.prototype.guidesEnabled = true;
//mxConstants.GUIDE_COLOR = '#FF0000';
//mxConstants.GUIDE_STROKEWIDTH = 1;

//使连线预览的形状符合实际
graph.connectionHandler.createEdgeState = function(me){
  var relation = doc.createElement('Relation');
  relation.setAttribute('sourceRow', this.currentRow || '0');
  relation.setAttribute('targetRow', '0');
  var edge = this.createEdge(relation);
  var style = this.graph.getCellStyle(edge);
  var state = new mxCellState(this.graph.view, edge, style);
  this.sourceRowNode = this.currentRowNode;
  return state;
};
//连线样式
// 两拐点直角：elbowEdgeStyle
// 两拐点直角：sideToSideEdgeStyle
// 两拐点直角：topToBottomEdgeStyle
// 多拐点直角：orthogonalEdgeStyle
// 多拐点直角：segmentEdgeStyle
// 两拐点斜角：entityRelationEdgeStyle
// 两拐点斜角：loopEdgeStyle
// WireConnector
function setEdgeStyle(type){
  switch (type){
    case '0':
      break;
    case '1':
      var style = graph.getStylesheet().getDefaultEdgeStyle();
      style[mxConstants.STYLE_EDGE] = mxEdgeStyle.ElbowConnector;
      style[mxConstants.STYLE_BENDABLE] =false;
      break;
    case '2':
      var style = graph.getStylesheet().getDefaultEdgeStyle();
      style[mxConstants.STYLE_EDGE] = mxEdgeStyle.ElbowConnector;
      style[mxConstants.STYLE_BENDABLE] =false;
      style[mxConstants.STYLE_ROUNDED] = true;
      break;
    case '3':
      var style = graph.getStylesheet().getDefaultEdgeStyle();
      //style[mxConstants.STYLE_LABEL_BACKGROUNDCOLOR] = '#FFFFFF';
      //style[mxConstants.STYLE_STROKEWIDTH] = '2';
      style[mxConstants.STYLE_EDGE] = mxEdgeStyle.EntityRelation;
      style[mxConstants.STYLE_ROUNDED] = true;
      break;
  }
}
//在单独的一步中添加cell
function writeFlowChart(){
	//graph.setEnabed(false);	
	model.beginUpdate();
	try{
		for(var i=0;i<tasks.length ;i++){
			tasks[i].write(parent);
		}
		for(var i=0;i<lines.length ;i++){
			lines[i].write(parent);
		}
	}finally{
	  // 更新显示
	  model.endUpdate();
    // 扩大可添加区域
    //container.setStyle('border','1px solid red');
    var height =container.getSize().y;
    if(height<361){
      container.setStyle('height','361px');
    }else{
      //修正高度为：10的整数倍+1，以便网格下边缘线可见
      container.setStyle('height',(height-(height%10)+11)+'px');
    }
	}
}
//---按钮栏------------
//添加任务
function addTask(taskType,x,y){
	new MooDialog.Iframe(url+'&domain='+$fnv('flowchart.domain')+'&fid='+$fnv('flowchart.fid'),{'class': 'MooDialog taskDialog'});
}
//修改任务
function openTask(url){
	new MooDialog.Iframe(url+'&domain='+$fnv('flowchart.domain')+'&fid='+$fnv('flowchart.fid'),{'class': 'MooDialog taskDialog'});
}
//变更连线风格
function updateEdgeStyle(style){
	 doLink('/adm/flowchart/style.do?domain='+$fnv('flowchart.domain')+'&fid='+$fnv('flowchart.fid')+'&style='+style);
}
//保存布局
function saveLayout(){
	var vertices = graph.getChildVertices(parent);
	var taskId='';
	var x='';
	var y='';
	var width='';
	var height='';
	for(var i=0;i<vertices.length;i++){
		var v = vertices[i];
		var g=v.getGeometry();
		taskId+=v.id.substring(4);
		x+=g.x;
		y+=g.y;
		width+=g.width;
		height+=g.height;
		if(i<vertices.length-1){
			taskId+=',';
			x+=',';
			y+=',';
			width+=',';
			height+=',';
		}
	}
	doLink('/adm/flowchart/layout.do?domain='+$fnv('flowchart.domain')+'&fid='+$fnv('flowchart.fid')+'&taskid='+taskId+'&x='+x+'&y='+y+'&width='+width+'&height='+height);
}
//---编辑模式----------
var sourceTaskId;
var sourceTaskType;
var sourceTaskActionId;
var targetTaskId;
//var connected=0;
function editGraph(){
  //网格
  container.addClass('grid');
  //工具栏
  addToolbar();
  //连线
  setConnection();
  //右键
	addContextMenu();
  //添加事件监听
  graph.addListener(mxEvent.ADD_CELLS, function(source, evt){
	  var cell = evt.getProperty('cells')[0];
    if(cell.isVertex()){
		  var g=cell.getGeometry();
      var type = graph.getCellStyle(cell)['shape']=='rhombus'?'3':'2';//rhombus:菱形
	    doLink('/adm/flowchart/addtask.do?domain='+$fnv('flowchart.domain')+'&fid='+$fnv('flowchart.fid')+'&x='+g.x+'&y='+g.y+'&width='+g.width+'&height='+g.height+'&type='+type);
    }else{
	    doLink('/adm/flowchart/addline.do?domain='+$fnv('flowchart.domain')+'&fid='+$fnv('flowchart.fid')+'&source='+sourceTaskId+'&target='+targetTaskId+'&type='+sourceTaskType+'&actionid='+sourceTaskActionId);
    }
  });
}
//右键
function addContextMenu(){
	graph.panningHandler.factoryMethod = function(menu, cell, evt){
    if(cell==null){
      return ;
    }
		if(cell.isEdge()){
      //test points
      /*var points = cell.getGeometry().points;
      if (points){
        var pointsStr = '';
        for(var i=0;i<points.length;i++){
            pointsStr+=points[i].x+','+points[i].y;
            if(i<points.length-1){
              pointsStr+=';';
            }
        }
        alert(pointsStr);
      }*/
      if(cell.id){
        menu.addItem('删除', '/common/images/tool/delete.png', function(){
          if(confirm('删除连线，你确认吗？')){
            doLink('/adm/flowchart/deleteline.do?domain='+$fnv('flowchart.domain')+'&fid='+$fnv('flowchart.fid')+'&id='+cell.id);
          }
        });
      }
    }else if(cell.isVertex() && cell.id!='task999'){
			menu.addItem('编辑', '/common/images/tool/edit.png', function(){
				openTask(allTasks[cell.id].getTaskUrl());
			});
			if(cell.id!='task1'){
				menu.addItem('删除', '/common/images/tool/delete.png', function(){
					if(confirm('删除任务，你确认吗？')){
						var taskId = cell.id.substring(4);
						doLink('/adm/flowchart/delete.do?fid='+$fnv('flowchart.fid')+'&tid='+taskId);
					}
				});
			}
		}
	};  
}
//连线
function setConnection(){
  graph.setConnectable(true);
  //连接目标不能为空
  graph.setAllowDanglingEdges(false);
  //图标
  mxConnectionHandler.prototype.connectImage = new mxImage('/domain/adm/flow/c/images/connector.gif', 14, 14);
  //合法起点：不是结束任务
  mxConnectionHandler.prototype.isValidSource=function(cell){return cell.isVertex() && allTasks[cell.id] && allTasks[cell.id].isValidSource();};
  //连线验证
  mxConnectionHandler.prototype.validateConnection=function(source,target){
    var sourceTask = allTasks[source.id];
    var targetTask = allTasks[target.id];
    if(!targetTask || sourceTask==targetTask){
      return '';//空字符串表示不通过、但不提示
    }
    sourceTaskId =sourceTask.id;
    sourceTaskType=sourceTask.type;
    sourceTaskActionId = sourceTask.actionId;
    targetTaskId =targetTask.id;
    for(var i=0;i<lines.length ;i++){
      if(lines[i].same(sourceTaskId,targetTaskId)){return '连线已存在';}
    }
    return null;
  }
  /*graph.addListener(mxEvent.CELL_CONNECTED, function(source, evt){
    connected+=1;
    if(connected==2){
      alert('connected: '+sourceTaskId+' - '+targetTaskId);
      connected=0;
    }
  });*/
}
//工具栏
function addToolbar(){
  var tbContainer = document.createElement('div');
  tbContainer.id = 'toolbarC';
  tbContainer.style.position = 'absolute';
  tbContainer.style.overflow = 'hidden';
  tbContainer.style.padding = '4px';
  tbContainer.style.left = '6px';
  tbContainer.style.top = $('msg').get('html')?'56px':'31px';
  tbContainer.style.width = '20px';
  tbContainer.style.height = '51px';
  tbContainer.style.bottom = '0px';
  tbContainer.style.backgroundColor = '#D4D0C8';
  
  document.body.appendChild(tbContainer);
  var toolbar = new mxToolbar(tbContainer);
	toolbar.enabled = false;
  var addVertex = function(icon, w, h, style)
  {
    var vertex = new mxCell(null, new mxGeometry(0, 0, w, h), style);
    vertex.setVertex(true);  
    addToolbarItem(graph, toolbar, vertex, icon);
  };  
  addVertex('/domain/adm/flow/c/images/rectangle.png', 100, 30, '');
  addVertex('/domain/adm/flow/c/images/rhombus.png', 100, 40, 'shape=rhombus');
  //toolbar.addLine();
}
function addToolbarItem(graph, toolbar, prototype, image){
  var funct = function(graph, evt, cell){
    graph.stopEditing(false);
    var pt = graph.getPointForEvent(evt);
    var vertex = graph.getModel().cloneCell(prototype);
    vertex.geometry.x = pt.x;
    vertex.geometry.y = pt.y;      
    graph.addCell(vertex);
    graph.setSelectionCell(vertex);
  }
  var img = toolbar.addMode(null, image, funct);
  mxUtils.makeDraggable(img, graph, funct);
}
//---监控----------
var taskInstDialog;
function monitorGraph(){
  //禁止一切调整
	graph.clearSelection();
	graph.setEnabled(false);
	graph.setTooltips(false);
  //点击事件
  graph.addListener(mxEvent.CLICK, function(source, evt){
    var cell = evt.getProperty('cell');
    if (cell && cell.isVertex() && allTasks[cell.id].getMonitorUrl()){
      taskInstDialog=new MooDialog.Iframe(allTasks[cell.id].getMonitorUrl(),{
        'class': 'MooDialog taskInstDialog'
      });
    }
  });
}
//---未使用----------
//自动布局 Stack/Partition/CompactTree/FastOrganic/Composite/EdgeLabel/
function hierarchicalLayout(){
	var layout = new mxHierarchicalLayout(graph);
	layout.execute(parent);
}
function circleLayout(){
	var circleLayout = new mxCircleLayout(graph);
	circleLayout.execute(parent);
}
function organicLayout(){
	var layout = new mxFastOrganicLayout(graph);
	graph.getModel().beginUpdate();
	try{
		layout.execute(parent);
	}catch (e){
		throw e;
	}finally{
		if (true){//动画效果
			// Default values are 6, 1.5, 20
			var morph = new mxMorphing(graph, 10, 1.7, 20);
			morph.addListener(mxEvent.DONE, function(){
				graph.getModel().endUpdate();
			});
			morph.startAnimation();
		}else{
			graph.getModel().endUpdate();
		}
	}
}
//设置图片样式
function setImageStyle(name){
	var style = new Object();
	style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
	style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
	style[mxConstants.STYLE_IMAGE] = '/domain/adm/flow/d/task/'+name+'.png';
	//style[mxConstants.STYLE_FONTCOLOR] = '#FFFFFF';
	graph.getStylesheet().putCellStyle(name, style);
}
