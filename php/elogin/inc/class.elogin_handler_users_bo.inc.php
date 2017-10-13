<?php

/**
 * ELogin - Egroupware
 * @link http://www.hw-softwareentwicklung.de
 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
 * @package elogin
 * @copyright (c) 2012-17 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
 * @version $Id$
 */

/**
 * elogin_handler_singleshare_bo
 */
class elogin_handler_singleshare_bo extends elogin_handler_bo {

	/**
	 * handle
	 * @throws Exception
	 */
	static public function handle() {
		/*if( !self::_openNewProcessFile(__CLASS__) ) {
			return;
		}*/

		try {
			self::_handle();
		}
		catch( Exception $ex ) {
			self::_errorLog($ex->getMessage(), $ex->getLine());
			self::_closeProcessFile(__CLASS__);
		}
		finally {
			self::_closeProcessFile(__CLASS__);
		}
	}

	/**
	 * _handle
	 * @throws Exception
	 */
	static protected function _handle() {
		// check provider and his users
		// 1. get all provider

		$providers = elogin_shareprovider_bo::getShareProviders();

		foreach( $providers as $provider ) {
			if( $provider instanceof  elogin_shareprovider_bo ) {
				if( $provider->isActiv() ) {
					
				}
			}
		}
	}
}