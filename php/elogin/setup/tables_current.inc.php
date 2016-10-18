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


$phpgw_baseline = array(
	'egw_elogin_shareproviders' => array(
		'fd' => array(
			'el_unid' => array('type' => 'varchar','precision' => '64'),
			'el_provider_name' => array('type' => 'varchar','precision' => '256'),
			'el_account_server' => array('type' => 'varchar','precision' => '128'),
			'el_account_port' => array('type' => 'int','precision' => '4'),
			'el_account_user' => array('type' => 'varchar','precision' => '128'),
			'el_account_password' => array('type' => 'varchar','precision' => '128'),
			'el_mount_address' => array('type' => 'varchar','precision' => '128'),
			'el_activ' => array('type' => 'bool'),
			'el_protocol' => array('type' => 'varchar','precision' => '25'),
			'el_api_version' => array('type' => 'varchar','precision' => '25'),
			'el_description' => array('type' => 'varchar','precision' => '256')
		),
		'pk' => array('el_unid'),
		'fk' => array(),
		'ix' => array('el_unid','el_account_server','el_account_user','el_activ'),
		'uc' => array()
	),
	'egw_elogin_usershares' => array(
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
	),
	'egw_elogin_machine' => array(
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
	),
	'egw_elogin_machine_logging' => array(
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
	),
	'egw_elogin_usershares_mount' => array(
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
	),
	'egw_elogin_cmd' => array(
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
			'el_name' => array('type' => 'varchar','precision' => '255'),
			'el_scheduler_time' => array('type' => 'int','precision' => '4'),
			'el_mountpoint_check' => array('type' => 'varchar','precision' => '25')
		),
		'pk' => array('el_unid'),
		'fk' => array(),
		'ix' => array('el_unid','el_machine_id','el_account_id','el_system','el_order','el_type','el_event','el_catid','el_script_type','el_name'),
		'uc' => array()
	),
	'egw_elogin_link' => array(
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
	)
);
