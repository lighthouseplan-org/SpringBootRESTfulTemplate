package com.lhp.backend.constant;

/**
 * byte Enum
 *
 */
public enum DeleteFlag {

    DELETED("DELETED", (byte) 1),
    NOT_DELETED("NOT_DELETED" ,(byte) 0);


    private final String deleted;
    private final byte b;

    DeleteFlag(String deleted, byte b) {
        this.deleted = deleted;
        this.b = b;
    }

    public String getDeleted() {
        return deleted;
    }

    public byte getB() {
        return b;
    }

}

