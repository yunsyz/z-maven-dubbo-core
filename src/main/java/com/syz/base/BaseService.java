package com.syz.base;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 
 * @ClassName: BaseService
 * @date: 2016年9月2日 下午2:52:49
 */
@Component
public class BaseService {

	@Resource(type = org.springframework.transaction.jta.JtaTransactionManager.class)
	private JtaTransactionManager txManager;

	/**
	 * @Title: bigenTransaction
	 * @Description: 开启事务
	 * @param definition
	 * @return
	 * @return: TransactionStatus
	 */
	protected TransactionStatus txBegin(Integer definition) {
		txManager.setRollbackOnCommitFailure(true);
		Integer tdf = null;
		if (definition == null) {
			tdf = TransactionDefinition.PROPAGATION_REQUIRED;
		} else {
			tdf = definition;
		}
		TransactionStatus txstatus = txManager.getTransaction(new DefaultTransactionDefinition(tdf.intValue()));
		return txstatus;
	}

	/**
	 * @Title: commitTransaction
	 * @Description: 事务提交
	 * @param status
	 * @return: void
	 */
	protected void txCommit(TransactionStatus txstatus) {
		txManager.commit(txstatus);
	}

	/**
	 * @Title: rollbackTransaction
	 * @Description: 事务回滚
	 * @param status
	 * @return: void
	 */
	protected void txRollback(TransactionStatus txstatus) {
		txManager.rollback(txstatus);
	}




}
