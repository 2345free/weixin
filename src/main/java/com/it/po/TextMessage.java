package com.it.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TextMessage extends BaseMessage {
    private String Content;
    private String MsgId;

}
