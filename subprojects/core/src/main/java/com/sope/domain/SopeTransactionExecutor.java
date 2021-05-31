package com.sope.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SopeTransactionExecutor {
	public interface TransactionConsumer {
		void consumeTransaction();
	}

	public interface TransactionSupplier<R> {
		R get();
	}

	@Transactional(readOnly = true)
	public void read(TransactionConsumer transactionConsumer) {
		transactionConsumer.consumeTransaction();
	}

	@Transactional(readOnly = true)
	public <R> R read(TransactionSupplier<R> transactionalSupplier) {
		return transactionalSupplier.get();
	}

	@Transactional
	public void write(TransactionConsumer transactionConsumer) {
		transactionConsumer.consumeTransaction();
	}

	@Transactional
	public <R> R write(TransactionSupplier<R> transactionalSupplier) {
		return transactionalSupplier.get();
	}
}