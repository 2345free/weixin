package com.it.menu;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ViewButton extends Button {
    //view���Ͳ˵�url
    private String url;

}
