package com.it.menu;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ClickButton extends Button{
	//Click���Ͳ˵�key
	private String key;

}
