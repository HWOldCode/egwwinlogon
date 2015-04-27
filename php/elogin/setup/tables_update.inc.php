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

