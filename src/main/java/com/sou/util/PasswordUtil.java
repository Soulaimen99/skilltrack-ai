package com.sou.util;

public class PasswordUtil {
	public static void main( String[] args ) {
		System.out.println( io.quarkus.elytron.security.common.BcryptUtil.bcryptHash( "fireball" ) );
	}
}
