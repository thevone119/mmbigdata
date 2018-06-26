//计算2点间的距离
function point_len(p1,p2){
	return Math.sqrt(Math.pow((p1.lng-p2.lng),2)+Math.pow((p1.lat-p2.lat),2));
}

//创建多边形区域
function createAreaOverlay(pl){
	if(pl.length==1){
		return new BMap.Circle(pl[0],1500, {strokeColor:"red", strokeWeight:3, strokeOpacity:0.5,fillColor:"red", fillOpacity:0.2});
	}
	if(pl.length==2){
		var cenp = centerPoint(pl);
		return new BMap.Circle(cenp,point_len(pl[0],pl[1])*100000/2+500, {strokeColor:"red", strokeWeight:3, strokeOpacity:0.5,fillColor:"red", fillOpacity:0.2});
	}
	if(pl.length==3){
		return new BMap.Polygon(pl,{strokeColor:"red", strokeWeight:3, strokeOpacity:0.5,fillOpacity:0.2});
	}

	var ret = polygonPoint(pl);
	return new BMap.Polygon(ret,{strokeColor:"red", strokeWeight:3, strokeOpacity:0.5,fillOpacity:0.2});
}

//计算多个点的中心点坐标
function centerPoint(pl){
	var maxy=0;
	var miny=100000;
	var maxx=0;
	var minx=100000;
	var cenx = 0;
	var ceny = 0;
	//选取最高的点,及中心点坐标
	for(i=0;i<pl.length;i++){
		if(pl[i].lat>maxy){
			maxy = pl[i].lat;
		}
		if(pl[i].lat<miny){
			miny = pl[i].lat;
		}
		if(pl[i].lng>maxx){
			maxx = pl[i].lng;
		}
		if(pl[i].lng<minx){
			minx = pl[i].lng;
		}
	}
	cenx = (maxx+minx)/2;
	ceny = (maxy+miny)/2;
	return new BMap.Point(cenx,ceny);
}

//计算3个点形成的角度
function triangleBias(p1,p2,p3){
	this.p1=p1;
	this.p2=p2;
	this.p3=p3;
	this.biasValue;
	this.type=0;
	this.init = function(){
		var x1 = p2.lng - p1.lng;
		var y1 = p2.lat - p1.lat;
		var x2 = p3.lng - p2.lng;
		var y2 = p3.lat - p2.lat;
		var samex = false;
		var samey = false;
		if((x1>=0&&x2>=0)||(x1<=0&&x2<=0)){
			samex = true;
		}
		if((y1>=0&&y2>=0)||(y1<=0&&y2<=0)){
			samey = true;
		}
		if(y1==0){
			y1 = 0.0000000000001;
		}
		if(y2==0){
			y2 = 0.0000000000001;
		}
		if(samex && samey){
			this.type=0;
			this.biasValue = Math.abs(x1/y1 - x2/y2);//<
		}else if(!samex&&!samey){
			this.type = 2;
			if(x1<0 && y1<0){
				this.biasValue =  Math.abs(y2/x2);
			}else if(x1>0 && y1<0){
				this.biasValue =  Math.abs(x2/y2);
			}else if(x1>0 && y1>0){
				this.biasValue =  Math.abs(y2/x2);
			}else {
				this.biasValue = -Math.abs(x1/y1 + x2/y2);//<
			}
		}else{
			this.type = 1;
			if(x1<0 && y1<0){
				this.biasValue =  Math.abs(x2/y2);
			}else if(x1>0 && y1<0){
				this.biasValue =  Math.abs(y2/x2);
			}else if(x1>0 && y1>0){
				this.biasValue =  Math.abs(x2/y2);
			}else {
				this.biasValue = Math.abs(1-(x1/y1 + x2/y2));//<
			}
		}
	}
}


function polygonPoint(pl){
	if(pl.length<4){
		return pl;
	}
	var rl = new Array();//返回的数组
	var maxy=0;
	var miny=100000;
	var maxx=0;
	var minx=100000;
	var maxyp=0;
	var cenx = 0;
	var ceny = 0;
	//选取最高的点,及中心点坐标
	for(i=0;i<pl.length;i++){
		if(pl[i].lat>maxy){
			maxy = pl[i].lat;
			maxyp=i;
		}
		if(pl[i].lat<miny){
			miny = pl[i].lat;
		}
		if(pl[i].lng>maxx){
			maxx = pl[i].lng;
		}
		if(pl[i].lng<minx){
			minx = pl[i].lng;
		}
	}
	cenx = (maxx+minx)/2;
	ceny = (maxy+miny)/2;
	
	rl[rl.length]=pl[maxyp];
	//从最高的点开始，选取凸角最大的点。
	while(true){
		var maxbias = null;//最大倾斜率
		var maxi=-1;//最小倾斜率对应的位置
		for(i=0;i<pl.length;i++){
			var hasp = false;
			for(j=1;j<rl.length;j++){
				if(pl[i].equals(rl[j])){
					hasp = true;
					break;
				}
			}
			if(rl.length==1){
				if(pl[i].equals(rl[0])){
					hasp = true;
				}
			}
			if(hasp){
				continue;
			}
			var p1;
			if(rl.length==1){
				p1 = new BMap.Point(rl[rl.length-1].lng+1,rl[rl.length-1].lat+0.0000001);
			}else{
				p1 = rl[rl.length-2];
			}
			var tbs = new triangleBias(p1,rl[rl.length-1],pl[i]);
			tbs.init();
			var ismax = false;
			if(maxbias==null){
				ismax = true;
			}else{
				if(tbs.type<maxbias.type){
					ismax = true;
				}else if(tbs.type==maxbias.type){
					if(tbs.biasValue<maxbias.biasValue){
						ismax = true;
					}
				}
			}
			if(ismax==true){
				maxbias = tbs;
				maxi = i;
			}
		}
		if(pl[maxi].equals(rl[0])){//回归
			return rl;
		}else{
			rl[rl.length] = pl[maxi];
		}
	}
}