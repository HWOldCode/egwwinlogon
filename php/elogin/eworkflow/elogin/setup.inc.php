<?php

    /**
	 * ELogin - Egroupware
	 *
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-15 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id:$
	 */

    $setup_info = array();

    // ---- share provider account ---------------------------------------------
	$setup_info_spa = array();
	$setup_info_spa['name']         = 'elogin';
    $setup_info_spa['app']          = 'elogin';
    $setup_info_spa['class']        = 'elogin_action_share_provider_account';
    $setup_info_spa['title']        = 'ELogin Share Provider Account';
    $setup_info_spa['version']      = '1.0';

    $setup_info_spa['author']       = 'Stefan Werfling';
    $setup_info_spa['license']      = 'HW license';
    $setup_info_spa['description']  = 'ELogin get Share Provider Account';

    $setup_info_spa['maintainer'] = array(
		'name' 	=> 'HW-Softwareentwicklung GbR',
		'email' => 'info@hw-softwareentwicklung.de'
        );

    $setup_info_spa['settings'] = array();

    $setup_info_spa['depends'] = array();

    $setup_info[] = $setup_info_spa;

    // ---- share provider shares ----------------------------------------------

    $setup_info_sps = array();
	$setup_info_sps['name']         = 'elogin';
    $setup_info_sps['app']          = 'elogin';
    $setup_info_sps['class']        = 'elogin_action_share_provider_shares';
    $setup_info_sps['title']        = 'ELogin Share Provider Shares';
    $setup_info_sps['version']      = '1.0';

    $setup_info_sps['author']       = 'Stefan Werfling';
    $setup_info_sps['license']      = 'HW license';
    $setup_info_sps['description']  = 'ELogin get Share Provider Shares';

    $setup_info_sps['maintainer'] = array(
		'name' 	=> 'HW-Softwareentwicklung GbR',
		'email' => 'info@hw-softwareentwicklung.de'
        );

    $setup_info_sps['settings'] = array();

    $setup_info_sps['depends'] = array();

    $setup_info[] = $setup_info_sps;