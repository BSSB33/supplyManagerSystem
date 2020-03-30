package com.elte.supplymanagersystem.enums;

/**
 * Every history record can have a HistoryType
 * These are realistic events in the life of an Order
 */
public enum HistoryType {
    PHONE_CALL, EMAIL_SENT, MADE_AN_OFFER, DISCUSSION, STARTED_SHIPPING, PAID, ORDER, OFFER, NOTE, SHIPPED
}