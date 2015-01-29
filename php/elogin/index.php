<?php


    /**
	 * ELogin - Egroupware
	 *
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-14 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id:$
	 */

	$GLOBALS['egw_info'] = array(
		'flags' => array(
			'noheader'                => true,
			'nonavbar'                => true,
			'currentapp'              => 'elogin',
			'enable_network_class'    => false,
			'enable_contacts_class'   => false,
			'enable_nextmatchs_class' => false,
			'include_xajax'		  	  => true,
		)
	);

	// Header Inc
	include('../header.inc.php');

	// Redirect
	$GLOBALS['egw']->redirect_link('/index.php','menuaction=elogin.elogin_ui.index');