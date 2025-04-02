package com.sou.model;

import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModelProperty;


@JsonTypeName( "LearningLogInput" )
@javax.annotation.Generated( value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-04-02T21:26:15.796988500+02:00[Europe/Berlin]", comments = "Generator version: 7.4.0" )
public class LearningLogInput {
	private @Valid String content;
	
	/**
	 *
	 **/
	public LearningLogInput content( String content ) {
		this.content = content;
		return this;
	}
	
	
	@ApiModelProperty( required = true, value = "" )
	@JsonProperty( "content" )
	@NotNull
	public String getContent() {
		return content;
	}
	
	@JsonProperty( "content" )
	public void setContent( String content ) {
		this.content = content;
	}
	
	
	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		LearningLogInput learningLogInput = (LearningLogInput) o;
		return Objects.equals( this.content, learningLogInput.content );
	}
	
	@Override
	public int hashCode() {
		return Objects.hash( content );
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( "class LearningLogInput {\n" );
		
		sb.append( "    content: " ).append( toIndentedString( content ) ).append( "\n" );
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

