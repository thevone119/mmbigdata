package com.bingo.business.map.controller;

		import com.bingo.business.map.util.MapUtil;
		import com.bingo.common.exception.DaoException;
		import com.bingo.common.exception.ServiceException;
		import com.bingo.common.utility.PubClass;
		import com.bingo.common.utility.XJsonInfo;
		import org.apache.logging.log4j.LogManager;
		import org.apache.logging.log4j.Logger;
		import org.springframework.context.annotation.Scope;
		import org.springframework.http.HttpHeaders;
		import org.springframework.http.HttpRequest;
		import org.springframework.web.bind.annotation.RequestBody;
		import org.springframework.web.bind.annotation.RequestMapping;
		import org.springframework.web.bind.annotation.ResponseBody;
		import org.springframework.web.bind.annotation.RestController;

		import javax.annotation.Resource;
		import javax.servlet.http.HttpServletRequest;
		import java.io.BufferedReader;
		import java.io.File;
		import java.io.FileInputStream;
		import java.io.InputStreamReader;
		import java.text.SimpleDateFormat;
		import java.util.*;

		import com.bingo.business.map.model.*;
		import com.bingo.business.map.service.*;

/**
 * @author huangtw
 * 2018-03-13 15:00:03
 * 对象功能: 区域表 Controller管理
 */
@RestController
@Scope("prototype")
@RequestMapping("/api/map/mapcfgarea")
public class MapCfgAreaController  {

	@Resource
	private PubClass pubClass;

	@Resource
	private MapCfgAreaService mapcfgareaService;

	public MapCfgAreaController(){

	}

	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
	@RequestMapping("/saveJson")
	public XJsonInfo saveJson(@RequestBody MapCfgArea vo) throws ServiceException, DaoException {
		mapcfgareaService.saveOrUpdate(vo);
		return new XJsonInfo();
	}

	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
	@RequestMapping("/save")
	public XJsonInfo save(MapCfgArea vo) throws ServiceException, DaoException {
		mapcfgareaService.saveOrUpdate(vo);
		return new XJsonInfo();
	}

	/**
	 * @description: <删除>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
	@RequestMapping("/delete")
	public XJsonInfo delete(String[] selRows) throws ServiceException, DaoException {
		for(String id:selRows){
			mapcfgareaService.delete(new Long(id));
		}
		return new XJsonInfo();
	}

	/**
	 * @description: <查询>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
	@RequestMapping("/query")
	public XJsonInfo query(Long id) throws ServiceException, DaoException {
		MapCfgArea vo = mapcfgareaService.get(id);
		if(vo==null){
			vo = new MapCfgArea();
		}
		return new XJsonInfo().setData(vo);
	}

	/**
	 * @description: <分页查询>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
	@RequestMapping("/findPage")
	public XJsonInfo findPage(MapCfgArea vo) throws ServiceException, DaoException {
		return  new XJsonInfo().setPageData(mapcfgareaService.findPage(vo));
	}


	@ResponseBody
	@RequestMapping("/impArea")
	public XJsonInfo impArea() throws Exception {
		File f = new File("E:\\datap\\map20180315\\茂名镇边界信息（不包括茂名市区）.csv");
		BufferedReader bufread;
		String read;
		String no;
		int i=0;
		Map<String,StringBuffer> zhen = new HashMap<>();
		bufread=new BufferedReader(new InputStreamReader(new FileInputStream(f),"gbk"));
		while ((read = bufread.readLine()) != null) {
			if (read == null) {
				continue;
			}
			String[] ls = read.split(",");
			if(ls==null || ls.length<3){
				continue;
			}
			String name = ls[0]+ls[1];
			String lng = ls[2];
			String lat = ls[3];
			if(!lng.startsWith("1")){
				continue;
			}
			StringBuffer pointstr = zhen.get(name);
			if(pointstr==null){
				pointstr = new StringBuffer(lng+","+lat+"|");
				zhen.put(name,pointstr);
			}else{
				pointstr.append(lng+","+lat+"|");
			}
		}

		//循环插入镇
		for(Map.Entry en :zhen.entrySet()){
			String name  = en.getKey().toString();
			String gpsstr = en.getValue().toString();
			MapCfgArea area = mapcfgareaService.queryByName(name);
			if(area ==null){
				area = new MapCfgArea();
				area.setName(name);
				area.setAreaBorderGps(gpsstr);
				area.setAreaType("polygon");
				area.setLayerId(1L);
				area.setBlng("110.946928");
				area.setBlat("21.663135");
				mapcfgareaService.saveOrUpdate(area);
			}
		}

		return   null;
	}


	@ResponseBody
	@RequestMapping("/impPoint")
	public XJsonInfo impPoint() throws Exception {
		File f = new File("E:\\datap\\map20180315\\站点信息表_4G.csv");
		BufferedReader bufread;
		String read;
		String no;
		int i=0;
		Map<String,StringBuffer> zhen = new HashMap<>();
		bufread=new BufferedReader(new InputStreamReader(new FileInputStream(f),"gbk"));
		while ((read = bufread.readLine()) != null) {
			if (read == null) {
				continue;
			}
			String[] ls = read.split(",");
			if(ls==null || ls.length<6){
				continue;
			}
			String fgs = ls[0];
			String name = ls[1];
			String lng = ls[5];
			String lat = ls[6];
			if(!fgs.equals("茂南") && !fgs.equals("电白")){
				continue;
			}
			if(!lng.startsWith("1")){
				continue;
			}
			String key  = lng+","+lat;
			StringBuffer pointstr = zhen.get(key);
			if(pointstr==null){
				pointstr = new StringBuffer(name);
				zhen.put(key,pointstr);
			}
		}

		//循环插入镇
		for(Map.Entry en :zhen.entrySet()){
			String key  = en.getKey().toString();
			String name = en.getValue().toString();
			String ps[]  = key.split(",");
			MapCfgArea area = mapcfgareaService.queryByName(name);
			if(area ==null){
				area = new MapCfgArea();
				area.setName(name);
				area.setAreaType("point");
				area.setLayerId(10L);
				area.setGlng(ps[0]);
				area.setGlat(ps[1]);
				mapcfgareaService.saveOrUpdate(area);
			}
		}

		return   null;
	}

	/**
	 * 处理部分偏移的点
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/HandleArea")
	public XJsonInfo HandleArea() throws Exception {
		List<MapCfgArea> list = mapcfgareaService.queryList(new MapCfgArea());
		int count = 0;
		for(MapCfgArea area :list){
			if(!"polygon2".equals(area.getAreaType())){
				continue;
			}
			List<MapPoint> listp = new ArrayList<MapPoint>();
			String pointbaidus = area.getAreaBorderBaidu();
			String pointgpss = area.getAreaBorderGps();
			String[] gps = pointgpss.split("\\|");
			String[] bps = pointbaidus.split("\\|");
			if(bps==null || bps.length<3 || gps.length!=bps.length){
				System.out.println("--------------------数据异常，数据异常---------------------------" +area.getName());
				continue;
			}
			boolean isupdate = false;
			MapPoint fbp = null;//第一个百度的点
			MapPoint fgp = null;//第一个GPS的点
			for(int i=0;i<bps.length;i++){
				String[] _bps = bps[i].split(",");
				String[] _gps = gps[i].split(",");
				if(_bps==null || _bps.length!=2){
					continue;
				}
				//百度的点
				MapPoint bp = new MapPoint(_bps[0],_bps[1]);
				//GPS的点
				MapPoint gp = new MapPoint(_gps[0],_gps[1]);
				if(fbp==null){
					fbp = bp;
					fgp = gp;
				}
				//根据GPS的点计算出百度的点
				MapPoint tbp = MapUtil.translate(gp,fgp,fbp);
				//判断计算出来的百度的点和转换的百度的点的差距，如果差距超过1000米，则使用计算的点
				double len = MapUtil.point_len2(bp,tbp);
				if(len<1000){
					listp.add(bp);
				}else{
					listp.add(tbp);
					System.out.println(len);
					isupdate = true;
				}
			}
			if(isupdate){
				System.out.println("update area");
				StringBuffer str = new StringBuffer();
				for(MapPoint p:listp){
					str.append(p.lng+","+p.lat+"|");
				}
				area.setAreaBorderBaidu(str.toString());
				mapcfgareaService.saveOrUpdate(area);
			}
		}

		for(MapCfgArea area :list) {
			if (!"point".equals(area.getAreaType())) {
				continue;
			}
			if(area.getBlng()==null){
				continue;
			}
			//百度的点
			MapPoint bp = new MapPoint(area.getBlng(),area.getBlat());
			//GPS的点
			MapPoint gp = new MapPoint(area.getGlng(),area.getGlat());
			//根据GPS的点计算出百度的点
			MapPoint tbp = MapUtil.translate(gp);
			//判断计算出来的百度的点和转换的百度的点的差距，如果差距超过1000米，则使用计算的点
			double len = MapUtil.point_len2(bp,tbp);
			if(len>500){
				area.setBlng(null);
				area.setBlat(null);
				mapcfgareaService.saveOrUpdate(area);
				count++;
				System.out.println(count+area.getName());
			}
		}

		return null;
	}

	@ResponseBody
	@RequestMapping("/getHeadersInfo")
	public Map getHeadersInfo(HttpServletRequest req){
		Map<String, String> map = new HashMap<String, String>();
		Enumeration headerNames = req.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = req.getHeader(key);
			map.put(key, value);
		}
		map.put("RemoteAddr",req.getRemoteAddr());
		map.put("RemoteHost",req.getRemoteHost());
		map.put("X-Real-IP",req.getHeader("X-Real-IP"));
		map.put("X-Host",req.getHeader("X-Host"));
		map.put("HTTP_X_FORWARDED_FOR",req.getHeader("HTTP_X_FORWARDED_FOR"));

		return map;
	}


}
