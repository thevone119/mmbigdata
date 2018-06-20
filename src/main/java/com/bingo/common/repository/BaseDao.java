package com.bingo.common.repository;

import com.bingo.common.exception.DaoException;
import com.bingo.common.model.Page;
import com.bingo.common.model.PageModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Copyright:广州市品高软件股份有限公司
 * @Author:李丽全
 * @Email:15119575223@139.com
 * @Telephone:15119575223
 * @Date:2017年7月13日 09:10:49
 * @Description:BaseDao实现类
 */
@SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
public abstract class BaseDao<T, ID extends Serializable> {
    //注入hibernate的jpa对象，操作数据库
    @PersistenceContext
    protected EntityManager entityManager;

    private Class<T> entityClass;

    /**
     * @return
     * @description <获取泛型的class>
     */
    public Class<T> getEntityClass() {
        if (entityClass == null) {
            entityClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        return entityClass;
    }



    public void save(T entity) throws DaoException {
        try {
            entityManager.persist(entity);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public void save(List<T> list) throws DaoException {
        try {
            list.forEach(q -> entityManager.persist(q));
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public T update(T entity) throws DaoException {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public void update(List<T> list) throws DaoException {
        try {
            list.forEach(q -> entityManager.merge(q));
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public T getById(Serializable id) throws DaoException {
        try {
            if (id != null) {
                return entityManager.find(getEntityClass(), id);
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return null;
    }

    public List<T> getById(Serializable[] id) throws DaoException {
        List<T> result = new ArrayList<>();
        if (id != null) {
            for (Serializable i : id) {
                result.add(getById(i));
            }
        }
        return result;
    }

    public void deleteById(Serializable id) throws DaoException {
        try {
            T entity = getById(id);
            if (entity != null) {
                entityManager.remove(entity);
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public List<T> query(String hql) throws DaoException {
        try {
            Query query = entityManager.createQuery(hql);
            return query.getResultList();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public List<T> queryAll() throws DaoException {
        try {
            return entityManager.createQuery("from " + getEntityClass().getSimpleName()).getResultList();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public T saveOrUpdate(T entity) throws DaoException {
        try {
            entityManager.merge(entity);
            return entity;
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public List<T> saveOrUpdate(List<T> list) throws DaoException {
        try {
            list.forEach(q -> entityManager.merge(q));
            return list;
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public List<T> query(String hql, Object[] params) throws DaoException {
        try {
            Query query = entityManager.createQuery(hql);
            int length = params.length;
            for (int i = 0; i < length; i++) {
                query.setParameter(i + 1, params[i]);
            }
            return query.getResultList();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public List<T> query(String sql, Map<String, Object> map) throws DaoException {
        try {
            Query query = entityManager.createQuery(sql);
            if (map != null) {
                Set<String> keySet = map.keySet();
                for (String strKey : keySet) {
                    Object obj = map.get(strKey);

                    query.setParameter(strKey, obj);
                }
            }

            return query.getResultList();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public long count(String hql, Object[] params) throws DaoException {
        try {
            Query query = entityManager.createQuery(hql);
            if (params != null) {
                int length = params.length;
                for (int i = 0; i < length; i++) {
                    query.setParameter(i + 1, params[i]);
                }
            }
            //uniqueResult如果查询结果有多个值则抛出错误
            return (long) query.getSingleResult();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    /**
     * @about 慎用。truncate会删除表中所有的数据，包括序列，同时回收表空间。当再插入数据时，序列号是1。
     */
    public void truncate(String tableName) throws DaoException {
        try {
            //hql不支持truncate table，必须使用sql。
            entityManager.createNativeQuery("truncate table " + tableName).executeUpdate();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public void deleteAll(Collection<T> entities) throws DaoException {
        try {
            if (entities != null) {
                for (Object entity : entities) {
                    entityManager.remove(entity);
                }
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public void delete(T entity) throws DaoException {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public void delete(List<T> list) throws DaoException {
        try {
            list.forEach(q -> entityManager.remove(q));
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public Page<T> queryForPage(PageModel model, String[] eqFields, Object[] eqParams, String[] likeFields,
                                Object[] likeParams, String[] dateFields, Date[][] dateParams, String groupBy, String orderBy) throws DaoException {
        Page<T> page = new Page<T>();
        String queryHql = getQueryHql(eqFields, eqParams, likeFields, likeParams, dateFields, dateParams);
        Object[] params = getParams(eqParams);
        long totalCount = count(getCountHql(queryHql), params);
        if (totalCount > 0) {
            if (groupBy != null && groupBy.length() > 0) {
                queryHql += " group by " + groupBy;
            }
            if (orderBy != null && orderBy.length() > 0) {
                queryHql += " order by " + orderBy;
            }
            //System.out.println(queryHql);
            page.setTotalCount(totalCount);
            List<T> data = queryPageListByHql(queryHql, params, model.getStart(), model.getLimit());
            page.setResult(data);
        }
        return page;
    }

    /**
     * @param eqFields
     * @param eqParams
     * @param likeFields
     * @param likeParams
     * @param dateFields
     * @param dateParams
     * @return
     * @about 分页逻辑很重要，防止查询条件是空时查不到数据。
     */
    public String getQueryHql(String[] eqFields, Object[] eqParams,
                              String[] likeFields, Object[] likeParams,
                              String[] dateFields, Date[][] dateParams) {
        StringBuffer queryHql = new StringBuffer();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        queryHql.append(" from ").append(getEntityClass().getName());

        queryHql.append(" where 1 = 1 ");

        if (eqFields != null) {
            for (int i = 0; i < eqFields.length; i++) {
                if (eqParams[i] != null && StringUtils.isNotBlank(String.valueOf(eqParams[i]))) {
                    queryHql.append("and ").append(String.valueOf(eqFields[i])).append(" = ? ");
                }
            }
        }

        if (dateFields != null) {
            for (int i = 0; i < dateFields.length; i++) {
                if (dateParams[i] != null && dateParams[i].length == 2) {
                    //开始时间
                    if (dateParams[i][0] != null) {
                        queryHql.append("and ").append(dateFields[i]).append(">=to_date('");
                        queryHql.append(format.format(dateParams[i][0])).append("','yyyy-mm-dd') ");
                    }

                    //结束时间
                    if (dateParams[i][1] != null) {
                        queryHql.append("and ").append(dateFields[i]).append("<to_date('");
                        queryHql.append(format.format(dateParams[i][1])).append("','yyyy-mm-dd')+1 ");
                    }
                }
            }
        }

        if (likeFields != null) {
            for (int i = 0; i < likeFields.length; i++) {
                if (likeParams[i] != null && StringUtils.isNotBlank(String.valueOf(likeParams[i]))) {
                    queryHql.append("and ").append(String.valueOf(likeFields[i]));
                    queryHql.append(" like '%").append(likeParams[i]).append("%' ");
                }
            }
        }

        return queryHql.toString();
    }

    public String getCountHql(String hql) {
        StringBuffer countHql = new StringBuffer();
        countHql.append("select count(*) ");
        String lower = hql.toLowerCase();
        int index = 0;
        index = lower.indexOf("from");
        if (index != -1) {
            countHql.append(hql.substring(index));
        }
        return countHql.toString();
    }

    /**
     * @param eqParams
     * @return
     * @description <过滤NULL空值>
     * @about 防止查询条件是空时查不到数据。
     */
    public Object[] getParams(Object[] eqParams) {
        List<Object> list = new ArrayList<Object>();
        if (eqParams != null) {
            for (int i = 0; i < eqParams.length; i++) {
                if (eqParams[i] != null && StringUtils.isNotBlank(String.valueOf(eqParams[i]))) {
                    list.add(eqParams[i]);
                }
            }
        }
        return list.toArray();
    }

    public List<T> queryPageListByHql(String hql, Object[] params, Pageable pageParams) throws DaoException {
        try {
            Query query = entityManager.createQuery(hql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    query.setParameter(i + 1, params[i]);
                }
            }
            if (pageParams != null) {
                query.setFirstResult(pageParams.getPageNumber() * pageParams.getPageSize());
                query.setMaxResults(pageParams.getPageSize());
            }

            return query.getResultList();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public List<T> queryPageListByHql(String hql, Object[] params, int start, int pageSize) throws DaoException {
        try {
            Query query = entityManager.createQuery(hql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    query.setParameter(i + 1, params[i]);
                }
            }

            query.setFirstResult(start);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public void deleteById(Serializable[] id) throws DaoException {
        if (id != null) {
            for (Serializable i : id) {
                //不知道i是空，会不会报错，怎么判断i是否为空？
                deleteById(i);
            }
        }
    }

    public T find(String hql, Object[] params) throws DaoException {
        try {
            Query query = entityManager.createQuery(hql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    query.setParameter(i + 1, params[i]);
                }
                query.setMaxResults(2);//只查询2条
            }
            //getSingleResult查不到结果时，它是抛异常的，不会返回null
            List<T> list = query.getResultList();
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return null;
    }

    public T find(String sql, Map<String, Object> map) throws DaoException {
        try {
            List<T> list = query(sql, map);
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return null;
    }


    public List<Object[]> queryBySql(String sql, Map<String, Object> map) throws DaoException {
        try {
            Query query = entityManager.createNativeQuery(sql);
            if (map != null) {
                Set<String> keySet = map.keySet();
                for (String strKey : keySet) {
                    Object obj = map.get(strKey);

                    query.setParameter(strKey, obj);
                }
            }

            return query.getResultList();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }
    public List<Object[]> queryBySql(String sql, Object[] params) throws DaoException {
        try {
            Query query = entityManager.createNativeQuery(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    query.setParameter(i + 1, params[i]);
                }
            }

            return query.getResultList();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public void executeBySql(String sql, Object[] params) throws DaoException {
        try {
            Query query = entityManager.createNativeQuery(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    query.setParameter(i + 1, params[i]);
                }
            }
            query.executeUpdate();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public void executeByHql(String hql, Object[] params) throws DaoException {
        Query query = entityManager.createQuery(hql);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
        query.executeUpdate();
    }

    public void executeBySql(String sql, Map<String, Object> map) throws DaoException {
        try {
            Query query = entityManager.createQuery(sql);
            if (map != null) {
                Set<String> keySet = map.keySet();
                for (String strKey : keySet) {
                    Object obj = map.get(strKey);

                    query.setParameter(strKey, obj);
                }
            }
            query.executeUpdate();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    /**
     * @param
     * @description: <查询总数>
     * @return:
     * @throws:
     */
    public long findCount(String sql, Object[] fldValues) throws DaoException {
        return findCount(sql, fldValues, false);
    }

    protected long findCount(String sql, Object[] fldValues, boolean isMultilist) {
        int index = -1;
        if (sql != null && sql.length() > 0) {
            index = sql.toLowerCase().indexOf("from ");
        }
        if (index != -1) {
            sql = "select count(1) " + sql.substring(index);
        } else {
            sql = "select count(1) " + entityClass.getName();
        }
        List<Object> list = new ArrayList<Object>();
        if (fldValues != null && fldValues.length > 0) {
            for (Object object : fldValues) {
                if (object != null && object.toString().length() > 0) {
                    list.add(object);
                }
            }
        }
        Query query;
        if (list.size() > 0) {
            query = entityManager.createQuery(sql);
            for (int i = 0; i < list.size(); i++) {
                query.setParameter((i + 1), list.get(i));
            }
        } else {
            query = entityManager.createQuery(sql);
        }
        if (isMultilist) {
            return Long.valueOf(query.getResultList().size());
        }
        return (Long) query.getSingleResult();
    }

    /**
     * 查询分页数据列表
     *
     * @param sql
     * @param firstResult
     * @param maxResults
     * @param paras
     * @return
     */
    protected List<T> findList(String sql, int firstResult, int maxResults, final Object[] paras) {
        Query query = entityManager.createQuery(sql);
        if (paras != null && paras.length > 0) {
            int index = 1;
            for (Object para : paras) {
                if (para != null && para.toString().length() > 0) {
                    query.setParameter(index, para);
                    index++;
                }
            }
        }
        //分页查询
        if (maxResults > 0) {
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResults);
        }
        return query.getResultList();
    }


    /**
     * 分页查询
     *
     * @param hql       完整的查询HQL 如: select a from XXXX a where a.name=? and a.user=? order by name
     * @param vo
     * @param fldValues 字段值 如:new Object[]{new Integer(1000),"myName"}
     * @return
     */
    public Page<T> findPage(String hql, PageModel vo, List<Object> fldValues) {
        Object[] v = null;
        if (fldValues != null && fldValues.size() > 0) {
            v = fldValues.toArray();
        }
        Page<T> page = new Page<T>();
        long totalCount = 0L;
        if(vo.getTotalCount() <= 0){//传总数就不查总数
            totalCount = findCount(hql, v, false);
        } else{
            totalCount = vo.getTotalCount();
        }
        if (totalCount > 0) {
            page.setTotalCount(totalCount);
            List data = findList(hql, vo.getStart(), vo.getLimit(), v);
            page.setResult(data);
        }
        return page;
    }

    /**
     * 分页查询
     *
     * @param hql       完整的查询HQL 如: select a from XXXX a where a.name=? and a.user=? order by name
     * @param vo
     * @param fldValues 字段值 如:new Object[]{new Integer(1000),"myName"}
     * @return
     */
    public Page<T> findPage(String hql, PageModel vo, Object[] fldValues) {
        Page<T> page = new Page<T>();
        long totalCount = 0L;
        if(vo.getTotalCount() <= 0){//传总数就不查总数
            totalCount = findCount(hql, fldValues, false);
        } else{
            totalCount = vo.getTotalCount();
        }
        if (totalCount > 0) {
            page.setTotalCount(totalCount);
            List data = findList(hql, vo.getStart(), vo.getLimit(), fldValues);
            page.setResult(data);
        }
        return page;
    }
}