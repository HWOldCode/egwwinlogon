<?php
/**
 * eGroupWare - Setup
 * http://www.egroupware.org
 * Created by eTemplates DB-Tools written by ralfbecker@outdoor-training.de
 *
 * @license http://opensource.org/licenses/gpl-license.php GPL - GNU General Public License
 * @package elogin
 * @subpackage setup
 * @version $Id$
 */

function elogin_upgrade1_9_068()
{
	$GLOBALS['egw_setup']->oProc->CreateTable('egw_elogin_usershares',array(
		'fd' => array(
			'el_unid' => array('type' => 'varchar','precision' => '64'),
			'el_provider_id' => array('type' => 'varchar','precision' => '64'),
			'el_egw_account' => array('type' => 'int','precision' => '4'),
			'el_sharepassword' => array('type' => 'varchar','precision' => '128'),
			'el_shareinfo' => array('type' => 'text')
		),
		'pk' => array('el_unid'),
		'fk' => array(),
		'ix' => array('el_unid','el_provider_id','el_egw_account'),
		'uc' => array()
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.069';
}


function elogin_upgrade1_9_069()
{
	$GLOBALS['egw_setup']->oProc->CreateTable('egw_elogin_machine',array(
		'fd' => array(
			'el_unid' => array('type' => 'varchar','precision' => '64'),
			'el_name' => array('type' => 'varchar','precision' => '256')
		),
		'pk' => array('el_unid'),
		'fk' => array(),
		'ix' => array('el_unid','el_name'),
		'uc' => array()
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.070';
}


function elogin_upgrade1_9_070()
{
	$GLOBALS['egw_setup']->oProc->CreateTable('egw_elogin_machine_logging',array(
		'fd' => array(
			'el_unid' => array('type' => 'varchar','precision' => '64'),
			'el_machine_id' => array('type' => 'varchar','precision' => '64'),
			'el_account_id' => array('type' => 'int','precision' => '4'),
			'el_event' => array('type' => 'varchar','precision' => '128'),
			'el_level' => array('type' => 'int','precision' => '4'),
			'el_logdate' => array('type' => 'timestamp'),
			'el_message' => array('type' => 'text')
		),
		'pk' => array('el_unid'),
		'fk' => array(),
		'ix' => array('el_unid','el_machine_id','el_account_id','el_event','el_level'),
		'uc' => array()
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.071';
}


function elogin_upgrade1_9_071()
{
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_shareproviders','el_mount_address',array(
		'type' => 'varchar',
		'precision' => '128'
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.072';
}


function elogin_upgrade1_9_072()
{
	$GLOBALS['egw_setup']->oProc->CreateTable('egw_elogin_usershares_mount',array(
		'fd' => array(
			'el_unid' => array('type' => 'varchar','precision' => '64'),
			'el_usershare_id' => array('type' => 'varchar','precision' => '64'),
			'el_machine_id' => array('type' => 'varchar','precision' => '64'),
			'el_account_id' => array('type' => 'int','precision' => '4'),
			'el_share_source' => array('type' => 'varchar','precision' => '512'),
			'el_mount_name' => array('type' => 'varchar','precision' => '128')
		),
		'pk' => array('el_unid'),
		'fk' => array(),
		'ix' => array('el_unid','el_usershare_id','el_machine_id','el_account_id','el_share_source','el_mount_name'),
		'uc' => array()
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.073';
}


function elogin_upgrade1_9_073()
{
	$GLOBALS['egw_setup']->oProc->CreateTable('egw_elogin_cmd',array(
		'fd' => array(
			'el_unid' => array('type' => 'varchar','precision' => '64'),
			'el_machine_id' => array('type' => 'varchar','precision' => '64'),
			'el_account_id' => array('type' => 'int','precision' => '4'),
			'el_command' => array('type' => 'varchar','precision' => '255'),
			'el_system' => array('type' => 'varchar','precision' => '128'),
			'el_order' => array('type' => 'int','precision' => '4'),
			'el_type' => array('type' => 'varchar','precision' => '128'),
			'el_event' => array('type' => 'varchar','precision' => '128')
		),
		'pk' => array('el_unid'),
		'fk' => array(),
		'ix' => array('el_unid','el_machine_id','el_account_id','el_system','el_order','el_type','el_event'),
		'uc' => array()
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.074';
}


function elogin_upgrade1_9_074()
{
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_cmd','el_condition',array(
		'type' => 'text'
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.075';
}


function elogin_upgrade1_9_075()
{
	/* done by RefreshTable() anyway
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_machine_logging','el_index',array(
		'type' => 'varchar',
		'precision' => '256'
	));*/
	$GLOBALS['egw_setup']->oProc->RefreshTable('egw_elogin_machine_logging',array(
		'fd' => array(
			'el_unid' => array('type' => 'varchar','precision' => '64'),
			'el_machine_id' => array('type' => 'varchar','precision' => '64'),
			'el_account_id' => array('type' => 'int','precision' => '4'),
			'el_event' => array('type' => 'varchar','precision' => '128'),
			'el_level' => array('type' => 'int','precision' => '4'),
			'el_logdate' => array('type' => 'timestamp'),
			'el_message' => array('type' => 'text'),
			'el_index' => array('type' => 'varchar','precision' => '256')
		),
		'pk' => array('el_unid'),
		'fk' => array(),
		'ix' => array('el_unid','el_machine_id','el_account_id','el_event','el_level','el_index'),
		'uc' => array()
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.076';
}


function elogin_upgrade1_9_076()
{
	/* done by RefreshTable() anyway
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_cmd','el_catid',array(
		'type' => 'int',
		'precision' => '4'
	));*/
	/* done by RefreshTable() anyway
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_cmd','el_script_type',array(
		'type' => 'varchar',
		'precision' => '128'
	));*/
	/* done by RefreshTable() anyway
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_cmd','el_script',array(
		'type' => 'text'
	));*/
	/* done by RefreshTable() anyway
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_cmd','el_options',array(
		'type' => 'text'
	));*/
	/* done by RefreshTable() anyway
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_cmd','el_name',array(
		'type' => 'varchar',
		'precision' => '255'
	));*/
	$GLOBALS['egw_setup']->oProc->RefreshTable('egw_elogin_cmd',array(
		'fd' => array(
			'el_unid' => array('type' => 'varchar','precision' => '64'),
			'el_machine_id' => array('type' => 'varchar','precision' => '64'),
			'el_account_id' => array('type' => 'int','precision' => '4'),
			'el_command' => array('type' => 'varchar','precision' => '255'),
			'el_system' => array('type' => 'varchar','precision' => '128'),
			'el_order' => array('type' => 'int','precision' => '4'),
			'el_type' => array('type' => 'varchar','precision' => '128'),
			'el_event' => array('type' => 'varchar','precision' => '128'),
			'el_condition' => array('type' => 'text'),
			'el_catid' => array('type' => 'int','precision' => '4'),
			'el_script_type' => array('type' => 'varchar','precision' => '128'),
			'el_script' => array('type' => 'text'),
			'el_options' => array('type' => 'text'),
			'el_name' => array('type' => 'varchar','precision' => '255')
		),
		'pk' => array('el_unid'),
		'fk' => array(),
		'ix' => array('el_unid','el_machine_id','el_account_id','el_system','el_order','el_type','el_event','el_catid','el_script_type','el_name'),
		'uc' => array()
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.077';
}

