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

	/**
	 * Base App Info
	 */
	$setup_info['elogin']['name']      			= 'elogin';
	$setup_info['elogin']['title']     			= 'ELogin';
	$setup_info['elogin']['version']			= '1.9.074';
	$setup_info['elogin']['app_order'] 			= 2;
	$setup_info['elogin']['enable']    			= 1;

	/**
	 * Base Dev Info
	 */
	$setup_info['elogin']['author']				= 'Stefan Werfling';
	$setup_info['elogin']['license']  			= 'HW license';
	$setup_info['elogin']['description']		= 'HW';

	$setup_info['elogin']['hooks']['settings']           		= 'elogin_hooks::settings';
	$setup_info['elogin']['hooks']['admin']           		= 'elogin_hooks::admin';
	$setup_info['elogin']['hooks']['sidebox_menu']       	= 'elogin_hooks::all_hooks';

	$setup_info['elogin']['maintainer'] = array(
		'name' 	=> 'HW-Softwareentwicklung GbR',
		'email' => 'info@hw-softwareentwicklung.de'
        );	
	
	// TABLES
	
	$setup_info['elogin']['tables'] = array('egw_elogin_shareproviders','egw_elogin_usershares','egw_elogin_machine','egw_elogin_machine_logging','egw_elogin_usershares_mount','egw_elogin_cmd');

