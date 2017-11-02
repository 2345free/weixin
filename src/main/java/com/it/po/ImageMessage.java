package com.it.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ImageMessage extends BaseMessage {
    private Image Image;

}
