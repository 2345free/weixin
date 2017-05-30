package com.it.trans;

import lombok.Data;

@Data
public class TransResult {
		private String from;
		private String to;
		private com.it.trans.Data data;
		private String errno;
}
