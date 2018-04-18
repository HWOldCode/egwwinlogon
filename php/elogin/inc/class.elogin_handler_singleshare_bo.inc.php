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
		$providers = elogin_shareprovider_bo::getShareProviders();

		foreach( $providers as $provider ) {
			if( $provider instanceof  elogin_shareprovider_bo ) {
				$collectiveShare = $provider->getCollectiveShare();

				if( $collectiveShare !== '' ) {
					try {
						$cprovider = elogin_shareprovider_bo::cast($provider);

						self::_createDirs($cprovider);
					}
					catch( Exception $ex ) {
						//TODO
						self::_errorLog($ex->getMessage(), $ex->getLine());
					}
				}
			}
		}
	}

	/**
	 * _createDirs
	 * @param elogin_shareprovider_bo $cprovider
	 * @return type
	 * @throws Exception
	 */
	static protected function _createDirs($cprovider) {
		if( !($cprovider instanceof elogin_shareprovider_bo) ) {
			return;
		}

		if( !($cprovider->isLogin()) ) {
			return;
		}

		$collectiveShare = trim($cprovider->getCollectiveShare());

		if( $collectiveShare == '' ) {
			return;
		}

		$collectiveShare	= '/' . $collectiveShare . '/';
		$groups				= elogin_bo::getEgroupwareGroups();

		// read exist user -------------------------------------------------
		$allEgwUser = elogin_bo::getEgroupwareAccounts();
		$allUserOnProvider = array();

		foreach( $allEgwUser as $key => $value ) {
			if( $cprovider->isUsernameExist($value['account_lid']) ) {
				$allUserOnProvider[] = $value['account_lid'];
			}
		}

		// check, create dirs ----------------------------------------------

		foreach( $groups as $group ) {
			$share_name = 'group ' . $group['account_lid'];

			// create?
			$sdExit = false;

			try {
				$sdExit = $cprovider->existShareDir($collectiveShare, $share_name);
			}
			catch( Exception $ex ) {
				if( $ex->getCode() == 408 ) {
					$sdExit = false;
				}
				else {
					throw $ex;
				}
			}

			if( !$sdExit ) {
				$sdExit = $cprovider->createShareDir($collectiveShare, $share_name);
			}

			if( !$sdExit ) {
				continue;
			}

			// permissions -------------------------------------------------
			$tusers = elogin_bo::getEgroupwareGroupAccounts($group['account_id']);
			$users = array();

			// only user add, exist on provider
			foreach( $tusers as $auser ) {
				if( in_array($auser, $allUserOnProvider) ) {
					$users[] = $auser;
				}
			}

			if( count($users) == 0 ) {
				$areturn = $cprovider->removeAllPermissionDir($collectiveShare, $share_name);

				self::_errorLog(
					'removeAllPermissionDir ' . ($areturn ? 'true' : 'false') . ':' .
					$collectiveShare . '/' . $share_name,
					__LINE__
					);
			}
			else {
				$areturn = $cprovider->addPermissionDirMulti(
					$collectiveShare,
					$share_name,
					$users,
					true,
					true
					);

				self::_errorLog(
					'addPermissionDirMulti ' . ($areturn ? 'true' : 'false') . ':' .
					$collectiveShare . '/' . $share_name,
					__LINE__
					);
			}
		}
	}
}