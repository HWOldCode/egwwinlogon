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


function elogin_upgrade1_9_077()
{
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_cmd','el_scheduler_time',array(
		'type' => 'int',
		'precision' => '4'
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.078';
}


function elogin_upgrade1_9_078()
{
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_cmd','el_mountpoint_check',array(
		'type' => 'varchar',
		'precision' => '25'
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.079';
}


function elogin_upgrade1_9_079()
{
	$GLOBALS['egw_setup']->oProc->CreateTable('egw_elogin_link',array(
		'fd' => array(
			'el_unid' => array('type' => 'varchar','precision' => '64'),
			'el_usershare_id' => array('type' => 'varchar','precision' => '64'),
			'el_usershare_mount_id' => array('type' => 'varchar','precision' => '64'),
			'el_filepath' => array('type' => 'varchar','precision' => '512'),
			'el_options' => array('type' => 'text')
		),
		'pk' => array('el_unid'),
		'fk' => array(),
		'ix' => array('el_unid','el_usershare_id','el_usershare_mount_id','el_filepath'),
		'uc' => array()
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.080';
}


function elogin_upgrade1_9_080()
{
	/* done by RefreshTable() anyway
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_shareproviders','el_activ',array(
		'type' => 'bool'
	));*/
	$GLOBALS['egw_setup']->oProc->RefreshTable('egw_elogin_shareproviders',array(
		'fd' => array(
			'el_unid' => array('type' => 'varchar','precision' => '64'),
			'el_provider_name' => array('type' => 'varchar','precision' => '256'),
			'el_account_server' => array('type' => 'varchar','precision' => '128'),
			'el_account_port' => array('type' => 'int','precision' => '4'),
			'el_account_user' => array('type' => 'varchar','precision' => '128'),
			'el_account_password' => array('type' => 'varchar','precision' => '128'),
			'el_mount_address' => array('type' => 'varchar','precision' => '128'),
			'el_activ' => array('type' => 'bool')
		),
		'pk' => array('el_unid'),
		'fk' => array(),
		'ix' => array('el_unid','el_provider_name','el_account_server','el_account_user','el_activ'),
		'uc' => array()
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.081';
}


function elogin_upgrade1_9_081()
{
	/* done by RefreshTable() anyway
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_machine','el_last_user_login_id',array(
		'type' => 'int',
		'precision' => '4'
	));*/
	/* done by RefreshTable() anyway
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_machine','el_last_user_login_time',array(
		'type' => 'timestamp'
	));*/
	$GLOBALS['egw_setup']->oProc->RefreshTable('egw_elogin_machine',array(
		'fd' => array(
			'el_unid' => array('type' => 'varchar','precision' => '64'),
			'el_name' => array('type' => 'varchar','precision' => '256'),
			'el_last_user_login_id' => array('type' => 'int','precision' => '4'),
			'el_last_user_login_time' => array('type' => 'timestamp')
		),
		'pk' => array('el_unid'),
		'fk' => array(),
		'ix' => array('el_unid','el_name','el_last_user_login_id','el_last_user_login_time'),
		'uc' => array()
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.082';
}


function elogin_upgrade1_9_082()
{
	$GLOBALS['egw_setup']->oProc->RefreshTable('egw_elogin_shareproviders',array(
		'fd' => array(
			'el_unid' => array('type' => 'varchar','precision' => '64'),
			'el_provider_name' => array('type' => 'varchar','precision' => '256'),
			'el_account_server' => array('type' => 'varchar','precision' => '128'),
			'el_account_port' => array('type' => 'int','precision' => '4'),
			'el_account_user' => array('type' => 'varchar','precision' => '128'),
			'el_account_password' => array('type' => 'varchar','precision' => '128'),
			'el_mount_address' => array('type' => 'varchar','precision' => '128'),
			'el_activ' => array('type' => 'bool')
		),
		'pk' => array('el_unid'),
		'fk' => array(),
		'ix' => array('el_unid','el_account_server','el_account_user','el_activ'),
		'uc' => array()
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.083';
}


function elogin_upgrade1_9_083()
{
	$GLOBALS['egw_setup']->oProc->RefreshTable('egw_elogin_machine',array(
		'fd' => array(
			'el_unid' => array('type' => 'varchar','precision' => '64'),
			'el_name' => array('type' => 'varchar','precision' => '256'),
			'el_last_user_login_id' => array('type' => 'int','precision' => '4'),
			'el_last_user_login_time' => array('type' => 'timestamp')
		),
		'pk' => array('el_unid'),
		'fk' => array(),
		'ix' => array('el_unid','el_last_user_login_id','el_last_user_login_time'),
		'uc' => array()
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.084';
}


function elogin_upgrade1_9_084()
{
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_shareproviders','el_protocol',array(
		'type' => 'varchar',
		'precision' => '25'
	));
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_shareproviders','el_api_version',array(
		'type' => 'varchar',
		'precision' => '25'
	));
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_shareproviders','el_description',array(
		'type' => 'varchar',
		'precision' => '256'
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.085';
}


function elogin_upgrade1_9_085()
{
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_shareproviders','el_collectiv_share',array(
		'type' => 'varchar',
		'precision' => '256'
	));
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_shareproviders','el_last_update',array(
		'type' => 'timestamp'
	));
	$GLOBALS['egw_setup']->oProc->AddColumn('egw_elogin_shareproviders','el_last_task_update',array(
		'type' => 'timestamp'
	));

	return $GLOBALS['setup_info']['elogin']['currentver'] = '1.9.086';
}

