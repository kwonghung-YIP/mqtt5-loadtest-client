package org.hung.pojo.odds;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class FullOdds {

	private Date updAt;
	private String colSt;
	//private List<CombinationOdds> cmb = new ArrayList<CombinationOdds>();
	private CombinationOdds[] cmb;

	@Data
	public static class CombinationOdds {
		
		private String cmbStr;
		private String cmbSt;
		private int scrOrd;
		private boolean hf;
		private double wP;
		private String odds;
		private int oDrp;

	}	
}
