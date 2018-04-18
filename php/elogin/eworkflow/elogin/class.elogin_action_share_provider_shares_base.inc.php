<?php

/**
 * ELogin - Egroupware
 * @link http://www.hw-softwareentwicklung.de
 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
 * @package elogin
 * @copyright (c) 2012-18 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
 * @version $Id$
 */

use EGroupware\Eworkflow\Entry\Entry;
use EGroupware\Eworkflow\Interfaces\IParam;

/**
 * elogin_action_share_provider_shares_base
 */
abstract class elogin_action_share_provider_shares_base extends Entry implements IParam {
	use EGroupware\Eworkflow\Traits\TParameterRegisterBase;

	// consts
	const PARAM_SHAREPROVIDER_CACHE_LOGGING = 'shareprovider_cache_logging';
	const PARAM_SP_USERSHARE_ENTRY			= 'sp_usershare_entry';

	/**
	 * provider cache logging
	 * @var string
	 */
	protected $_provider_cache_logging = '';

	/**
	 * usershare entry id
	 * @var string
	 */
	protected $_usershare_entryid = "";

	/**
	 * getProviderCacheLogging
	 * @return string
	 */
	public function getProviderCacheLogging() {
		return $this->_params->getVariableByParam(
			$this->_provider_cache_logging, static::PARAM_SHAREPROVIDER_CACHE_LOGGING);
	}

	/**
	 * setProviderCacheLogging
	 * @param string $enable
	 */
	public function setProviderCacheLogging($enable) {
		$this->_provider_cache_logging = $this->_params->saveVariableToParam(
			$enable, static::PARAM_SHAREPROVIDER_CACHE_LOGGING);
	}

	/**
	 * getUserShareEntryid
	 * @return string
	 */
	public function getUserShareEntryid() {
		return $this->_params->getVariableByParam(
			$this->_usershare_entryid, static::PARAM_SP_USERSHARE_ENTRY);
	}

	/**
	 * setUserShareEntryid
	 * @param string $id
	 */
	public function setUserShareEntryid($id) {
		$this->_usershare_entryid = $this->_params->saveVariableToParam(
			$id, static::PARAM_SP_USERSHARE_ENTRY);
	}

	/**
	 * getParameterRegister
	 * @return ParamRegister
	 */
	public function getParameterRegister() {
		return $this->_getParameterRegister();
	}
}