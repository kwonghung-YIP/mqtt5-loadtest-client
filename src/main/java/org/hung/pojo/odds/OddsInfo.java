package org.hung.pojo.odds;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OddsInfo {

	private long count;
	private LocalDateTime sentTime;
	
	private FullOdds fullodds;
	private TopNOdds topN;
	private BankerTopNOdds bKTopN;
	
}
