package com.shanjin.dao;


import com.shanjin.incr.model.IncRule;
import com.shanjin.incr.util.IMybExtMapper;
import com.shanjin.model.RuleItem;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-12 17:22:45 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public interface IncRuleMapper extends IMybExtMapper<IncRule, Integer> {

    List<RuleItem> findRulesByItemIds(@Param("itemIds") Collection<Long> itemIds);
}