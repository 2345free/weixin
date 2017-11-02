package com.it.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class NewsMessage extends BaseMessage {
    private int ArticleCount;
    private List<News> Articles;
}
