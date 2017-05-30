package com.it.po;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NewsMessage extends BaseMessage{
	private int ArticleCount;
	private List<News> Articles;
}
