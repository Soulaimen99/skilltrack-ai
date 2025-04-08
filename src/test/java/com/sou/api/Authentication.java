package com.sou.api;

public class Authentication {
	public static final String USERNAME = System.getenv( "TEST_USER" ) != null
			? System.getenv( "TEST_USER" )
			: "sou";

	public static final String PASSWORD = System.getenv( "TEST_PASSWORD" ) != null
			? System.getenv( "TEST_PASSWORD" )
			: "fireball";
}