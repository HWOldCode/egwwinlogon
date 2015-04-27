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
			'el_account_password' => array('type' => 'varchar','precision' => '128')
		),
		'pk' => array('el_unid'),
		'fk' => array(),
		'ix' => array('el_unid','el_provider_name','el_account_server','el_account_user'),
		'uc' => array()
	)
);
