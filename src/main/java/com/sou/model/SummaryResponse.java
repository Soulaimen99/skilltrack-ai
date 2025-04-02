package com.sou.model;

import java.util.Objects;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModelProperty;


@JsonTypeName( "SummaryResponse" )
@javax.annotation.Generated( value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-04-02T21:26:15.796988500+02:00[Europe/Berlin]", comments = "Generator version: 7.4.0" )
public class SummaryResponse {
	private @Valid String summary;
	
	/**
	 *
	 **/
	public SummaryResponse summary( String summary ) {
		this.summary = summary;
		return this;
	}
	
	
	@ApiModelProperty( value = "" )
	@JsonProperty( "summary" )
	public String getSummary() {
		return summary;
	}
	
	@JsonProperty( "summary" )
	public void setSummary( String summary ) {
		this.summary = summary;
	}
	
	
	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		SummaryResponse summaryResponse = (SummaryResponse) o;
		return Objects.equals( this.summary, summaryResponse.summary );
	}
	
	@Override
	public int hashCode() {
		return Objects.hash( summary );
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( "class SummaryResponse {\n" );
		
		sb.append( "    summary: " ).append( toIndentedString( summary ) ).append( "\n" );
		sb.append( "}" );
		return sb.toString();
	}
	
	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString( Object o ) {
		if ( o == null ) {
			return "null";
		}
		return o.toString().replace( "\n", "\n    " );
	}
	
	
}

