package com.sou.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordUtil {

	private static final Logger logger = LoggerFactory.getLogger( PasswordUtil.class );

	public static void main( String[] args ) {
		logger.info( io.quarkus.elytron.security.common.BcryptUtil.bcryptHash( System.getenv( "TEST_PASSWORD" ) ) );
	}
}