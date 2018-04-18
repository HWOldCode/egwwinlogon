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
use EGroupware\Eworkflow\Interfaces\IEntry;
use EGroupware\Eworkflow\Interfaces\IParam;
use EGroupware\Eworkflow\Interfaces\ILinkStyle;
use EGroupware\Eworkflow\Entry\Entrys;
use EGroupware\Eworkflow\Entry\EntryPath;
use EGroupware\Ecoman\Ecoman\Workflow\Entry\EntryGroup as EcomanEntryGroup;
use EGroupware\Api\Framework;

/**
 * elogin_action_share_provider_shares
 */
class elogin_action_share_provider_shares extends Entry implements IEntry, IParam, ILinkStyle {
	use EGroupware\Eworkflow\Traits\TParameterRegisterBase;

	// link action
	const LINK_ACTION   = 'action';
	const LINK_ERROR    = 'error';

	// Param
	const PARAM_SHARES_PROVIDER_ENTRY   = 'shares_provider_entry';
	const PARAM_SHARES_SHARENAME        = 'shares_sharename';

	/**
	 * logger
	 * @var Logger
	 */
	static protected $_logger = null;

	/**
	 * type
	 * @var string
	 */
	protected $_type = Entry::TYPE_ACTION;

	/**
	 * share provider entryid
	 * @var string
	 */
	protected $_shareprovider_entryid = "";

	/**
	 * sharename
	 * @var string
	 */
	protected $_sharename = "";

	/**
	 * getEntryDefaultIcon
	 * @return string
	 */
	public function getEntryDefaultIcon() {
		return "share.png";
	}

	/**
	 * getEtemplate
	 * @return null|etemplate|string
	 */
	public function getEtemplate() {
		return 'entry.action_egw_elogin_shareprovider_shares';
	}

	/**
	 * acceptLinks
	 * accept links
	 * @return array
	 */
	public function acceptLinks() {
		return array(
			static::LINK_ACTION,
			static::LINK_ERROR
			);
	}

	/**
	 * getLineStyle
	 * @return array
	 */
	public function getLineStyle() {
		return array(
			static::LINK_ERROR => array(
				self::STYLE_LINE => self::LINESTYLE_DASH
				)
		);
	}

	/**
	 * getInfo
	 * @return array
	 */
	static public function getInfo() {
		return array(
			'title' => lang('Action EGW ELogin Share Provider Shares'),
			'type' => self::TYPE_ACTION,
			'class' => static::_getClassName(self),
			'category' => array(
				'ELogin',
				),
			);
	}

	/**
	 * getShareProviderEntryId
	 * @return string
	 */
	public function getShareProviderEntryId() {
		return $this->_params->getVariableByParam(
			$this->_shareprovider_entryid, static::PARAM_SHARES_PROVIDER_ENTRY);
	}

	/**
	 * setShareProviderEntryId
	 * @param string $id
	 */
	public function setShareProviderEntryId($id) {
		$this->_shareprovider_entryid = $this->_params->saveVariableToParam(
			$id, static::PARAM_SHARES_PROVIDER_ENTRY);
	}

	/**
	 * getShareName
	 * @return string
	 */
	public function getShareName() {
		return $this->_params->getVariableByParam(
			$this->_sharename, static::PARAM_SHARES_SHARENAME);
	}

	/**
	 * setShareName
	 * @param string $name
	 */
	public function setShareName($name) {
		$this->_sharename = $this->_params->saveVariableToParam(
			$name, static::PARAM_SHARES_SHARENAME);
	}

	/**
	 * getProvider
	 * @return elogin_shareprovider_bo
	 */
	public function getProvider() {
		$entry = Entrys::loadEntry($this->getShareProviderEntryId());

		if( $entry instanceof elogin_action_share_provider_account ) {
			$provider = $entry->getProvider();

			if( $provider instanceof elogin_shareprovider_bo ) {
				return $provider;
			}
		}

		return null;
	}

	/**
	 * uiEdit
	 * @param array $content
	 */
	public function uiEdit(&$content, &$option_sel, &$readonlys) {
		if( isset($content['button']) && isset($content['button']['save']) ) {
			$this->setShareProviderEntryId($content['provider_entrys']);
			$this->setShareName($content['provider_shares']);
		}

		$content['provider_entrys'] = $this->getShareProviderEntryId();
		$content['provider_shares'] = $this->getShareName();

		$option_sel['provider_entrys'] = array();
		$option_sel['provider_shares'] = array();

		$group = new EcomanEntryGroup($this->getGroupEntryId());
		$path = new EntryPath($group);


		// -----------------------------------------------------------------
		$fentry = $path->findEntryO($this, 'elogin_action_share_provider_account');

		while( $fentry instanceof Entry ) {
			$entry = Entrys::loadEntry($fentry->getId());

			if( $entry instanceof elogin_action_share_provider_account ) {
				$description = $entry->getDescription();

				$option_sel['provider_entrys'][$entry->getId()] =
					($description == '' ? 'Title Empty' : $description);

				$fentry = $path->findEntryO($entry, 'elogin_action_share_provider_account');
			}
			else {
				break;
			}
		}

		// -----------------------------------------------------------------

		if( $content['provider_entrys'] != '' ) {
			$entry = Entrys::loadEntry($content['provider_entrys']);

			if( $entry instanceof elogin_action_share_provider_account ) {
				$provider = $entry->getProvider();

				if( $provider instanceof elogin_shareprovider_bo ) {
					if( $provider->isLogin() ) {
						$shares = $provider->getShares();

						foreach( $shares as $share ) {
							$option_sel['provider_shares'][$share['name']] = $share['name'];
						}
					}
					else {
						Framework::message(lang('Login faild by: ' . $provider->getProviderName()), 'warning');
					}
				}
				else {
					Framework::message(lang('Provider not found'), 'warning');
				}
			}
			else {
				$this::$_logger->severe('ShareProvider Account not found! "' . $content['provider_entrys'] . '"');
			}
		}

		// -----------------------------------------------------------------

		parent::uiEdit($content, $option_sel, $readonlys);
	}

	/**
	 * execute
	 * @param type $params
	 * @return type
	 */
	public function execute($params) {
		if( !$this->_setStart($params) ) { return; }

		// get link for next action
		$this->_execNextEntryByLinkName(self::LINK_ACTION, $params);
	}

	/**
	 * getParameterRegister
	 * @return ParamRegister
	 */
	public function getParameterRegister() {
		return $this->_getParameterRegister();
	}
}