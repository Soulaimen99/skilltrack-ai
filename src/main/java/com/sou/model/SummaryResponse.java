package com.sou.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
@Builder
public class SummaryResponse {
	
	private String summary;
	
}
