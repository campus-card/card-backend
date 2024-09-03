package org.wlow.card.student.exception;

/**
 * 购买商品时发生异常
 */
public class PurchaseException extends RuntimeException {
    public PurchaseException(String message) {
        super(message);
    }
}
