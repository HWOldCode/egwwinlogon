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

    // ---- share provider dir exist -------------------------------------------

    $setup_info_spde = array();
	$setup_info_spde['name']         = 'elogin';
    $setup_info_spde['app']          = 'elogin';
    $setup_info_spde['class']        = 'elogin_action_share_provider_dir_exist';
    $setup_info_spde['title']        = 'ELogin Share Provider dir exist';
    $setup_info_spde['version']      = '1.0';

    $setup_info_spde['author']       = 'Stefan Werfling';
    $setup_info_spde['license']      = 'HW license';
    $setup_info_spde['description']  = 'ELogin get Share Provider dir exist';

    $setup_info_spde['maintainer'] = array(
		'name' 	=> 'HW-Softwareentwicklung GbR',
		'email' => 'info@hw-softwareentwicklung.de'
        );

    $setup_info_spde['settings'] = array();

    $setup_info_spde['depends'] = array();

    $setup_info[] = $setup_info_spde;

    // ---- share provider dir create ------------------------------------------

    $setup_info_spdc = array();
	$setup_info_spdc['name']         = 'elogin';
    $setup_info_spdc['app']          = 'elogin';
    $setup_info_spdc['class']        = 'elogin_action_share_provider_dir_create';
    $setup_info_spdc['title']        = 'ELogin Share Provider dir create';
    $setup_info_spdc['version']      = '1.0';

    $setup_info_spdc['author']       = 'Stefan Werfling';
    $setup_info_spdc['license']      = 'HW license';
    $setup_info_spdc['description']  = 'ELogin get Share Provider dir create';

    $setup_info_spdc['maintainer'] = array(
		'name' 	=> 'HW-Softwareentwicklung GbR',
		'email' => 'info@hw-softwareentwicklung.de'
        );

    $setup_info_spdc['settings'] = array();

    $setup_info_spdc['depends'] = array();

    $setup_info[] = $setup_info_spdc;

    // ---- share provider dir permission remove -------------------------------

    $setup_info_spdpr = array();
	$setup_info_spdpr['name']         = 'elogin';
    $setup_info_spdpr['app']          = 'elogin';
    $setup_info_spdpr['class']        = 'elogin_action_share_provider_dir_permission_remove';
    $setup_info_spdpr['title']        = 'ELogin Share Provider dir permission remove';
    $setup_info_spdpr['version']      = '1.0';

    $setup_info_spdpr['author']       = 'Stefan Werfling';
    $setup_info_spdpr['license']      = 'HW license';
    $setup_info_spdpr['description']  = 'ELogin get Share Provider dir permission remove';

    $setup_info_spdpr['maintainer'] = array(
		'name' 	=> 'HW-Softwareentwicklung GbR',
		'email' => 'info@hw-softwareentwicklung.de'
        );

    $setup_info_spdpr['settings'] = array();

    $setup_info_spdpr['depends'] = array();

    $setup_info[] = $setup_info_spdpr;

    // ---- share provider dir permission set ----------------------------------

    $setup_info_spdps = array();
	$setup_info_spdps['name']         = 'elogin';
    $setup_info_spdps['app']          = 'elogin';
    $setup_info_spdps['class']        = 'elogin_action_share_provider_dir_permission_set';
    $setup_info_spdps['title']        = 'ELogin Share Provider dir permission set';
    $setup_info_spdps['version']      = '1.0';

    $setup_info_spdps['author']       = 'Stefan Werfling';
    $setup_info_spdps['license']      = 'HW license';
    $setup_info_spdps['description']  = 'ELogin get Share Provider dir permission set';

    $setup_info_spdps['maintainer'] = array(
		'name' 	=> 'HW-Softwareentwicklung GbR',
		'email' => 'info@hw-softwareentwicklung.de'
        );

    $setup_info_spdps['settings'] = array();

    $setup_info_spdps['depends'] = array();

    $setup_info[] = $setup_info_spdps;

    // ---- share provider dir permission setpm --------------------------------

    $setup_info_spdpspm = array();
	$setup_info_spdpspm['name']         = 'elogin';
    $setup_info_spdpspm['app']          = 'elogin';
    $setup_info_spdpspm['class']        = 'elogin_action_share_provider_dir_permission_setpm';
    $setup_info_spdpspm['title']        = 'ELogin Share Provider dir permission set projectmanager';
    $setup_info_spdpspm['version']      = '1.0';

    $setup_info_spdpspm['author']       = 'Stefan Werfling';
    $setup_info_spdpspm['license']      = 'HW license';
    $setup_info_spdpspm['description']  = 'ELogin get Share Provider dir permission set projectmanager';

    $setup_info_spdpspm['maintainer'] = array(
		'name' 	=> 'HW-Softwareentwicklung GbR',
		'email' => 'info@hw-softwareentwicklung.de'
        );

    $setup_info_spdpspm['settings'] = array();

    $setup_info_spdpspm['depends'] = array();

    $setup_info[] = $setup_info_spdpspm;

	// ---- machine trigger ----------------------------------------------------

	$setup_info_mt = array();
	$setup_info_mt['name']         = 'elogin';
    $setup_info_mt['app']          = 'elogin';
    $setup_info_mt['class']        = 'elogin_action_machine_trigger';
    $setup_info_mt['title']        = 'ELogin Machine Trigger';
    $setup_info_mt['version']      = '1.0';

    $setup_info_mt['author']       = 'Stefan Werfling';
    $setup_info_mt['license']      = 'HW license';
    $setup_info_mt['description']  = 'ELogin Machine Trigger';

    $setup_info_mt['maintainer'] = array(
		'name' 	=> 'HW-Softwareentwicklung GbR',
		'email' => 'info@hw-softwareentwicklung.de'
        );

    $setup_info_mt['settings'] = array();

    $setup_info_mt['depends'] = array();

    $setup_info[] = $setup_info_mt;