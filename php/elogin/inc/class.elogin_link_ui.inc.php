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

use EGroupware\Api;
use EGroupware\Api\Etemplate;

/**
 * elogin_link_ui
 */
class elogin_link_ui {

	/**
	 * public methode
	 * @var array
	 */
	public $public_functions = array(
		'open'		=> true,
		'link_list'	=> true,
		'edit'		=> true
		);

	/**
	 * link_list
	 * @param array $content
	 */
	public function link_list($content=null) {
		if( !$GLOBALS['egw_info']['user']['apps']['admin'] ) {
			die("Only for Admins!");
		}

		$readonlys = array();

		if( !is_array($content) ) {
			if( !($content['nm'] = Api\Cache::getSession('elogin_link_list', 'elogin')) ) {
				$content['nm'] = array(		// I = value set by the app, 0 = value on return / output
					'get_rows'      =>	'elogin.elogin_link_ui.get_rows_link',	// I  method/callback to request the data for the rows eg. 'notes.bo.get_rows'
					'no_filter'     => true,// I  disable the 1. filter
					'no_filter2'    => true,// I  disable the 2. filter (params are the same as for filter)
					'no_cat'        => false,// I  disable the cat-selectbox
					//'never_hide'    => true,// I  never hide the nextmatch-line if less then maxmatch entrie
					'row_id'        => 'el_unid',
					'actions'       => self::index_get_actions(),
					'favorites'     => false
					);
			}
		}

		$tpl = new Etemplate('elogin.link_list');
		$tpl->exec(
			'elogin.elogin_link_ui.link_list',
			$content,
			array(),
			$readonlys,
			array(),
			0);
	}

	/**
	 * index_get_actions
	 * @param array $query
	 * @return array
	 */
	static public function index_get_actions($query=array()) {
		$group = 1;

		$actions = array(
			'edit' => array(
				'caption'	=> 'Edit',
				'group'		=> $group,
				'default'	=> false,
				'icon'		=> 'edit',
				'hint'		=> 'Edit Link',
				'enabled'	=> true,
				'url'       => 'menuaction=elogin.elogin_link_ui.edit&uid=$id',
				'popup'     => '600x425',//egw_link::get_registry('elogin', 'add_popup'),
				),
			'open' => array(
				'caption'	=> 'Open',
				'group'		=> $group,
				'default'	=> true,
				'icon'		=> 'view',
				'hint'		=> 'Open Link',
				'enabled'	=> true,
				'url'       => 'menuaction=elogin.elogin_link_ui.open&uid=$id',
				'popup'     => '1x1',
				),
			);

		return $actions;
	}

	/**
	 * get_rows_link
	 * @param array $query
	 * @param array $rows
	 * @param array $readonlys
	 * @return int
	 */
	public function get_rows_link(&$query, &$rows, &$readonlys) {
		Api\Cache::setSession('elogin_link_list', 'elogin', $query);

		$count = elogin_link_bo::get_rows($query, $rows, $readonlys);

		foreach( $rows as &$row ) {
			$row['icon'] = 'link.png';
		}

		return $count;
	}

	/**
	 * open
	 * @param array $content
	 */
	public function open($content=array()) {
		$readonlys = array();

		$uid = ( isset($content['uid']) ? $content['uid'] : null);
		$uid = ( $uid == null ? (isset($_GET['uid']) ? $_GET['uid'] : null) : $uid);

		if( $uid == null ) {
			// error
		}

		$link = new elogin_link_bo($uid);
		$content['linkopen'] = $link->buildUri();

		Api\Header\ContentSecurityPolicy::add('frame-src', array(
			"egwwinlogon://*"
			));

		$tpl = new Etemplate('elogin.link.open');
		$tpl->exec(
			'elogin.elogin_link_ui.open',
			$content,
			array(),
			$readonlys,
			array(),
			0);
	}

	/**
	 * edit
	 * @param array $content
	 */
	public function edit($content=array()) {
		$preserv	= array();
		$readonlys	= array();
		$option_sel	= array();

		$uid = ( isset($content['uid']) ? $content['uid'] : null);
		$uid = ( $uid == null ? (isset($_GET['uid']) ? $_GET['uid'] : null) : $uid);

		$link = null;

		if( $uid !== null ) {
			$link = new elogin_link_bo($uid);

			$preserv['uid'] = $link->getId();
		}

		// -----------------------------------------------------------------

		if( isset($content['button']) && isset($content['button']['apply']) ) {
			$content['button']['save'] = "pressed";
		}

		if( isset($content['button']) && isset($content['button']['save']) ) {
			if( $link == null ) {
				$link = new elogin_link_bo();
			}

			$link->setUserShareId($content['usershare']);
			$link->setUserShareMountId($content['mountshare']);
			$link->setFilePath($content['filepath']);

			$link->save();

			$preserv['uid'] = $link->getId();
		}

		// -----------------------------------------------------------------

		if( $link instanceof elogin_link_bo ) {
			$content['usershare']	= $link->getUserShareId();
			$content['mountshare']	= $link->getUserShareMountId();
			$content['filepath']	= $link->getFilePath();
		}

		// -----------------------------------------------------------------

		$usershare = array();

		$us_query		= array();
		$us_rows		= array();
		$us_readonlys	= array();

		elogin_usershares_bo::get_rows($us_query, $us_rows, $us_readonlys);

		foreach( $us_rows as &$trow ) {
			$t = new elogin_usershares_bo($trow['el_unid']);

			if( $t instanceof elogin_usershares_bo ) {
				$provider = $t->getProvider(true);

				if( $provider instanceof elogin_shareprovider_bo ) {
					if( (count($usershare) == 0) ) {
						if( !isset($content['usershare']) ) {
							$content['usershare'] = $trow['el_unid'];
						}
					}

					$usershare[$trow['el_unid']] =
						$provider->getProviderName() . ' - ' . $t->getUsername();
				}
			}
		}

		$option_sel['usershare'] = $usershare;

		// -----------------------------------------------------------------

		$usermounts = array();

		if( isset($content['usershare']) ) {
			$t = new elogin_usershares_bo($content['usershare']);

			if( $t instanceof elogin_usershares_bo ) {
				$mounts = $t->getUserSharesMounts();

				foreach( $mounts as $mount ) {
					$usermounts[$mount->getId()] = $mount->getMountname();
				}
			}
		}

		$option_sel['mountshare'] = $usermounts;

		// -----------------------------------------------------------------

		$etemplate = new Etemplate('elogin.link.dialog');
		$etemplate->exec(
			'elogin.elogin_link_ui.edit',
			array_merge($content, $preserv),
			$option_sel,
			$readonlys,
			$preserv,
			2);
	}
}