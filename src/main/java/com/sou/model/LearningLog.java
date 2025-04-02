package com.sou.model;

import java.util.Objects;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.LocalDate;


@JsonTypeName( "LearningLog" )
@javax.annotation.Generated( value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-04-02T21:26:15.796988500+02:00[Europe/Berlin]", comments = "Generator version: 7.4.0" )
public class LearningLog {
	private @Valid Long id;
	private @Valid String content;
	private @Valid String tags;
	private @Valid LocalDate date;
	
	/**
	 *
	 **/
	public LearningLog id( Long id ) {
		this.id = id;
		return this;
	}
	
	
	@ApiModelProperty( value = "" )
	@JsonProperty( "id" )
	public Long getId() {
		return id;
	}
	
	@JsonProperty( "id" )
	public void setId( Long id ) {
		this.id = id;
	}
	
	/**
	 *
	 **/
	public LearningLog content( String content ) {
		this.content = content;
		return this;
	}
	
	
	@ApiModelProperty( value = "" )
	@JsonProperty( "content" )
	public String getContent() {
		return content;
	}
	
	@JsonProperty( "content" )
	public void setContent( String content ) {
		this.content = content;
	}
	
	/**
	 *
	 **/
	public LearningLog tags( String tags ) {
		this.tags = tags;
		return this;
	}
	
	
	@ApiModelProperty( value = "" )
	@JsonProperty( "tags" )
	public String getTags() {
		return tags;
	}
	
	@JsonProperty( "tags" )
	public void setTags( String tags ) {
		this.tags = tags;
	}
	
	/**
	 *
	 **/
	public LearningLog date( LocalDate date ) {
		this.date = date;
		return this;
	}
	
	
	@ApiModelProperty( value = "" )
	@JsonProperty( "date" )
	public LocalDate getDate() {
		return date;
	}
	
	@JsonProperty( "date" )
	public void setDate( LocalDate date ) {
		this.date = date;
	}
	
	
	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		LearningLog learningLog = (LearningLog) o;
		return Objects.equals( this.id, learningLog.id ) &&
					   Objects.equals( this.content, learningLog.content ) &&
					   Objects.equals( this.tags, learningLog.tags ) &&
					   Objects.equals( this.date, learningLog.date );
	}
	
	@Override
	public int hashCode() {
		return Objects.hash( id, content, tags, date );
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( "class LearningLog {\n" );
		
		sb.append( "    id: " ).append( toIndentedString( id ) ).append( "\n" );
		sb.append( "    content: " ).append( toIndentedString( content ) ).append( "\n" );
		sb.append( "    tags: " ).append( toIndentedString( tags ) ).append( "\n" );
		sb.append( "    date: " ).append( toIndentedString( date ) ).append( "\n" );
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

