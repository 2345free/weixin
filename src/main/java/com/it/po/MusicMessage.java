package com.it.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MusicMessage extends BaseMessage {
    private Music Music;

}
