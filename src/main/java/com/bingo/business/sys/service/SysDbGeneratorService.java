package com.bingo.business.sys.service;

import com.bingo.business.sys.model.SysDbGenerator;
import com.bingo.business.sys.repository.SysDbGeneratorRepository;
import com.bingo.common.exception.DaoException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangtw
 * 2017-08-23 16:34:30
 * 对象功能: 系统系列号生成 service管理
 */
@Service
@Transactional
public class SysDbGeneratorService {
	
	@Resource
	private SysDbGeneratorRepository sysdbgeneratorRepository;

	private static Map<String,SysDbGenerator> map = new HashMap<String,SysDbGenerator>();

	/**
	 * 获取序列号根据表名获取表的下一个序列号
	 * @param tableName
	 * @return
	 * @throws DaoException
	 */
	@Transactional
	public synchronized long nextValue(String tableName) throws DaoException {
		long step = 100;//步长
		//String  updatesql = "update from T_SYS_DB_GENERATOR set SEQUENCE_NEXT_HI_VALUE=SEQUENCE_NEXT_HI_VALUE+"+step +"where tableName='"+tableName+"'";
		SysDbGenerator dbg = map.get(tableName);
		if(dbg==null || dbg.currValue>=dbg.sequenceNextHiValue){
			dbg = sysdbgeneratorRepository.getById(tableName);
			dbg.currValue = dbg.sequenceNextHiValue;
			dbg.setSequenceNextHiValue(dbg.getSequenceNextHiValue()+step);
			sysdbgeneratorRepository.saveOrUpdate(dbg);
			map.put(tableName, dbg);
		}

		dbg.currValue++;
		return dbg.currValue;
	}
	

	
}
