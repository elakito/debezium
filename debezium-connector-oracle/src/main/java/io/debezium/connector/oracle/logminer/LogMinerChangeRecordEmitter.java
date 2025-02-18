/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.oracle.logminer;

import io.debezium.DebeziumException;
import io.debezium.connector.oracle.BaseChangeRecordEmitter;
import io.debezium.connector.oracle.logminer.events.EventType;
import io.debezium.data.Envelope.Operation;
import io.debezium.pipeline.spi.OffsetContext;
import io.debezium.pipeline.spi.Partition;
import io.debezium.relational.Table;
import io.debezium.util.Clock;

/**
 * Emits change records based on an event read from Oracle LogMiner.
 */
public class LogMinerChangeRecordEmitter extends BaseChangeRecordEmitter<Object> {

    private final EventType eventType;
    private final Object[] oldValues;
    private final Object[] newValues;

    public LogMinerChangeRecordEmitter(Partition partition, OffsetContext offset, EventType eventType, Object[] oldValues,
                                       Object[] newValues, Table table, Clock clock) {
        super(partition, offset, table, clock);
        this.eventType = eventType;
        this.oldValues = oldValues;
        this.newValues = newValues;
    }

    @Override
    protected Operation getOperation() {
        switch (eventType) {
            case INSERT:
                return Operation.CREATE;
            case UPDATE:
            case SELECT_LOB_LOCATOR:
                return Operation.UPDATE;
            case DELETE:
                return Operation.DELETE;
            default:
                throw new DebeziumException("Unsupported operation type: " + eventType);
        }
    }

    @Override
    protected Object[] getOldColumnValues() {
        return oldValues;
    }

    @Override
    protected Object[] getNewColumnValues() {
        return newValues;
    }
}
