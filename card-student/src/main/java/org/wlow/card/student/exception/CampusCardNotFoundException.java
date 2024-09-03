package org.wlow.card.student.exception;

/**
 * 校园卡不存在异常
 */
public class CampusCardNotFoundException extends RuntimeException {
    public CampusCardNotFoundException(String message) {
        super(message);
    }
}
