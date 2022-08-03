package com.lhp.backend.constant;

import lombok.Data;

@Data
public class StatusCode {
    public static final String userUnauthed ="4010";
    public static final String userDisabled ="4011";
    public static final String userLocked ="4012";
}
